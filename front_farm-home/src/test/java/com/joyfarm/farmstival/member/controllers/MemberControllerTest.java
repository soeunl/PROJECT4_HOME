package com.joyfarm.farmstival.member.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyfarm.farmstival.global.rests.JSONData;
import com.joyfarm.farmstival.member.services.MemberSaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

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
    @DisplayName("회원 가입 테스트")
    void joinTest() throws Exception {
        RequestJoin form = new RequestJoin();
        //form.setEmail("user01@test.org");
        //form.setPassword("_aA123456");
        //form.setConfirmPassword(form.getPassword());
        //form.setUserName("사용자01");
        //form.setMobile("010-1000-1000");
        //form.setAgree(true);

        String params = om.writeValueAsString(form); // JSON 문자열로 변환!

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON) // 요청의 Content-Type을 JSON으로 설정
                        .characterEncoding(Charset.forName("UTF-8")) // 요청의 캐릭터 인코딩을 UTF-8로 설정
                        .content(params)) // 요청 본문에 params 변수에 담긴 JSON 데이터를 설정
                .andDo(print()); // 요청과 응답을 콘솔에 출력
    }

    @Test
    @DisplayName("토큰 발급 테스트") // 로그인 정보를 이용하여 토큰을 발급받고, 발급받은 토큰을 사용하여 다른 API를 호출하는 과정을 테스트
    void tokenTest() throws Exception{
        RequestLogin loginForm = new RequestLogin(); // 로그인 정보를 담는 RequestLogin 객체를 생성
        loginForm.setEmail(form.getEmail()); // 로그인 폼에서 가져온 이메일을 로그인 정보 객체에 설정
        loginForm.setPassword(form.getPassword()); // 로그인 폼에서 가져온 비밀번호를 로그인 정보 객체에 설정

        String params = om.writeValueAsString(loginForm); // 로그인 정보 객체를 JSON 문자열로 변환

        String body = mockMvc.perform(post("/account/token")
                        .contentType(MediaType.APPLICATION_JSON) // 요청의 Content-Type을 JSON으로 설정
                        .content(params) // 요청 본문에 로그인 정보 JSON 문자열을 포함
                ) // 요청과 응답을 콘솔에 출력
                .andDo(print())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
                 // 바디 데이터로 가지고 온다.

        JSONData data = om.readValue(body, JSONData.class); // JSON 데이터로 바꾸기
        String token = (String)data.getData(); // 파싱된 JSON 객체에서 토큰 값을 추출

        mockMvc.perform(get("/account/test2")
                        .header("Authorization", "Bearer " + token)) // : 요청 헤더에 Authorization 필드를 추가하고, Bearer 토큰 형식으로 발급받은 토큰을 포함
                .andDo(print()); // 요청과 응답을 콘솔에 출력
    }
}
