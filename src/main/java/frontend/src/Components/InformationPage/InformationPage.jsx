import {useParams} from 'react-router';
import './InformationPage.css';
import React, {useEffect, useState} from 'react';
import {useTitle} from "../../global/useTitle";
import {View} from "../Common/View.jsx";

const InformationPage = () => {
    const {viewId} = useParams();

    useTitle(`Information for View ${viewId}`);

    return (
        <div className="information-page">
            <h1>Content:</h1>
            <View viewId={viewId} />
        </div>
    );
};
export default InformationPage;