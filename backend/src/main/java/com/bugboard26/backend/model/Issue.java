package com.bugboard26.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    // Relazione tra tabelle 

    // Utente creatore
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Utente incaricato di occuparsi della issue
    @ManyToOne
    @JoinColumn(name = "assegnato_id")
    private User assegnato;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Issue() {
    }

    public Issue(String title, String description, IssueType type, User author) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.author = author;
        this.status = IssueStatus.TODO;
        this.createdAt = LocalDateTime.now();
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getAssegnato() {
        return assegnato;
    }

    public void setAssegnato(User assegnato) {
        this.assegnato = assegnato;
    }
}