package com.joyfarm.farmstival.member.validators;

import com.joyfarm.farmstival.global.validators.MobileValidator;
import com.joyfarm.farmstival.global.validators.PasswordValidator;
import com.joyfarm.farmstival.member.controllers.RequestJoin;
import com.joyfarm.farmstival.member.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class JoinValidator implements Validator, PasswordValidator, MobileValidator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestJoin.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }

        /**
         * 1. 이미 가입된 회원인지 체크
         * 2. 비밀번호, 비밀번호 확인 일치 여부
         * 3. 비밀번호 복잡성 체크
         * 4. 휴대전화번호 형식 체크
         */

        RequestJoin form = (RequestJoin) target;
        String email = form.getEmail();
        String password = form.getPassword();
        String confirmPassword = form.getConfirmPassword();
        String mobile = form.getMobile();

        // 1. 이미 가입된 회원인지 체크
        if (memberRepository.exists(email)) {
            errors.rejectValue("email", "Duplicated");
            // 입력된 이메일로 회원이 이미 존재하는지 확인
            // 만약 이미 존재하는 회원이라면 errors 객체에 오류 메시지를 추가
        }

        // 2. 비밀번호, 비밀번호 확인 일치 여부
        if (!password.equals(confirmPassword)) {
            errors.rejectValue("confirmPassword", "Mismatch.password");
            // 입력된 비밀번호와 비밀번호 확인 값이 일치하는지 비교
            // 일치하지 않으면 비밀번호 확인 필드에 대한 오류 메시지를 추가
        }

        // 3. 비밀번호 복잡성 체크 - 알파벳 대소문자 각각 1개 이상, 숫자 1개 이상, 특수문자 1개 이상
        if (!alphaCheck(password, false) || !numberCheck(password) || !specialCharsCheck(password)) {
            errors.rejectValue("password", "Complexity");
            // 비밀번호가 특정 조건(알파벳 대소문자, 숫자, 특수문자 포함)을 만족하는지 검사
            // 조건을 만족하지 않으면 비밀번호 필드에 대한 복잡성 오류 메시지를 추가
        }

        // 4. 휴대전화번호 형식 체크
        if (!mobileCheck(mobile)) {
            errors.rejectValue("mobile", "Mobile");
            // 입력된 휴대전화번호가 올바른 형식인지 검사
            // 올바른 형식이 아니면 휴대전화번호 필드에 대한 오류 메시지를 추가
        }
    }
}