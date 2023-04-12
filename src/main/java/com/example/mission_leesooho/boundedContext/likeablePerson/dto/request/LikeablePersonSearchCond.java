package com.example.mission_leesooho.boundedContext.likeablePerson.dto.request;

import lombok.Getter;

@Getter
public class LikeablePersonSearchCond {

    private String name;

    private Integer attractiveTypeCode;

    public LikeablePersonSearchCond(String name, Integer attractiveTypeCode) {
        this.name = name;
        this.attractiveTypeCode = attractiveTypeCode;
    }
}
