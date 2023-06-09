package com.example.mission_leesooho.boundedContext.likeablePerson.entity;

import com.example.mission_leesooho.boundedContext.base.BaseTimeEntity;
import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import com.example.mission_leesooho.global.rsData.RsData;
import com.example.mission_leesooho.standard.util.Ut;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;


@Slf4j
@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeablePerson extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "push_insta_member_id", nullable = false)
    private InstaMember pushInstaMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_insta_member_id", nullable = false)
    private InstaMember pullInstaMember;

    private int attractiveTypeCode;

    private LocalDateTime modifyUnlockDate;

    public boolean isModifyUnlocked() {
        return modifyUnlockDate.withNano(0).isBefore(LocalDateTime.now().withNano(0)) ||
                Objects.equals(this.modifyUnlockDate.withNano(0), LocalDateTime.now().withNano(0));
    }

    public String getModifyUnlockDateRemainStrHuman() {
        return remainTime();
    }
    private String remainTime() {

        Duration remain = Duration.between(LocalDateTime.now(), this.getModifyUnlockDate());

        long hour = remain.toHours();
        long min = remain.toMinutes() % 60;

        if (remain.toSeconds() % 60 > 0) {
            min += 1;
        }
        if (min == 60) {
            hour += 1;
            min = 0;
        }

        if (hour == 0) {
            if (min < 10) {
                return "0" + min + "분";
            } else {
                return min + "분";
            }
        } else if (min < 10) {
            return hour + "시간0" + min + "분";
        } else {
            return hour + "시간" + min + "분";
        }
    }

    public String getAttractiveTypeDisplayName() {
        return switch (attractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            default -> "능력";
        };
    }

    private void modifyUnlockDate(Long time) {
        this.modifyUnlockDate = LocalDateTime.now().plusSeconds(time);
    }

    public void modifyAttractiveTypeCode(Integer attractiveTypeCode, Long time) {
        this.attractiveTypeCode = attractiveTypeCode;
        modifyUnlockDate(time);
    }

    public RsData modifyATWithRsData(Integer attractiveTypeCode, Long time) {
        if (this.attractiveTypeCode == attractiveTypeCode) {
            return RsData.of("F-1", "이미 설정되었습니다.");
        }
        this.attractiveTypeCode = attractiveTypeCode;
        modifyUnlockDate(time);

        return RsData.of("S-1", "성공");
    }

    public String getAttractiveTypeDisplayNameWithIcon() {
        return switch (attractiveTypeCode) {
            case 1 -> "<i class=\"fa-solid fa-person-rays\"></i>";
            case 2 -> "<i class=\"fa-regular fa-face-smile\"></i>";
            default -> "<i class=\"fa-solid fa-people-roof\"></i>";
        } + "&nbsp;" + getAttractiveTypeDisplayName();
    }

    public String getJdenticon() {
        return Ut.hash.sha256(pushInstaMember.getId() + "_likes_" + pullInstaMember.getId());
    }
}


