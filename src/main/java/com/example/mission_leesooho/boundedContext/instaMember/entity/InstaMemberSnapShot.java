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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class InstaMemberSnapShot extends InstaMemberBase {

    @Id
    private Long id;

    private String eventTypeCode;
    private String username;

    @ManyToOne
    private InstaMember instaMember;
}