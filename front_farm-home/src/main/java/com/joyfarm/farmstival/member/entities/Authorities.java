package com.joyfarm.farmstival.member.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.joyfarm.farmstival.member.constants.Authority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@IdClass(AuthoritiesId.class) // 복합 키를 사용하는 엔티티임을 선언하고, 복합 키 클래스인 AuthoritiesId를 지정
@NoArgsConstructor
@AllArgsConstructor
public class Authorities {
    @Id
    @JsonIgnore
    @ManyToOne(fetch= FetchType.LAZY)
    private Member member;

    @Id
    @Column(length=20)
    @Enumerated(EnumType.STRING) // 데이터베이스에 문자열로 저장
    private Authority authority;
}