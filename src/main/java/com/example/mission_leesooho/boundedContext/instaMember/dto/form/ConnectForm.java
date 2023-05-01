package com.example.mission_leesooho.boundedContext.instaMember.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConnectForm(@NotBlank @Size(min = 3, max = 30) String username,
                          @NotBlank @Size(min = 1, max = 1) String gender) {
}