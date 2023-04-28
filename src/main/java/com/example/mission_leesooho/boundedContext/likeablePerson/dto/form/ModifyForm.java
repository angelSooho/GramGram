package com.example.mission_leesooho.boundedContext.likeablePerson.dto.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ModifyForm(@NotNull @Min(1) @Max(3) int attractiveTypeCode) {
}