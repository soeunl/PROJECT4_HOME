import React from 'react';
import KakaoMap from '../../map/KakaoMap';

const markers = [
  {
    lat: 37.557756188912954,
    lng: 126.94062742683245,
    info: { content: '<h1>바로 노출되는 인포윈도우</h1>', removable: true }, // 인포윈도우 바로 노출
  },
  {
    lat: 37.557287959390024,
    lng: 126.94120499658828,
    // 마커 이미지 바꾸는 용도(전체 url 주소로 나와야 함!)
    image:
      'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
    info: {
      content: '<h1>클릭시 토글 형태로 나오는 인포윈도우</h1>',
      clickable: true,
    }, // 클릭시 토글 형태로 인포윈도우 노출
  },
  { lat: 37.561184514897825, lng: 126.94069261563956 },
];

const options = {
  // currentLocation: true, // 현재 위치 기반 (주소 기반을 위해 지금은 주석처리)
  // address: '경기도 용인시 에버랜드로 199',
  center: {
    // DB에 있는 값 가지고 와서 출력할때는 이거 사용하세요~
    lat: 33.450701,
    lng: 126.570667,
  },
  zoom: 3,
  // marker: markers, -> 마커가 있으면 마커 기준으로 나오므로 주석처리함!
  // 어떠한 지역에서 여러개 마커 표시를 할때는 이것을 사용함
  // 전체 마커 동일한 이미지
  markerImage:
    'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
};

const Main = () => {
  return <KakaoMap {...options} />; // 리턴으로 KakaoMap으로 보냄
};

export default React.memo(Main);
