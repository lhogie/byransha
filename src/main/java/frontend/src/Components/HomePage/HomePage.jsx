import React, { useState } from 'react';
import './HomePage.css';
import { useNavigate } from "react-router-dom";
import { Box, Button, Card, CardContent, CircularProgress, Grid2, Typography, Select, MenuItem, Menu } from '@mui/material';
import { useTitle } from "../../global/useTitle";
import { useApiData } from '../../hooks/useApiData';
import { View } from "../Common/View.jsx";
import { DragDropContext, Droppable, Draggable } from '@hello-pangea/dnd';

const HomePage = () => {
    const navigate = useNavigate();
    const { data, isLoading } = useApiData('');
    useTitle("Home");

    const [views, setViews] = useState([]);
    const [columns, setColumns] = useState(2);
    const [menuAnchor, setMenuAnchor] = useState(null);

    React.useEffect(() => {
        if (data?.data?.results) {
            setViews(data.data.results);
        }
    }, [data]);

    if (isLoading) {
        return (
            <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh", bgcolor: '#ffffff' }}>
                <CircularProgress sx={{ color: '#1e88e5' }} />
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

    const handleMenuOpen = (event) => setMenuAnchor(event.currentTarget);
    const handleMenuClose = () => setMenuAnchor(null);

    const handleDeleteView = (endpoint) => {
        setViews(views.filter((view) => view.endpoint !== endpoint));
        handleMenuClose();
    };

    const onDragEnd = (result) => {
        if (!result.destination) return;
        const reorderedViews = Array.from(views);
        const [movedView] = reorderedViews.splice(result.source.index, 1);
        reorderedViews.splice(result.destination.index, 0, movedView);
        setViews(reorderedViews);
    };

    const getCardContentBackgroundColor = (view) => {
        switch (view.response_type) {
            case "business": return '#d1e3f6';
            case "development": return '#e0f2e9';
            case "technical": return '#f9e1cc';
            default: return '#ffffff';
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
            className="home-page"
        >
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Button
                            variant="outlined"
                            onClick={handleMenuOpen}
                            sx={{ borderColor: '#306DAD', color: '#306DAD', '&:hover': { borderColor: '#255a8c', bgcolor: '#f5f7ff' } }}
                        >
                            Delete Grid
                        </Button>
                        <Menu
                            anchorEl={menuAnchor}
                            open={Boolean(menuAnchor)}
                            onClose={handleMenuClose}
                            PaperProps={{ sx: { maxHeight: 300, overflowY: 'auto' } }}
                        >
                            {views.map((view) => (
                                <MenuItem
                                    key={view.endpoint}
                                    onClick={() => handleDeleteView(view.endpoint)}
                                    sx={{ fontSize: '14px', color: '#424242', '&:hover': { bgcolor: '#e8eaf6' } }}
                                >
                                    {view.pretty_name}
                                </MenuItem>
                            ))}
                        </Menu>
                    </Box>
                    <Select
                        value={columns}
                        onChange={(e) => setColumns(e.target.value)}
                        sx={{
                            minWidth: 80,
                            '& .MuiSelect-select': { padding: '6px 12px' },
                            '& .MuiOutlinedInput-notchedOutline': { borderColor: '#306DAD' },
                        }}
                    >
                        <MenuItem value={1}>1</MenuItem>
                        <MenuItem value={2}>2</MenuItem>
                        <MenuItem value={3}>3</MenuItem>
                        <MenuItem value={4}>4</MenuItem>
                    </Select>
                </Box>
            </Box>
            <DragDropContext onDragEnd={onDragEnd}>
                <Droppable droppableId="views" direction="horizontal">
                    {(provided, snapshot) => (
                        <Grid2
                            container
                            spacing={4}
                            {...provided.droppableProps}
                            ref={provided.innerRef}
                            sx={{
                                display: 'flex',
                                flexWrap: 'wrap',
                                bgcolor: snapshot.isDraggingOver ? '#e8eaf6' : '#ffffff',
                            }}
                        >
                            {views.map((view, index) => (
                                <Draggable key={view.endpoint} draggableId={view.endpoint} index={index}>
                                    {(provided, snapshot) => (
                                        <Grid2
                                            size={{ xs: 12, sm: 12 / columns }}
                                            ref={provided.innerRef}
                                            {...provided.draggableProps}
                                            {...provided.dragHandleProps}
                                            sx={{
                                                opacity: snapshot.isDragging ? 0.8 : 1,
                                            }}
                                        >
                                            <Card
                                                sx={{
                                                    cursor: 'grab',
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
                                                    bgcolor: snapshot.isDragging ? '#f5f7ff' : '#ffffff',
                                                }}
                                                onClick={(e) => {
                                                    if (e.defaultPrevented) return;
                                                    navigate(`/information/${view.endpoint.replaceAll(' ', '_')}`);
                                                }}
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
                                                        variant="subtitle1"
                                                        sx={{
                                                            marginBottom: '16px',
                                                            color: '#424242',
                                                            fontWeight: '500',
                                                            flexShrink: 0,
                                                        }}
                                                    >
                                                        {view.what_is_this}
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
                                                            wordBreak: 'break-word',
                                                            overflowWrap: 'break-word',
                                                            whiteSpace: 'pre-wrap',
                                                            maxWidth: '100%',
                                                        }}
                                                    >
                                                        {view.error ? view.error : <View viewId={view.endpoint.replaceAll(' ', '_')} />}
                                                    </Typography>
                                                </CardContent>
                                            </Card>
                                        </Grid2>
                                    )}
                                </Draggable>
                            ))}
                            {provided.placeholder}
                        </Grid2>
                    )}
                </Droppable>
            </DragDropContext>
        </Box>
    );
};
export default HomePage;