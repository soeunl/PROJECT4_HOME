package com.joyfarm.farmstival.global.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseMemberEntity extends BaseEntity {

    @CreatedBy // 엔티티를 생성한 사용자의 ID 또는 이름을 자동으로 설정
    @Column(length=65, updatable = false)
    private String createdBy;

    @LastModifiedBy // 엔티티를 수정한 사용자의 ID 또는 이름을 자동으로 설정
    @Column(length=65, insertable = false)
    private String modifiedBy;

}
