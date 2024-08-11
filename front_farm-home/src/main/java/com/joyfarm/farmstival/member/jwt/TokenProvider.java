package com.joyfarm.farmstival.member.jwt;

import com.joyfarm.farmstival.global.exceptions.UnAuthorizedException;
import com.joyfarm.farmstival.member.MemberInfo;
import com.joyfarm.farmstival.member.services.MemberInfoService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class TokenProvider {

    private final JwtProperties properties; // 유효시간을 가지고 오기 위해 사용
    private final AuthenticationManagerBuilder authenticationManagerBuilder; // 토큰을 가지고 승인 정보를 만들어 준다.
    private final MemberInfoService infoService;

    /**
     * JWT 토큰 생성
     * 
     * @param email
     * @param password
     * @return
     */
    
    // 토큰 생성하기 (이메일과 패스워드로 토큰 생성)
    // 주어진 이메일과 비밀번호를 이용하여 사용자 인증을 수행하고, 인증에 성공하면 JWT(JSON Web Token) 토큰을 생성하여 반환
    public String createToken(String email, String password){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        // 이메일과 패스워드로 검증을 하고, 이상이 없으면 토큰을 생성
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            // 스프링 시큐리티의 AuthenticationManager를 생성

            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
                // 로그인 성공 시 -> JWT 토큰을 발급한다. 토큰을 발급하고 제공하는 것이 로그인 절차이다.
                long now = (new Date()).getTime();
                Date validity = new Date(now + properties.getValidSeconds() * 1000);
                // JWT 토큰의 유효 시간을 계산하여 Date 객체로 생성
                // 여기에 지정된 시간만큼 현재 시간에서 유효시간이 설정된다. 환경변수에 따라 충분히 가감이 가능하다.

                return Jwts.builder().setSubject(authentication.getName())
                        .signWith(getKey(), SignatureAlgorithm.HS512)
                        // HMAC + SHA512(Hash를 만들어 주는 것)
                        // .getName() -> 사용자 이메일 가지고 오는 것!
                        // 이곳에서 가지고 오는 이메을은 확실하게 인증된 사용자 이메일. 로그인된 사용자 정보를 가지고 올 때 활용!
                        .expiration(validity) // 토큰의 만료 시간을 설정
                        .compact();
                        // 만료 시간도 토큰에 포함한다. 그렇기 때문에 따로 저장소에서 관리하지 않아도 된다.
            }
        return null;
    }

    /**
     * 토큰으로 회원 인증 객체 초회
     *
     * @param token
     * @return
     */

    // 토큰 정보로 인증한 사용자 정보 가져오기
    // 입력받은 JWT 토큰을 검증하고, 유효한 토큰이라면 토큰 안에 포함된 사용자 정보를 기반으로 Authentication 객체를 생성(인증된 사용자의 정보)하여 반환
    public Authentication getAuthentication(String token){

        // 토큰 검증 먼저 한다.
        validateToken(token);

        // 이상이 없으면 이곳으로 유입이 된다.
        Claims claims = Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token) // 이 토큰에 사용자 인증 정보가 들어있다.
                .getPayload();

        String email = claims.getSubject(); // 사용자 이메일(subject)을 추출

        MemberInfo memberInfo = (MemberInfo)infoService.loadUserByUsername(email); // 추출한 이메일을 이용하여 사용자 정보를 데이터베이스에서 조회

        return new UsernamePasswordAuthenticationToken(memberInfo, token, memberInfo.getAuthorities());
        // 조회된 사용자 정보와 토큰, 그리고 사용자의 권한 정보를 이용하여 UsernamePasswordAuthenticationToken 객체를 생성하고 반환
        // 이 객체는 스프링 시큐리티에서 인증된 사용자의 정보
    }

    // 토큰 검증 (유효한지, 만료되지 않았는지)
    // 입력받은 JWT 토큰의 유효성을 검증하고, 문제가 발생하면 해당하는 예외를 발생
    public void validateToken(String token){
        // 여기서 토큰 유효성 검사를 한다.
        String errorCode = null; // 발생한 오류 코드를 저장하기 위함
        try {
            Jwts.parser().setSigningKey(getKey()).build().parseClaimsJws(token).getPayload();
            // getPayload() -> 인증 정보를 가지고 온다.
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // 변조된 JWT 토큰
            errorCode = "Malformed.jwt";
            e.printStackTrace();
        } catch (ExpiredJwtException e) {
            // 유효시간이 만료된 JWT 토큰
            errorCode = "Expired.jwt";
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 형식의 JWT 토큰
            errorCode = "Unsupported.jwt";
            e.printStackTrace();
        } catch (Exception e) {
            // 예상치 못한 예외가 발생한 경우
            errorCode = "Error.jwt";
            e.printStackTrace();
        }
        if (errorCode != null) {
            throw new UnAuthorizedException(errorCode);
        }
        // 토큰 유효성 검증에 실패했음을 나타냄
        // 토큰 예외 종류에 따라 어떤 예외인지 알 수 있음
    }

    private Key getKey() { // 비밀키를 생성하는 private 메서드
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
        // 여기서 키값으로 검증한다. 데이터가 바뀌어서 키가 바뀌면 검증에 실패한다.
    }
}
