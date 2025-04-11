import React from "react";
import {AppProvider} from "@toolpad/core";
import {Outlet} from "react-router-dom";
import {createTheme} from "@mui/material";
import {router} from "./router";

const theme = createTheme({
    cssVariables: true,
    colorSchemes: {
        dark: false
    }
})

export default function App() {
    return  <AppProvider router={router} theme={theme} branding={{
        title: '',
        logo: <img src="/logo.svg" alt="I3S" width={"100%"} height={"100%"} color={"inherit"} />,
        homeUrl: '/home',
    }}>
        <Outlet />
    </AppProvider>
}