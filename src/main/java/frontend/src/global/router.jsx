import {createBrowserRouter} from "react-router-dom";
import App from "./App";
import LoginForm from "../Components/LoginForm/LoginForm";
import MainLayout from "./MainLayout";
import HomePage from "../Components/HomePage/HomePage";
import InformationPage from "../Components/InformationPage/InformationPage";
import React from "react";
import GridViewer from '../Components/GridView/GridView'

export const router = createBrowserRouter([
    {
        Component: App,
        children: [
            {
                path: '/',
                element: <LoginForm/>,
            },
            {
                element: <MainLayout/>,
                children: [
                    {
                        path: '/home',
                        element: <HomePage/>,
                    },
                    {
                        path: '/information/:viewId',
                        element: <InformationPage/>,
                    },
                    {
                        path: '/grid',
                        element: <GridViewer/>
                    }
                ]
            }]
    }
], {
    future: {
        v7_fetcherPersist: true,
        v7_normalizeFormMethod: true,
        v7_partialHydration: true,
        v7_relativeSplatPath: true,
        v7_skipActionErrorRevalidation: true,
    }
})