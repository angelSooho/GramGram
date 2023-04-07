package com.example.mission_leesooho.boundedContext.likeablePerson.dto;

import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeablePersonResponse {

    private InstaMember toInstaMember;
    private int attractiveTypeCode;

    public LikeablePersonResponse(InstaMember toInstaMember, int attractiveTypeCode) {
        this.toInstaMember = toInstaMember;
        this.attractiveTypeCode = attractiveTypeCode;
    }
}
