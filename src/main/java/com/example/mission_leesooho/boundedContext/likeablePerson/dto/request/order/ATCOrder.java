package com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.order;

import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;

import java.util.Comparator;

public class ATCOrder implements Comparator<LikeablePerson> {
    @Override
    public int compare(LikeablePerson p1, LikeablePerson p2) {
        if (p1.getAttractiveTypeCode() < p2.getAttractiveTypeCode()){
            return -1;
        } else if(p1.getAttractiveTypeCode() == p2.getAttractiveTypeCode()){
            if(p1.getCreateDate().isAfter(p2.getCreateDate())) {
                return -1;
            } else {
                return 1;
            }
        }
        return 1;
    }
}