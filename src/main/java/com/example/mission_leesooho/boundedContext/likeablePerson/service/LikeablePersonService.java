package com.example.mission_leesooho.boundedContext.likeablePerson.service;

import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import com.example.mission_leesooho.boundedContext.instaMember.repository.InstaMemberRepository;
import com.example.mission_leesooho.boundedContext.instaMember.service.InstaMemberService;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.LikeablePersonSearchCond;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.request.order.*;
import com.example.mission_leesooho.boundedContext.likeablePerson.dto.response.LikeablePersonResponse;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import com.example.mission_leesooho.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.example.mission_leesooho.boundedContext.member.entity.Member;
import com.example.mission_leesooho.boundedContext.notification.entity.Notification;
import com.example.mission_leesooho.boundedContext.notification.repository.NotificationRepository;
import com.example.mission_leesooho.global.event.EventAfterModifyAttractiveType;
import com.example.mission_leesooho.global.rsData.RsData;
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
    private final NotificationRepository notificationRepository;

    @Value("${likeable-person.lst-max}")
    private long lst_max;

    @Value("${likeable-person.time-limit}")
    private long time_limit;

    public RsData<LikeablePersonResponse> like(Member member, String username, int attractiveTypeCode, String gender) {

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

        InstaMember pullInstaMember = instaMemberService.findByUsernameOrCreate(username, gender).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .pushInstaMember(member.getInstaMember())
                .pullInstaMember(pullInstaMember)
                .attractiveTypeCode(attractiveTypeCode)
                .modifyUnlockDate(LocalDateTime.now().plusSeconds(time_limit))
                .build();

        return CreateOrModifyLikeablePerson(member, username, pullInstaMember, likeablePerson);
    }

    private RsData<LikeablePersonResponse> CreateOrModifyLikeablePerson(Member member, String username, InstaMember pullInstaMember, LikeablePerson likeablePerson) {

        LikeablePersonResponse likeResponse = new LikeablePersonResponse(username, likeablePerson.getAttractiveTypeCode());

        String info = SameAttractiveTypeCodeSearch(likeablePerson);

        // 분할 필요함
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

                Notification notifiCation = Notification
                        .builder()
                        .readDate(null)
                        .pullInstaMember(likeablePerson.getPullInstaMember())
                        .pushInstaMember(likeablePerson.getPushInstaMember())
                        .typeCode("Like")
                        .oldAT(likeablePerson.getAttractiveTypeDisplayName())
                        .ChangeAT(null)
                        .build();
                notificationRepository.save(notifiCation);

                // 테스트용 (나에게 온 알림 확인)
                testNotification(member, likeablePerson);

                likeablePersonRepository.save(likeablePerson);
                return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeResponse);
            }
            default -> {
                return RsData.of("F-3", "수정 가능시간까지 %s 남았습니다.".formatted(info));
            }
        }
    }

    public String SameAttractiveTypeCodeSearch(LikeablePerson likeablePerson) {

        LikeablePersonSearchCond SearchCond = new LikeablePersonSearchCond(likeablePerson.getPushInstaMember().getId(), likeablePerson.getPullInstaMember().getId());

        Optional<LikeablePerson> specificLikeablePerson = likeablePersonRepository.findSpecificLikeablePerson(SearchCond);

        if (specificLikeablePerson.isPresent()) {
            if (Objects.equals(specificLikeablePerson.get().getPullInstaMember().getId(), likeablePerson.getPullInstaMember().getId())) {
                if (specificLikeablePerson.get().getAttractiveTypeCode() == likeablePerson.getAttractiveTypeCode()) {
                    return "error";
                } else {
                    if (!specificLikeablePerson.get().isModifyUnlocked()) {
                        return specificLikeablePerson.get().getModifyUnlockDateRemainStrHuman();
                    } else {
                        Notification notifiCation = Notification
                                .builder()
                                .readDate(null)
                                .pullInstaMember(specificLikeablePerson.get().getPullInstaMember())
                                .pushInstaMember(specificLikeablePerson.get().getPushInstaMember())
                                .typeCode("AT")
                                .oldAT(specificLikeablePerson.get().getAttractiveTypeDisplayName())
                                .ChangeAT(specificLikeablePerson.get().getAttractiveTypeDisplayName())
                                .build();
                        notificationRepository.save(notifiCation);
                        specificLikeablePerson.get().modifyAttractiveTypeCode(likeablePerson.getAttractiveTypeCode(), time_limit);
//                        specificLikeablePerson.get().modifyUnlockDate(time_limit);

                        return "modify";
                    }
                }
            }
        }
        return "new";
    }

    public RsData<LikeablePersonResponse> cancel(Member member, Long id) {

        LikeablePerson likeablePerson = likeablePersonRepository.findById(id).orElseThrow();

        if (!likeablePerson.isModifyUnlocked()) {
            return RsData.of("F-3", "삭제 가능시간까지 %s 남았습니다.".formatted(likeablePerson.getModifyUnlockDateRemainStrHuman()));
        }
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

    public void orderBy(List<LikeablePerson> likeablePeople, String gender, String ATC, String sortType) {

        if(!gender.equals("U")) {
            likeablePeople = likeablePeople.stream().filter(likeablePerson -> likeablePerson.getPushInstaMember().getGender().equals(gender)).toList();
        } else if(!Objects.equals(ATC, "0")) {
            likeablePeople = likeablePeople.stream().filter(likeablePerson -> likeablePerson.getAttractiveTypeCode() == Integer.parseInt(ATC)).toList();
        }

        switch (sortType) {
            case "1" -> likeablePeople.sort(new LatestOrder());
            case "2" -> likeablePeople.sort(new OldOrder());
            case "3" -> likeablePeople.sort(new PopularOrder());
            case "4" -> likeablePeople.sort(new UnpopularOrder());
            case "5" -> likeablePeople.sort(new GenderOrder());
            case "6" -> likeablePeople.sort(new ATCOrder());
        }
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
        if (!modifyLikeablePerson.isModifyUnlocked()) {
            return RsData.of("F-3", "수정 가능시간까지 %s 남았습니다.".formatted(modifyLikeablePerson.getModifyUnlockDateRemainStrHuman()));
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

        modifyAttractionTypeCode(likeablePerson.get(), attractiveTypeCode, time_limit);
        log.info("time = {} {}", LocalDateTime.now(), likeablePerson.get().getModifyUnlockDate());

        Notification notifiCation = Notification
                .builder()
                .readDate(null)
                .pullInstaMember(likeablePerson.get().getPullInstaMember())
                .pushInstaMember(likeablePerson.get().getPushInstaMember())
                .typeCode("AT")
                .oldAT(oldAttractiveTypeDisplayName)
                .ChangeAT(likeablePerson.get().getAttractiveTypeDisplayName())
                .build();
        notificationRepository.save(notifiCation);

        // test용 (나에게 온 알림 확인)
        testModifyNotification(member,likeablePerson.get(), oldAttractiveTypeDisplayName, notifiCation);

        String newAttractiveTypeDisplayName = likeablePerson.get().getAttractiveTypeDisplayName();

        return RsData.of("S-3", "%s님에 대한 호감사유를 %s에서 %s(으)로 변경합니다.".formatted(username, oldAttractiveTypeDisplayName, newAttractiveTypeDisplayName), likeablePerson.get());
    }

    private void testNotification(Member member, LikeablePerson likeablePerson) {
        Notification notifiCation1 = Notification
                .builder()
                .readDate(null)
                .pullInstaMember(member.getInstaMember())
                .pushInstaMember(likeablePerson.getPullInstaMember())
                .typeCode("Like")
                .oldAT(likeablePerson.getAttractiveTypeDisplayName())
                .ChangeAT(null)
                .build();
        Notification notifiCation2 = Notification
                .builder()
                .readDate(null)
                .pullInstaMember(member.getInstaMember())
                .pushInstaMember(likeablePerson.getPullInstaMember())
                .typeCode("Like")
                .oldAT(likeablePerson.getAttractiveTypeDisplayName())
                .ChangeAT(null)
                .build();
        Notification notifiCation3 = Notification
                .builder()
                .readDate(null)
                .pullInstaMember(member.getInstaMember())
                .pushInstaMember(likeablePerson.getPullInstaMember())
                .typeCode("Like")
                .oldAT(likeablePerson.getAttractiveTypeDisplayName())
                .ChangeAT(null)
                .build();
        notificationRepository.save(notifiCation1);
        notificationRepository.save(notifiCation2);
        notificationRepository.save(notifiCation3);
    }

    private void testModifyNotification(Member member, LikeablePerson likeablePerson, String oldAttractiveTypeDisplayName, Notification notifiCation) {
        Notification notifiCation1 = Notification
                .builder()
                .readDate(null)
                .pullInstaMember(member.getInstaMember())
                .pushInstaMember(likeablePerson.getPullInstaMember())
                .typeCode("AT")
                .oldAT(oldAttractiveTypeDisplayName)
                .ChangeAT(likeablePerson.getAttractiveTypeDisplayName())
                .build();
        notificationRepository.save(notifiCation1);
        Notification notifiCation2 = Notification
                .builder()
                .readDate(null)
                .pullInstaMember(member.getInstaMember())
                .pushInstaMember(likeablePerson.getPullInstaMember())
                .typeCode("AT")
                .oldAT(oldAttractiveTypeDisplayName)
                .ChangeAT(likeablePerson.getAttractiveTypeDisplayName())
                .build();
        notificationRepository.save(notifiCation2);
        Notification notifiCation3 = Notification
                .builder()
                .readDate(null)
                .pullInstaMember(member.getInstaMember())
                .pushInstaMember(likeablePerson.getPullInstaMember())
                .typeCode("AT")
                .oldAT(oldAttractiveTypeDisplayName)
                .ChangeAT(likeablePerson.getAttractiveTypeDisplayName())
                .build();
        notificationRepository.save(notifiCation3);
    }

    private void modifyAttractionTypeCode(LikeablePerson likeablePerson, int attractiveTypeCode, Long time) {
        int oldAttractiveTypeCode = likeablePerson.getAttractiveTypeCode();
        RsData rsData = likeablePerson.modifyATWithRsData(attractiveTypeCode, time);

        if (rsData.isSuccess()) {
            publisher.publishEvent(new EventAfterModifyAttractiveType(this, likeablePerson, oldAttractiveTypeCode, attractiveTypeCode));
        }
    }
}
