package com.joyfarm.farmstival.member.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class MemberAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 사용자가 인증되지 않은 상태로 보호된 리소스에 접근했을 때 어떤 동작을 수행할지 정의하는 클래스
        /**
         * 회원 전용 페이지로 접근한 경우 - /mypage -> 로그인 페이지 이동
         * 관리자 페이지로 접근한 경우 - 응답 코드 401, 에러페이지 출력
         */

        String uri = request.getRequestURI(); // 사용자가 요청한 URI를 가져옴
        if (uri.contains("/admin")) { // 관리자 페이지
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            // HTTP 응답 코드 401 (Unauthorized)을 전송
        } else { // 회원 전용 페이지
            String qs = request.getQueryString(); // 요청 URI에 포함된 쿼리 스트링 (파라미터) 값 가져오기
            String redirectUrl = uri.replace(request.getContextPath(), ""); // 현재 애플리케이션의 컨텍스트 패스 가져오기
            if (StringUtils.hasText(qs)) { // 쿼리 스트링 값이 null이 아니고, 공백 문자열도 아닌지 확인, 유효한 쿼리 스트링 값이 있다면, 원래 요청 URI에 쿼리 스트링을 포함하여 리다이렉트 URL을 생성
                redirectUrl += "?" + qs;
            }
            // 회원 전용 페이지에 접근한 경우 로그인 페이지로 리다이렉트하며, 원래 요청하려고 했던 URI를 파라미터로 전달하여 로그인 후 해당 페이지로 이동할 수 있도록 함!!

            response.sendRedirect(request.getContextPath() + "/member/login?redirectUrl=" + redirectUrl);
            // 리다이렉트 URL 파라미터 (redirectUrl)에는 원래 요청하려고 했던 URI를 포함
        }
    }
}
