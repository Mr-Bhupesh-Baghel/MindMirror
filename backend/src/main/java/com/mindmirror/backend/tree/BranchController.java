package com.mindmirror.backend.tree;
import com.mindmirror.backend.user.AppUser;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/branches")
public class BranchController {
 private final BranchService service; public BranchController(BranchService service) { this.service=service; }
 @GetMapping public List<BranchDtos.BranchResponse> list(@AuthenticationPrincipal AppUser user) { return service.list(user); }
 @PostMapping("/blueprint/{blueprint}") public List<BranchDtos.BranchResponse> blueprint(@AuthenticationPrincipal AppUser user,@PathVariable String blueprint) { return service.applyBlueprint(user,blueprint); }
 @PostMapping public BranchDtos.BranchResponse create(@AuthenticationPrincipal AppUser user,@Valid @RequestBody BranchDtos.BranchRequest request) { return service.create(user,request); }
 @PatchMapping("/{id}") public BranchDtos.BranchResponse update(@AuthenticationPrincipal AppUser user,@PathVariable String id,@Valid @RequestBody BranchDtos.BranchRequest request) { return service.update(user,id,request); }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void archive(@AuthenticationPrincipal AppUser user,@PathVariable String id) { service.archive(user,id); }
 @GetMapping("/{id}/workspace") public BranchDtos.WorkspaceResponse workspace(@AuthenticationPrincipal AppUser user,@PathVariable String id) { return service.workspace(user,id); }
 @PostMapping("/{id}/items") public BranchDtos.ItemResponse addItem(@AuthenticationPrincipal AppUser user,@PathVariable String id,@Valid @RequestBody BranchDtos.ItemRequest request) { return service.addItem(user,id,request); }
 @PostMapping("/{branchId}/items/{itemId}/toggle") public BranchDtos.ItemResponse toggle(@AuthenticationPrincipal AppUser user,@PathVariable String branchId,@PathVariable String itemId) { return service.toggleItem(user,branchId,itemId); }
 @DeleteMapping("/{branchId}/items/{itemId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteItem(@AuthenticationPrincipal AppUser user,@PathVariable String branchId,@PathVariable String itemId) { service.deleteItem(user,branchId,itemId); }
}
