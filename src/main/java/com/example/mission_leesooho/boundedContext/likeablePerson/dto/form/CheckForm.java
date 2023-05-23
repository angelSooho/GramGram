package com.example.mission_leesooho.boundedContext.likeablePerson.dto.form;

import jakarta.validation.constraints.NotNull;

public record CheckForm(@NotNull String attractiveTypeCode,
                        @NotNull String genderCode,
                        @NotNull String sortType) {
}
