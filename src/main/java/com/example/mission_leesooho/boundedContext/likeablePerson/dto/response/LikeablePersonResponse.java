package com.example.mission_leesooho.boundedContext.likeablePerson.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeablePersonResponse {

    private String name;
    private int attractiveTypeCode;

    @QueryProjection
    public LikeablePersonResponse(String name, int attractiveTypeCode) {
        this.name = name;
        this.attractiveTypeCode = attractiveTypeCode;
    }
}
