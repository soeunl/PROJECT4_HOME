package com.joyfarm.farmstival.member.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// 스프링 시큐리티 내에서 사용하는 필터는 별도의 방식이 있다. 여기서 정의하고 추가하면 된다.
// 스프링 시큐리티에서는 필터 전, 후 등 특정 시점에 추가할 수 있는 기능이 있다.
// 그 점을 활용해서 토큰으로 로그인을 하고 로그인을 유지하는 방식을 기본 필터 전에 먼저 추가할 것이다.
public class JwtFilter extends GenericFilterBean {

    private final TokenProvider provider;

    /**
     * 요청 헤더 Authorization : Bearer JWT 토큰 값
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */

    @Override // 필터를 통한 토큰 기반 인증 처리
    // 서블릿 필터를 이용하여 클라이언트의 요청에 포함된 토큰을 추출하고, 이를 바탕으로 사용자 인증을 처리하는 기능을 수행
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String token = getToken(request); // getToken 메서드를 통해 요청에서 토큰을 추출
        if(StringUtils.hasText(token)) { // 추출된 토큰이 유효한 문자열인지 확인
            // 토큰을 가지고 회원 인증 객체를 추출한 다음 로그인 유지 처리를 한다.
            Authentication authentication = provider.getAuthentication(token);
            // 토큰을 기반으로 사용자 정보를 조회하고 인증 객체를 생성
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 생성된 인증 객체를 SecurityContextHolder에 설정하여 스프링 시큐리티의 보안 컨텍스트에 사용자 정보를 저장
            // -> 이것을 넣으면 로그인 유지가 된다.
        }
        chain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 JWT 토큰 추출
     * Authorization : Bearer JWT 토큰 값
     * Bearer -> 토큰 인증방식
     *
     * @param request
     * @return
     */

    // 토큰을 추출할 수 있는 메서드 추가 (여기서 추출해서 헤더에 실어서 보낸다)
    // Authorization 헤더를 읽어와 Bearer 방식으로 전달된 토큰을 추출하
    private String getToken(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;
        String bearerToken = req.getHeader("Authorization");
        // HttpServletRequest의 getHeader 메소드를 이용하여 Authorization 헤더의 값을 가져와 bearerToken 변수에 저장
        // Authorization 헤더는 일반적으로 토큰 정보를 담고 있음
        if(StringUtils.hasText(bearerToken) // bearerToken이 null이거나 공백 문자열이 아닌 경우에만 조건문을 실행
                && bearerToken.toUpperCase().startsWith("BEARER ")) {
            // bearer로 시작하는 토큰을 여기서 추출한다.
            // bearerToken을 대문자로 변환하여 "BEARER " 문자열로 시작하는지 확인한다.
            // 이것은 정해져 있는 방식이다.
            // bearer 뒤부터 잘라서 추출한다.
            return bearerToken.substring(7).trim();
            // substring 메서드를 사용하여 "Bearer " 이후의 토큰 부분만 추출하고, 양쪽의 공백을 제거한 후 반환
        }
        return null;
    }
}
