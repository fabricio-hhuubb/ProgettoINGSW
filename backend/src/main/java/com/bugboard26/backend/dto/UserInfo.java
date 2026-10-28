package com.bugboard26.backend.dto;

import com.bugboard26.backend.model.Role;

public record UserInfo(String email, Role role) {
}