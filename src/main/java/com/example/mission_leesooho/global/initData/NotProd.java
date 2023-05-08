package com.example.mission_leesooho.global.initData;

import com.example.mission_leesooho.boundedContext.instaMember.service.InstaMemberService;
import com.example.mission_leesooho.boundedContext.likeablePerson.service.LikeablePersonService;
import com.example.mission_leesooho.boundedContext.member.entity.Member;
import com.example.mission_leesooho.boundedContext.member.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${custom.id.FACEBOOK_CLIENT_ID}")
    private String facebook_client_id;



    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            InstaMemberService instaMemberService,
            LikeablePersonService likeablePersonService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                Member memberAdmin = memberService.join("admin", "1234").getData();
                Member memberUser1 = memberService.join("user1", "1234").getData();
                Member memberUser2 = memberService.join("user2", "1234").getData();
                Member memberUser3 = memberService.join("user3", "1234").getData();
                Member memberUser4 = memberService.join("user4", "1234").getData();
                Member memberUser5 = memberService.join("user5", "1234").getData();

                Member memberUserByKakao = memberService.whenSocialLogin("KAKAO", kakao_client_id).getData();
                Member memberUserByGoogle1 = memberService.whenSocialLogin("GOOGLE", google_client_id1).getData();
                Member memberUserByGoogle2 = memberService.whenSocialLogin("GOOGLE", google_client_id2).getData();
                Member memberUserByNaver = memberService.whenSocialLogin("NAVER", naver_client_id).getData();
                Member memberUser9ByFacebook = memberService.whenSocialLogin("FACEBOOK", facebook_client_id).getData();

                instaMemberService.connect(memberUser2, "insta_user2", "M");
                instaMemberService.connect(memberUser3, "insta_user3", "W");
                instaMemberService.connect(memberUser4, "insta_user4", "M");
                instaMemberService.connect(memberUser5, "insta_user5", "W");

                // 원활한 테스트와 개발을 위해서 자동으로 만들어지는 호감이 삭제, 수정이 가능하도록 쿨타임해제
//                LikeablePersonResponse likeablePersonToinstaUser4 = likeablePersonService.like(memberUser3, "insta_user4", 1).getData();
//                Ut.reflection.setFieldValue(likeablePersonToinstaUser4, "modifyUnlockDate", LocalDateTime.now().minusSeconds(1));
//                LikeablePersonResponse likeablePersonToinstaUser100 = likeablePersonService.like(memberUser3, "insta_user100", 2).getData();
//                Ut.reflection.setFieldValue(likeablePersonToinstaUser100, "modifyUnlockDate", LocalDateTime.now().minusSeconds(1));
            }
        };
    }
}
