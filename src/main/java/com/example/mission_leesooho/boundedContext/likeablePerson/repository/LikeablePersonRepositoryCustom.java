package com.example.mission_leesooho.boundedContext.likeablePerson.repository;

import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.LikeablePersonSearchCond;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.response.LikeablePersonResponse;

import java.util.List;

public interface LikeablePersonRepositoryCustom {

    LikeablePersonResponse findSpecificLikeablePerson(LikeablePersonSearchCond cond);
    List<LikeablePersonResponse> findSpecificLikeablePeople(LikeablePersonSearchCond cond);
}
