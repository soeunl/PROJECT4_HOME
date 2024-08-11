import styled, { css } from 'styled-components';
import { buttonColor } from '../../styles/color'; // 버튼 색상정보
import fontSize from '../../styles/fontSize'; // 버튼의 폰트 크기 정보
const { big, medium, normal } = fontSize;
// big, medium, normal 변수에 fontSize 객체의 값을 할당

const commonStyle = css`
  width: 100%;
  border-radius: 3px;
  cursor: pointer;
`;
// commonStyle 변수에 공통되는 버튼의 스타일을 정의

export const SmallButton = styled.button`
  font-size: ${normal};
  height: 30px;
  ${commonStyle}

  ${({ color }) =>
    buttonColor[color] &&
    css`
      background: ${buttonColor[color][0]};
      color: ${buttonColor[color][1]};
      border: 1px solid ${buttonColor[color][2]};
    `}
  ${({ width }) => css`
    width: ${width}px;
  `}  
`;

export const MidButton = styled.button`
  font-size: ${medium};
  height: 40px;
  ${commonStyle}

  ${({ color }) =>
    buttonColor[color] &&
    css`
      background: ${buttonColor[color][0]};
      color: ${buttonColor[color][1]};
      border: 1px solid ${buttonColor[color][2]};
    `}
`;

export const BigButton = styled.button`
  font-size: ${big};
  height: 45px;
  ${commonStyle}

  ${({ color }) =>
    buttonColor[color] &&
    css`
      background: ${buttonColor[color][0]};
      color: ${buttonColor[color][1]};
      border: 1px solid ${buttonColor[color][2]};
    `}
`;

export const ButtonGroup = styled.div`
  display: flex;
  width: ${({ width }) => (width ? `${width}px` : '100%')};
  margin: 20px auto;

  button {
    width: 0;
    flex-grow: 1;
  }

  button + button {
    margin-left: 5px;
  }
`;
