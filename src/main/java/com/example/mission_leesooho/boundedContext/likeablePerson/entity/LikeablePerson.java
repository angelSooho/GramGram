package com.example.mission_leesooho.boundedContext.likeablePerson.entity;

import com.example.mission_leesooho.boundedContext.base.BaseTimeEntity;
import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
public class LikeablePerson extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "push_insta_member_id", nullable = false)
    private InstaMember pushInstaMember; // 호감을 표시한 사람(인스타 멤버)
//    private String fromInstaMemberUsername; // 혹시 몰라서 기록

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_insta_member_id", nullable = false)
    private InstaMember pullInstaMember; // 호감을 받은 사람(인스타 멤버)
//    private String toInstaMemberUsername; // 혹시 몰라서 기록

    private int attractiveTypeCode; // 매력포인트(1=외모, 2=성격, 3=능력)

    public String getAttractiveTypeDisplayName() {
        return switch (attractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            default -> "능력";
        };
    }

    public void modifyAttractiveTypeCode(Integer attractiveTypeCode) {
        this.attractiveTypeCode = attractiveTypeCode;
    }
}
