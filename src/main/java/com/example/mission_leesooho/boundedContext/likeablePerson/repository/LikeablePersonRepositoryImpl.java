package com.example.mission_leesooho.boundedContext.likeablePerson.repository;

import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.LikeablePersonOptionCond;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.LikeablePersonSearchCond;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.mission_leesooho.boundedContext.instaMember.entity.QInstaMember.instaMember;
import static com.example.mission_leesooho.boundedContext.likeablePerson.entity.QLikeablePerson.likeablePerson;


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

    @Override
    public List<LikeablePerson> findByIdWithOption(LikeablePersonOptionCond cond) {
        return queryFactory
                .selectFrom(likeablePerson)
                .leftJoin(likeablePerson.pullInstaMember, instaMember)
                .where(
                        !Objects.equals(cond.gender(), "U") ? pushInstaMebmerIdEq(cond) : null,
                        cond.ATC() != 0 ? attractiveTypeCodeEq(cond): null
                )
                .orderBy(sort(cond))
                .fetch();
    }

    private OrderSpecifier<?> sort(LikeablePersonOptionCond cond) {

        return switch (cond.sortType()) {
            case 2 -> likeablePerson.id.asc();
            case 3 -> likeablePerson.pushInstaMember.likeCount.desc();
            case 4 -> likeablePerson.pushInstaMember.likeCount.asc();
            case 5 -> likeablePerson.pushInstaMember.gender.desc();
            case 6 -> likeablePerson.attractiveTypeCode.asc();
            default -> likeablePerson.id.desc();
        };
    }

    private static BooleanExpression attractiveTypeCodeEq(LikeablePersonOptionCond cond) {
        return likeablePerson.attractiveTypeCode.eq(cond.ATC());
    }

    private static BooleanExpression pushInstaMebmerIdEq(LikeablePersonSearchCond cond) {
        return likeablePerson.pushInstaMember.id.eq(cond.pushId());
    }

    private static BooleanExpression pushInstaMebmerIdEq(LikeablePersonOptionCond cond) {
        return likeablePerson.pushInstaMember.gender.eq(cond.gender());
    }

    private static BooleanExpression pullInstaMemberIdEq(LikeablePersonSearchCond cond) {
        return likeablePerson.pullInstaMember.id.eq(cond.pullId());
    }
}
