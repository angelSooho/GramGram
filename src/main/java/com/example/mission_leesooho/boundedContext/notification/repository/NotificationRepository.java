package com.example.mission_leesooho.boundedContext.notification.repository;

import com.example.mission_leesooho.boundedContext.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
}
