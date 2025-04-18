import React from "react";
import {Outlet} from "react-router";
import {createTheme} from "@mui/material";
import {ReactRouterAppProvider} from "@toolpad/core/react-router";
import {useApiData} from "../hooks/useApiData.js";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";

const theme = createTheme({
    cssVariables: true,
    colorSchemes: {
        dark: false
    }
})

export default function App() {
    const { data, isLoading, error } = useApiData('');

    const NAVIGATION = isLoading || error || !data?.data?.results
        ? [{ kind: 'page', title: 'Loading...', segment: 'home', icon: <MenuOutlinedIcon /> }]
        : data.data.results.map((view) => ({
            kind: 'page',
            title: view.endpoint,
            segment: `information/${view.endpoint.replaceAll(' ', '_')}`,
            icon: <MenuOutlinedIcon />
        }));

    return  <ReactRouterAppProvider
        navigation={NAVIGATION}
        theme={theme}
        branding={{
        title: '',
        logo: <img src="/logo.svg" alt="I3S" width={"100%"} height={"100%"} color={"inherit"} />,
        homeUrl: '/home',
    }}>
        <Outlet />
    </ReactRouterAppProvider>
}