package com.example.mission_leesooho.boundedContext.likeablePerson.dto.form;

import jakarta.validation.constraints.*;

public record AddForm(@NotBlank @Size(min = 3, max = 30) String username,
                      @NotNull @Min(1) @Max(3) int attractiveTypeCode,
                      @NotNull String genderCode) {
}