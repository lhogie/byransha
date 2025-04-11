import {useParams} from 'react-router';
import './InformationPage.css';
import React from 'react';
import {useTitle} from "../../global/useTitle";
import {View} from "../Common/View.jsx";

const InformationPage = () => {
    const {viewId} = useParams();

    useTitle(`Information for View ${viewId}`);

    return <div className="information-page">
        <h1>Information for View {viewId}</h1>
        <div>
            <h2>Content:</h2>
            <View viewId={viewId}/>
        </div>
    </div>
};

export default InformationPage;