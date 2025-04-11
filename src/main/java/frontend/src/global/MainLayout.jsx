import { Outlet, useLocation, useNavigate, Link as RouterLink } from "react-router-dom";
import { DashboardLayout, PageContainer } from "@toolpad/core";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import { Box, MenuItem, Select, Typography, Breadcrumbs, Link, Stack } from "@mui/material";
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';
import { useApiData } from '../hooks/useApiData';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import { useState, useEffect } from "react";

const MainLayout = () => {
    const navigate = useNavigate();
    const { pathname } = useLocation();
    const [viewMode, setViewMode] = useState(pathname.startsWith("/grid") ? "grid" : "default");
    const hideSidebar = pathname.startsWith("/grid");
    const [history, setHistory] = useState(
        pathname === "/home" || pathname === "/grid" ? [] : [pathname]
    );
    const [menuAnchor, setMenuAnchor] = useState(null);
    const { data, isLoading, error } = useApiData('');

    const NAVIGATION = isLoading || error || !data?.data?.results
        ? [{ kind: 'link', title: 'Loading...', segment: 'home', icon: <MenuOutlinedIcon /> }]
        : data.data.results.map((view) => ({
            kind: 'link',
            title: view.endpoint,
            segment: `information/${view.endpoint.replaceAll(' ', '_')}`,
            icon: <MenuOutlinedIcon />
        }));

    useEffect(() => {
        setHistory((prev) => {
            if (pathname === "/home" || pathname === "/grid") return [];
            const newPath = pathname.startsWith("/information/") ? pathname : `/information/${pathname.split("/information/")[1] || pathname}`;
            return prev.includes(newPath) ? prev : [...prev, newPath].slice(-5);
        });
    }, [pathname]);

    const handleViewChange = (event) => {
        const selectedView = event.target.value;
        setViewMode(selectedView);
        navigate(selectedView === "grid" ? "/grid" : "/home");
    };

    const handleHistoryClick = (path) => {
        setHistory((prev) => prev.slice(0, prev.indexOf(path) + 1));
        navigate(path);
    };

    const handleMoreClick = (event) => setMenuAnchor(event.currentTarget);
    const handleMenuClose = () => setMenuAnchor(null);

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
                navigation={NAVIGATION}
                hideNavigation={hideSidebar}
                disableCollapsibleSidebar={hideSidebar}
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
                                {visibleHistory.map((path) => (
                                    <Link
                                        key={path}
                                        component="button"
                                        onClick={() => handleHistoryClick(path)}
                                        sx={{
                                            color: path === currentNode ? '#306DAD' : '#546e7a',
                                            fontSize: '14px',
                                            fontWeight: path === currentNode ? '500' : '400',
                                            textDecoration: 'none',
                                            p: '4px 8px',
                                            borderRadius: '2px',
                                            bgcolor: path === currentNode ? '#e8eaf6' : 'transparent',
                                            transition: 'all 0.2s ease',
                                            '&:hover': {
                                                color: '#306DAD',
                                                bgcolor: '#f5f7ff',
                                            },
                                        }}
                                    >
                                        {path.replace('/information/', '')}
                                    </Link>
                                ))}
                            </Breadcrumbs>
                            <Menu
                                anchorEl={menuAnchor}
                                open={Boolean(menuAnchor)}
                                onClose={handleMenuClose}
                                PaperProps={{
                                    sx: {
                                        borderRadius: '4px',
                                        boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
                                        mt: 1,
                                    }
                                }}
                            >
                                {history.map((path) => (
                                    <MenuItem
                                        key={path}
                                        onClick={() => { handleHistoryClick(path); handleMenuClose(); }}
                                        sx={{
                                            fontSize: '14px',
                                            color: path === currentNode ? '#306DAD' : '#546e7a',
                                            bgcolor: path === currentNode ? '#f5f7ff' : 'transparent',
                                            '&:hover': {
                                                bgcolor: '#e8eaf6',
                                                color: '#306DAD',
                                            },
                                        }}
                                    >
                                        {path.replace('/information/', '')}
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
                                <Typography sx={{ color: '#ef5350', fontSize: '14px' }}>Error</Typography>
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
                                    }}
                                >
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
                                        v{data?.data["session ID"] || "N/A"}
                                    </Typography>
                                    <Typography sx={{ color: '#90a4ae', fontSize: '12px' }}>
                                        v{data?.data["backend version"] || "N/A"}
                                    </Typography>
                                </Box>
                            )}
                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                <Select
                                    value={viewMode}
                                    onChange={handleViewChange}
                                    sx={{
                                        '& .MuiOutlinedInput-notchedOutline': { border: 'none' },
                                        '& .MuiSelect-select': {
                                            padding: '6px 12px',
                                            borderRadius: '4px',
                                            '&:hover': { bgcolor: '#f5f5f5' },
                                        },
                                        '& .MuiSelect-icon': { color: '#90a4ae' },
                                        minWidth: 120,
                                    }}
                                    MenuProps={{
                                        PaperProps: {
                                            sx: {
                                                borderRadius: '4px',
                                                boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
                                                '& .MuiMenuItem-root': {
                                                    fontSize: '14px',
                                                    color: '#546e7a',
                                                    '&:hover': { bgcolor: '#e8eaf6' },
                                                    '&.Mui-selected': { bgcolor: '#f5f7ff', color: '#306DAD' },
                                                },
                                            }
                                        }}
                                    }
                                >
                                    <MenuItem value="default">Default Layout</MenuItem>
                                    <MenuItem value="grid">Grid Layout</MenuItem>
                                </Select>
                            </Box>
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