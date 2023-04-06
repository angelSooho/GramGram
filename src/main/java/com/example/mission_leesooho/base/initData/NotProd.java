package com.example.mission_leesooho.base.initData;

import com.example.mission_leesooho.boundedContext.instaMember.service.InstaMemberService;
import com.example.mission_leesooho.boundedContext.likeablePerson.service.LikeablePersonService;
import com.example.mission_leesooho.boundedContext.member.entity.Member;
import com.example.mission_leesooho.boundedContext.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile({"dev", "test"})
public class NotProd {

    @Autowired Environment environment;

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
            Member memberUserByKakao = memberService.whenSocialLogin("KAKAO", environment.getProperty("KAKAO_CLIENT_ID")).getData();
            Member memberUserByGoogle1 = memberService.whenSocialLogin("GOOGLE", environment.getProperty("GOOGLE_CLIENT_ID1")).getData();
            Member memberUserByGoogle2 = memberService.whenSocialLogin("GOOGLE", environment.getProperty("GOOGLE_CLIENT_ID2")).getData();

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
