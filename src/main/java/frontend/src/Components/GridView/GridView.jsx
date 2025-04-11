import { useNavigate } from 'react-router-dom';
import { Box, Button, Card, CardContent, CircularProgress, Grid2, Typography } from '@mui/material';
import React, { useCallback } from 'react';
import { View } from "../Common/View.jsx";
import { useTitle } from "../../global/useTitle.jsx";
import { useApiData, useApiMutation } from '../../hooks/useApiData';
import {useQueryClient} from "@tanstack/react-query";

const GridView = () => {
    const navigate = useNavigate();
    useTitle("Views");
    const { data, isLoading, error, refetch } = useApiData(''); // Adjust endpoint as needed
    const { data: navData, isLoading: navIsLoading, error: navIsError, refetch: refetchNav } = useApiData('bnode_nav2');
    const queryClient = useQueryClient()

    const jumpMutation = useApiMutation('jump', {
        onSuccess: async () => {
            await queryClient.invalidateQueries()
        },
    });

    const jumpToNode = useCallback((nodeId) => {
        jumpMutation.mutate(`target=${nodeId}`);
    }, []);

    if (isLoading) {
        return (
            <Box
                sx={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    height: "100vh",
                    bgcolor: '#ffffff',
                }}
            >
                <CircularProgress sx={{ color: '#1e88e5' }} />
            </Box>
        );
    }

    if (error) {
        return (
            <Box sx={{ bgcolor: '#ffebee', p: 2, borderRadius: 2, color: '#d32f2f', textAlign: 'center' }}>
                Error: {error.message}
            </Box>
        );
    }

    if (!data || !data.data || !data.data.results) {
        return (
            <Box sx={{ bgcolor: '#fff3e0', p: 2, borderRadius: 2, color: '#ef6c00', textAlign: 'center' }}>
                Error: Data is null.
            </Box>
        );
    }

    const views = data.data.results || [];

    // Function to determine card content background color based on response_type
    const getCardContentBackgroundColor = (view) => {
        switch (view.response_type) {
            case "business":
                return '#d1e3f6'; //blue for business
            case "development":
                return '#e0f2e9'; //teal for development
            case "technical":
                return '#f9e1cc'; //orange for technical
            default:
                return '#ffffff';
        }
    };

    return (
        <Box
            sx={{
                padding: { xs: '10px', md: '40px' },
                maxWidth: '100%',
                margin: '0 auto',
                bgcolor: '#ffffff',
                minHeight: '100vh',
            }}
        >
            {navIsLoading && <CircularProgress sx={{ color: '#1e88e5', display: 'block', mx: 'auto' }} />}

            <Grid2 container spacing={4}>
                {views.map((view, index) => (
                    <Grid2 size={{ xs: 12, sm: 6 }} key={index}>
                        <Card
                            sx={{
                                cursor: 'pointer',
                                aspectRatio: '1',
                                transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                                border: '1px solid #e0e0e0',
                                borderRadius: 2,
                                '&:hover': {
                                    transform: 'translateY(-6px)',
                                    boxShadow: '0 8px 24px rgba(63, 81, 181, 0.2)',
                                    borderColor: '#3f51b5',
                                },
                                display: 'flex',
                                flexDirection: 'column',
                            }}
                            onClick={() => navigate(`/information/${view.endpoint.replaceAll(' ', '_')}`)}
                        >
                            <CardContent
                                sx={{
                                    padding: '24px',
                                    height: '100%',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    overflow: 'hidden',
                                    bgcolor: getCardContentBackgroundColor(view),
                                }}
                            >
                                <Typography
                                    variant="h6"
                                    sx={{
                                        marginBottom: '16px',
                                        flexShrink: 0,
                                        color: '#283593',
                                        fontWeight: '600',
                                    }}
                                >
                                    {view.pretty_name.replace(/(?:^|\s)\S/g, (match) => match.toUpperCase())}
                                </Typography>
                                <Typography
                                    variant="body2"
                                    sx={{
                                        flex: 1,
                                        overflow: 'auto',
                                        color: '#424242',
                                        msOverflowStyle: 'none',
                                        scrollbarWidth: 'thin',
                                        scrollbarColor: '#3f51b5 #e8eaf6',
                                        '&::-webkit-scrollbar': { width: '6px' },
                                        '&::-webkit-scrollbar-thumb': {
                                            bgcolor: '#3f51b5',
                                            borderRadius: '3px',
                                        },
                                        '&::-webkit-scrollbar-track': { bgcolor: '#e8eaf6' },
                                        wordBreak: 'break-word', // Break long words
                                        overflowWrap: 'break-word', // Ensure wrapping
                                        whiteSpace: 'pre-wrap', // Preserve whitespace and wrap
                                        maxWidth: '100%', // Constrain to card width
                                    }}
                                >
                                    {view.error ? view.error : <View viewId={view.endpoint.replaceAll(' ', '_')} />}
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid2>
                ))}
            </Grid2>
        </Box>
    );
};

export default GridView;