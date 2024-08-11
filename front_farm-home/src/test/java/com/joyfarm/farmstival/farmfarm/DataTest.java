package com.joyfarm.farmstival.farmfarm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyfarm.farmstival.farmfarm.entities.QTourPlace;
import com.joyfarm.farmstival.farmfarm.entities.TourPlace;
import com.joyfarm.farmstival.farmfarm.entities.TourPlaceTag;
import com.joyfarm.farmstival.farmfarm.repositories.TourPlaceRepository;
import com.joyfarm.farmstival.farmfarm.repositories.TourPlaceTagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
//@ActiveProfiles("test")
public class DataTest {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TourPlaceRepository repository;

    @Autowired
    private TourPlaceTagRepository tagRepository;

    @Test
    void test1() throws Exception {
        File file = new File("D:/data/data1.json");
        List<Map<String, String>> tmp = om.readValue(file, new TypeReference<>() {});

        List<TourPlace> items = tmp.stream()
                .map(d -> TourPlace.builder() // 빌더 패턴을 통해 TourPlace 객체를 생성
                        // 맵 객체의 키에 해당하는 JSON 데이터를 get 메소드를 사용하여 가져옴
                        // 가져온 데이터를 해당 TourPlace 객체의 필드에 설정
                        .title(d.get("여행지명"))
                        .latitude(Double.valueOf(d.get("위도")))
                        .longitude(Double.valueOf(d.get("경도")))
                        .tel(d.get("연락처"))
                        .address(d.get("주소"))
                        .description(d.get("설명"))
                        .photoUrl(d.get("사진파일"))
                        .tourDays(Integer.valueOf(d.get("여행일")))
                        .build()).toList();

        repository.saveAllAndFlush(items);
        // TourPlace 객체 리스트 items 를 레포지토리(repository) 를 통해 데이터베이스에 저장
    }

    @Test
    void test2() throws Exception {
        File file = new File("D:/data/data2.json");
        List<Map<String, String>> tmp = om.readValue(file, new TypeReference<>() {});

        tmp.forEach(d -> {
            String tagsTmp = d.get("태그");
            List<TourPlaceTag> tags = null;
            if (StringUtils.hasText(tagsTmp)) {
                tags = Arrays.stream(tagsTmp.split(","))
                        .map(s -> TourPlaceTag.builder().tag(s).build())
                        .toList();
                tagRepository.saveAllAndFlush(tags);
            }

            TourPlace item = TourPlace.builder()
                    .tags(tags)
                    .title(d.get("주제"))
                    .sido(d.get("시도"))
                    .sigungu(d.get("시군구"))
                    .address(d.get("시도") + " " + d.get("시군구"))
                    .description(d.get("요약"))
                    .period(d.get("월"))
                    .photoUrl(d.get("사진파일"))
                    .course(d.get("코스정보")).build();
            repository.saveAndFlush(item);
        });
    }

    @Test
    void test3() { // sido (시도)와 sigungu (시군구) 정보를 추출하여 업데이트
        QTourPlace tourPlace = QTourPlace.tourPlace;
        List<TourPlace> items = repository.findAll();
        for (TourPlace item : items) {
            String address = item.getAddress(); // 각 엔티티의 주소를 가져옴
            String sido = address.substring(0, 2); // 주소의 앞 두 글자를 시도로 추출

            String _sido = null;
            // 앞 두 글자에 해당하는 시도명을 찾아 _sido에 저장
            // 유사한 시도가 발견되면 해당 시도의 sido 값을 _sido에 저장하고 변환
            if (sido.equals("충북")) {
                _sido = "충청북도";
            } else if (sido.equals("충남")) {
                _sido = "충청남도";
            } else if (sido.equals("전북")) {
                _sido = "전라북도";
            } else if (sido.equals("전남")) {
                _sido = "전라남도";
            } else if (sido.equals("경북")) {
                _sido = "경상북도";
            } else if (sido.equals("경남")) {
                _sido = "경상남도";
            }

            // TourPlace 엔티티의 sido 정보가 없는 경우, 데이터베이스에서 유사한 sido 정보를 찾아 보완하는 역할
            if (_sido != null) {
                item.setSido(_sido);
                continue;
            }

            // TourPlace 엔티티의 sido 정보가 없는 경우, 데이터베이스에서 유사한 sido 정보를 찾아 보완하는 역할
            List<TourPlace> items2 = (List<TourPlace>)repository.findAll(tourPlace.sido.contains(sido));
            if (items2 == null || items2.isEmpty()) continue;

            TourPlace item2 = items2.get(0);

            if (item2 != null) {
                _sido = item2.getSido();

                item.setSido(_sido);
            }
        }

        repository.saveAllAndFlush(items);

        for (TourPlace item : items) { // TourPlace 엔티티들의 sigungu 정보를 보완하는 역할
            if (StringUtils.hasText(item.getSigungu())) continue;

            String address = item.getAddress();

            String sigungu = address.indexOf("경기도") > -1 ? address.substring(3, 6) : address.substring(2, 5);
            // "경기도"가 있다면 주소 문자열의 3번째 인덱스부터 6번째 인덱스까지의 부분 문자열을 추출
            // "경기도"가 없다면 주소 문자열의 2번째 인덱스부터 5번째 인덱스까지의 부분 문자열을 추출, 다른 시도의 경우를 가정한 것

            item.setSigungu(sigungu);
        }

        repository.saveAllAndFlush(items);
    }
}
