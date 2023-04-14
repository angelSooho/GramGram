package com.example.mission_leesooho.boundedContext.likeablePerson.dto.request;

import lombok.Getter;

@Getter
public class LikeablePersonSearchCond {

    private final Long pushId;
    private final Long pullId;

    public LikeablePersonSearchCond(Long pushId, Long pullId) {
        this.pushId = pushId;
        this.pullId = pullId;
    }
}
