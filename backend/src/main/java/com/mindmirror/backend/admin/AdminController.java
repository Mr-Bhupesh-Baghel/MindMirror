package com.mindmirror.backend.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.mindmirror.backend.admin.dto.AdminStatsResponse;
import com.mindmirror.backend.admin.dto.AdminUserResponse;
import com.mindmirror.backend.admin.dto.AdminUserUpdateRequest;
import com.mindmirror.backend.feedback.FeedbackService;
import com.mindmirror.backend.feedback.dto.FeedbackResponse;
import com.mindmirror.backend.feedback.dto.PagedResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final String XLSX_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final AdminService adminService;
    private final FeedbackService feedbackService;

    public AdminController(AdminService adminService, FeedbackService feedbackService) {
        this.adminService = adminService;
        this.feedbackService = feedbackService;
    }

    @GetMapping("/users")
    PagedResponse<AdminUserResponse> users(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return PagedResponse.from(adminService.listUsers(pageRequest));
    }

    @PatchMapping("/users/{id}")
    AdminUserResponse updateUser(@PathVariable Long id, @Valid @RequestBody AdminUserUpdateRequest request) {
        return adminService.updateUser(id, request);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
    }

    @GetMapping("/feedback")
    PagedResponse<FeedbackResponse> feedback(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return PagedResponse.from(feedbackService.list(pageRequest));
    }

    @GetMapping("/stats")
    AdminStatsResponse stats() {
        return adminService.stats();
    }

    @GetMapping("/export")
    ResponseEntity<byte[]> export(
        @RequestParam(defaultValue = "users") String dataset,
        @RequestParam(defaultValue = "csv") String format
    ) {
        byte[] body = adminService.export(dataset, format);
        String filename = adminService.exportFilename(dataset, format);
        MediaType mediaType = "xlsx".equalsIgnoreCase(format)
            ? MediaType.parseMediaType(XLSX_MEDIA_TYPE)
            : MediaType.parseMediaType("text/csv");

        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
            .body(body);
    }
}
