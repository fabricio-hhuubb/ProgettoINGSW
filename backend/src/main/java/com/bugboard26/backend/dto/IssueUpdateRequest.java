package com.bugboard26.backend.dto;

import com.bugboard26.backend.model.IssueType;
import com.bugboard26.backend.model.Priority;

public record IssueUpdateRequest(
        String title,
        String description,
        IssueType type,
        Priority priority
) {}