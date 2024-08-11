package com.joyfarm.farmstival.global.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsFilterConfig { // CORS 필터를 설정

    @Value("${cors.allow.origins}")
    private String allowedOrigins;
    // application.properties 파일이나 다른 환경 설정 소스에서 "cors.allow.origins" 키 값을 가져옴
    // 외부 설정 파일에 정의된 값을 Bean의 필드에 주입하는 데 사용

    // Cors 관련 헤더 -> 응답 헤더에 추가한다. (서버가 자원을 줄지 말지를 알려주는 것이므로)
        @Bean
        public CorsFilter corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedMethod("*"); // 모든 요청 메서드 허용
            config.addAllowedHeader("*"); // 모든 요청 헤더 허용
            if (!allowedOrigins.equals("*")) {
                config.setAllowCredentials(true); // 클라이언트가 쿠키, 인증 정보 등을 포함한 요청을 허용
            }
            config.addAllowedOrigin(allowedOrigins);
            config.addExposedHeader("*");

            source.registerCorsConfiguration("/**", config); // 모든 URL 패턴("/**")에 대해 config에서 설정한 CORS 설정을 적용

            return new CorsFilter(source);
        }
}
