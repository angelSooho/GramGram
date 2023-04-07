package com.example.mission_leesooho.boundedContext.likeablePerson.dto.response;

import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeablePersonResponse {

    private String name;
    private int attractiveTypeCode;

    public LikeablePersonResponse(String name, int attractiveTypeCode) {
        this.name = name;
        this.attractiveTypeCode = attractiveTypeCode;
    }
}
