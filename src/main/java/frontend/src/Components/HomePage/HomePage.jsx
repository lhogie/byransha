import React, { useState, memo } from 'react';
import './HomePage.css';
import { useNavigate } from "react-router";
import { Box, Button, Card, CardContent, CircularProgress, Typography, Checkbox, ListItemText, Menu, MenuItem } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import { useTitle } from "../../global/useTitle";
import { useApiData } from '../../hooks/useApiData';
import { View } from "../Common/View.jsx";
import { DragDropContext, Droppable, Draggable } from '@hello-pangea/dnd';

const ViewCard = memo(({ view, onClick, dragHandleProps }) => {
    return <Card
        sx={{
            cursor: 'pointer',
            aspectRatio: '1',
            border: '1px solid #e0e0e0',
            borderRadius: 2,
            display: 'flex',
            flexDirection: 'column',
            bgcolor: view.response_type === 'technical' ? '#fff9c4' : '#ffffff',
        }}
        onClick={onClick}
    >
        <Box 
            {...dragHandleProps}
            sx={{
                height: '40px',
                width: '100%',
                bgcolor: view.response_type === 'technical' ? '#fff8b0' : '#f5f5f5',
                borderBottom: '1px solid #e0e0e0',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'grab',
            }}
        >
            <Typography variant="caption" sx={{ color: '#757575' }}>
                Drag here
            </Typography>
        </Box>
        <CardContent
            sx={{
                padding: { xs: '12px', sm: '16px' },
                height: 'calc(100% - 40px)',
                display: 'flex',
                flexDirection: 'column',
                overflow: 'hidden',
                bgcolor: view.response_type === 'technical' ? '#fff9c4' : '#ffffff',
            }}
        >
            <Typography
                variant="h5"
                sx={{
                    marginBottom: '8px',
                    flexShrink: 0,
                    color: '#283593',
                    fontWeight: '600',
                    fontSize: { xs: '1rem', sm: '1.25rem' },
                }}
            >
                {view.pretty_name.replace(/(?:^|\s)\S/g, (match) => match.toUpperCase())}
            </Typography>
            <Typography
                variant="subtitle1"
                sx={{
                    marginBottom: '8px',
                    color: '#424242',
                    fontWeight: '500',
                    flexShrink: 0,
                    fontSize: { xs: '0.8rem', sm: '0.875rem' },
                }}
            >
                {view.what_is_this}
            </Typography>
            <Box
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
                    fontSize: { xs: '0.75rem', sm: '0.875rem' },
                }}
            >
                {view.error ? view.error : (
                    <React.Suspense fallback={<div>Loading view...</div>}>
                        <View
                            viewId={view.endpoint.replaceAll(' ', '_')}
                            sx={{
                                bgcolor: view.response_type === 'technical' ? '#fff9c4' : '#ffffff',
                                width: '100%',
                            }}
                        />
                    </React.Suspense>
                )}
            </Box>
        </CardContent>
    </Card>
});

const HomePage = () => {
    const navigate = useNavigate();
    const { data, isLoading } = useApiData('');
    useTitle("Home");

    const [views, setViews] = useState([]);
    const [columns, setColumns] = useState(2);
    const [selectMenuAnchor, setSelectMenuAnchor] = useState(null);
    const [selectedViews, setSelectedViews] = useState([]);
    const [showTechnicalViews, setShowTechnicalViews] = useState(false);

    const getAutoColumnCount = () => {
        const width = window.innerWidth;

        if (width < 900) return 1;
        else if (width < 1600) return 2;
        else if (width < 2100) return 3;
        return 4;
    };

    React.useEffect(() => {
        if (data?.data?.results) {
            const filteredViews = showTechnicalViews
                ? data.data.results
                : data.data.results.filter(view => view.response_type !== 'technical');
            setViews(filteredViews);
            setSelectedViews(prev => {
                const newSelected = prev.filter(endpoint =>
                    filteredViews.some(view => view.endpoint === endpoint)
                );
                filteredViews.forEach(view => {
                    if (!newSelected.includes(view.endpoint)) {
                        newSelected.push(view.endpoint);
                    }
                });
                return newSelected;
            });
        }
    }, [data, showTechnicalViews]);

    React.useEffect(() => {
        const handleResize = () => setColumns(getAutoColumnCount());

        window.addEventListener('resize', handleResize);
        handleResize(); // Initialize on mount

        return () => window.removeEventListener('resize', handleResize);
    }, []);


    if (isLoading) {
        return (
            <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh", bgcolor: '#2e3b55' }}>
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

    const handleSelectMenuOpen = (event) => setSelectMenuAnchor(event.currentTarget);
    const handleSelectMenuClose = () => setSelectMenuAnchor(null);

    const handleViewToggle = (endpoint) => {
        setSelectedViews((prev) =>
            prev.includes(endpoint)
                ? prev.filter((id) => id !== endpoint)
                : [...prev, endpoint]
        );
    };

    const handleTechnicalViewsToggle = () => {
        setShowTechnicalViews(prev => !prev);
    };

    const onDragEnd = (result) => {
        if (!result.destination) return;
        const reorderedViews = Array.from(views);
        const [movedView] = reorderedViews.splice(result.source.index, 1);
        reorderedViews.splice(result.destination.index, 0, movedView);
        setViews(reorderedViews);
    };

    const incrementColumns = () => setColumns((prev) => Math.min(prev + 1, views.length));
    const decrementColumns = () => setColumns((prev) => Math.max(prev - 1, 1));

    const isSpecialView = (view) => {
        const specialViewIds = ['char_example_xy', 'bnode_in_outs_nivo_view'];
        const specialContentTypes = ['image/svg', 'image/svg+xml', 'image/png', 'image/jsondot'];
        return (
            specialViewIds.includes(view.endpoint) ||
            view.endpoint.endsWith('_distribution') ||
            specialContentTypes.some(type => view.endpoint.includes(type.replace('/', '_')))
        );
    };

    return (
        <Box
            sx={{
                padding: { xs: '8px', sm: '16px', md: '40px' },
                maxWidth: '100%',
                margin: '0 auto',
                bgcolor: '#2e3b55',
                minHeight: '100vh',
                zIndex: 1,
            }}
            className="home-page"
        >
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: { xs: 'column', sm: 'row' },
                    justifyContent: 'space-between',
                    alignItems: { xs: 'flex-start', sm: 'center' },
                    mb: { xs: 2, sm: 4 },
                    gap: { xs: 2, sm: 0 },
                }}
            >
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: { xs: 'column', sm: 'row' },
                        gap: { xs: 1, sm: 2 },
                        alignItems: { xs: 'flex-start', sm: 'center' },
                        width: { xs: '100%', sm: 'auto' },
                    }}
                >
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Button
                            variant="outlined"
                            onClick={handleSelectMenuOpen}
                            sx={{
                                minWidth: { xs: 36, sm: 40 },
                                borderWidth: '2px',
                                borderColor: '#90caf9',
                                color: '#90caf9',
                                fontSize: { xs: '0.75rem', sm: '0.875rem' },
                                padding: { xs: '4px 8px', sm: '6px 12px' },
                                '&:hover': {
                                    borderColor: '#42a5f5',
                                    bgcolor: '#37474f',
                                },
                            }}
                        >
                            Auto
                        </Button>
                        <Menu
                            anchorEl={selectMenuAnchor}
                            open={Boolean(selectMenuAnchor)}
                            onClose={handleSelectMenuClose}
                            PaperProps={{ sx: { maxHeight: 300, overflowY: 'auto', width: { xs: 200, sm: 250 } } }}
                        >
                            {(showTechnicalViews ? data.data.results : data.data.results.filter(view => view.response_type !== 'technical')).map((view) => (
                                <MenuItem
                                    key={view.endpoint}
                                    onClick={() => handleViewToggle(view.endpoint)}
                                    sx={{ fontSize: '14px', color: '#424242', '&:hover': { bgcolor: '#e8eaf6' } }}
                                >
                                    <Checkbox
                                        checked={selectedViews.includes(view.endpoint)}
                                        sx={{ color: '#90caf9', '&.Mui-checked': { color: '#90caf9' } }}
                                    />
                                    <ListItemText primary={view.pretty_name} />
                                </MenuItem>
                            ))}
                        </Menu>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Checkbox
                            checked={showTechnicalViews}
                            onChange={handleTechnicalViewsToggle}
                            sx={{
                                color: '#90caf9',
                                '&.Mui-checked': { color: '#90caf9' },
                                padding: { xs: '4px', sm: '6px' },
                            }}
                        />
                        <Typography sx={{ color: '#90caf9', fontSize: { xs: '0.75rem', sm: '0.875rem' } }}>
                            Show Technical Views
                        </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Button
                            variant="outlined"
                            onClick={decrementColumns}
                            disabled={columns === 1}
                            sx={{
                                minWidth: { xs: 36, sm: 40 },
                                borderWidth: '2px',
                                borderColor: '#90caf9',
                                color: '#90caf9',
                                fontSize: { xs: '0.75rem', sm: '0.875rem' },
                                padding: { xs: '4px', sm: '6px' },
                                '&:hover': {
                                    borderColor: '#42a5f5',
                                    bgcolor: '#37474f',
                                },
                            }}
                        >
                            <RemoveIcon fontSize="small" />
                        </Button>
                        <Typography sx={{ color: '#ffffff', fontWeight: 'bold', fontSize: { xs: '0.875rem', sm: '1rem' } }}>
                            {columns}
                        </Typography>
                        <Button
                            variant="outlined"
                            onClick={incrementColumns}
                            disabled={columns === views.length}
                            sx={{
                                minWidth: { xs: 36, sm: 40 },
                                borderWidth: '2px',
                                borderColor: '#90caf9',
                                color: '#90caf9',
                                fontSize: { xs: '0.75rem', sm: '0.875rem' },
                                padding: { xs: '4px', sm: '6px' },
                                '&:hover': {
                                    borderColor: '#42a5f5',
                                    bgcolor: '#37474f',
                                },
                            }}
                        >
                            <AddIcon fontSize="small" />
                        </Button>
                    </Box>
                </Box>
            </Box>
            <DragDropContext onDragEnd={onDragEnd}>
                <Droppable droppableId="views">
                    {(provided, snapshot) => (
                        <Box
                            sx={{
                                display: 'flex',
                                flexWrap: 'wrap',
                                gap: { xs: 2, sm: 4 },
                                bgcolor: snapshot.isDraggingOver ? '#37474f' : '#2e3b55',
                            }}
                            {...provided.droppableProps}
                            ref={provided.innerRef}
                        >
                            {views
                                .filter((view) => selectedViews.includes(view.endpoint))
                                .map((view, index) => (
                                    <Draggable 
                                        key={view.endpoint} 
                                        draggableId={view.endpoint} 
                                        index={index}
                                    >
                                        {(provided, snapshot) => (
                                            <Box
                                                sx={{
                                                    width: {
                                                        xs: '100%',
                                                        sm: `calc(${100 / Math.min(columns, 2)}% - 16px)`,
                                                        md: `calc(${100 / columns}% - 32px)`,
                                                    },
                                                    opacity: snapshot.isDragging ? 0.8 : 1,
                                                }}
                                                ref={provided.innerRef}
                                                {...provided.draggableProps}
                                            >
                                                <ViewCard 
                                                    view={view}  
                                                    onClick={(e) => {
                                                        if (e.defaultPrevented) return;
                                                        navigate(`/information/${view.endpoint.replaceAll(' ', '_')}`);
                                                    }} 
                                                    dragHandleProps={provided.dragHandleProps}
                                                />
                                            </Box>
                                        )}
                                    </Draggable>
                                ))}
                            {provided.placeholder}
                        </Box>
                    )}
                </Droppable>
            </DragDropContext>
        </Box>
    );
};
export default HomePage;
