package com.joyfarm.farmstival.member.services;

import com.joyfarm.farmstival.member.MemberInfo;
import com.joyfarm.farmstival.member.constants.Authority;
import com.joyfarm.farmstival.member.entities.Authorities;
import com.joyfarm.farmstival.member.entities.Member;
import com.joyfarm.farmstival.member.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 스프링 시큐리티에서 유저의 정보를 불리오기 위해 구현
 */
@Service
@RequiredArgsConstructor
public class MemberInfoService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /* 회원 정보가 필요할때마다 호출되는 메서드 */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //유저의 정보를 불러와서 UserDetails로 리턴

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        // 회원 레포지토리를 이용하여 이메일로 회원 정보를 조회
        // 회원 없을 경우 예외 발생(UsernameNotFoundException)

        /*
        MemberInfo쪽에 getAuthorities()메서드를 통해서 사용자 권한 조회,
        권한이 null이거나 비어있을 때 대체 할 기본권한 -> USER, null이 아닌 경우 기존 권한 그대로 반환
         */
        List<Authorities> tmp = member.getAuthorities();
        if(tmp == null || tmp.isEmpty()){
            tmp = List.of(Authorities.builder().member(member).authority(Authority.USER).build());
        } // 회원(Member) 엔티티의 권한(Authorities) 정보를 처리
        // 회원에게 권한 정보가 없을 경우, 기본 권한인 USER를 부여

        /*
        tmp에서 가져온 Authorities 객체 리스트를 Spring Security가 이해할 수 있는 SimpleGrantedAuthority 객체 리스트로 변환하는 가공 단계가 필요하다.
         */
        // 회원의 권한 정보를 스프링 시큐리티에서 사용할 수 있는 형태로 변환
        List<SimpleGrantedAuthority> authorities = tmp.stream()
                .map(a -> new SimpleGrantedAuthority(a.getAuthority().name()))
                .toList();
        // 각 Authorities 객체에서 권한 정보를 가져옴, 권한 정보(enum)의 이름을 문자열로 변환, 변환된 문자열을 이용하여 SimpleGrantedAuthority 객체를 생성
        // Authority enum의 name 메서드를 호출하여 문자열로 변환해야한다.(authority는 enum상수로 되어있기 때문!)

        return MemberInfo.builder() // mberInfo 객체를 생성하여 스프링 시큐리티의 UserDetails 인터페이스를 구현
                // 스프링 시큐리티에서 사용자 인증 및 권한 부여에 사용
                .email(member.getEmail())
                .password(member.getPassword())
                .member(member)
                .authorities(authorities)
                .build();

        // 회원의 권한 정보를 스프링 시큐리티에서 사용할 수 있는 형태로 변환하고, 변환된 권한 정보와 함께 MemberInfo 객체를 생성하여 반환하는 역할
        // 이렇게 생성된 MemberInfo 객체는 스프링 시큐리티에서 사용자 인증 및 권한 부여에 사용되어, 사용자가 접근할 수 있는 시스템 자원을 제한하는 데 활용됨
    }
}
