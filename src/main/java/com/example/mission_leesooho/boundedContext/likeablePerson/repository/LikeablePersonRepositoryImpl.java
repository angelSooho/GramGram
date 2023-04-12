package com.example.mission_leesooho.boundedContext.likeablePerson.repository;

import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.LikeablePersonSearchCond;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.response.LikeablePersonResponse;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.response.QLikeablePersonResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.example.mission_leesooho.boundedContext.likeablePerson.entity.QLikeablePerson.likeablePerson;
import static com.example.mission_leesooho.boundedContext.instaMember.entity.QInstaMember.instaMember;


public class LikeablePersonRepositoryImpl implements LikeablePersonRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public LikeablePersonRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public LikeablePersonResponse findSpecificLikeablePerson(LikeablePersonSearchCond cond) {
        return queryFactory
                .select(new QLikeablePersonResponse(
                        likeablePerson.toInstaMemberUsername,
                        likeablePerson.attractiveTypeCode
                ))
                .from(likeablePerson)
                .leftJoin(instaMember)
                .fetchJoin()
                .where(
                        InstaMemberIdEq(),
                        LikeablePersonNameEq(cond),
                        LikeablepersonCodeEq(cond)
                )
                .fetchOne();
    }

    @Override
    public List<LikeablePersonResponse> findSpecificLikeablePeople(LikeablePersonSearchCond cond) {
        return queryFactory
                .select(new QLikeablePersonResponse(
                    likeablePerson.toInstaMemberUsername,
                        likeablePerson.attractiveTypeCode
                ))
                .from(likeablePerson)
                .leftJoin(instaMember)
                .fetchJoin()
                .where(
                        InstaMemberIdEq(),
                        LikeablePersonNameEq(cond)
                )
                .fetch();
    }

    private static BooleanExpression InstaMemberIdEq() {
        return likeablePerson.pushInstaMember.id.eq(instaMember.id);
    }

    private static BooleanExpression LikeablePersonNameEq(LikeablePersonSearchCond cond) {
        return likeablePerson.pullInstaMember.username.eq(cond.getName());
    }

    private static BooleanExpression LikeablepersonCodeEq(LikeablePersonSearchCond cond) {
        return likeablePerson.attractiveTypeCode.eq(cond.getAttractiveTypeCode());
    }
}
