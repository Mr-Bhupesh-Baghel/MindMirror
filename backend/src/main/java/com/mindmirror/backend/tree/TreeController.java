package com.mindmirror.backend.tree;

import com.mindmirror.backend.user.AppUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tree")
public class TreeController {
    private final TreeService tree;
    public TreeController(TreeService tree) { this.tree = tree; }
    @GetMapping public TreeDtos.TreeState state(@AuthenticationPrincipal AppUser user) { return tree.state(user); }
    @PostMapping("/today/{habit}") public TreeDtos.TreeState toggle(@AuthenticationPrincipal AppUser user, @PathVariable Habit habit) { return tree.toggle(user, habit); }
}
