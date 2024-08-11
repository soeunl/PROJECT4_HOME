package com.joyfarm.farmstival.member.controllers;

import com.joyfarm.farmstival.global.Utils;
import com.joyfarm.farmstival.global.exceptions.BadRequestException;
import com.joyfarm.farmstival.global.rests.JSONData;
import com.joyfarm.farmstival.member.MemberInfo;
import com.joyfarm.farmstival.member.entities.Member;
import com.joyfarm.farmstival.member.jwt.TokenProvider;
import com.joyfarm.farmstival.member.services.MemberSaveService;
import com.joyfarm.farmstival.member.validators.JoinValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class MemberController {

    private final JoinValidator joinValidator;
    private final MemberSaveService saveService;
    private final Utils utils;
    private final TokenProvider tokenProvider;

    /* 로그인한 회원 정보 조회 */
    // 인증된 사용자의 정보를 가져와서 JSON 형식으로 변환하여 반환함
    @GetMapping
    @PreAuthorize("isAuthenticated()") // 해당 메소드를 호출하기 위해서는 사용자가 인증되어야 한다(로그인된 사용자만 이 메소드를 호출할 수 있다)
    public JSONData info(@AuthenticationPrincipal MemberInfo memberInfo) { // 인증된 사용자의 정보를 담고 있는 MemberInfo 객체를 주입
        Member member = memberInfo.getMember();
        // 인증된 사용자의 정보를 담고 있는 memberInfo 객체에서 Member 객체를 추출
        return new JSONData(member); // 추출한 Member 객체를 JSONData 객체에 담아서 반환
    }

    /* 회원 가입 시 응답 코드 201 */
    @PostMapping // /account 쪽에 Post 방식으로 접근하면 -> 회원가입
    // 요청받은 JSON 데이터를 검증하고, 유효하면 회원 정보를 저장한 후(회원가입) HTTP 상태 코드 201(Created)를 반환
    public ResponseEntity join(@RequestBody @Valid RequestJoin form, Errors errors){
        // 회원 가입 정보는 JSON 데이터로 전달 -> @RequestBody
        // 요청 body에 담긴 JSON 데이터를 RequestJoin 객체에 바인딩하고, @Valid 애노테이션을 통해 유효성 검사를 수행

        joinValidator.validate(form, errors); // 유효성 검사 결과를 담는 객체

        if (errors.hasErrors()){ // 유효성 검사에서 오류가 발생하면 BadRequestException 예외를 발생
            throw new BadRequestException(utils.getErrorMessages(errors)); // 검증 실패시 400 응답코드, 검증 실패시 JSON형태로 응답 메시지 반환
        }

        saveService.save(form); // 유효성 검사를 통과한 회원 정보를 saveService를 이용하여 데이터베이스에 저장

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* 로그인 절차 완료 시 토큰(=교환권) 발급 */
    @PostMapping("/token") // 요청받은 로그인 정보를 검증하고, 유효하면 토큰을 생성하여 JSON 형태로 반환
    public JSONData token(@RequestBody @Valid RequestLogin form, Errors errors){
        // 요청 body에 담긴 JSON 데이터를 RequestLogin 객체에 바인딩하고, @Valid 애노테이션을 통해 유효성 검사를 수행
        if (errors.hasErrors()){ // 유효성 검사에서 오류가 발생하면 BadRequestException 예외를 발생
            throw new BadRequestException(utils.getErrorMessages(errors));
        }
            String token = tokenProvider.createToken(form.getEmail(), form.getPassword()); // 유효성 검사를 통과하면 tokenProvider를 이용하여 이메일과 비밀번호를 기반으로 토큰을 생성
        
        return new JSONData(token); // 이상이 없으면 JSONData로 토큰 발급(생성된 토큰을 JSONData 객체에 담아서 반환)
    }

    @GetMapping("/test1")
    public void memberOnly() {
        log.info("회원전용!");
    }

    @GetMapping("/test2")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void adminOnly() {
        log.info("관리자 전용!");
    }
}
