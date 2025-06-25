import React, { useState, memo } from 'react';
import './HomePage.css';
import { useNavigate } from "react-router";
import { Box, Button, Card, CardContent, CircularProgress, Typography, Checkbox, ListItemText, Menu, MenuItem } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import CloseIcon from '@mui/icons-material/Close';
import Expand from '@mui/icons-material/AspectRatio';
import { useTitle } from "../../global/useTitle";
import { useApiData, useApiMutation  } from '../../hooks/useApiData';
import { View } from "../Common/View.jsx";
import DragIndicatorIcon from '@mui/icons-material/DragIndicator';
import { DragDropContext, Droppable, Draggable } from '@hello-pangea/dnd';
import { ChromePicker } from 'react-color';

const ViewCard = memo(({ view, onClick, dragHandleProps, handleViewToggle }) => {

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

            <Typography className="DragHere" variant="caption" sx={{ color: '#757575' }}>
                {`${view.endpoint.replace(/_/g, ' ').replace(/(?:^|\s)\S/g, (match) => match.toUpperCase())} - ${view.what_is_this}`                 }
            </Typography>
            <button className="expand-card" onClick={onClick} aria-label="expand"> <Expand /> </button>
            <button className= "erased-card" onClick={(e) => {handleViewToggle(view.endpoint)}}> <CloseIcon /> </button>
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
    const [showTechnicalViews, setShowTechnicalViews] = useState(() => {
            const saved = localStorage.getItem('showTechnicalViews');
            return saved ? JSON.parse(saved) : false;
    });

    const [showColorPicker, setShowColorPicker] = useState(false);
    const [pickerColor, setPickerColor] = useState('#ffffff');
    const visibleViews = views.filter(view => selectedViews.includes(view.endpoint));
    const rowColumns = Math.min(columns, visibleViews.length);

    const jumpToId = useApiMutation('jump');

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
            setSelectedViews(() => {
                const saved = JSON.parse(localStorage.getItem('selectedViewsSaved'));
                let newSelected;
                if (!saved || saved.length === 0) {
                    newSelected = filteredViews.map(view => view.endpoint);
                } else {
                    newSelected = saved.filter(endpoint =>
                        filteredViews.some(view => view.endpoint === endpoint)
                    );
                }
                localStorage.setItem('selectedViewsSaved', JSON.stringify(newSelected));
                return newSelected;
            });

            const savedOrder = JSON.parse(localStorage.getItem('viewOrder')) || [];
            let orderedViews = filteredViews;

            if (savedOrder.length > 0) {
                orderedViews = [...filteredViews].sort((a, b) => {
                    const indexA = savedOrder.indexOf(a.endpoint);
                    const indexB = savedOrder.indexOf(b.endpoint);
                    return (indexA === -1 ? Infinity : indexA) - (indexB === -1 ? Infinity : indexB);
                });
            }

            setViews(orderedViews);

        }
    }, [data, showTechnicalViews]);

    React.useEffect(() => {
        const handleResize = () => setColumns(getAutoColumnCount());

        window.addEventListener('resize', handleResize);
        handleResize();

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
        setSelectedViews((prev) => {
            const newSelected = prev.includes(endpoint)
                ? prev.filter((id) => id !== endpoint)
                : [...prev, endpoint];

            localStorage.setItem('selectedViewsSaved', JSON.stringify(newSelected));

            const technicalViews = views.filter(view => view.response_type === 'technical');
            const openTechnicalViews = technicalViews.filter(view => newSelected.includes(view.endpoint));
            if (openTechnicalViews.length === 0 && showTechnicalViews) {
                setShowTechnicalViews(false);
                localStorage.setItem('showTechnicalViews', JSON.stringify(false));
            }
            return newSelected;
        });
    };

    const handleTechnicalViewsToggle = () => {
        setShowTechnicalViews(prev => {
            const newValue = !prev;
            localStorage.setItem('showTechnicalViews', JSON.stringify(newValue));

            const techViews = data?.data?.results?.filter(view => view.response_type === 'technical') || [];
            const techEndpoints = techViews.map(view => view.endpoint);

            setSelectedViews(prevSelected => {
                let updated;
                if (newValue) {
                    updated = [...new Set([...prevSelected, ...techEndpoints])];
                } else {
                    updated = prevSelected.filter(endpoint => !techEndpoints.includes(endpoint));
                }
                localStorage.setItem('selectedViewsSaved', JSON.stringify(updated));
                return updated;
            });

            return newValue;
        });
    };


    const onDragEnd = (result) => {
        if (!result.destination) return;
        const reorderedViews = Array.from(views);
        const [movedView] = reorderedViews.splice(result.source.index, 1);
        reorderedViews.splice(result.destination.index, 0, movedView);
        setViews(reorderedViews);

        const order = reorderedViews.map((view) => view.endpoint);
        localStorage.setItem('viewOrder', JSON.stringify(order));
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
                            Views
                        </Button>
                        <DragDropContext
                          onDragEnd={(result) => {
                            if (!result.destination) return;
                            const reordered = Array.from(views);
                            const [moved] = reordered.splice(result.source.index, 1);
                            reordered.splice(result.destination.index, 0, moved);
                            setViews(reordered);
                            localStorage.setItem('viewOrder', JSON.stringify(reordered.map(v => v.endpoint)));
                          }}
                        >
                          <Menu
                            anchorEl={selectMenuAnchor}
                            open={Boolean(selectMenuAnchor)}
                            onClose={handleSelectMenuClose}
                            PaperProps={{
                              sx: {
                                maxHeight: 500,
                                maxWidth: 400,
                                overflowY: 'auto',
                                width: 'auto',
                                padding: 1,
                                borderRadius: '8px'
                              },
                            }}
                          >
                            <Droppable droppableId="menu-views">
                              {(provided) => (
                                <div ref={provided.innerRef} {...provided.droppableProps}>
                                  {views.map((view, index) => (
                                    <Draggable key={view.endpoint} draggableId={view.endpoint} index={index}>
                                      {(provided) => (
                                        <MenuItem
                                          ref={provided.innerRef}
                                          {...provided.draggableProps}
                                          sx={{
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'space-between',
                                            paddingRight: 1,
                                            fontSize: '14px',
                                            color: view.response_type === 'technical' ? '#283593' : '#424242',
                                            backgroundColor: view.response_type === 'technical' ? '#fff9c4' : 'transparent',
                                            borderRadius: '8px',
                                            '&:hover': {
                                              backgroundColor: view.response_type === 'technical' ? '#fff8b0' : '#e8eaf6',
                                            },
                                          }}
                                        >
                                          <Box
                                            sx={{ display: 'flex', alignItems: 'center', flexGrow: 1 , borderRadius: '18px'}}
                                            onClick={() => handleViewToggle(view.endpoint)}
                                          >
                                            <Checkbox
                                              checked={selectedViews.includes(view.endpoint)}
                                              sx={{ color: '#90caf9', '&.Mui-checked': { color: '#90caf9' }}}
                                            />
                                            <ListItemText primary={view.pretty_name} />
                                          </Box>
                                          <Box {...provided.dragHandleProps} sx={{ cursor: 'grab', ml: 1, display: 'flex', alignItems: 'center' }}>
                                            <DragIndicatorIcon fontSize="small" sx={{ color: '#90caf9' }} />
                                          </Box>
                                        </MenuItem>
                                      )}
                                    </Draggable>
                                  ))}
                                  {provided.placeholder}
                                </div>
                              )}
                            </Droppable>
                          </Menu>
                        </DragDropContext>
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
                <Button
                    variant="outlined"
                    onClick={() => navigate('/add-node')}
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
                    Add new node
                </Button>
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
                                                        md: `calc(${100 / rowColumns}% - 32px)`,
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
                                                        if(view.endpoint.endsWith('show_out')) {
                                                            jumpToId.mutate({node: view.result?.dialect.split('@')[1]});
                                                            navigate(`/add-node/form/${view.result?.dialect.split('@')[0]}`);
                                                        }
                                                        else navigate(`/information/${view.endpoint.replaceAll(' ', '_')}`);
                                                    }}
                                                    dragHandleProps={provided.dragHandleProps}
                                                    handleViewToggle={handleViewToggle}
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
