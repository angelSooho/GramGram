package com.example.mission_leesooho.boundedContext.instaMember.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConnectByApiForm(@NotBlank @Size(min = 1, max = 1) String gender) {
}