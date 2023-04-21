package com.example.mission_leesooho.boundedContext.likeablePerson.repository;

import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeablePersonRepository extends JpaRepository<LikeablePerson, Long>, LikeablePersonRepositoryCustom {

    LikeablePerson findByPullInstaMember (InstaMember instaMember);
}
