package com.example.mission_leesooho.boundedContext.likeablePerson.service;

import com.example.mission_leesooho.base.rsData.RsData;
import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import com.example.mission_leesooho.boundedContext.instaMember.service.InstaMemberService;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.response.likeResponse;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import com.example.mission_leesooho.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.example.mission_leesooho.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeablePersonService {

    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    public RsData<likeResponse> like(Member member, String username, int attractiveTypeCode) {
        if (!member.hasConnectedInstaMember()) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(member.getInstaMember()) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeResponse likeResponse = new likeResponse(username, likeablePerson.getAttractiveTypeCode());

        likeablePersonRepository.save(likeablePerson); // 저장

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeResponse);
    }

    public RsData<likeResponse> delete(Member member, Long id) {

        LikeablePerson likeablePerson = likeablePersonRepository.findById(id).orElseThrow();

        if (!member.getInstaMember().getId().equals(likeablePerson.getFromInstaMember().getId())) {
            log.error("error : {}", "delete fail");
            return RsData.of("F-2", "삭제권한이 없습니다.");
        } else {
            log.info("info : {}", "delete success");
            likeablePersonRepository.delete(likeablePerson);
        }
        likeResponse likeResponse = new likeResponse(likeablePerson.getToInstaMember().getUsername(), likeablePerson.getAttractiveTypeCode());

        return RsData.of("S-1", "인스타유저(%s)를 호감상대에서 삭제했습니다.".formatted(likeResponse.getName()), likeResponse);
    }

    @Transactional(readOnly = true)
    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }
}
