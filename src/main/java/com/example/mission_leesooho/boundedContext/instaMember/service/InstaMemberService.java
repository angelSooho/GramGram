package com.example.mission_leesooho.boundedContext.instaMember.service;

import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMemberSnapShot;
import com.example.mission_leesooho.boundedContext.instaMember.repository.InstaMemberSnapShotRepository;
import com.example.mission_leesooho.boundedContext.likeablePerson.entity.LikeablePerson;
import com.example.mission_leesooho.global.rsData.RsData;
import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import com.example.mission_leesooho.boundedContext.instaMember.repository.InstaMemberRepository;
import com.example.mission_leesooho.boundedContext.member.entity.Member;
import com.example.mission_leesooho.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstaMemberService {
    private final InstaMemberRepository instaMemberRepository;
    private final MemberService memberService;
    private final InstaMemberSnapShotRepository InstaMemberSnapShotRepository;

    public Optional<InstaMember> findByUsername(String username) {
        return instaMemberRepository.findByUsername(username);
    }

    @Transactional
    public RsData<InstaMember> connect(Member member, String username, String gender) {
        Optional<InstaMember> opInstaMember = findByUsername(username);

        if (opInstaMember.isPresent() && !opInstaMember.get().getGender().equals("U")) {
            return RsData.of("F-1", "해당 인스타그램 아이디는 이미 다른 사용자와 연결되었습니다.");
        }

        RsData<InstaMember> instaMemberRsData = findByUsernameOrCreate(username, gender);

        memberService.updateInstaMember(member, instaMemberRsData.getData());

        return instaMemberRsData;
    }

    private RsData<InstaMember> create(String username, String gender) {
        InstaMember instaMember = InstaMember
                .builder()
                .username(username)
                .gender(gender)
                .build();

        instaMemberRepository.save(instaMember);

        return RsData.of("S-1", "인스타계정이 등록되었습니다.", instaMember);
    }

    @Transactional
    public RsData<InstaMember> findByUsernameOrCreate(String username) {
        Optional<InstaMember> opInstaMember = findByUsername(username);

        return opInstaMember
                .map(instaMember -> RsData.of("S-2", "인스타계정이 등록되었습니다.", instaMember))
                .orElseGet(() -> create(username, "U"));
    }

    @Transactional
    public RsData<InstaMember> findByUsernameOrCreate(String username, String gender) {
        Optional<InstaMember> opInstaMember = findByUsername(username);

        if (opInstaMember.isPresent()) {
            InstaMember instaMember = opInstaMember.get();
            instaMember.updateGender(gender);
            instaMemberRepository.save(instaMember);

            return RsData.of("S-2", "인스타계정이 등록되었습니다.", instaMember);
        }

        return create(username, gender);
    }

    private void saveSnapshot(InstaMemberSnapShot snapshot) {
        InstaMemberSnapShotRepository.save(snapshot);
    }

    public void whenAfterModifyAttractiveType(LikeablePerson likeablePerson, int oldAttractiveTypeCode) {
        InstaMember pushInstaMember = likeablePerson.getPushInstaMember();
        InstaMember pullInstaMember = likeablePerson.getPullInstaMember();

        pullInstaMember.decreaseLikesCount(pushInstaMember.getGender(), oldAttractiveTypeCode);
        pullInstaMember.increaseLikesCount(pushInstaMember.getGender(), likeablePerson.getAttractiveTypeCode());

        InstaMemberSnapShot snapshot = pullInstaMember.snapshot("ModifyAttractiveType");

        saveSnapshot(snapshot);
    }

    public void whenAfterLike(LikeablePerson likeablePerson) {
        InstaMember pushInstaMember = likeablePerson.getPushInstaMember();
        InstaMember pullInstaMember = likeablePerson.getPullInstaMember();

        pullInstaMember.increaseLikesCount(pushInstaMember.getGender(), likeablePerson.getAttractiveTypeCode());

        InstaMemberSnapShot snapshot = pullInstaMember.snapshot("Like");

        saveSnapshot(snapshot);

        // 알림
    }

    public void whenBeforeCancelLike(LikeablePerson likeablePerson) {
        InstaMember pushInstaMember = likeablePerson.getPushInstaMember();
        InstaMember pullInstaMember = likeablePerson.getPullInstaMember();

        pullInstaMember.decreaseLikesCount(pushInstaMember.getGender(), likeablePerson.getAttractiveTypeCode());

        InstaMemberSnapShot snapshot = pullInstaMember.snapshot("CancelLike");

        saveSnapshot(snapshot);
    }

    public void whenAfterFromInstaMemberChangeGender(InstaMember instaMember, String oldGender) {
        instaMember
                .getPushLikeablePeople()
                .forEach(likeablePerson -> {
                    InstaMember toInstaMember = likeablePerson.getPullInstaMember();
                    toInstaMember.decreaseLikesCount(oldGender, likeablePerson.getAttractiveTypeCode());
                    toInstaMember.increaseLikesCount(instaMember.getGender(), likeablePerson.getAttractiveTypeCode());

                    InstaMemberSnapShot snapshot = toInstaMember.snapshot("FromInstaMemberChangeGender");

                    saveSnapshot(snapshot);
                });
    }

    public RsData<InstaMember> connect(Member member, String gender, String oauthId, String username, String accessToken) {
        Optional<InstaMember> opInstaMember = instaMemberRepository.findByOauthId(oauthId);

        if (opInstaMember.isPresent()) {
            InstaMember instaMember = opInstaMember.get();
            instaMember.connectMemberName(username, accessToken, gender);
            instaMemberRepository.save(instaMember);

            member.connectInstaMember(instaMember);

            return RsData.of("S-3", "인스타계정이 연결되었습니다.", instaMember);
        }

        opInstaMember = findByUsername(username);

        if (opInstaMember.isPresent()) {
            InstaMember instaMember = opInstaMember.get();
            instaMember.connectMemberOauthId(oauthId, accessToken, gender);
            instaMemberRepository.save(instaMember);

            member.connectInstaMember(instaMember);

            return RsData.of("S-4", "인스타계정이 연결되었습니다.", instaMember);
        }

        InstaMember instaMember = connect(member, username, gender).getData();

        instaMember.connect(oauthId, accessToken);

        return RsData.of("S-5", "인스타계정이 연결되었습니다.", instaMember);
    }
}
