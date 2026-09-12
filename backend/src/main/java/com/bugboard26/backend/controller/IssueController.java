package com.bugboard26.backend.controller;

import com.bugboard26.backend.dto.IssueCreateRequest;
import com.bugboard26.backend.dto.IssueUpdateRequest;
import com.bugboard26.backend.model.Issue;
import com.bugboard26.backend.model.IssueStatus;
import com.bugboard26.backend.model.IssueType;
import com.bugboard26.backend.model.User;
import com.bugboard26.backend.repository.IssueRepository;
import com.bugboard26.backend.repository.UserRepository;
import com.bugboard26.backend.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.bugboard26.backend.model.IssueType;
import com.bugboard26.backend.model.IssueStatus;
import com.bugboard26.backend.model.Priority;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueRepository issueRepository;

    public IssueController(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @PostMapping
    public ResponseEntity<?> createIssue(@RequestBody IssueCreateRequest request,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User author = userDetails.getUser();

        Issue issue = new Issue(
                request.title(),
                request.description(),
                request.type(),
                author
        );
        issue.setPriority(request.priority());

        Issue saved = issueRepository.save(issue);

        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Issue>> getIssues(
        @RequestParam(required = false) IssueType type,
        @RequestParam(required = false) IssueStatus status,
        @RequestParam(required = false) Priority priority) {

        List<Issue> issues = issueRepository.findAll();

        if (type != null) {
            issues = issues.stream().filter(i -> i.getType() == type).toList();
        }
        if (status != null) {
            issues = issues.stream().filter(i -> i.getStatus() == status).toList();
        }
        if (priority != null) {
            issues = issues.stream().filter(i -> i.getPriority() == priority).toList();
        }

        return ResponseEntity.ok(issues);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIssue(
        @PathVariable Long id,
        @RequestBody IssueUpdateRequest request,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

            Issue issue = issueRepository.findById(id).orElseThrow(() -> new RuntimeException("Issue non trovata"));

        User currentUser = userDetails.getUser();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isAuthor = issue.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isAuthor) {
            return ResponseEntity.status(403).body("Non hai i permessi per modificare questa issue");
        }

        issue.setTitle(request.title());
        issue.setDescription(request.description());
        issue.setType(request.type());
        issue.setPriority(request.priority());

        Issue updated = issueRepository.save(issue);

        return ResponseEntity.ok(updated);
    }   

}   