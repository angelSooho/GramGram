package com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.order;

import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;

import java.util.Comparator;

public class UnpopularOrder implements Comparator<LikeablePerson> {
    @Override
    public int compare(LikeablePerson p1, LikeablePerson p2) {
        if (p1.getPushInstaMember().getLikes() > p2.getPushInstaMember().getLikes()) { // 인기있는 데이터가 앞에 있다면
            return 1; // 순서를 바꾼다.(인기 없는 순으로 정렬)
        } else {
            return -1;
        }
    }
}