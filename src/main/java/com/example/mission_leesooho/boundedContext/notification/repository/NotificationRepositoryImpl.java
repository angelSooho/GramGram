package com.example.mission_leesooho.boundedContext.notification.repository;

import com.example.mission_leesooho.boundedContext.notification.entity.Notification;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.mission_leesooho.boundedContext.instaMember.entity.QInstaMember.instaMember;
import static com.example.mission_leesooho.boundedContext.notification.entity.QNotification.notification;

public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public NotificationRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Notification> findById(Long id, Pageable pageable) {
        List<Notification> result = queryFactory
                .selectFrom(notification)
                .leftJoin(notification.pullInstaMember, instaMember)
                .where(
                        instaMember.id.eq(id)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notification.count())
                .from(notification)
                .leftJoin(notification.pullInstaMember, instaMember)
                .where(
                        instaMember.id.eq(id));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public long findByIdForMine(Long id) {
        long result = queryFactory
                .delete(notification)
                .where(
                        notification.pullInstaMember.id.eq(id)
                )
                .execute();

        em.flush();
        em.clear();

        return result;
    }
}
