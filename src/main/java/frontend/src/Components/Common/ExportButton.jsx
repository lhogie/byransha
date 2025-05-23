import React, {useState} from 'react';
import {Button, MenuItem, Select, FormControl, InputLabel} from "@mui/material";
import "./ExportButton.css";
import { jsPDF } from "jspdf";
import "jspdf-autotable";


const ExportButton = ({data}) => {
    const [format, setFormat] = useState("csv");

    const handleFormatChange = (event) => {
        setFormat(event.target.value);
    };

    const handleExport = () => {
        if (format === "csv") {
            exportToCSV(data);
        } else if (format === "json") {
            exportToJSON(data);
        } else if (format === "pdf") {
            exportToPDF(data);
        }
    }

    const exportToCSV = (data) => {
        const csvContent = [
            Object.keys(data[0]).join(","),
            ...data.map(row => Object.values(row).join(","))
        ].join("\n");

        const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
        const url = URL.createObjectURL(blob);

        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", "export.csv");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    const exportToJSON = (data) => {
        const jsonContent = JSON.stringify(data, null, 2);
        const blob = new Blob([jsonContent], { type: "application/json;charset=utf-8;" });
        const url = URL.createObjectURL(blob);

        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", "export.json");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    const exportToPDF = (data) => {
        const doc = new jsPDF();

        if (!data || data.length === 0) {
            console.error("Aucune donnée à exporter");
            return;
        }

        const headers = [Object.keys(data[0])];
        const rows = data.map(row => Object.values(row));

        doc.autoTable({
            head: headers,
            body: rows,
            startY: 20,
            styles: { fontSize: 10 },
            headStyles: { fillColor: [48, 109, 173] }
        });

        doc.save("export.pdf");
    };


    return (
        <div className={"export-button-container"}>
            <FormControl variant="outlined" sx={{
                minWidth: 100, marginRight: 2}}>
                <InputLabel id="format-select-label">Format</InputLabel>
                <Select
                    labelId="format-select-label"
                    value={format}
                    onChange={handleFormatChange}
                    label="Format"
                    sx={{
                        "& .MuiSelect-icon": {
                            paddingLeft: "5rem",
                        },
                    }}>
                    <MenuItem value="csv">CSV</MenuItem>
                    <MenuItem value="json">JSON</MenuItem>
                    <MenuItem value="pdf">PDF</MenuItem>
                </Select>
            </FormControl>
            <Button className="custom-export-button"
                    variant="contained"
                    color="primary" onClick={handleExport}>
                Exporter
            </Button>
        </div>
    );
};

export default ExportButton;