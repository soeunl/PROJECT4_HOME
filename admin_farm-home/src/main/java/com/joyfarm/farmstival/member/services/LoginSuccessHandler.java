package com.joyfarm.farmstival.member.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    // 사용자가 성공적으로 로그인했을 때 어떤 동작을 수행할지 정의하는 클래스
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession();
        // 현재 HTTP 요청의 세션 객체를 가져옴

        // 세션에 남아 있는 requestLogin 값 제거
        session.removeAttribute("requestLogin");

        // 로그인 성공시 - redirectUrl이 있으면 해당 주소로 이동, 아니면 메인 페이지 이동
        String redirectUrl = request.getParameter("redirectUrl");
        redirectUrl = StringUtils.hasText(redirectUrl) ? redirectUrl.trim() : "/";

        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
    // 사용자가 성공적으로 로그인했을 때 다음과 같은 작업을 수행
    // 세션에 남아 있을 수 있는 로그인 요청 여부 표시를 제거
    // 요청 파라미터에 지정된 리다이렉트 주소가 있다면 해당 주소로 이동
    //만약 리다이렉트 주소가 없거나 유효하지 않다면 메인 페이지 (/)로 이동
}
