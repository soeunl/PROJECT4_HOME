package com.joyfarm.farmstival.global.configs;


import com.joyfarm.farmstival.member.services.LoginFailureHandler;
import com.joyfarm.farmstival.member.services.LoginSuccessHandler;
import com.joyfarm.farmstival.member.services.MemberAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity // 스프링 시큐리티를 활성화
@EnableMethodSecurity // 메서드에 접근 권한을 설정
public class SecurityConfig { // 스프링 시큐리티 설정을 담당하는 SecurityConfig 클래스

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /* 로그인, 로그아웃 S */
        http.formLogin(f -> {
            f.loginPage("/member/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .successHandler(new LoginSuccessHandler()) // 로그인 성공 시 LoginSuccessHandler를 호출
                    .failureHandler(new LoginFailureHandler()); // 로그인 실패 시 LoginFailureHandler를 호출
        });


        http.logout(f -> {
            f.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                    .logoutSuccessUrl("/member/login");
            // 로그아웃 성공 시 /member/login 페이지로 리다이렉트

        });
        /* 로그인, 로그아웃 E */

        /* 인가(접근 통제) 설정 S */
        // 사용자가 어떤 요청을 할 때, 해당 요청에 대한 접근 권한을 검사하고 허용 또는 거부하는 로직을 설정
        http.authorizeHttpRequests(c -> { // HTTP 요청에 대한 권한을 설정
            /*
            c.requestMatchers("/member/**").anonymous()
                    .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
                    .anyRequest().authenticated();
            */
            c.requestMatchers("/mypage/**").authenticated() // 회원 전용
                    // /mypage 하위의 모든 URL에 대해 인증된 사용자만 접근할 수 있도록 설정
                    // 로그인된 사용자만 접근 가능
                    .requestMatchers("/admin/**")
                    .hasAnyAuthority("ADMIN")
                    // /admin 하위의 모든 URL에 대해 "ADMIN" 권한을 가진 사용자만 접근할 수 있도록 설정
                    .anyRequest().permitAll();
                    // 위에서 명시되지 않은 나머지 모든 요청은 누구나 접근할 수 있도록 설정
        });

        http.exceptionHandling(c -> { // 인증/인가 예외 발생 시 처리하는 로직을 설정
            c.authenticationEntryPoint(new MemberAuthenticationEntryPoint()).accessDeniedHandler((req, res, e) -> {
                res.sendError(HttpStatus.UNAUTHORIZED.value()); // 인가되지 않은 사용자가 권한이 없는 자원에 접근하려 할 때 HttpStatus.UNAUTHORIZED (401 Unauthorized) 상태 코드와 함께 에러를 응답
            });
        });
        /* 인가(접근 통제) 설정 E */


        // iframe 자원 출처를 같은 서버 자원으로 한정
        http.headers(c -> c.frameOptions(f -> f.sameOrigin())); // sameOrigin 옵션은 현재 페이지와 같은 도메인의 콘텐츠만 포함할 수 있도록 제한

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}