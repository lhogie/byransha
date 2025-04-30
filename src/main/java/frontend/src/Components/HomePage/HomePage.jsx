import React, { useState } from 'react';
import './HomePage.css';
import { useNavigate } from "react-router";
import { Box, Button, Card, CardContent, CircularProgress, Typography, Checkbox, ListItemText, Menu, MenuItem } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import { useTitle } from "../../global/useTitle";
import { useApiData } from '../../hooks/useApiData';
import { View } from "../Common/View.jsx";
import { DragDropContext, Droppable, Draggable } from '@hello-pangea/dnd';

const HomePage = () => {
    const navigate = useNavigate();
    const { data: apiResponse, isLoading } = useApiData('');
    useTitle("Home");

    const [viewConfigs, setViewConfigs] = useState([]);
    const [columns, setColumns] = useState(2);
    const [selectMenuAnchor, setSelectMenuAnchor] = useState(null);
    const [selectedViewEndpoints, setSelectedViewEndpoints] = useState([]);
    const [showTechnicalViews, setShowTechnicalViews] = useState(false);


    React.useEffect(() => {
        if (apiResponse?.data?.results) {
            const allViews = apiResponse.data.results;
            setViewConfigs(allViews);

            const initiallyVisibleViews = allViews
                .filter(view => showTechnicalViews || view.response_type !== 'technical')
                .map(view => view.endpoint);

            setSelectedViewEndpoints(prevSelected => {
                const currentVisibleSet = new Set(initiallyVisibleViews);
                const newSelected = prevSelected.filter(endpoint => currentVisibleSet.has(endpoint));
                initiallyVisibleViews.forEach(endpoint => {
                    if (!newSelected.includes(endpoint)) {
                        newSelected.push(endpoint);
                    }
                });
                return newSelected;
            });
        }
    }, [apiResponse, showTechnicalViews]);

    const displayedViews = React.useMemo(() => {
        const selectedSet = new Set(selectedViewEndpoints);
        return viewConfigs.filter(view => selectedSet.has(view.endpoint));
    }, [viewConfigs, selectedViewEndpoints]);

    if (isLoading) {
        return (
            <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh", bgcolor: '#2e3b55' }}>
                <CircularProgress sx={{ color: '#1e88e5' }} />
            </Box>
        );
    }

    if (!apiResponse || !apiResponse.data || !apiResponse.data.results) {
        return (
            <Box sx={{ bgcolor: '#fff3e0', p: 2, borderRadius: 2, color: '#ef6c00', textAlign: 'center' }}>
                Error: Could not load view configurations.
            </Box>
        );
    }

    const handleSelectMenuOpen = (event) => setSelectMenuAnchor(event.currentTarget);
    const handleSelectMenuClose = () => setSelectMenuAnchor(null);

    const handleViewToggle = (endpoint) => {
        setSelectedViewEndpoints((prev) =>
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

        setViewConfigs(currentConfigs => {
            const items = Array.from(currentConfigs);
            const sourceItemEndpoint = displayedViews[result.source.index].endpoint;
            const sourceIndexInFullList = items.findIndex(item => item.endpoint === sourceItemEndpoint);

            if (sourceIndexInFullList === -1) return items;

            const [reorderedItem] = items.splice(sourceIndexInFullList, 1);

            let destinationIndexInFullList = -1;
            if (result.destination.index === 0) {
                const firstVisibleEndpoint = displayedViews[0].endpoint;
                destinationIndexInFullList = items.findIndex(item => item.endpoint === firstVisibleEndpoint);
                if (destinationIndexInFullList === -1) destinationIndexInFullList = 0;
            } else {
                const itemBeforeDestinationEndpoint = displayedViews[result.destination.index - 1].endpoint;
                const indexBeforeInFullList = items.findIndex(item => item.endpoint === itemBeforeDestinationEndpoint);
                if (indexBeforeInFullList !== -1) {
                    destinationIndexInFullList = indexBeforeInFullList + 1;
                } else {
                    const destinationItemEndpoint = displayedViews[result.destination.index].endpoint;
                    destinationIndexInFullList = items.findIndex(item => item.endpoint === destinationItemEndpoint);
                    if (destinationIndexInFullList === -1) destinationIndexInFullList = items.length;
                }
            }

            items.splice(destinationIndexInFullList, 0, reorderedItem);
            return items;
        });
    };


    const incrementColumns = () => setColumns((prev) => Math.min(prev + 1, 20));
    const decrementColumns = () => setColumns((prev) => Math.max(prev - 1, 1));

    const isSpecialView = (view) => {
        const specialViewIds = ['char_example_xy', 'bnode_in_outs_nivo_view', 'graph_nivo_view'];
        const specialContentTypes = ['image/svg', 'image/svg+xml', 'image/png', 'image/jsondot', 'text/dot'];
        const isDistribution = view.endpoint.endsWith('_distribution');
        const hasResult = !!view.result;

        return (
            specialViewIds.includes(view.endpoint) ||
            isDistribution ||
            (hasResult && specialContentTypes.includes(view.result.contentType)) ||
            (!hasResult && specialContentTypes.some(type => view.endpoint.includes(type.replace(/[\/+]/g, '_'))))
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
                            Select Views
                        </Button>
                        <Menu
                            anchorEl={selectMenuAnchor}
                            open={Boolean(selectMenuAnchor)}
                            onClose={handleSelectMenuClose}
                            PaperProps={{ sx: { maxHeight: 300, overflowY: 'auto', width: { xs: 200, sm: 250 } } }}
                        >
                            {viewConfigs
                                .filter(view => showTechnicalViews || view.response_type !== 'technical')
                                .map((view) => (
                                    <MenuItem
                                        key={view.endpoint}
                                        onClick={() => handleViewToggle(view.endpoint)}
                                        sx={{ fontSize: '14px' }}
                                    >
                                        <Checkbox checked={selectedViewEndpoints.includes(view.endpoint)} size="small" />
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
                            {showTechnicalViews ? 'Hide Technical Views' : 'Show Technical Views'}
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
                            disabled={columns === 20}
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
                            {displayedViews
                                .map((view, index) => (
                                    <Draggable key={view.endpoint} draggableId={view.endpoint} index={index}>
                                        {(provided, snapshot) => (
                                            <Box
                                                sx={{
                                                    width: {
                                                        xs: '100%',
                                                        sm: isSpecialView(view) && columns >= 3 ? '100%' : `calc(${100 / Math.min(columns, 2)}% - 16px)`,
                                                        md: isSpecialView(view) && columns >= 3 ? '100%' : `calc(${100 / columns}% - 32px)`,
                                                    },
                                                    flexBasis: {
                                                        sm: isSpecialView(view) && columns >= 3 ? '100%' : 'auto',
                                                        md: isSpecialView(view) && columns >= 3 ? '100%' : 'auto',
                                                    },
                                                    opacity: snapshot.isDragging ? 0.8 : 1,
                                                }}
                                                ref={provided.innerRef}
                                                {...provided.draggableProps}
                                                {...provided.dragHandleProps}
                                            >
                                                <Card
                                                    sx={{
                                                        cursor: 'grab',
                                                        aspectRatio: isSpecialView(view) ? '4 / 3' : '1',
                                                        border: '1px solid #e0e0e0',
                                                        borderRadius: 2,
                                                        display: 'flex',
                                                        flexDirection: 'column',
                                                        bgcolor: '#ffffff',
                                                    }}
                                                    onClick={(e) => {
                                                        if (e.defaultPrevented) return;
                                                        navigate(`/information/${view.endpoint.replaceAll(' ', '_')}`);
                                                    }}
                                                >
                                                    <CardContent
                                                        sx={{
                                                            padding: { xs: '12px', sm: '16px' },
                                                            height: '100%',
                                                            display: 'flex',
                                                            flexDirection: 'column',
                                                            overflow: 'hidden',
                                                            bgcolor: '#ffffff',
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
                                                                fontSize: { xs: '0.75rem', sm: '0.875rem' },
                                                            }}
                                                        >
                                                            {view.error ? view.error : <View viewId={view.endpoint.replaceAll(' ', '_')} />}
                                                        </Typography>
                                                    </CardContent>
                                                </Card>
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