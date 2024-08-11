package com.joyfarm.farmstival.member.repositories;

import com.joyfarm.farmstival.member.entities.Member;
import com.joyfarm.farmstival.member.entities.QMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {
    @EntityGraph(attributePaths = "authorities") // 회원 엔티티(Member)와 연관된 authorities 필드(회원 권한 정보)도 함께 페치 조인
    Optional<Member> findByEmail(String email);
    // 이메일 주소를 이용하여 회원 정보를 조회
    // Optional 클래스를 반환하며, 회원 정보가 존재하면 Optional 객체 안에 회원 정보(Member)가 담겨 있고, 존재하지 않으면 빈 Optional 객체를 반환

    default boolean exists(String email) { // 이메일 주소로 회원 존재 여부를 확인
        QMember member = QMember.member;
        // 이메일 주소로 회원 존재 여부를 확인하는 메소드
        return exists(member.email.eq(email));
        // 이메일 필드(member.email)가 입력받은 이메일(email)과 일치하는 회원 레코드가 존재하는지 여부를 확인
    }
}
