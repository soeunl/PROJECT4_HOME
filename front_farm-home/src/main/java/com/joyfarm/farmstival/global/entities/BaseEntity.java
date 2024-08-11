package com.joyfarm.farmstival.global.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate // 엔티티가 생성될 때 현재 시각을 자동으로 설정
    @Column(updatable = false) // 데이터가 생성된 후에는 해당 컬럼의 값을 변경할 수 없도록함
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 수정될 때 현재 시각을 자동으로 설정
    @Column(insertable = false) // 수정 시각은 엔티티가 수정될 때만 설정
    private LocalDateTime modifiedAt;

    @Column(insertable = false)
    private LocalDateTime deletedAt;
}
