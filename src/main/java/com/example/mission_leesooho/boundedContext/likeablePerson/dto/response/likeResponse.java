package com.example.mission_leesooho.boundedContext.likeablePerson.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class likeResponse {

    private String name;
    private int attractiveTypeCode;

    public likeResponse(String name, int attractiveTypeCode) {
        this.name = name;
        this.attractiveTypeCode = attractiveTypeCode;
    }
}
