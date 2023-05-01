package com.example.mission_leesooho.boundedContext.likeablePerson.service;

import com.example.mission_leesooho.global.event.EventAfterModifyAttractiveType;
import com.example.mission_leesooho.global.rsData.RsData;
import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import com.example.mission_leesooho.boundedContext.instaMember.repository.InstaMemberRepository;
import com.example.mission_leesooho.boundedContext.instaMember.service.InstaMemberService;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.LikeablePersonSearchCond;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.response.LikeablePersonResponse;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import com.example.mission_leesooho.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.example.mission_leesooho.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LikeablePersonService {

    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    private final InstaMemberRepository instaMemberRepository;;
    private final ApplicationEventPublisher publisher;

    @Value("${likeable-person.lst-max}")
    private long lst_max;

    @Value("${likeable-person.time-limit}")
    private long time_limit;

    public RsData<LikeablePersonResponse> like(Member member, String username, int attractiveTypeCode) {

        if (!member.hasConnectedInstaMember()) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        if (member.getInstaMember().getPushLikeablePeople().size() >= lst_max) {
            log.info("최대인원 = {}", lst_max);
            return RsData.of("F-3",  lst_max + "명이상의 호감표시를 할 수 없습니다.");
        }

        InstaMember pullInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .pushInstaMember(member.getInstaMember())
                .pullInstaMember(pullInstaMember)
                .attractiveTypeCode(attractiveTypeCode)
                .build();

        return CreateOrModifyLikeablePerson(member, username, pullInstaMember, likeablePerson);
    }

    private RsData<LikeablePersonResponse> CreateOrModifyLikeablePerson(Member member, String username, InstaMember pullInstaMember, LikeablePerson likeablePerson) {

        LikeablePersonResponse likeResponse = new LikeablePersonResponse(username, likeablePerson.getAttractiveTypeCode());

        String info = SameAttractiveTypeCodeSearch(likeablePerson);

        switch (info) {
            case "error" -> {
                log.error("make, modify error");
                return RsData.of("F-4", "동일한 옵션의 호감유저를 추가할 수 없습니다.");
            }
            case "modify" -> {
                return RsData.of("S-2", "입력하신 인스타유저(%s)의 호감옵션을 변경하였습니다.".formatted(username), likeResponse);
            }
            case "new" ->  {
                member.getInstaMember().addfLikePeople(likeablePerson);
                pullInstaMember.addtLikePeople(likeablePerson);

                likeablePersonRepository.save(likeablePerson); // 저장
                return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeResponse);
            }
        }
        throw new RuntimeException("예외가 발생했습니다.");
    }

    public RsData<LikeablePersonResponse> cancel(Member member, Long id) {

        LikeablePerson likeablePerson = likeablePersonRepository.findById(id).orElseThrow();

        if (!member.getInstaMember().getId().equals(likeablePerson.getPushInstaMember().getId())) {
            log.error("delete fail");
            return RsData.of("F-1", "삭제권한이 없습니다.");
        } else {
            deleteList(member, likeablePerson);
            likeablePersonRepository.delete(likeablePerson);
        }
        LikeablePersonResponse likeResponse = new LikeablePersonResponse(likeablePerson.getPullInstaMember().getUsername(), likeablePerson.getAttractiveTypeCode());

        return RsData.of("S-1", "인스타유저(%s)를 호감상대에서 삭제했습니다.".formatted(likeResponse.getName()), likeResponse);
    }

    private void deleteList(Member member, LikeablePerson likeablePerson) {
        InstaMember toInstaMember = instaMemberRepository.findById(likeablePerson.getPullInstaMember().getId()).orElseThrow();
        member.getInstaMember().deletefLikePeople(likeablePerson);
        toInstaMember.deletetLikePeople(likeablePerson);
    }

    @Transactional(readOnly = true)
    public List<LikeablePerson> show(Member member) {

        InstaMember instaMember = member.getInstaMember();

        if (member.getInstaMember() != null) {
            return instaMember.getPushLikeablePeople();
        }
        throw new RuntimeException();
    }

    public String SameAttractiveTypeCodeSearch(LikeablePerson likeablePerson) {

        LikeablePersonSearchCond SearchCond = new LikeablePersonSearchCond(likeablePerson.getPushInstaMember().getId(), likeablePerson.getPullInstaMember().getId());

        Optional<LikeablePerson> specificLikeablePerson = likeablePersonRepository.findSpecificLikeablePerson(SearchCond);

        if (specificLikeablePerson.isPresent()) {
            if (Objects.equals(specificLikeablePerson.get().getPullInstaMember().getId(), likeablePerson.getPullInstaMember().getId())) {
                if (specificLikeablePerson.get().getAttractiveTypeCode() == likeablePerson.getAttractiveTypeCode()) {
                    return "error";
                } else {
                    specificLikeablePerson.get().modifyAttractiveTypeCode(likeablePerson.getAttractiveTypeCode());
                    return "modify";
                }
            }
        }
        return "new";
    }

    public RsData<LikeablePerson> ModifyLike(Long id, Member member) {

        LikeablePerson modifyLikeablePerson = likeablePersonRepository.findById(id).orElseThrow();
        InstaMember fromInstaMember = member.getInstaMember();

        if (!member.hasConnectedInstaMember()) {
            return RsData.of("F-1", "먼저 본인의 인스타그램 아이디를 입력해주세요.");
        }
        if (!Objects.equals(modifyLikeablePerson.getPushInstaMember().getId(), fromInstaMember.getId())) {
            return RsData.of("F-2", "해당 호감표시를 취소할 권한이 없습니다.");
        }

        return RsData.of("S-1", "호감표시를 수정합니다.", modifyLikeablePerson);
    }

    public RsData<LikeablePerson> modifyAttractiveCode(Member member, Long id, int attractiveTypeCode) {

        Optional<LikeablePerson> likeablePerson = likeablePersonRepository.findById(id);

        if (likeablePerson.isEmpty()) {
            return RsData.of("F-1", "존재하지 않는 호감표시입니다.");
        }

        String oldAttractiveTypeDisplayName = likeablePerson.get().getAttractiveTypeDisplayName();
        String username = likeablePerson.get().getPullInstaMember().getUsername();

        modifyAttractionTypeCode(likeablePerson.get(), attractiveTypeCode);

        String newAttractiveTypeDisplayName = likeablePerson.get().getAttractiveTypeDisplayName();

        return RsData.of("S-3", "%s님에 대한 호감사유를 %s에서 %s(으)로 변경합니다.".formatted(username, oldAttractiveTypeDisplayName, newAttractiveTypeDisplayName), likeablePerson.get());
    }

    private void modifyAttractionTypeCode(LikeablePerson likeablePerson, int attractiveTypeCode) {
        int oldAttractiveTypeCode = likeablePerson.getAttractiveTypeCode();
        RsData rsData = likeablePerson.modifyATWithRsData(attractiveTypeCode);

        if (rsData.isSuccess()) {
            publisher.publishEvent(new EventAfterModifyAttractiveType(this, likeablePerson, oldAttractiveTypeCode, attractiveTypeCode));
        }
    }
}
