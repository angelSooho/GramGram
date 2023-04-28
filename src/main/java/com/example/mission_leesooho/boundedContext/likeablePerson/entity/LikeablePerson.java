package com.example.mission_leesooho.boundedContext.likeablePerson.entity;

import com.example.mission_leesooho.boundedContext.base.BaseTimeEntity;
import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import com.example.mission_leesooho.global.rsData.RsData;
import com.example.mission_leesooho.standard.util.Ut;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.GenerationType.IDENTITY;


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

    public RsData modifyATWithRsData(Integer attractiveTypeCode) {
        if (this.attractiveTypeCode == attractiveTypeCode) {
            return RsData.of("F-1", "이미 설정되었습니다.");
        }
        this.attractiveTypeCode = attractiveTypeCode;

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


