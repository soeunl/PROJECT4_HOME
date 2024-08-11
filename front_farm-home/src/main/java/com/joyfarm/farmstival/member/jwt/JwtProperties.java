package com.joyfarm.farmstival.member.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="jwt")
// JWT 토큰 발행 및 검증에 필요한 설정 정보를 담는 클래스
// 이 클래스를 통해 JWT 토큰 발행 및 검증에 필요한 비밀키와 유효 시간을 설정하고, 안전하고 효율적인 JWT 기반 인증 시스템을 구축할 수 있음!
public class JwtProperties {
    private String secret; //검증을 위한 비밀번호
    private Integer validSeconds; //유효시간
}
