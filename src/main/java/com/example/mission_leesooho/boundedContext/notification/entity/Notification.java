package com.example.mission_leesooho.boundedContext.notification.entity;

import com.example.mission_leesooho.boundedContext.base.BaseTimeEntity;
import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime readDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private InstaMember pullInstaMember; // 메세지 받는 사람(호감 받는 사람)

    @ManyToOne(fetch = FetchType.LAZY)
    private InstaMember pushInstaMember; // 메세지를 발생시킨 행위를 한 사람(호감표시한 사람)

    private String typeCode; // 호감표시=Like, 호감사유변경=ModifyAttractiveType

    private String oldAT; // 해당사항 없으면 0
    private String ChangeAT; // 해당사항 없으면 0

    public void updateReadDate() {
        this.readDate = LocalDateTime.now();
    }
}