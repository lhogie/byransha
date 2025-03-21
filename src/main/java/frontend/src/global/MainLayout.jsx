import React, { useState } from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { DashboardLayout, PageContainer } from "@toolpad/core";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import {Box, MenuItem, Select, Typography, Breadcrumbs, Link, Stack} from "@mui/material";
import {useApiData} from '../hooks/useApiData';

const MainLayout = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [currentView, setCurrentView] = useState(location.pathname.startsWith("/grid") ? "grid" : "default");
    const hideSidebar = location.pathname.startsWith("/grid");

    const { data, isLoading, error } = useApiData('current_node');

    const NAVIGATION = !isLoading && !error && data?.data?.results
        ? data.data.results[0].result.data.views.data.map((view, index) => ({
            kind: 'link',
            title: view.label,
            segment: `information/${view.label.replaceAll(' ', '_')}`,
            icon: <MenuOutlinedIcon />
        }))
        : [{ kind: 'link', title: 'Loading...', segment: 'home', icon: <MenuOutlinedIcon /> }];

    const handleViewChange = (event) => {
        const selectedView = event.target.value;
        setCurrentView(selectedView);
        if (selectedView === "grid") {
            navigate(`/grid`);
        } else if (selectedView === "default") {
            navigate(`/information/0`);
        }
    };

    let pathnames = ["test", "test2", "test3"];
    return (
        <Box sx={{
            '& .MuiDrawer-root .MuiDrawer-paper, & [role="navigation"]': {
                '&::-webkit-scrollbar': { display: 'none' },
                msOverflowStyle: 'none',
                scrollbarWidth: 'none',
                overflow: '-moz-scrollbars-none'
            },
            '& .MuiDrawer-root .MuiDrawer-paper *, & [role="navigation"] *': {
                '&::-webkit-scrollbar': { display: 'none' },
                msOverflowStyle: 'none',
                scrollbarWidth: 'none',
                overflow: '-moz-scrollbars-none'
            }
        }}>
            <DashboardLayout
                navigation={NAVIGATION}
                hideNavigation={hideSidebar}
                disableCollapsibleSidebar={hideSidebar}
                slots={{
                    appTitle: () =>(
                        <Stack direction='row' alignItems='center' spacing={5}>
                            <Box sx={{
                                display: 'flex',
                                alignItems: 'center',
                                '& .MuiSvgIcon-root': { cursor: 'pointer' },
                                height: '40px'
                            }}>
                            <img src="/logo.svg" alt="I3S" color={"inherit"} width={'100%'} height={'100%'}/>
                            </Box>
                            <Breadcrumbs separator=">" aria-label="breadcrumb">
                                <Link to="/home" color="inherit">
                                    Home
                                </Link>
                                {pathnames.map((value, index) => {
                                    const last = index === pathnames.length - 1;
                                    const to = `/${pathnames.slice(0, index + 1).join('/')}`;

                                    return last ? (
                                        <Typography color="textPrimary" key={to}>
                                            {value}
                                        </Typography>
                                    ) : (
                                        <Link color="inherit" to={to} key={to}>
                                            {value}
                                        </Link>
                                    );
                                })}
                            </Breadcrumbs>
                        </Stack>),
                    toolbarActions: () => (
                        <Box sx={{
                            display: 'flex',
                            alignItems: 'center',
                            '& .MuiSvgIcon-root': { cursor: 'pointer' }
                        }}>
                            <Box display="flex" alignItems="center">
                                <Select
                                    value={currentView}
                                    onChange={handleViewChange}
                                    sx={{
                                        '& .MuiOutlinedInput-notchedOutline': { border: 'none' },
                                        '&.MuiOutlinedInput-root': {
                                            minWidth: 120,
                                            backgroundColor: 'transparent',
                                            '&:hover .MuiOutlinedInput-notchedOutline': { border: 'none' },
                                            '&.Mui-focused .MuiOutlinedInput-notchedOutline': { border: 'none' }
                                        },
                                        '& .MuiSelect-select': {
                                            padding: '8px 14px',
                                            borderRadius: '8px',
                                            '&:hover': { backgroundColor: 'rgba(0, 0, 0, 0.04)' }
                                        },
                                        '& .MuiSelect-icon': { color: 'inherit' }
                                    }}
                                    MenuProps={{
                                        PaperProps: {
                                            sx: {
                                                backgroundColor: '#fff',
                                                borderRadius: '8px',
                                                boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
                                                mt: 1,
                                                '& .MuiMenuItem-root': {
                                                    padding: '10px 16px',
                                                    '&:hover': { backgroundColor: 'rgba(0, 0, 0, 0.04)' },
                                                    '&.Mui-selected': {
                                                        backgroundColor: 'rgba(0, 0, 0, 0.08)',
                                                        '&:hover': { backgroundColor: 'rgba(0, 0, 0, 0.12)' }
                                                    }
                                                }
                                            }
                                        }
                                    }}
                                    variant="outlined"
                                >
                                    <MenuItem value="default" sx={{ fontSize: "14px" }}>Default Layout</MenuItem>
                                    <MenuItem value="grid" sx={{ fontSize: "14px" }}>Grid Layout</MenuItem>
                                </Select>
                            </Box>
                        </Box>
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