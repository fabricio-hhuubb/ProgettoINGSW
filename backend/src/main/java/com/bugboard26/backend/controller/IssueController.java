package com.bugboard26.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bugboard26.backend.dto.AssegnazioneRequest;
import com.bugboard26.backend.dto.IssueCreateRequest;
import com.bugboard26.backend.dto.IssueUpdateRequest;
import com.bugboard26.backend.model.Issue;
import com.bugboard26.backend.model.IssueStatus;
import com.bugboard26.backend.model.IssueType;
import com.bugboard26.backend.model.Priority;
import com.bugboard26.backend.model.Role;
import com.bugboard26.backend.model.User;
import com.bugboard26.backend.repository.IssueRepository;
import com.bugboard26.backend.repository.UserRepository;
import com.bugboard26.backend.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public IssueController(IssueRepository issueRepository,
            UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createIssue(@RequestBody IssueCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User author = userDetails.getUser();

        Issue issue = new Issue(
                request.title(),
                request.description(),
                request.type(),
                author);
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

        // Verifichiamo prima che esista un assegnatario
        // poi confrontiamo il suo ID con quello dell'utente collegato
        boolean isAssegnato = issue.getAssegnato() != null
                && issue.getAssegnato().getId().equals(currentUser.getId());

        if (!isAdmin && !isAssegnato) {
            return ResponseEntity.status(403)
                    .body("Non hai i permessi per modificare questa issue");
        }

        issue.setTitle(request.title());
        issue.setDescription(request.description());
        issue.setType(request.type());
        issue.setPriority(request.priority());

        Issue updated = issueRepository.save(issue);

        return ResponseEntity.ok(updated);

    }

    // Restituisce i dati della issue selezionata nel frontend
    @GetMapping("/{id}")
    public ResponseEntity<Issue> getIssueById(@PathVariable Long id) {
        Issue issue = issueRepository.findById(id).orElse(null);

        if (issue == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(issue);
    }

    // NUOVO METODO salva l'utente assegnato alla issue
    @PutMapping("/{id}/assegnato")
    public ResponseEntity<?> assignIssue(
            @PathVariable Long id,
            @RequestBody AssegnazioneRequest request) {

        Issue issue = issueRepository.findById(id).orElse(null);

        if (issue == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.utenteId() == null) {
            return ResponseEntity.badRequest()
                    .body("Seleziona un utente");
        }

        User user = userRepository.findById(request.utenteId()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("Utente non trovato");
        }

        issue.setAssegnato(user);
        issueRepository.save(issue);

        return ResponseEntity.ok("Assegnazione salvata");
    }

}