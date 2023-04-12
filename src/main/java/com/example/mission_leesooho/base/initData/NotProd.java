package com.example.mission_leesooho.base.initData;

import com.example.mission_leesooho.boundedContext.instaMember.service.InstaMemberService;
import com.example.mission_leesooho.boundedContext.likeablePerson.service.LikeablePersonService;
import com.example.mission_leesooho.boundedContext.member.entity.Member;
import com.example.mission_leesooho.boundedContext.member.service.MemberService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "test"})
public class NotProd {

    @Value("${custom.id.KAKAO_CLIENT_ID}")
    private String kakao_client_id;

    @Value("${custom.id.GOOGLE_CLIENT_ID1}")
    private String google_client_id1;

    @Value("${custom.id.GOOGLE_CLIENT_ID2}")
    private String google_client_id2;

    @Value("${custom.id.NAVER_CLIENT_ID}")
    private String naver_client_id;

    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            InstaMemberService instaMemberService,
            LikeablePersonService likeablePersonService
    ) {
        return args -> {
            Member memberAdmin = memberService.join("admin", "1234").getData();
            Member memberUser1 = memberService.join("user1", "1234").getData();
            Member memberUser2 = memberService.join("user2", "1234").getData();
            Member memberUser3 = memberService.join("user3", "1234").getData();
            Member memberUser4 = memberService.join("user4", "1234").getData();

            // API 시크릿 키값 저장
            Member memberUserByKakao = memberService.whenSocialLogin("KAKAO", kakao_client_id).getData();
            Member memberUserByGoogle1 = memberService.whenSocialLogin("GOOGLE", google_client_id1).getData();
            Member memberUserByGoogle2 = memberService.whenSocialLogin("GOOGLE", google_client_id2).getData();
            Member memberUserByNaver = memberService.whenSocialLogin("NAVER", naver_client_id).getData();


            instaMemberService.connect(memberUser1, "insta_user1", "M");
            instaMemberService.connect(memberUser2, "insta_user2", "M");
            instaMemberService.connect(memberUser3, "insta_user3", "W");
            instaMemberService.connect(memberUser4, "insta_user4", "M");

            // 첫번째 호감인원 객체 생성
            likeablePersonService.like(memberUser1, "insta_user3", 1);

            likeablePersonService.like(memberUser3, "insta_user4", 1);
            likeablePersonService.like(memberUser3, "insta_user100", 2);
        };
    }
}
