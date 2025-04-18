import {useParams} from 'react-router';
import {Box, Card, CardContent, Typography} from '@mui/material';
import React from 'react';
import './VerticalView.css';
import {View} from "../Common/View.jsx";

const VerticalView = () => {
    const {viewId} = useParams();

    return (
        <Box className="vertical-view">
            <Typography variant="h4" gutterBottom>
                View {viewId} Details
            </Typography>
            <Card className="content-card">
                <CardContent>
                    <Box className="content-container">
                        <View viewId={viewId}/>
                    </Box>
                </CardContent>
            </Card>
        </Box>
    );
};

export default VerticalView; 