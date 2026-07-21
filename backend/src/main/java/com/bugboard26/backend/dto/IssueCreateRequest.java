package com.bugboard26.backend.dto;

import com.bugboard26.backend.model.IssueType;
import com.bugboard26.backend.model.Priority;

public record IssueCreateRequest(
        String title,
        String description,
        IssueType type,
        Priority priority // opzionale, può essere null
) {}