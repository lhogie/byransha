import { useParams } from 'react-router';
import './FormPage.css';
import React, {useEffect, useState, useCallback, useRef, useMemo} from 'react';
import {
    Box,
    Button,
    CircularProgress,
    Container,
    IconButton,
    Paper, SpeedDial, SpeedDialAction, SpeedDialIcon,
    Typography
} from '@mui/material';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import { useNavigate } from "react-router";
import {useApiData, useApiMutation} from "../../hooks/useApiData.js";
import NestedFields from './FormComponents/NestedFields';
import {createKey, shortenAndFormatLabel} from "../../utils/utils.js";
import DownloadIcon from "@mui/icons-material/Download";
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from "@mui/icons-material/Add";
import {saveAs} from 'file-saver'

const FormPage = () => {
    const { classForm } = useParams();
    const navigate = useNavigate();
    const { data: rawApiData, isLoading: loading, error, refetch } = useApiData(`class_attribute_field`);
    const rootId = rawApiData?.data?.node_id || null;
    const pageName = classForm.split('.').pop()
    const exportCSVMutation = useApiMutation('export_csv');
    const ref = useRef();

    if (loading) return <Box display="flex" justifyContent="center" alignItems="center" height="100vh"><CircularProgress /></Box>
    if (error) return <Typography color="error">Error loading form fields: {error.message || 'Unknown error'}</Typography>;

    return (
        <>
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
                    ref={ref}
                    fieldKey={createKey(rootId, pageName)}
                    rootId={rootId}
                    isRoot
                    field={{
                        id: rootId,
                    }}
                />

            </Container>
            <SpeedDial
                ariaLabel="SpeedDial playground example"
                sx={{ position: 'absolute', bottom: 16, right: 16 }}
                hidden={false}
                icon={<SpeedDialIcon />}
            >
                {[
                    {
                        icon: <DownloadIcon />,
                        name: 'Exporter',
                        onClick: async () => {
                            const response = await exportCSVMutation.mutateAsync({
                                node_id: rootId,
                                includeAllFields: true,
                                maxDepth: 4
                            });

                            const blob = new Blob([response?.data?.results?.[0]?.result?.data], { type: 'text/csv' });
                            saveAs(blob, `${pageName}.csv`);
                        }
                    },
                    {
                        icon: <AddIcon />,
                        name: 'Ajouter une nouvelle entrée'
                    },
                    {
                        icon: <DeleteIcon />,
                        name: 'Supprimer',
                    },
                ].map((action) => (
                    <SpeedDialAction
                        key={action.name}
                        icon={action.icon}
                        tooltipTitle={action.name}
                        onClick={action.onClick}
                    />
                ))}
            </SpeedDial>
        </>
    );
};

export default FormPage;
