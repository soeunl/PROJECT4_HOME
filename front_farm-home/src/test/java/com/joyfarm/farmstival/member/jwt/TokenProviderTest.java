package com.joyfarm.farmstival.member.jwt;

import com.joyfarm.farmstival.member.controllers.RequestJoin;
import com.joyfarm.farmstival.member.services.MemberSaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("Test")
public class TokenProviderTest {
    @Autowired
    private TokenProvider provider;

    @Autowired
    private MemberSaveService saveService;

    private RequestJoin form;
    @BeforeEach
    void init() {
        form = new RequestJoin();
        form.setEmail("user01@test.org");
        form.setPassword("_aA123456");
        form.setConfirmPassword(form.getPassword());
        form.setMobile("010-1000-1000");
        form.setUserName("사용자01");
        form.setAgree(true);

        saveService.save(form);
    }

    @Test
    @DisplayName("토큰 발급 테스트")
    @WithMockUser(username = "user01@test.org", password = "_aA123456", authorities = "USER")
    void createTokenTest() {
        String token = provider.createToken("user01@test.org", "_aA123456");
        //provider 객체의 createToken 메소드를 호출하여 이메일과 비밀번호를 이용하여 토큰을 생성
        // 생성된 토큰은 token 변수에 저장
        // provider.createToken 메소드가 올바른 이메일과 비밀번호를 입력받으면 유효한 토큰을 생성하는지 확인
        System.out.println(token);
    }
}
