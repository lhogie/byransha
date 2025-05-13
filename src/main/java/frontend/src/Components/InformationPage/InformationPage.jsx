import {useParams} from 'react-router';
import './InformationPage.css';
import React, {useEffect, useState} from 'react';
import {useTitle} from "../../global/useTitle";
import {View} from "../Common/View.jsx";
import {IconButton} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import CodeIcon from '@mui/icons-material/Code';
import {useNavigate} from "react-router";

const InformationPage = () => {
    const {viewId} = useParams();

    const navigate = useNavigate();

    useTitle(`Information for View ${viewId}`);

    return (
        <div className="information-page">
            <h1>Content:</h1>
            <View viewId={viewId} />
            <IconButton className="close-button" onClick={() => {navigate("/home")}} aria-label="close">
                <CloseIcon />
            </IconButton>
        </div>
    );
};
export default InformationPage;