import React from 'react';
import styled from 'styled-components';
import classNames from 'classnames';
import { NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { color } from '../styles/color';
import fontSize from '../styles/fontSize';

const { dark, primary, light } = color;

const MenuBox = styled.nav`
  background: ${dark};

  div {
    display: flex;
    height: 50px;

    a {
      color: ${light};
      line-height: 50px;
      padding: 0 50px;
      font-size: ${fontSize.medium};

      &.on {
        background: ${primary};
      }
    }
  }
`;

const MainMenu = () => {
  const { t } = useTranslation();

  return (
    <MenuBox>
      <div className="layout-width">
        <NavLink
          to="/news"
          className={({ isActive }) => classNames({ on: isActive })}
        >
          {t('뉴스')}
        </NavLink>
        <NavLink
          to="/reservation"
          className={({ isActive }) => classNames({ on: isActive })}
        >
          {t('농촌체험 예약')}
        </NavLink>
        <NavLink
          to="/travel_festival"
          className={({ isActive }) => classNames({ on: isActive })}
        >
          {t('조이팜의 추천')}
        </NavLink>
        <NavLink
          to="/my_reservation"
          className={({ isActive }) => classNames({ on: isActive })}
        >
          {t('나의 예약현황')}
        </NavLink>
        <NavLink
          to="/community"
          className={({ isActive }) => classNames({ on: isActive })}
        >
          {t('커뮤니티')}
        </NavLink>
      </div>
    </MenuBox>
  );
};

export default React.memo(MainMenu);
