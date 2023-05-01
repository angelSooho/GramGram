package com.example.mission_leesooho.boundedContext.instaMember.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstaMemberSnapShot extends InstaMemberBase {

    private String eventTypeCode;
    private String username;

    @ManyToOne
    private InstaMember instaMember;
}