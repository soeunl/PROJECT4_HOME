package com.joyfarm.farmstival.farmfarm.entities;

import com.joyfarm.farmstival.global.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor @AllArgsConstructor
public class TourPlace extends BaseEntity {
    @Id
    @GeneratedValue
    private Long seq;

    @Column(length=150, nullable = false)
    private String title; // 여행지 명
    private Double latitude; // 위도
    private Double longitude; // 경도

    @Column(length=50)
    private String tel; // 연락처

    @Column(length=20)
    private String sido;

    @Column(length=20)
    private String sigungu;

    @Column(length=150)
    private String address; // 주소

    @Lob
    private String description; // 설명

    private String course; // 코스

    private String photoUrl; // 사진 파일 주소

    @Column(length=40)
    private String period; // 여행 기간

    private Integer tourDays; // 여행일

    @ManyToMany(fetch = FetchType.LAZY)
    // 하나의 투어 장소는 여러 개의 태그를 가질 수 있고, 하나의 태그는 여러 개의 투어 장소에 속할 수 있으므로 다대다로 연결
    private List<TourPlaceTag> tags;
}