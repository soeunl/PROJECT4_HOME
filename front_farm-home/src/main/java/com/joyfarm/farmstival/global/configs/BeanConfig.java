package com.joyfarm.farmstival.global.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig { // 자주 쓰는 빈들을 수동 등록

    @Bean
    public ObjectMapper objectMapper(){ // JSON 데이터를 자바 객체로 변환하거나, 자바 객체를 JSON 데이터로 변환하는 데 사용
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        // Java 8의 새로운 날짜 및 시간 API를 ObjectMapper에서 사용할 수 있도록 설정

        return om;
    }

    @Bean
    public RestTemplate restTemplate(){
        // 다른 서버의 REST API를 호출하는 데 사용
        return new RestTemplate();
    }
}
