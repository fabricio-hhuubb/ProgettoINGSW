package com.bugboard26.backend.dto;

import com.bugboard26.backend.model.Role;

public record RegisterRequest(String email, String password, Role role) {
}