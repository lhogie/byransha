import axios from "axios";
import React, {useCallback, useEffect, useRef, useState} from "react";
import {ResponsiveLine, ResponsiveLineCanvas} from "@nivo/line";
import {ResponsiveBar, ResponsiveBarCanvas} from "@nivo/bar";
import CircularProgress from "@mui/material/CircularProgress";
import {graphviz} from "d3-graphviz";
import CustomCodeBlock from "../../global/CustomCodeBlock.jsx";
import {ResponsiveNetwork, ResponsiveNetworkCanvas} from "@nivo/network";
import './View.css'
import {useApiData, useApiMutation} from "../../hooks/useApiData.js";
import {useQueryClient} from "@tanstack/react-query";
import { Box, Button, Modal, Typography, IconButton, Tooltip } from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import CodeIcon from '@mui/icons-material/Code';
import ExportButton from './ExportButton.jsx';
import { saveAs } from 'file-saver';

const exportToCSV = (data, fileName) => {
    const csvRows = [];
    const headers = Object.keys(data[0]);
    csvRows.push(headers.join(','));

    data.forEach(row => {
        const values = headers.map(header => row[header]);
        csvRows.push(values.join(','));
    });

    const csvContent = csvRows.join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    saveAs(blob, fileName);
};

const modalStyle = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: '80%',
    maxWidth: '800px',
    maxHeight: '80vh',
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
    display: 'flex',
    flexDirection: 'column',
};

const modalHeaderStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    mb: 2,
};

const modalContentStyle = {
    overflowY: 'auto',
};

export const View = ({ viewId, sx }) => {
    const { data: rawApiData, isLoading: loading, error, refetch } = useApiData(viewId);
    const graphvizRef = useRef(null);
    const queryClient = useQueryClient();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const supportedExportTypes = ['text/csv', 'image/png', 'image/jpeg', 'application/pdf'];
    const handleOpenModal = (event) => {
        event.stopPropagation();
        setIsModalOpen(true);
    };
    const handleCloseModal = (event) => {
        event.stopPropagation();
        setIsModalOpen(false);
    };

    const jumpMutation = useApiMutation('jump', {
        onSuccess: async () => {
            await queryClient.invalidateQueries();
        },
    });

    const jumpToNode = useCallback((nodeId) => {
        jumpMutation.mutate(`node_id=${nodeId}`);
    }, [jumpMutation]);

    useEffect(() => {
        if (!rawApiData) return;
        const { data: content } = rawApiData;
        if (!content?.results?.[0]?.result) return;
        const contentType = content.results[0].result.contentType;

        if (contentType === 'text/dot' && graphvizRef.current) {
            graphviz(graphvizRef.current).renderDot(content.results[0].result.data);
        }
    }, [rawApiData]);

    const handleExportCSV = () => {
        if (Array.isArray(resultData)) {
            exportToCSV(resultData, `view_${viewId}_data.csv`);
        }
    };

    const displayContent = useCallback((content, contentType) => {
        if (!content) {
            return <div className="error-message">No content available.</div>;
        }

        const backgroundColor = sx?.bgcolor || 'transparent';

        if (contentType === 'text/json') {
            if (viewId === 'char_example_xy') {
                const parsedChartData = parseNivoChartData(content);

                return (
                    <div className="graph">
                        <ResponsiveLineCanvas
                            data={parsedChartData}
                            margin={{ top: 50, right: 90, bottom: 50, left: 60 }}
                            xScale={{ type: 'linear' }}
                            yScale={{ type: 'linear', min: -1, max: 1, stacked: false }}
                            axisBottom={{
                                legend: 'X Axis',
                                legendOffset: 40,
                                legendPosition: 'middle',
                                tickSize: 5,
                                tickPadding: 5,
                                legendFontSize: 14,
                            }}
                            axisLeft={{
                                legend: 'Y Axis',
                                legendOffset: -50,
                                legendPosition: 'middle',
                                tickSize: 5,
                                tickPadding: 5,
                                legendFontSize: 14,
                            }}
                            colors={{ scheme: 'category10' }}
                            pointSize={12}
                            pointColor={{ theme: 'background' }}
                            pointBorderWidth={2}
                            useMesh={true}
                            legends={[
                                {
                                    anchor: 'bottom-right',
                                    direction: 'column',
                                    justify: false,
                                    translateX: 80,
                                    translateY: 0,
                                    itemsSpacing: 4,
                                    itemDirection: 'left-to-right',
                                    itemWidth: 90,
                                    itemHeight: 24,
                                    itemOpacity: 0.85,
                                    itemTextSize: 14,
                                    symbolSize: 14,
                                    symbolShape: 'circle',
                                    effects: [
                                        {
                                            on: 'hover',
                                            style: {
                                                itemOpacity: 1,
                                            },
                                        },
                                    ],
                                },
                            ]}
                        />
                    </div>
                );
            } else if (viewId.endsWith('_distribution')) {
                const barChartData = parseBarChartData(content);
                const keys = Object.values(content).length > 0 ? Object.keys(Object.values(content).reduce((a, b) => Object.assign({}, a, b)), []).sort() : [];
                return (
                    <div className="graph">
                        <ResponsiveBarCanvas
                            data={barChartData}
                            keys={keys}
                            indexBy={"group"}
                            margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
                            padding={0.3}
                            groupMode="grouped"
                            valueScale={{ type: 'linear' }}
                            indexScale={{ type: 'band', round: true }}
                            colors={{ scheme: 'nivo' }}
                            defs={[
                                {
                                    id: 'dots',
                                    type: 'patternDots',
                                    background: 'inherit',
                                    color: '#38bcb2',
                                    size: 4,
                                    padding: 1,
                                    stagger: true,
                                },
                                {
                                    id: 'lines',
                                    type: 'patternLines',
                                    background: 'inherit',
                                    color: '#eed312',
                                    rotation: -45,
                                    lineWidth: 6,
                                    spacing: 10,
                                },
                            ]}
                            borderColor={{
                                from: 'color',
                                modifiers: [['darker', 1.6]],
                            }}
                            labelSkipWidth={12}
                            labelSkipHeight={12}
                            labelTextColor={{
                                from: 'color',
                                modifiers: [['darker', 1.6]],
                            }}
                            legends={[
                                {
                                    dataFrom: 'keys',
                                    anchor: 'bottom-right',
                                    direction: 'column',
                                    justify: false,
                                    translateX: 120,
                                    translateY: 0,
                                    itemsSpacing: 2,
                                    itemWidth: 100,
                                    itemHeight: 20,
                                    itemDirection: 'left-to-right',
                                    itemOpacity: 0.85,
                                    symbolSize: 20,
                                    effects: [{ on: 'hover', style: { itemOpacity: 1 } }],
                                },
                            ]}
                            role="application"
                            ariaLabel="Nivo bar chart demo"
                            barAriaLabel={e => e.id + ": " + e.formattedValue + " in country: " + e.indexValue}
                        />
                    </div>
                );
            } else if (viewId.endsWith('nivo_view')) {
                return (
                    <div
                        className="graph"
                        onClick={(e) => {
                            e.stopPropagation();
                            e.preventDefault();
                        }}
                    >
                        <ResponsiveNetworkCanvas
                            data={{
                                nodes: content.nodes.map((node) => ({
                                    ...node,
                                    id: node.label,
                                })).reduce((accumulator, current) => {
                                    if (!accumulator.find((item) => item.id === current.id)) {
                                        accumulator.push(current);
                                    }
                                    return accumulator;
                                }, []),
                                links: content.links.map((link) => ({
                                    ...link,
                                    target: link.target.label,
                                    source: link.source.label,
                                })),
                            }}
                            onClick={(node, event) => {
                                event.preventDefault();
                                event.stopPropagation();
                                jumpToNode(node.id.split('@')[1]);
                            }}
                            margin={{ top: 0, right: 0, bottom: 0, left: 0 }}
                            linkDistance={e => e.distance}
                            centeringStrength={0.3}
                            repulsivity={6}
                            nodeSize={n => n.size}
                            activeNodeSize={n => 1.5 * n.size}
                            nodeColor={e => e.color}
                            nodeBorderWidth={1}
                            nodeBorderColor={{
                                from: 'color',
                                modifiers: [['darker', 0.8]],
                            }}
                            linkThickness={n => 2 + 2 * n.target.data.height}
                            linkBlendMode="multiply"
                            motionConfig="wobbly"
                        />
                    </div>
                );
            } else if (viewId === 'bnode_nav2') {
                return (
                    <>
                        <Box sx={{ mb: 2 }}>
                            {Object.keys(content.ins).map((inNode) => (
                                <Button
                                    key={inNode}
                                    onClick={(event) => {
                                        event.stopPropagation();
                                        event.preventDefault();
                                        jumpToNode(content.ins[inNode]);
                                    }}
                                    variant="contained"
                                    sx={{
                                        bgcolor: '#3949ab',
                                        color: '#fff',
                                        mr: 1,
                                        mb: 1,
                                        '&:hover': { bgcolor: '#5c6bc0' },
                                    }}
                                >
                                    {inNode} ({content.ins[inNode]})
                                </Button>
                            ))}
                        </Box>
                        <Box sx={{ paddingY: '10px' }}>
                            {Object.keys(content.outs).map((outNode) => (
                                <Button
                                    key={outNode}
                                    onClick={(event) => {
                                        event.stopPropagation();
                                        event.preventDefault();
                                        jumpToNode(content.outs[outNode]);
                                    }}
                                    variant="contained"
                                    sx={{
                                        bgcolor: '#00897b',
                                        color: '#fff',
                                        mr: 1,
                                        mb: 1,
                                        '&:hover': { bgcolor: '#26a69a' },
                                    }}
                                >
                                    {outNode} ({content.outs[outNode]})
                                </Button>
                            ))}
                        </Box>
                    </>
                );
            } else {
                return (
                    <div className="content-container" style={{ background: backgroundColor }}>
                        <CustomCodeBlock language="json" code={JSON.stringify(content, null, "\t")} style={{ background: backgroundColor }} />
                    </div>
                );
            }
        } else if (contentType === 'text/dot') {
            return (
                <div className="content-container graphviz-container" style={{ background: backgroundColor }}>
                    <div ref={graphvizRef} />
                </div>
            );
        } else if (contentType === 'text/html') {
            return (
                <div className="content-container html-content" style={{ background: backgroundColor }}>
                    <div dangerouslySetInnerHTML={{ __html: content }} />
                </div>
            );
        } else if (contentType === 'image/svg') {
            return (
                <div className="content-container" style={{ background: 'transparent' }}>
                    <div dangerouslySetInnerHTML={{ __html: content }} style={{ background: 'transparent' }} />
                </div>
            );
        } else if (contentType === 'image/svg+xml') {
            return (
                <div className="content-container" style={{ background: 'transparent' }}>
                    <img src={`data:image/svg+xml;base64,${content}`} alt="Graphviz" style={{ background: 'transparent' }} />
                </div>
            );
        } else if (contentType === 'text/plain') {
            return (
                <div className="content-container" style={{ background: backgroundColor }}>
                    <pre>{content}</pre>
                </div>
            );
        } else if (contentType === 'image/png' || contentType === 'image/jpeg') {
            return (
                <div className="content-container" style={{ background: backgroundColor }}>
                    <img src={`data:${contentType};base64,${content}`} alt="Content" />
                </div>
            );
        } else if (contentType === 'image/jsondot') {
            return (
                <div className="content-container" style={{ background: backgroundColor }}>
                    <CustomCodeBlock language="json" code={JSON.stringify(content, null, "\t")} style={{ background: backgroundColor }} />
                </div>
            );
        } else if (contentType === 'text/java') {
            return (
                <div className="content-container" style={{ background: backgroundColor }}>
                    <CustomCodeBlock language="java" code={content} style={{ background: backgroundColor }} />
                </div>
            );
        } else {
            return (
                <div className="error-message">
                    Unsupported content type: {contentType}
                </div>
            );
        }
    }, [viewId, jumpToNode, graphvizRef, sx]);

    const parseNivoChartData = (content) => {
        const result = [];
        for (let key of Object.keys(content)) {
            const cosData = content?.[key] || {};
            const cosLine = {
                id: key,
                data: cosData.map(val => {
                    const key = Object.keys(val)[0];
                    return {
                        x: parseFloat(key),
                        y: parseFloat(val[key]),
                    };
                }),
            };
            result.push(cosLine);
        }
        return result;
    };

    const parseBarChartData = (content) => {
        const result = [];
        for (let group of Object.keys(content)) {
            const cosData = content?.[group] || {};
            const cosLine = {
                group: group,
                ...cosData,
            };
            result.push(cosLine);
        }
        return result;
    };

    const renderJsonViewer = (dataForModal) => (
        <>
            <Tooltip title="Show Raw Backend Response">
                <IconButton
                    onClick={handleOpenModal}
                    size="small"
                    sx={{
                        position: 'relative',
                        bottom: 95,
                        left: 1050,
                        zIndex: 10,
                        color: 'primary.main',
                    }}
                    aria-label="Show raw JSON"
                    disabled={dataForModal === null || dataForModal === undefined}
                >
                    <CodeIcon />
                </IconButton>
            </Tooltip>
            <Modal
                open={isModalOpen}
                onClose={handleCloseModal}
                aria-labelledby="raw-json-modal-title"
                aria-describedby="raw-json-modal-description"
            >
                <Box sx={modalStyle}>
                    <Box sx={modalHeaderStyle}>
                        <Typography id="raw-json-modal-title" variant="h6" component="h2">
                            Raw Backend Response
                        </Typography>
                        <IconButton onClick={handleCloseModal} aria-label="close">
                            <CloseIcon />
                        </IconButton>
                    </Box>
                    <Box sx={modalContentStyle}>
                        {dataForModal ? (
                            <CustomCodeBlock
                                language="json"
                                code={JSON.stringify(dataForModal, null, 2)}
                            />
                        ) : (
                            <Typography>No raw data available to display.</Typography>
                        )}
                    </Box>
                </Box>
            </Modal>
        </>
    );

    if (loading) {
        return (
            <Box sx={{ position: 'relative', padding: 2, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '200px' }}>
                {renderJsonViewer(rawApiData)}
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Box className="information-page" sx={{ position: 'relative', padding: 2 }}>
                {renderJsonViewer(rawApiData)}
                <div className="error-message" style={{ marginTop: '40px' }}>
                    Error fetching data: {error.message}
                </div>
            </Box>
        );
    }

    if (!rawApiData) {
        return (
            <Box className="information-page" sx={{ position: 'relative', padding: 2 }}>
                {renderJsonViewer(null)}
                <div className="error-message" style={{ marginTop: '40px' }}>
                    No data available.
                </div>
            </Box>
        );
    }

    const { data: dataContent, headers } = rawApiData;

    if (dataContent?.results?.[0]?.error !== undefined) {
        return (
            <Box className="information-page" sx={{ position: 'relative', padding: 2 }}>
                {renderJsonViewer(rawApiData)}
                <div className="error-message" style={{ marginTop: '40px' }}>
                    Backend Error: {dataContent.results[0].error}
                </div>
            </Box>
        );
    }

    const resultData = dataContent?.results?.[0]?.result?.data;
    const resultContentType = dataContent?.results?.[0]?.result?.contentType;

    if (!resultData || !resultContentType) {
        return (
            <Box className="information-page" sx={{ position: 'relative', padding: 2 }}>
                {renderJsonViewer(rawApiData)}
                <div className="error-message" style={{ marginTop: '40px' }}>
                    Result data or content type missing in the response.
                </div>
            </Box>
        );
    }

    const exportData = rawApiData.data.results[0].result.data;

    return (
        <Box>
            {renderJsonViewer(rawApiData)}
            <Box sx={{ position: 'relative', padding: 2 }}>
                <Box sx={{ mt: 4 }}>
                    {displayContent(resultData, resultContentType)}
                </Box>
            </Box>
            {supportedExportTypes.includes(resultContentType) && (
                <Box>
                    <ExportButton data={exportData} fileName={`view_${viewId}_data.csv`} />
                </Box>
            )}

        </Box>
    );
};