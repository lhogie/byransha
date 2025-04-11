import React from 'react';
import './HomePage.css';
import { Link } from "react-router";
import CircularProgress from "@mui/material/CircularProgress";
import { useTitle } from "../../global/useTitle";
import {useApiData} from '../../hooks/useApiData';

const HomePage = () => {
    const { data, isLoading } = useApiData('');
    useTitle("Home");

    if (isLoading) {
        return (
            <div
                style={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    height: "100vh",
                }}
            >
                <CircularProgress />
            </div>
        );
    }

    if (!data || !data.data || !data.data.results) {
        return <div>Error: Data is null.</div>;
    }

    return (
        <div className="home-page">
            <h1>Vue du Noeud Courant</h1>
            <h3>Vues Disponibles:</h3>
            <ul>
                {data.data.results.map((view, index) => (
                    <li key={view.id}>
                        <Link to={`/information/${view.endpoint.replaceAll(' ',  '_')}`}>
                            <strong>{view.endpoint}</strong>
                        </Link>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default HomePage;