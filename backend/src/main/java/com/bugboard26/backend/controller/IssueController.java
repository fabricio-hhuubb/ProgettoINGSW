package com.bugboard26.backend.controller;

import com.bugboard26.backend.dto.IssueCreateRequest;
import com.bugboard26.backend.model.Issue;
import com.bugboard26.backend.model.User;
import com.bugboard26.backend.repository.IssueRepository;
import com.bugboard26.backend.repository.UserRepository;
import com.bugboard26.backend.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}