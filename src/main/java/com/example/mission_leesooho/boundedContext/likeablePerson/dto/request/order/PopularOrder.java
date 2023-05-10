package com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.order;

import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;

import java.util.Comparator;

public class PopularOrder implements Comparator<LikeablePerson> {
    @Override
    public int compare(LikeablePerson p1, LikeablePerson p2) {
        if (p1.getPushInstaMember().getLikes() > p2.getPushInstaMember().getLikes()) {
            return -1;
        } else {
            return 1;
        }
    }
}