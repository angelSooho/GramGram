package com.example.mission_leesooho.boundedContext.notification.repository;

import com.example.mission_leesooho.boundedContext.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepositoryCustom {

    Page<Notification> findById(Long id, Pageable pageable);
    long findByIdForMine(Long id);

}
