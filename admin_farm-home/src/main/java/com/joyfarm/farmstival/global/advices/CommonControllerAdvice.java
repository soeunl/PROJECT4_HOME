package com.joyfarm.farmstival.global.advices;

import com.joyfarm.farmstival.member.MemberUtil;
import com.joyfarm.farmstival.member.entities.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@ControllerAdvice("com.joyfarm.farmstival")
public class CommonControllerAdvice {

    private final MemberUtil memberUtil;

    @ModelAttribute("loggedMember")
    public Member loggedMember() {
        return memberUtil.getMember();
    }

    @ModelAttribute("isLogin")
    public boolean isLogin() {
        return memberUtil.isLogin();
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        return memberUtil.isAdmin();
    }
}