package com.example.mission_leesooho.boundedContext.notification.service;

import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import com.example.mission_leesooho.boundedContext.notification.entity.Notification;
import com.example.mission_leesooho.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<Notification> findMyNotification(InstaMember instaMember, int offset) {

        Pageable pageable = PageRequest.of(offset, 5);
        Page<Notification> notifications = notificationRepository.findById(instaMember.getId(), pageable);

        for (Notification notification : notifications) {
            notification.updateReadDate();
        }

        return notifications;
    }


    @Transactional
    public void delete(Long id) {
        Optional<Notification> findNotice = notificationRepository.findById(id);
        findNotice.ifPresent(notificationRepository::delete);
    }

    @Transactional
    public void deleteAll(Long id) {
        notificationRepository.findByIdForMine(id);
    }
}
