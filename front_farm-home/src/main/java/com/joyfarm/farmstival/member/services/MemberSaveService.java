package com.joyfarm.farmstival.member.services;

import com.joyfarm.farmstival.member.constants.Authority;
import com.joyfarm.farmstival.member.controllers.RequestJoin;
import com.joyfarm.farmstival.member.entities.Authorities;
import com.joyfarm.farmstival.member.entities.Member;
import com.joyfarm.farmstival.member.repositories.AuthoritiesRepository;
import com.joyfarm.farmstival.member.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberSaveService {
    private final MemberRepository memberRepository; // 회원 정보를 데이터베이스에 저장하고 조회
    private final AuthoritiesRepository authoritiesRepository; // 회원 권한 정보를 데이터베이스에 저장하고 조회
    private final PasswordEncoder passwordEncoder; // 비밀번호 해시화

    /**
     * 회원 가입 처리
     *
     * @param form
     */
    public void save(RequestJoin form) { // 회원 가입 요청 정보(RequestJoin)를 받아 회원 가입 처리를 수행
        Member member = new ModelMapper().map(form, Member.class); // form 객체의 속성과 Member 클래스의 속성 이름이 일치하면, 해당 값을 자동으로 복사, 매핑이 완료되면, member 변수에는 form 객체의 값을 가지는 새로운 Member 객체가 생성
        String hash = passwordEncoder.encode(form.getPassword()); // BCrypt 해시화
        member.setPassword(hash);

        save(member, List.of(Authority.USER));
        // 회원 정보와 권한 정보를 저장
        // 파라미터로 기본 권한(USER)을 넘겨줌
    }


    public void save(Member member, List<Authority> authorities) { // 회원 정보(Member)와 권한 정보(List<Authority>)를 받아 실제 데이터 저장 로직을 수행

        // 휴대전화번호 숫자만 기록
        String mobile = member.getMobile();
        if (StringUtils.hasText(mobile)) { // 휴대전화 번호 문자열이 null이나 공백이 아닌지 확인

            mobile = mobile.replaceAll("\\D", "");
            member.setMobile(mobile);
            // 유효한 휴대전화 번호라면 숫자 이외의 모든 문자를 제거
            // 정제된 휴대전화 번호를 member.setMobile(mobile) 메소드를 사용하여 다시 설정
        }

        memberRepository.saveAndFlush(member);
        // 회원 정보를 데이터베이스에 저장

        // 권한 추가, 수정 S
        if (authorities != null) { // authorities 파라미터가 null이 아니면 권한 정보 처리 로직을 수행
            List<Authorities> items = authoritiesRepository.findByMember(member); // 해당 회원에게 부여된 기존 권한 정보를 가져옴
            authoritiesRepository.deleteAll(items); // 기존 권한 정보를 삭제
            authoritiesRepository.flush(); // 기존 권한 정보 삭제를 실제 데이터베이스에 반영합

            items = authorities.stream().map(a -> Authorities.builder()
                    .member(member)
                    .authority(a)
                    .build()).toList();
            // 파라미터로 받은 권한 정보 리스트를 새로운 Authorities 객체 리스트로 변환

            authoritiesRepository.saveAllAndFlush(items);
            // 변환된 Authorities 객체 리스트를 데이터베이스에 저장
        }
        // 권한 추가, 수정 E
    }
}