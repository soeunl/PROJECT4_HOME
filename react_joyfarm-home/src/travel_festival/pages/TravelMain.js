import React from "react";
import { Helmet } from "react-helmet-async";
import { useTranslation } from "react-i18next";
import styled from "styled-components";


const OuterBox = styled.div`
  margin-bottom: 150px;
`;

const TravelMain = () => {
    const { t } = useTranslation();
    return (
        <>
            <Helmet>
                <title>{t('조이팜의 추천여행')}</title>
            </Helmet>
            <OuterBox className="layout-width">
                <h1>여행, 축제 페이지</h1>
            </OuterBox>
        </>      
    );
}

export default React.memo(TravelMain);