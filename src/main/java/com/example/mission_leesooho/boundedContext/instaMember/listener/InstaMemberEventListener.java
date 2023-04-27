package com.example.mission_leesooho.boundedContext.instaMember.listener;

import com.example.mission_leesooho.boundedContext.instaMember.service.InstaMemberService;
import com.example.mission_leesooho.global.event.EventAfterLike;
import com.example.mission_leesooho.global.event.EventAfterModifyAT;
import com.example.mission_leesooho.global.event.EventAfterModifyPIGender;
import com.example.mission_leesooho.global.event.EventBeforeCancelLike;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InstaMemberEventListener {

    private final InstaMemberService instaMemberService;

    @EventListener
    @Transactional
    public void listen(EventAfterModifyAT event) {
        instaMemberService.whenAfterModifyAttractiveType(event.getLikeablePerson(), event.getOldAttractiveTypeCode());
    }

    @EventListener
    public void listen(EventAfterLike event) {
        instaMemberService.whenAfterLike(event.getLikeablePerson());
    }

    @EventListener
    public void listen(EventBeforeCancelLike event) {
        instaMemberService.whenBeforeCancelLike(event.getLikeablePerson());
    }

    @EventListener
    public void listen(EventAfterModifyPIGender event) {
        instaMemberService.whenAfterFromInstaMemberChangeGender(event.getInstaMember(), event.getOldGender());
    }
}