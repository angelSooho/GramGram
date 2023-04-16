package com.example.mission_leesooho.boundedContext.likeablePerson.repository;

import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.LikeablePersonSearchCond;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.Optional;

import static com.example.mission_leesooho.boundedContext.likeablePerson.entity.QLikeablePerson.likeablePerson;
import static com.example.mission_leesooho.boundedContext.instaMember.entity.QInstaMember.instaMember;


public class LikeablePersonRepositoryImpl implements LikeablePersonRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public LikeablePersonRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<LikeablePerson> findSpecificLikeablePerson(LikeablePersonSearchCond cond) {
        return Optional.ofNullable(queryFactory
                .selectFrom(likeablePerson)
                .leftJoin(instaMember)
                .fetchJoin()
                .where(
                        pushInstaMebmerIdEq(cond),
                        pullInstaMemberIdEq(cond)
                )
                .fetchOne());
    }

    private static BooleanExpression pushInstaMebmerIdEq(LikeablePersonSearchCond cond) {
        return likeablePerson.pushInstaMember.id.eq(cond.pushId());
    }

    private static BooleanExpression pullInstaMemberIdEq(LikeablePersonSearchCond cond) {
        return likeablePerson.pullInstaMember.id.eq(cond.pullId());
    }
}
