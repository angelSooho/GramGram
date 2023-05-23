package com.example.mission_leesooho.boundedContext.likeablePerson.repository;

import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.LikeablePersonOptionCond;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.LikeablePersonSearchCond;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;

import java.util.List;
import java.util.Optional;

public interface LikeablePersonRepositoryCustom {

    Optional<LikeablePerson> findSpecificLikeablePerson(LikeablePersonSearchCond cond);
    List<LikeablePerson> findByIdWithOption(LikeablePersonOptionCond cond);
}
