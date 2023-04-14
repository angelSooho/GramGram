package com.example.mission_leesooho.boundedContext.likeablePerson.service;

import com.example.mission_leesooho.base.initData.AppConfig;
import com.example.mission_leesooho.base.initData.NotProd;
import com.example.mission_leesooho.base.rsData.RsData;
import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import com.example.mission_leesooho.boundedContext.instaMember.service.InstaMemberService;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.LikeablePersonSearchCond;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.response.LikeablePersonResponse;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import com.example.mission_leesooho.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.example.mission_leesooho.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeablePersonService {

    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    private final long lstMax = AppConfig.getLst_max();

    public RsData<LikeablePersonResponse> like(Member member, String username, int attractiveTypeCode) {
        if (!member.hasConnectedInstaMember()) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        /**
         * 케이스 5
         * */
        if (member.getInstaMember().getPushLikeablePeople().size() >= lstMax) {
            log.info("lst_max = {}", lstMax);
            return RsData.of("F-3",  lstMax + "명이상의 호감표시를 할 수 없습니다.");
        }

        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .pushInstaMember(member.getInstaMember()) // 호감을 표시하는 사람의 인스타 멤버
//                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .pullInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
//                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();
        LikeablePersonResponse likeResponse = new LikeablePersonResponse(username, likeablePerson.getAttractiveTypeCode());

        log.info("create likeableperson = {} {}", likeablePerson.getPushInstaMember().getId(), likeablePerson.getPullInstaMember().getId());
        /**
         * 호감유저 동일 옵션인지 확인 메소드
         * 케이스 4, 6 동시처리
         * */
        String info = SameAttractiveTypeCodeSearch(likeablePerson);

        switch (info) {
            case "error" -> {
                return RsData.of("F-4", "동일한 옵션의 호감유저를 추가할 수 없습니다.");
            }
            case "modify" -> {
                log.info("modify success");
                return RsData.of("S-2", "입력하신 인스타유저(%s)의 호감옵션을 변경하였습니다.".formatted(username), likeResponse);
            }
            case "new" ->  {
                member.getInstaMember().addfLikePeople(likeablePerson);
                toInstaMember.addtLikePeople(likeablePerson);
                log.info("make success = {}", likeablePerson);
                likeablePersonRepository.save(likeablePerson); // 저장
                return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeResponse);
            }
        }
        log.error("exception 발생");
        throw new RuntimeException("예외가 발생했습니다.");
    }

    public RsData<LikeablePersonResponse> delete(Member member, Long id) {

        LikeablePerson likeablePerson = likeablePersonRepository.findById(id).orElseThrow();

        if (!member.getInstaMember().getId().equals(likeablePerson.getPushInstaMember().getId())) {
            log.error("error : {}", "delete fail");
            return RsData.of("F-1", "삭제권한이 없습니다.");
        } else {
            log.info("info : {}", "delete success");
            likeablePersonRepository.delete(likeablePerson);
        }
        LikeablePersonResponse likeResponse = new LikeablePersonResponse(likeablePerson.getPullInstaMember().getUsername(), likeablePerson.getAttractiveTypeCode());

        member.getInstaMember().deletefLikePeople(likeablePerson);

        return RsData.of("S-1", "인스타유저(%s)를 호감상대에서 삭제했습니다.".formatted(likeResponse.getName()), likeResponse);
    }

    @Transactional(readOnly = true)
    public List<LikeablePerson> show(Member member) {

        InstaMember instaMember = member.getInstaMember();

        // 인스타인증을 했는지 체크
        if (member.getInstaMember() != null) {
            return instaMember.getPushLikeablePeople();
        }

        throw new RuntimeException();
    }

    /**
     * 호감유저 동일 옵션인지 확인 메소드
     * querydsl로 동적쿼리 작성
     * */
    public String SameAttractiveTypeCodeSearch(LikeablePerson likeablePerson) {

        LikeablePersonSearchCond SearchCond = new LikeablePersonSearchCond(likeablePerson.getPushInstaMember().getId(), likeablePerson.getPullInstaMember().getId());

        log.info("cond check = {}, {}", likeablePerson.getPushInstaMember().getId(), likeablePerson.getPullInstaMember().getId());
        Optional<LikeablePerson> specificLikeablePerson = likeablePersonRepository.findSpecificLikeablePerson(SearchCond);

        if (specificLikeablePerson.isPresent()) {
            log.info("error or modify = {} {}", specificLikeablePerson.get().getPullInstaMember().getUsername(), likeablePerson.getPullInstaMember().getUsername());
            if (Objects.equals(specificLikeablePerson.get().getPullInstaMember().getId(), likeablePerson.getPullInstaMember().getId())) {
                if (specificLikeablePerson.get().getAttractiveTypeCode() == likeablePerson.getAttractiveTypeCode()) {
                    return "error";
                } else {
                    log.info("modify = user:{}, {} code: {} -> {}", specificLikeablePerson.get().getPullInstaMember().getUsername(),
                            likeablePerson.getPullInstaMember().getUsername(), specificLikeablePerson.get().getAttractiveTypeCode(), likeablePerson.getAttractiveTypeCode());

                    specificLikeablePerson.get().modifyAttractiveTypeCode(likeablePerson.getAttractiveTypeCode());
                    return "modify";
                }
            }
        }
        return "new";
    }
}
