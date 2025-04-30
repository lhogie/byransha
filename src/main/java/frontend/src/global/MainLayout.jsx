import { Outlet, useLocation, useNavigate, Link as RouterLink } from "react-router";
import { DashboardLayout, PageContainer } from "@toolpad/core";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import { Box, MenuItem, Select, Typography, Breadcrumbs, Link, Stack, Button } from "@mui/material";
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';
import {useApiData, useApiMutation} from '../hooks/useApiData';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import {useState, useEffect, useCallback} from "react";
import {useQueryClient} from "@tanstack/react-query";
import axios from "axios";



const MainLayout = () => {
    const navigate = useNavigate();
    const { pathname } = useLocation();
    const [viewMode, setViewMode] = useState(pathname.startsWith("/grid") ? "grid" : "default");
    const hideSidebar = pathname.startsWith("/grid");
    const [menuAnchor, setMenuAnchor] = useState(null);
    const { data, isLoading, error } = useApiData('');
    const { data: historyData, isLoading: isHistoryLoading } = useApiData('user_history');
    const queryClient = useQueryClient()
    const jumpMutation = useApiMutation('jump', {
        onSuccess: async () => {
            await queryClient.invalidateQueries()
        },
    });

    const logoutMutation = useApiMutation('logout', {
        onSuccess: async () => {
            await queryClient.invalidateQueries()
            navigate('/')
        },
    });

    const jumpToNode = useCallback((nodeId) => {
        jumpMutation.mutate(`node_id=${nodeId}`);
    }, []);

    const handleViewChange = (event) => {
        const selectedView = event.target.value;
        setViewMode(selectedView);
        navigate(selectedView === "grid" ? "/grid" : "/home");
    };

    const handleHistoryClick = (hist) => {
        jumpToNode(hist.id.toString())
    };

    const handleLogout = async () => {
        logoutMutation.mutate()
    };

    const handleMoreClick = (event) => setMenuAnchor(event.currentTarget);
    const handleMenuClose = () => setMenuAnchor(null);

    const history = historyData?.data?.results?.[0].result.data ?? [];
    const visibleHistory = history.length > 3 ? history.slice(-2) : history;
    const currentNode = history[history.length - 1];

    return (
        <Box sx={{
            '& .MuiDrawer-root .MuiDrawer-paper, & [role="navigation"]': {
                '&::-webkit-scrollbar': { display: 'none' },
                msOverflowStyle: 'none',
                scrollbarWidth: 'none',
            }
        }}>
            <DashboardLayout
                hideNavigation={true}
                disableCollapsibleSidebar={true}
                slots={{
                    appTitle: () => (
                        <Stack direction="row" alignItems="center" spacing={2}>
                            <Box sx={{ height: '40px', display: 'flex', alignItems: 'center' }}>
                                <img src="/logo.svg" alt="I3S" style={{ height: '100%' }} />
                            </Box>
                            <Breadcrumbs
                                separator={<ChevronRightIcon sx={{ color: '#b0bec5', fontSize: '18px' }} />}
                                aria-label="navigation history"
                                sx={{
                                    bgcolor: 'transparent',
                                    p: '4px 0',
                                    '& .MuiBreadcrumbs-ol': { alignItems: 'center' },
                                }}
                            >
                                <Link
                                    component={RouterLink}
                                    to={viewMode === "grid" ? "/grid" : "/home"}
                                    sx={{
                                        color: '#546e7a',
                                        fontSize: '14px',
                                        textDecoration: 'none',
                                        p: '4px 8px',
                                        borderRadius: '2px',
                                        '&:hover': {
                                            color: '#306DAD',
                                            bgcolor: '#f5f7ff',
                                        },
                                    }}
                                >
                                    {viewMode === "grid" ? "Grid" : "Home"}
                                </Link>
                                {history.length > 3 && (
                                    <IconButton
                                        onClick={handleMoreClick}
                                        size="small"
                                        sx={{
                                            color: '#90a4ae',
                                            p: '2px',
                                            '&:hover': { color: '#306DAD', bgcolor: '#f5f7ff' },
                                        }}
                                    >
                                        <MoreHorizIcon fontSize="small" />
                                    </IconButton>
                                )}
                                {visibleHistory.map((hist) => (
                                    <Link
                                        key={hist.id}
                                        component="button"
                                        onClick={() => handleHistoryClick(hist)}
                                        sx={{
                                            color: hist === currentNode ? '#306DAD' : '#546e7a',
                                            fontSize: '14px',
                                            fontWeight: hist === currentNode ? '500' : '400',
                                            textDecoration: 'none',
                                            p: '4px 8px',
                                            borderRadius: '2px',
                                            bgcolor: hist === currentNode ? '#e8eaf6' : 'transparent',
                                            transition: 'all 0.2s ease',
                                            '&:hover': {
                                                color: '#306DAD',
                                                bgcolor: '#f5f7ff',
                                            },
                                        }}
                                    >
                                        {hist.pretty_name}
                                    </Link>
                                ))}
                            </Breadcrumbs>
                            <Menu
                                anchorEl={menuAnchor}
                                open={Boolean(menuAnchor)}
                                onClose={handleMenuClose}
                                anchorOrigin={
                                    { vertical: 'center', horizontal: 'center' }
                                }
                                transformOrigin={
                                    { vertical: 'top', horizontal: 'center' }
                                }
                                PaperProps={{
                                    sx: {
                                        borderRadius: '4px',
                                        boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
                                        mt: '5vh',
                                        ml: '18vw',
                                    }
                                }}
                            >
                                {history.map((hist) => (
                                    <MenuItem
                                        key={hist.id}
                                        onClick={() => { handleHistoryClick(hist); handleMenuClose(); }}
                                        sx={{
                                            fontSize: '14px',
                                            color: hist === currentNode ? '#306DAD' : '#546e7a',
                                            bgcolor: hist === currentNode ? '#f5f7ff' : 'transparent',
                                            '&:hover': {
                                                bgcolor: '#e8eaf6',
                                                color: '#306DAD',
                                            },
                                        }}
                                    >
                                        {hist.pretty_name}
                                    </MenuItem>
                                ))}
                            </Menu>
                        </Stack>
                    ),
                    toolbarActions: () => (
                        <Stack direction="row" alignItems="center" spacing={2}>
                            {/* User Info Before Select */}
                            {isLoading ? (
                                <Typography sx={{ color: '#90a4ae', fontSize: '14px' }}>Loading...</Typography>
                            ) : error ? (
                                <Typography sx={{ color: '#780906', fontSize: '14px' }}>Error</Typography>
                            ) : (
                                <Box
                                    sx={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: 1,
                                        p: '6px 12px',
                                        bgcolor: '#f5f7ff',
                                        borderRadius: '4px',
                                        transition: 'background-color 0.2s ease',
                                        '&:hover': { bgcolor: '#e8eaf6' },
                                    }}>

                                    <Typography
                                        sx={{
                                            color: '#306DAD',
                                            fontSize: '14px',
                                            fontWeight: '500',
                                        }}
                                    >
                                        {data?.data?.username || "Unknown User"}
                                    </Typography>
                                    <Typography sx={{ color: '#546e7a', fontSize: '14px' }}>
                                        ({data?.data?.user_id || "N/A"})
                                    </Typography>
                                    <Typography sx={{ color: '#90a4ae', fontSize: '12px' }}>
                                        v{data?.data["backend version"] || "N/A"}
                                    </Typography>
                                </Box>
                            )}
                            <Button variant="outlined" size="small" onClick={handleLogout} sx={{ ml :2}}>
                                Se deconnecter
                            </Button>
                        </Stack>
                    )
                }}
            >
                <PageContainer>
                    <Outlet />
                </PageContainer>
            </DashboardLayout>
        </Box>
    );
};

export default MainLayout;