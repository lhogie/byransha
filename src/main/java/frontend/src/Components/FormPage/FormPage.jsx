import { useParams } from 'react-router';
import './FormPage.css';
import React, {useEffect, useState, useCallback, useRef, useMemo} from 'react';
import {
    Box,
    Button,
    CircularProgress,
    Container,
    IconButton,
    Paper,
    Typography
} from '@mui/material';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import { useNavigate } from "react-router";
import {useApiData, useApiMutation} from "../../hooks/useApiData.js";
import NestedFields from './FormComponents/NestedFields';
import {createKey, shortenAndFormatLabel} from "../../utils/utils.js";

const FormPage = () => {
    const { classForm } = useParams();
    const navigate = useNavigate();
    const { data: rawApiData, isLoading: loading, error, refetch } = useApiData(`class_attribute_field`);
    const rootId = rawApiData?.data?.node_id || null;
    const pageName = classForm.split('.').pop()

    if (loading) return <Box display="flex" justifyContent="center" alignItems="center" height="100vh"><CircularProgress /></Box>
    if (error) return <Typography color="error">Error loading form fields: {error.message || 'Unknown error'}</Typography>;

    return (
        <Container className="form-page" component={Paper} elevation={3} sx={{ p: 3, position: 'relative' }}>
            <IconButton
                className="close-button"
                onClick={() => navigate(-1)}
                aria-label="close"
                sx={{ position: 'absolute', top: 16, right: 16 }}
            >
                <CloseRoundedIcon />
            </IconButton>

            <Typography variant="h4" component="h1" gutterBottom>
                Form for: {shortenAndFormatLabel(pageName)}
            </Typography>

            <NestedFields
                fieldKey={createKey(rootId, pageName)}
                rootId={rootId}
                isRoot
                field={{
                    id: rootId,
                }}
            />
        </Container>
    );
};

export default FormPage;
