import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom'; // Navigate는 react-router-dom에서 페이지 이동을 위한 컴포넌트
import UserInfoContext from '../../../member/modules/UserInfoContext'; // UserInfoContext는 로그인 정보를 관리하는 Context API

// 로그인한 사용자만 접근 가능한 컴포넌트를 생성
const MemberOnly = ({ children }) => {
  const {
    states: { isLogin },
  } = useContext(UserInfoContext); // UserInfoContext에서 로그인 상태를 가져오고 isLogin 변수에 로그인 상태를 저장

  return isLogin ? children : <Navigate to="/member/login" replace={true} />;
}; // 로그인 상태를 확인하여 로그인된 경우 자식 컴포넌트(children)를 렌더링하고, 로그인되지 않은 경우 로그인 페이지로 리다이렉트
// Navigate 컴포넌트를 사용하여 /member/login으로 이동
// replace 속성을 true로 설정하여 브라우저 히스토리에 현재 페이지를 남기지 않음

export default React.memo(MemberOnly);
