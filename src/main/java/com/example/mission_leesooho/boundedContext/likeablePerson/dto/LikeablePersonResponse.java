package com.example.mission_leesooho.boundedContext.likeablePerson.dto;

import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeablePersonResponse {

    private InstaMember toInstaMember;
    private int attractiveTypeCode;

}
