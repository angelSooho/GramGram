package com.example.mission_leesooho.boundedContext.member.service;

import com.example.mission_leesooho.global.rsData.RsData;
import com.example.mission_leesooho.boundedContext.instaMember.entity.InstaMember;
import com.example.mission_leesooho.boundedContext.member.entity.Member;
import com.example.mission_leesooho.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public RsData<Member> join(String username, String password) {
        return join("IU", username, password);
    }

    // 내부 처리함수, 일반회원가입, 소셜로그인을 통한 회원가입(최초 로그인 시 한번만 발생)에서 이 함수를 사용함
    private RsData<Member> join(String providerTypeCode, String username, String password) {

        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }

        // 소셜 로그인을 통한 회원가입에서는 비번이 없다.
        if (StringUtils.hasText(password)) {
            password = passwordEncoder.encode(password);
        }

        Member member = Member
                .builder()
                .providerTypeCode(providerTypeCode)
                .username(username)
                .password(password)
                .build();

        memberRepository.save(member);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    // 아이디 찾기, 비밀번호 찾기 로직 미완성
//    public RsData<Member> searchId(String username, String password) {
//
//        if (findByUsername(username).isPresent()) {
//            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
//        }
//
//        // 소셜 로그인을 통한 회원가입에서는 비번이 없다.
//        if (StringUtils.hasText(password)) password = passwordEncoder.encode(password);
//
//        Member member = Member
//                .builder()
//                .providerTypeCode("IU")
//                .username(username)
//                .password(password)
//                .build();
//
//        memberRepository.save(member);
//
//        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
//    }
//
//    public RsData<Member> searchPwd(String username, String password) {
//
//        if (findByUsername(username).isPresent()) {
//            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
//        }
//
//        // 소셜 로그인을 통한 회원가입에서는 비번이 없다.
//        if (StringUtils.hasText(password)) password = passwordEncoder.encode(password);
//
//        Member member = Member
//                .builder()
//                .providerTypeCode("IU")
//                .username(username)
//                .password(password)
//                .build();
//
//        memberRepository.save(member);
//
//        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
//    }

    // 해당 회원에게 인스타 계정을 연결시킨다.
    // 1:1 관계
    public void updateInstaMember(Member member, InstaMember instaMember) {
        member.SelectInsta(instaMember);
        memberRepository.save(member); // 여기서 실제로 UPDATE 쿼리 발생
    }

    // 소셜 로그인(카카오, 구글, 네이버) 로그인이 될 때 마다 실행되는 함수
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username) {
        Optional<Member> opMember = findByUsername(username); // username 예시 : KAKAO__1312319038130912, NAVER__1230812300

        if (opMember.isPresent()) return RsData.of("S-2", "로그인 되었습니다.", opMember.get());

        // 소셜 로그인를 통한 가입시 비번은 없다.
        return join(providerTypeCode, username, ""); // 최초 로그인 시 딱 한번 실행
    }
}
