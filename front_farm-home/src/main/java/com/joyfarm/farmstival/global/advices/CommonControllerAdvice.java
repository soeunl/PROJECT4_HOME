package com.joyfarm.farmstival.global.advices;

import com.joyfarm.farmstival.global.Utils;
import com.joyfarm.farmstival.global.exceptions.CommonException;
import com.joyfarm.farmstival.global.rests.JSONData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice("com.joyfarm.farmstival")
public class CommonControllerAdvice { //
    // 예외 상황을 처리하는 공통 컨트롤러 어드바이스
    // 발생한 예외가 CommonException 타입인 경우, CommonException 객체에서 제공하는 상태 코드와 에러 메시지를 사용
    // Spring Security 관련 예외인 경우, 해당 예외에 맞는 에러 코드를 설정

    private final Utils utils;

    @ExceptionHandler(Exception.class) // 모든 예외를 처리
    public ResponseEntity<JSONData> errorHandler(Exception e) {

        Object message = e.getMessage();

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        if (e instanceof CommonException commonException) {
            status = commonException.getStatus();
            // commonException 객체에서 제공하는 상태 코드로 설정

            // 에러 코드인 경우는 메세지 조회
            if (commonException.isErrorCode()) message = utils.getMessage(e.getMessage());
            // 에러 코드에 해당하는 메시지를 조회하여 message에 할당

            Map<String, List<String>> errorMessages = commonException.getErrorMessages();
            if (errorMessages != null) message = errorMessages;
            // errorMessages가 null이 아닌 경우 에러 메시지 목록을 message에 할당
        }

        String errorCode = null;
        if (e instanceof BadCredentialsException) { // 아이디 또는 비밀번호가 일치하지 않는 경우
            errorCode = "BadCredentials.Login";
        } else if (e instanceof DisabledException) { // 탈퇴한 회원
            errorCode = "Disabled.Login";
        } else if (e instanceof CredentialsExpiredException) { // 비밀번호 유효기간 만료
            errorCode = "CredentialsExpired.Login";
        } else if (e instanceof AccountExpiredException) { // 사용자 계정 유효기간 만료
            errorCode = "AccountExpired.Login";
        } else if (e instanceof LockedException) { // 사용자 계정이 잠겨있는 경우
            errorCode = "Locked.Login";
        } else if (e instanceof AuthenticationException) {
            errorCode = "Fail.Login";
        } else if (e instanceof AuthorizationDeniedException) {
            errorCode = "UnAuthorized";
        }

        if (StringUtils.hasText(errorCode)) {
            message = utils.getMessage(errorCode); // errorCode에 해당하는 메시지를 조회하여 message에 할당
            status = HttpStatus.UNAUTHORIZED; // 사용자 인증에 실패했음 나타내기 위해 401로 설정
        }

        JSONData data = new JSONData(); // JSON으로 응답
        data.setSuccess(false);
        data.setMessage(message); // 에러 메세지
        data.setStatus(status); // 상태 코드

        e.printStackTrace();

        return ResponseEntity.status(status).body(data);
    }
}