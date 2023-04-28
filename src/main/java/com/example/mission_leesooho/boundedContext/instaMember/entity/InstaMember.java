package com.example.mission_leesooho.boundedContext.instaMember.entity;

import com.example.mission_leesooho.boundedContext.base.BaseTimeEntity;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstaMember extends InstaMemberBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String gender;

    @OneToMany(mappedBy = "pushInstaMember", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY) // 내가 호감을 누른
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Builder.Default
    private List<LikeablePerson> pushLikeablePeople = new ArrayList<>();

    @OneToMany(mappedBy = "pullInstaMember", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY) // 내가 호감을 받은
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Builder.Default
    private List<LikeablePerson> pullLikeablePeople = new ArrayList<>();

    public void addfLikePeople(LikeablePerson likeablePerson) {
        this.pushLikeablePeople.add(likeablePerson);
    }

    public void deletefLikePeople(LikeablePerson likeablePerson) {
        this.pushLikeablePeople.remove(likeablePerson);
    }

    public void addtLikePeople(LikeablePerson likeablePerson) {
        this.pullLikeablePeople.add(likeablePerson);
    }

    public void deletetLikePeople(LikeablePerson likeablePerson) {
        this.pullLikeablePeople.remove(likeablePerson);
    }


    public String getGenderDisplayName() {
        return switch (gender) {
            case "W" -> "여성";
            default -> "남성";
        };
    }

    public String getGenderDisplayNameWithIcon() {
        return switch (gender) {
            case "W" -> "<i class=\"fa-solid fa-person-dress\"></i>";
            default -> "<i class=\"fa-solid fa-person\"></i>";
        } + "&nbsp;" + getGenderDisplayName();
    }

    public void increaseLikesCount(String gender, int attractiveTypeCode) {
        if (gender.equals("W") && attractiveTypeCode == 1) likesCountByGenderWomanAndAttractiveTypeCode1++;
        if (gender.equals("W") && attractiveTypeCode == 2) likesCountByGenderWomanAndAttractiveTypeCode2++;
        if (gender.equals("W") && attractiveTypeCode == 3) likesCountByGenderWomanAndAttractiveTypeCode3++;
        if (gender.equals("M") && attractiveTypeCode == 1) likesCountByGenderManAndAttractiveTypeCode1++;
        if (gender.equals("M") && attractiveTypeCode == 2) likesCountByGenderManAndAttractiveTypeCode2++;
        if (gender.equals("M") && attractiveTypeCode == 3) likesCountByGenderManAndAttractiveTypeCode3++;
    }

    public void decreaseLikesCount(String gender, int attractiveTypeCode) {
        if (gender.equals("W") && attractiveTypeCode == 1) likesCountByGenderWomanAndAttractiveTypeCode1--;
        if (gender.equals("W") && attractiveTypeCode == 2) likesCountByGenderWomanAndAttractiveTypeCode2--;
        if (gender.equals("W") && attractiveTypeCode == 3) likesCountByGenderWomanAndAttractiveTypeCode3--;
        if (gender.equals("M") && attractiveTypeCode == 1) likesCountByGenderManAndAttractiveTypeCode1--;
        if (gender.equals("M") && attractiveTypeCode == 2) likesCountByGenderManAndAttractiveTypeCode2--;
        if (gender.equals("M") && attractiveTypeCode == 3) likesCountByGenderManAndAttractiveTypeCode3--;
    }

    public void updateGender(String gender) {
        this.gender = gender;
    }

    public InstaMemberSnapShot snapshot(String eventTypeCode) {
        return InstaMemberSnapShot.builder()
                .eventTypeCode(eventTypeCode)
                .username(username)
                .instaMember(this)
                .gender(gender)
                .likesCountByGenderManAndAttractiveTypeCode1(likesCountByGenderManAndAttractiveTypeCode1)
                .likesCountByGenderManAndAttractiveTypeCode2(likesCountByGenderManAndAttractiveTypeCode2)
                .likesCountByGenderManAndAttractiveTypeCode3(likesCountByGenderManAndAttractiveTypeCode3)
                .likesCountByGenderWomanAndAttractiveTypeCode1(likesCountByGenderWomanAndAttractiveTypeCode1)
                .likesCountByGenderWomanAndAttractiveTypeCode2(likesCountByGenderWomanAndAttractiveTypeCode2)
                .likesCountByGenderWomanAndAttractiveTypeCode3(likesCountByGenderWomanAndAttractiveTypeCode3)
                .build();

    }

}
