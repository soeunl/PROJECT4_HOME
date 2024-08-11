package com.joyfarm.farmstival.member.entities;

import com.joyfarm.farmstival.global.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data @Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue
    private Long seq;

    @Column(length=65, unique = true, nullable = false)
    private String email;

    @Column(length=65, nullable = false)
    private String password;

    @Column(length=40, nullable = false)
    private String userName;

    @Column(length=15, nullable = false)
    private String mobile;

    @ToString.Exclude
    @OneToMany(mappedBy = "member") // 관계의 주인이 Authorities 엔티티이며, Authorities 엔티티의 member 필드를 통해 관계가 매핑됨
    private List<Authorities> authorities;
}
