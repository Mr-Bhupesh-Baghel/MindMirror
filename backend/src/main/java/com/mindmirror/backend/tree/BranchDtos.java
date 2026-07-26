package com.mindmirror.backend.tree;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;
public final class BranchDtos {
 private BranchDtos() { }
 public record BranchResponse(String id, String parentId, String name, String icon, String color, int position) { }
 public record BranchRequest(@NotBlank @Size(max=100) String name, @Size(max=16) String icon, @Pattern(regexp="^#[0-9a-fA-F]{6}$", message="Color must be hex") String color, String parentId) { }
 public record ItemResponse(String id, String type, String title, String content, boolean completed, LocalDate dueOn) { }
 public record ItemRequest(@NotBlank @Pattern(regexp="GOAL|NOTE|RESOURCE|ACHIEVEMENT|EVENT") String type, @NotBlank @Size(max=200) String title, @Size(max=10000) String content, LocalDate dueOn) { }
 public record WorkspaceResponse(BranchResponse branch, List<ItemResponse> items) { }
}
