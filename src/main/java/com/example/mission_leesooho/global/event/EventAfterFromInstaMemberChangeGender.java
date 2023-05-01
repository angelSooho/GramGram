package com.example.mission_leesooho.global.event;

import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterFromInstaMemberChangeGender extends ApplicationEvent {
    private final InstaMember instaMember;
    private final String oldGender;

    public EventAfterFromInstaMemberChangeGender(Object source, InstaMember instaMember, String oldGender) {
        super(source);
        this.instaMember = instaMember;
        this.oldGender = oldGender;
    }
}
