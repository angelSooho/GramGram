package com.example.mission_leesooho.boundedContext.member.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserForm(@NotBlank @Size(min = 4, max = 30) String username,
                       @NotBlank @Size(min = 4, max = 30) String password) {
    }