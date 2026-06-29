package com.mindmirror.backend.feedback;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mindmirror.backend.feedback.dto.CreateFeedbackRequest;
import com.mindmirror.backend.feedback.dto.FeedbackResponse;
import com.mindmirror.backend.feedback.dto.PagedResponse;
import com.mindmirror.backend.security.AuthenticatedUser;
import com.mindmirror.backend.user.entity.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    FeedbackResponse create(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody CreateFeedbackRequest request
    ) {
        User user = authenticatedUser == null ? null : authenticatedUser.getUser();
        return feedbackService.create(request, user);
    }

    @GetMapping
    PagedResponse<FeedbackResponse> list(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return PagedResponse.from(feedbackService.list(pageRequest));
    }

    @GetMapping("/{id}")
    FeedbackResponse get(@PathVariable Long id) {
        return feedbackService.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        feedbackService.delete(id);
    }
}
