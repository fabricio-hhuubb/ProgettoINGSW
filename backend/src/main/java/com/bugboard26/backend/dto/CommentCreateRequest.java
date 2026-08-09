package com.bugboard26.backend.dto;

public record CommentCreateRequest(String text) {}

// in modo da ricevere in ingresso solo il testo e non tutti gli altri dati