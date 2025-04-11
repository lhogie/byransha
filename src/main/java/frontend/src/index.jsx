import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import { StyledEngineProvider } from "@mui/material";
import { RouterProvider } from "react-router-dom";
import { router } from "./global/router";
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'

const queryClient = new QueryClient();

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        <StyledEngineProvider injectFirst>
            <QueryClientProvider client={queryClient}>
                <RouterProvider router={router} />
                <ReactQueryDevtools initialIsOpen={false} />
            </QueryClientProvider>
        </StyledEngineProvider>
    </React.StrictMode>
);