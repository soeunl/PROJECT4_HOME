package com.joyfarm.farmstival.member;

import com.joyfarm.farmstival.member.entities.Member;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * VO - 수정 불가, 읽기만 가능
 */
@Data
@Builder
public class MemberInfo implements UserDetails {
    /**
     * UserDetails: 스프링 시큐리티에서 사용자의 정보를 담는 인터페이스
     * 스프링 시큐리티가 사용자 인증 정보를 관리하고 인증 절차를 수행하는데 필요한 사용자의 정보를 제공해준다.
     */
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Member member; //회원정보 가져오도록

    //사용자의 권한을 스프링 시큐리티가 인식 할 수 있도록 해줌
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    } // 사용자의 권한 목록을 반환

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    } // 계정이 만료되었는지 여부를 반환, 기본값은 true로 설정되어 계정이 만료되지 않은 것으로 간주

    @Override
    public boolean isAccountNonLocked() {
        return true;
    } // 계정이 잠겼는지 여부를 반환, 기본값은 true로 설정되어 계정이 잠기지 않은 것으로 간주

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    } // 비밀번호가 만료되었는지 여부를 반환, 기본값은 true로 설정되어 비밀번호가 만료되지 않은 것으로 간주

    @Override
    public boolean isEnabled() {
        return true;
    } // 계정이 활성화되었는지 여부를 반환, 기본값은 true로 설정되어 계정이 활성화된 것으로 간주
}
