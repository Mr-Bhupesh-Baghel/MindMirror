package com.mindmirror.backend.tree;
import com.mindmirror.backend.user.AppUser;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BranchService {
 private final TreeBranchRepository branches; private final BranchItemRepository items;
 public BranchService(TreeBranchRepository branches, BranchItemRepository items) { this.branches = branches; this.items = items; }
 @Transactional public List<BranchDtos.BranchResponse> list(AppUser user) { return branches.findByUserAndArchivedFalseOrderByPositionAsc(user).stream().map(this::branch).toList(); }
 @Transactional public List<BranchDtos.BranchResponse> applyBlueprint(AppUser user, String blueprint) { if (!branches.findByUserAndArchivedFalseOrderByPositionAsc(user).isEmpty()) return list(user); List<String[]> entries = switch (blueprint.toLowerCase()) { case "student" -> List.of(new String[]{"Learning","📚","#6477a8"},new String[]{"Health","❤️","#c76d6d"},new String[]{"Personal","😊","#bd8b56"}); case "developer" -> List.of(new String[]{"Career","💼","#5d7caa"},new String[]{"Learning","📚","#6477a8"},new String[]{"Health","❤️","#c76d6d"}); case "entrepreneur" -> List.of(new String[]{"Business","🚀","#5d7caa"},new String[]{"Finance","💰","#bd8b56"},new String[]{"Personal","😊","#aa759a"}); case "wellness" -> List.of(new String[]{"Health","❤️","#c76d6d"},new String[]{"Mindfulness","🧘","#aa759a"},new String[]{"Personal","😊","#bd8b56"}); case "creative" -> List.of(new String[]{"Creative Work","🎨","#aa759a"},new String[]{"Learning","📚","#6477a8"},new String[]{"Inspiration","✨","#bd8b56"}); case "finance" -> List.of(new String[]{"Finances","💰","#bd8b56"},new String[]{"Career","💼","#5d7caa"},new String[]{"Goals","🎯","#6f9e63"}); case "family" -> List.of(new String[]{"Family","👨‍👩‍👧","#c76d6d"},new String[]{"Home","🏠","#bd8b56"},new String[]{"Personal","😊","#aa759a"}); case "empty" -> List.of(); default -> List.of(new String[]{"Health","❤️","#c76d6d"},new String[]{"Learning","📚","#6477a8"},new String[]{"Career","💼","#5d7caa"},new String[]{"Personal","😊","#bd8b56"}); }; for(int i=0;i<entries.size();i++) branches.save(new TreeBranch(user,null,entries.get(i)[0],entries.get(i)[1],entries.get(i)[2],i)); return list(user); }
 @Transactional public BranchDtos.BranchResponse create(AppUser user, BranchDtos.BranchRequest request) { TreeBranch parent = request.parentId() == null || request.parentId().isBlank() ? null : owned(user, request.parentId()); int position = branches.findByUserAndArchivedFalseOrderByPositionAsc(user).size(); return branch(branches.save(new TreeBranch(user,parent,request.name(), value(request.icon(),"🌿"),value(request.color(),"#6f9e63"),position))); }
 @Transactional public BranchDtos.BranchResponse update(AppUser user, String id, BranchDtos.BranchRequest request) { TreeBranch branch=owned(user,id); branch.update(request.name(),request.icon(),request.color()); return branch(branch); }
 @Transactional public void archive(AppUser user, String id) { owned(user,id).archive(); }
 @Transactional public BranchDtos.WorkspaceResponse workspace(AppUser user, String id) { TreeBranch branch=owned(user,id); return new BranchDtos.WorkspaceResponse(branch(branch),items.findByBranchOrderByCreatedAtDesc(branch).stream().map(this::item).toList()); }
 @Transactional public BranchDtos.ItemResponse addItem(AppUser user,String id,BranchDtos.ItemRequest request) { TreeBranch branch=owned(user,id); return item(items.save(new BranchItem(branch,request.type(),request.title(),request.content(),request.dueOn()))); }
 @Transactional public BranchDtos.ItemResponse toggleItem(AppUser user,String branchId,String itemId) { TreeBranch branch=owned(user,branchId); BranchItem item=items.findByIdAndBranch(UUID.fromString(itemId),branch).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Item not found")); item.toggle(); return item(item); }
 @Transactional public void deleteItem(AppUser user,String branchId,String itemId) { TreeBranch branch=owned(user,branchId); items.delete(items.findByIdAndBranch(UUID.fromString(itemId),branch).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Item not found"))); }
 private TreeBranch owned(AppUser user,String id) { try { return branches.findByIdAndUser(UUID.fromString(id),user).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Branch not found")); } catch(IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Branch not found"); } }
 private String value(String candidate,String fallback) { return candidate==null||candidate.isBlank()?fallback:candidate; }
 private BranchDtos.BranchResponse branch(TreeBranch b) { return new BranchDtos.BranchResponse(b.getId().toString(),b.getParent()==null?null:b.getParent().getId().toString(),b.getName(),b.getIcon(),b.getColor(),b.getPosition()); }
 private BranchDtos.ItemResponse item(BranchItem i) { return new BranchDtos.ItemResponse(i.getId().toString(),i.getItemType(),i.getTitle(),i.getContent(),i.isCompleted(),i.getDueOn()); }
}
