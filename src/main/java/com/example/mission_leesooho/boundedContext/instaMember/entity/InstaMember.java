package com.example.mission_leesooho.boundedContext.instaMember.entity;

import com.example.mission_leesooho.boundedContext.base.BaseTimeEntity;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class InstaMember extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String gender;

    @OneToMany(mappedBy = "pushInstaMember", cascade = CascadeType.REMOVE) // 내가 호감을 누른
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Builder.Default
    private List<LikeablePerson> pushLikeablePeople = new ArrayList<>();

    @OneToMany(mappedBy = "pullInstaMember", cascade = CascadeType.REMOVE) // 내가 호감을 받은
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Builder.Default
    private List<LikeablePerson> pullLikeablePeople = new ArrayList<>();



    public void SelectGender(String gender) {
        this.gender = gender;
    }

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



}
