package com.joyfarm.farmstival.member;


import com.joyfarm.farmstival.member.constants.Authority;
import com.joyfarm.farmstival.member.entities.Authorities;
import com.joyfarm.farmstival.member.entities.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberUtil {

    public boolean isLogin() {
        return getMember() != null;
    }
    // 사용자가 로그인되어 있는지 여부를 반환
    // getMember() 메서드를 호출하여 현재 로그인한 회원 정보를 가져옴
    // 가져온 회원 정보가 null이 아니면 로그인된 상태 (true) 를 반환
    // null이면 로그인되지 않은 상태 (false) 를 반환

    public boolean isAdmin() { // 사용자가 관리자인지 여부를 반환
        if (isLogin()) { // 사용자가 로그인되어 있는지 먼저 확인
            Member member = getMember(); // 로그인되어 있다면 getMember() 메서드를 호출하여 현재 로그인한 회원 정보를 가져옴
            List<Authorities> authorities = member.getAuthorities(); // 회원 정보에서 권한 목록 (authorities) 을 가져옴
            return authorities.stream().anyMatch(s -> s.getAuthority() == Authority.ADMIN); // Authority.ADMIN 값과 일치하는 권한이 있는지 확인
            //  관리자 권한 (Authority.ADMIN) 을 가진 권한이 하나라도 있다면 true를 반환
        }

        return false;
    }

    public Member getMember() { // 현재 로그인한 사용자의 Member 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 인증 정보 가져오기
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof MemberInfo) {
            // 가져온 인증 정보가 null이 아니고, 인증된 상태이며, Principal 타입이 MemberInfo인지 확인
            MemberInfo memberInfo = (MemberInfo) authentication.getPrincipal(); // 인증된 사용자 정보 통에서 인증된 사용자의 정보를 꺼내오는 느낌

            return memberInfo.getMember(); // MemberInfo 객체의 getMember() 메서드를 호출하여 Member 객체를 반환
        }

        return null;
        // SecurityContextHolder =  스프링 시큐리티에서 현재 인증 정보를 저장하는 컨텍스트
        // Authentication 인터페이스 = 인증된 사용자에 대한 정보를 담고 있는 객체(사용자의 인증 여부나 인증에 사용된 정보, 인증된 사용자에 대한 추가적인 정보), 인증 과정 전체를 나타내는 개념으로, 사용자의 인증 정보를 담는 통
        // Principal 인터페이스 = 인증된 사용자의 고유 식별 정보(인증된 주체, 실제 인증된 사용자), Principal은 Authentication의 일부분으로 볼 수 있음, 인증된 사용자 자체를 나타내는 개념으로, Authentication 통 안에 들어있는 사용자 정보
        // MemberInfo 클래스 = 사용자 정보를 담고 있는 클래스
    }
}