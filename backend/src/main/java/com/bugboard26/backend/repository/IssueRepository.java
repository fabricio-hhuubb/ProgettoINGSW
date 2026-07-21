package com.bugboard26.backend.repository;

import com.bugboard26.backend.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    
}