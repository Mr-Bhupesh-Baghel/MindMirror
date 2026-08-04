package com.mindmirror.backend.tree;

import com.mindmirror.backend.user.AppUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skills")
public class SkillController {
    private final TreeService tree;
    public SkillController(TreeService tree) { this.tree = tree; }
    @PostMapping("/{skillKey}/lessons/complete")
    public TreeDtos.TreeState completeLesson(@AuthenticationPrincipal AppUser user, @PathVariable String skillKey) { return tree.completeLesson(user, skillKey); }
}
