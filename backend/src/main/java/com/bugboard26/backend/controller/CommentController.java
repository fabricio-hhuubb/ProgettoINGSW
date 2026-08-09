package com.bugboard26.backend.controller;

import com.bugboard26.backend.dto.CommentCreateRequest;
import com.bugboard26.backend.model.Comment;
import com.bugboard26.backend.model.Issue;
import com.bugboard26.backend.model.User;
import com.bugboard26.backend.repository.CommentRepository;
import com.bugboard26.backend.repository.IssueRepository;
import com.bugboard26.backend.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues/{issueId}/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;

    public CommentController(CommentRepository commentRepository, IssueRepository issueRepository) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
    }

    @PostMapping
    public ResponseEntity<?> addComment(@PathVariable Long issueId,
                                         @RequestBody CommentCreateRequest request,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue non trovata"));

        User author = userDetails.getUser();

        Comment comment = new Comment(request.text(), author, issue);
        Comment saved = commentRepository.save(comment);

        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long issueId) {
        List<Comment> comments = commentRepository.findByIssueIdOrderByCreatedAtAsc(issueId);
        return ResponseEntity.ok(comments);
    }
}