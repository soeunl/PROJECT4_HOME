package com.joyfarm.farmstival.global.configs;

import com.joyfarm.farmstival.member.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(c -> c.disable()) // Cors 정책을 바꾸는 설정
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                // corsFilter를 통해 Cors 정책을 바꾼다. UsernamePasswordAuthenticationFilter.class가 하기 전에!
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // jwtFilter를 통해 로그인 유지 처리를 한다. UsernamePasswordAuthenticationFilter.class가 하기 전에!
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 세션쪽 기술을 활용하지 않고 로그인을 유지할 것이므로 STATELESS로 무상태 처리를 한다.
                // STATELESS가 되면 세션을 사용하지 않게 된다.
                .exceptionHandling(h -> {
                    h.authenticationEntryPoint((req, res, e) -> res.sendError(HttpStatus.UNAUTHORIZED.value()));
                    // 회원가입이랑 토큰 발급 말고는 모두 인증이 필요하게 만들 것이다.
                    h.accessDeniedHandler((req, res, e) -> res.sendError(HttpStatus.UNAUTHORIZED.value()));
                })
                .authorizeHttpRequests(c -> {
                    c.requestMatchers(
                                    "/account",
                                    "/account/token"
                            ).permitAll() // 회원가입, 로그인(토큰)은 모든 접근 가능
                            .anyRequest().authenticated(); // 그외에는 인증 필요
                });

        return http.build();

        // corsFilter를 통해 CORS 정책을 설정하여 다른 도메인에서 자원에 접근할 수 있도록 허용한다.
        // jwtFilter를 통해 JWT 토큰을 이용한 인증을 수행하고, 세션 기반 인증 대신 토큰 기반 인증을 사용하도록 설정한다.
        // 세션을 사용하지 않는 stateless 방식으로 설정한다. JWT 토큰을 이용하여 사용자 상태를 관리하기 때문에 세션이 필요하지 않기 떄문이다.
        // /account와 /account/token은 누구나 접근할 수 있도록 하고, 나머지 URL은 인증된 사용자만 접근할 수 있도록 설정한다.
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
