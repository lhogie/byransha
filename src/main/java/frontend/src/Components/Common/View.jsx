import Cytoscape from 'cytoscape';
import React, {useCallback, useEffect, useRef, useState, useMemo, memo, useTransition} from "react";
import CircularProgress from "@mui/material/CircularProgress";
import {graphviz} from "d3-graphviz";
import CustomCodeBlock from "../../global/CustomCodeBlock.jsx";
import {useApiData, useApiMutation} from "../../hooks/useApiData.js";
import {useQueryClient} from "@tanstack/react-query";
import {Box, Button, Modal, Typography, IconButton, Tooltip,Card, CardContent, CardMedia, CardActions} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import CodeIcon from '@mui/icons-material/Code';
import ExportButton from './ExportButton.jsx';
import { saveAs } from 'file-saver';
import { Suspense } from 'react';
import ReactECharts from 'echarts-for-react';
import './View.css'
import 'react-json-view-lite/dist/index.css'
import {JsonView, collapseAllNested} from 'react-json-view-lite';
import { ChromePicker } from 'react-color';
import CytoscapeComponent from 'react-cytoscapejs';
import fcose from 'cytoscape-fcose';

Cytoscape.use(fcose);

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
    overflow: 'hidden',
};

const modalHeaderStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    mb: 2,
};

const modalContentStyle = {
    overflowY: 'auto',
    maxHeight: 'calc(80vh - 100px)',
    width: '100%',
    scrollbarWidth: 'thin',
    scrollbarColor: '#888 #f1f1f1',
    '&::-webkit-scrollbar': {
        width: '8px',
        height: '8px',
    },
    '&::-webkit-scrollbar-track': {
        background: '#f1f1f1',
        borderRadius: '4px',
    },
    '&::-webkit-scrollbar-thumb': {
        background: '#888',
        borderRadius: '4px',
    },
    '&::-webkit-scrollbar-thumb:hover': {
        background: '#555',
    },
};

const MemoizedLineChart = memo(({ data }) => {
    const option = useMemo(() => ({
        tooltip: {
            trigger: 'axis',
            confine: true,
        },
        color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'],
        legend: {
            data: data.map(series => series.id),
            orient: 'vertical',
            right: 10,
            top: 'center'
        },
        grid: {
            left: '5%',
            right: '15%',
            bottom: '10%',
            top: '10%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            name: 'X Axis',
            nameLocation: 'middle',
            nameGap: 30
        },
        yAxis: {
            type: 'value',
            name: 'Y Axis',
            min: -1,
            max: 1,
            nameLocation: 'middle',
            nameGap: 50
        },
        series: data.map(series => ({
            name: series.id,
            type: 'line',
            data: series.data.map(point => [point.x, point.y]),
            showSymbol: false,
            symbolSize: 8,
            emphasis: {
                focus: 'series'
            },
            animationDuration: 300
        }))
    }), [data]);

    return <ReactECharts lazyUpdate option={option} style={{ height: '100%', minHeight: '300px', width: '100%' }} />;
});

const MemoizedBarChart = memo(({ prettyName, data, keys }) => {
    const option = useMemo(() => ({
        tooltip: {
            confine: true,
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            },
        },
        grid: {
            containLabel: false
        },
        xAxis: {
            data: keys,
            axisLabel: {
                rotate: 45,
                formatter: function (value) {
                    return value.length > 10 ? value.substring(0, 10) + '...' : value;
                }
            },
        },
        yAxis: {
            type: 'value'
        },
        series: {
            name: prettyName,
            type: 'bar',
            data: keys.map(key => {
                return data[key]
            }),
            emphasis: {
                focus: 'series',
                blurScope: 'coordinateSystem'
            },
            animationDuration: 300,
        }
    }), [data, keys]);

    return <ReactECharts lazyUpdate option={option} style={{ height: '100%', minHeight: '300px', width: '100%' }} />;
});

const MemoizedNetworkChart = memo(({ data, onNodeClick }) => {

    const processedData = useMemo(() => {
        return data;
    }, [data]);

    const elements = useMemo(() => (CytoscapeComponent.normalizeElements({
        nodes: processedData.nodes.map(node => ({
            data: {
                id: node.id,
                label: node.label,
                size: node.size || 30,
                color: node.color || '#1f77b4',
                x: node.x,
                y: node.y
            }
        })),
        edges: processedData.links.map(link => ({
            data: {
                source: link.source,
                target: link.target
            }
        }))
    })), [processedData]);

    const onChartClick = useCallback((params) => {
        if (!params) return;
        if (params.dataType === 'node' && onNodeClick) {
            onNodeClick({ id: params.data.id }, { preventDefault: () => {console.log(params.data)}, stopPropagation: () => {} });
        } else if (params.dataType === 'edge' && onNodeClick) {
            const partAfterFirstAt = params.data.target.split("@")[1];
            const number = partAfterFirstAt.split("(")[0];
            onNodeClick({ id: number }, { preventDefault: () => {}, stopPropagation: () => {} });
        }
    }, [onNodeClick]);

    const events = useMemo(() => ({ click: onChartClick }), [onChartClick]);

    // Define styles for nodes and edges
    const cytoscapeStyles = useMemo(() => [
        {
            selector: 'node',
            style: {
                'background-color': 'data(color)',
                'width': 'data(size)',
                'height': 'data(size)',
                'text-valign': 'top',
                'text-halign': 'center',
                'text-margin-y': -5,
                'font-size': '12px',
                'color': '#000000',
                'text-outline-width': 2,
                'text-outline-color': '#ffffff',
                'z-index': 1,
                // Hide label by default - will be shown on hover via event handlers
                'label': ''
            }
        },
        {
            selector: 'edge',
            style: {
                'width': 2,
                'line-color': '#cccccc',
                'target-arrow-color': '#cccccc',
                'target-arrow-shape': 'triangle',
                'curve-style': 'bezier'
            }
        }
    ], []);

    return (
        <Box sx={{ height: '100%', minHeight: '600px', width: '100%' }}>
            <CytoscapeComponent
                elements={elements}
                style={{ width: '100%', height: '100%' }}
                stylesheet={cytoscapeStyles}
                layout={{
                    quality: 'proof',
                    name: 'fcose',
                    animate: false,
                    animationDuration: 1500,
                    fit: true,
                    padding: 30,
                    nodeDimensionsIncludeLabels: true
                }}
                cy={(cy) => {
                    // Store the Cytoscape instance for potential future use
                    cy.on('tap', 'node', (event) => {
                        const node = event.target;
                        if (onNodeClick) {
                            onNodeClick({ id: node.id() }, event);
                        }
                    });

                    // Add hover events to show/hide labels
                    cy.on('mouseover', 'node', (event) => {
                        const node = event.target;
                        node.style('label', node.data('label'));
                    });

                    cy.on('mouseout', 'node', (event) => {
                        const node = event.target;
                        node.style('label', '');
                    });
                }}
            />
        </Box>
    );
});

export const View = ({ viewId, sx }) => {
    const { data: rawApiData, isLoading: loading, error, refetch } = useApiData(viewId);
    const graphvizRef = useRef(null);
    const queryClient = useQueryClient();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isPending, startTransition] = useTransition();
    const supportedExportTypes = ['text/csv', 'image/png', 'image/jpeg', 'application/pdf'];

    const [hex, setHex] = useState('#ffffff');
    const saveColour = useApiMutation('update_colour');
    const handleHexChange = useCallback(
        (colour) => {
            setHex(colour.hex);
            saveColour.mutate({ view_id: viewId, value: colour.hex });
        },
        [saveColour, viewId]
    );

    const handleOpenModal = (event) => {
        event.stopPropagation();
        startTransition(() => {
            setIsModalOpen(true);
        });
    };

    const stringifyJson = useCallback((data, indent = 2) => {
        if (!data) return "";
        return JSON.stringify(data, null, indent === "tab" ? "\t" : indent);
    }, []);

    const handleCloseModal = (event) => {
        event.stopPropagation();
        startTransition(() => {
            setIsModalOpen(false);
        });
    };

    const jumpMutation = useApiMutation('jump', {
        onSuccess: async () => {
            await queryClient.invalidateQueries();
        },
    });

    const jumpToNode = useCallback((nodeId) => {
        jumpMutation.mutate(`node_id=${nodeId}`);
    }, [jumpMutation]);

    const debounce = (func, wait) => {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    };

    useEffect(() => {
        if (!rawApiData) return;
        const { data: content } = rawApiData;
        if (!content?.results?.[0]?.result) return;
        const contentType = content.results[0].result.contentType;

        if (contentType === 'text/dot' && graphvizRef.current) {
            graphviz(graphvizRef.current, {
                engine: 'dot',
                fit: true,
                zoom: true,
            })
                .tweenPaths(false)
                .tweenShapes(false)
                .renderDot(content.results[0].result.data);
        }
    }, [rawApiData]);

    const handleExportCSV = () => {
        if (Array.isArray(resultData)) {
            exportToCSV(resultData, `view_${viewId}_data.csv`);
        }
    };

    const backgroundColor = useMemo(() => sx?.bgcolor || 'transparent', [sx?.bgcolor]);

    const parseNivoChartData = useCallback((content) => {
        if (!content) return [];

        const result = [];
        for (let key of Object.keys(content)) {
            const cosData = content?.[key] || {};
            const cosLine = {
                id: key,
                data: cosData.map(val => {
                    const k = Object.keys(val)[0];
                    return {
                        x: parseFloat(k),
                        y: parseFloat(val[k]),
                    };
                }),
            };
            result.push(cosLine);
        }
        return result;
    }, []);

    const parseBarChartData = useCallback((content) => {
        if (!content) return {};

        const result = {};
        for (let value of Object.values(content)) {
            for (let key of Object.keys(value)) {
                result[key] = value?.[key] || {};
            }
        }
        return result;
    }, []);

    const getDistributionKeys = useCallback((content) => {
        if (!content || Object.values(content).length === 0) return [];
        const keySet = new Set();
        Object.values(content).forEach(obj => {
            Object.keys(obj).forEach(key => keySet.add(key));
        });
        return Array.from(keySet).sort();
    }, []);

    const getNetworkData = useCallback((content) => {
        if (!content || !content.nodes) return { nodes: [], links: [] };

        const uniqueNodesMap = new Map();

        // Process nodes
        content.nodes.forEach(node => {
            // Ensure node has an id, fallback to label if id is missing
            const nodeId = node.id || node.label;

            const transformedNode = {
                ...node,
                id: nodeId,
                // Ensure label exists
                label: node.label || nodeId,
                // Add default size and color if not present
                size: node.size || 30,
                color: node.color || '#1f77b4'
            };

            if (!uniqueNodesMap.has(transformedNode.id)) {
                uniqueNodesMap.set(transformedNode.id, transformedNode);
            }
        });

        // Process links
        const transformedLinks = content.links.map(link => {
            // Handle both object references and direct string references
            const sourceId = typeof link.source === 'object' ? 
                (link.source.id || link.source.label) : link.source;

            const targetId = typeof link.target === 'object' ? 
                (link.target.id || link.target.label) : link.target;

            return {
                ...link,
                source: sourceId,
                target: targetId
            };
        });

        const processedData = {
            nodes: Array.from(uniqueNodesMap.values()),
            links: transformedLinks
        };

        return processedData;
    }, []);

    const handleNodeClick = useCallback((node, event) => {
        if (event) {
            event.preventDefault();
            event.stopPropagation();
        }
        if (node && node.id) {
            const nodeId = node.id.includes('@') ? node.id.split('@')[1] : node.id;
            jumpToNode(nodeId);
        }
    }, [jumpToNode]);

    const displayContent = useCallback((content, contentType) => {
        if (!content) {
            return <div className="error-message">No content available.</div>;
        }

        if (contentType === 'text/json') {
            if (viewId === 'show_out') {
                if (!Array.isArray(content)) {
                    return <Typography sx={{ p: 2 }} color="error">Error: Expected an array for 'show_out' data, but received type {typeof content}.</Typography>;
                }
                if (content.length === 0) {
                    return <Typography sx={{ p: 2 }}>No output nodes connected.</Typography>;
                }

                return (
                    <Box sx={{ p: 1, display: 'flex', flexWrap: 'wrap', gap: 2 }}>
                        {content.map((outNode) => {
                            const isImage = outNode.mimeType?.startsWith('image/') && outNode.value;
                            const hasValue = outNode.hasOwnProperty('value') && outNode.value !== null && outNode.value !== undefined;

                            return (
                                <Card key={outNode.id} sx={{ minWidth: 275, maxWidth: 350, display: 'flex', flexDirection: 'column' }}>
                                    <CardContent sx={{ flexGrow: 1 }}>
                                        <Typography variant="h6" component="div" sx={{wordBreak: 'break-word'}}>
                                            {outNode.name}
                                        </Typography>
                                        {isImage && (
                                            <CardMedia
                                                component="img"
                                                sx={{ maxHeight: 200, width: 'auto', objectFit: 'contain', mt: 1, border: '1px solid #eee' }}
                                                image={`data:${outNode.mimeType};base64,${outNode.value}`}
                                                alt={`Output value for ${outNode.name}`}
                                            />
                                        )}
                                        {!isImage && hasValue && (
                                            <Typography variant="body2" component="pre" sx={{ whiteSpace: 'pre-wrap', wordBreak: 'break-all', mt: 1, p: 1, backgroundColor: '#f5f5f5', borderRadius: 1 }}>
                                                {typeof outNode.value === 'object' ? JSON.stringify(outNode.value, null, 2) : String(outNode.value)}
                                            </Typography>
                                        )}
                                        {!hasValue && !isImage && (
                                            <Typography variant="body2" sx={{ fontStyle: 'italic', mt: 1 }}>
                                                (No displayable value)
                                            </Typography>
                                        )}
                                    </CardContent>

                                    {outNode.editable === "true" && (
                                        <CardActions>
                                            <Button
                                                size="small"
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    alert(`Edit action for: ${outNode.name} (ID: ${outNode.id}) - Not implemented yet.`);
                                                }}
                                            >
                                                Edit
                                            </Button>
                                        </CardActions>
                                    )}
                                </Card>
                            );
                        })}
                    </Box>
                );
            } else if (viewId === 'char_example_xy') {
                const parsedChartData = parseNivoChartData(content);

                return (
                    <div className="graph"
                         onClick={(e) => {
                             e.stopPropagation();
                             e.preventDefault();
                         }}>
                        <Suspense fallback={<CircularProgress />}>
                            <MemoizedLineChart data={parsedChartData} />
                        </Suspense>
                    </div>
                );
            } else if (viewId.endsWith('_distribution')) {
                const barChartData = parseBarChartData(content);
                const keys = getDistributionKeys(content);
                const prettyName = dataContent?.results?.[0]?.pretty_name
                return (
                    <div className="graph"
                         onClick={(e) => {
                             e.stopPropagation();
                             e.preventDefault();
                         }}>
                        <Suspense fallback={<CircularProgress />}>
                            <MemoizedBarChart prettyName={prettyName} data={barChartData} keys={keys} />
                        </Suspense>
                    </div>
                );
            } else if (viewId.endsWith('nivo_view')) {
                const networkData = getNetworkData(content);

                return (
                    <div
                        className="graph"
                        onClick={(e) => {
                            e.stopPropagation();
                            e.preventDefault();
                        }}
                    >
                        <Suspense fallback={<CircularProgress />}>
                            <MemoizedNetworkChart
                                data={networkData}
                                onNodeClick={handleNodeClick}
                            />
                        </Suspense>
                    </div>
                );
            } else if (viewId === 'bnode_navigator') {
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
                        <Suspense fallback={<CircularProgress />}>
                            <JsonView data={content} shouldExpandNode={collapseAllNested} />
                        </Suspense>
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
                    <Suspense fallback={<CircularProgress />}>
                        <JsonView data={content} />
                    </Suspense>
                </div>
            );
        } else if (contentType === 'text/java') {
            return (
                <div className="content-container" style={{ background: backgroundColor }}>
                    <Suspense fallback={<CircularProgress />}>
                        <CustomCodeBlock language="java" code={content} style={{ background: backgroundColor }} />
                    </Suspense>
                </div>
            );
        } else if (contentType === 'text/hex') {
            return (
                <div className="content-container" style={{ background: backgroundColor }}>
                    <ChromePicker
                        color={hex}
                        disableAlpha
                        onChangeComplete={handleHexChange}
                    />
                </div>
            );
        } else {
            return (
                <div className="error-message">
                    Unsupported content type: {contentType}
                </div>
            );
        }
    }, [viewId, jumpToNode, graphvizRef, backgroundColor, stringifyJson]);

    const renderJsonViewer = useMemo(() => {
        return (dataForModal) => {
            return (
                <>
                    <Tooltip title="Show Raw Backend Response">
                        <IconButton
                            onClick={handleOpenModal}
                            size="small"
                            sx={{
                                position: 'absolute',
                                top: 5,
                                right: 5,
                                zIndex: 10,
                                width: 30,
                                height: 30,
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
                                    <Suspense fallback={<CircularProgress />}>
                                        <JsonView
                                            data={dataForModal}
                                        />
                                    </Suspense>
                                ) : (
                                    <Typography>No raw data available to display.</Typography>
                                )}
                            </Box>
                        </Box>
                    </Modal>
                </>
            );
        };
    }, [isModalOpen, handleOpenModal, handleCloseModal, stringifyJson]);

    if (loading) {
        return (
            <Box className="view-container" sx={{ position: 'relative', padding: 2, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
                {renderJsonViewer(rawApiData)}
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Box className="view-container information-page" sx={{ position: 'relative', padding: 2 }}>
                {renderJsonViewer(rawApiData)}
                <div className="error-message" style={{ marginTop: '40px' }}>
                    Error fetching data: {error.message}
                </div>
            </Box>
        );
    }

    if (!rawApiData) {
        return (
            <Box className="view-container information-page" sx={{ position: 'relative', padding: 2 }}>
                {renderJsonViewer(null)}
                <div className="error-message" style={{ marginTop: '40px' }}>
                    No data available.
                </div>
            </Box>
        );
    }

    const { data: dataContent } = rawApiData;

    if (dataContent?.results?.[0]?.error !== undefined) {
        return (
            <Box className="view-container information-page" sx={{ position: 'relative', padding: 2 }}>
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
            <Box className="view-container information-page" sx={{ position: 'relative', padding: 2 }}>
                {renderJsonViewer(resultData) || renderJsonViewer(resultContentType)}
                <div className="error-message" style={{ marginTop: '40px' }}>
                    Result data or content type missing in the response.
                </div>
            </Box>
        );
    }

    const exportData = rawApiData.data.results[0].result.data;

    return (
        <Box className="view-container" sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            {renderJsonViewer(exportData)}
            <Box sx={{
                position: 'relative',
                padding: 2,
                flex: 1,
                minHeight: '300px',
                display: 'flex',
                flexDirection: 'column',
                overflow: 'hidden'
            }}>
                <Box sx={{
                    mt: 4,
                    flex: 1,
                    overflow: 'auto',
                    '&::-webkit-scrollbar': {
                        width: '8px',
                        height: '8px',
                    },
                    '&::-webkit-scrollbar-track': {
                        background: '#f1f1f1',
                        borderRadius: '4px',
                    },
                    '&::-webkit-scrollbar-thumb': {
                        background: '#888',
                        borderRadius: '4px',
                    },
                    '&::-webkit-scrollbar-thumb:hover': {
                        background: '#555',
                    },
                }}>
                    {displayContent(resultData, resultContentType)}
                </Box>
            </Box>
            {supportedExportTypes.includes(resultContentType) && (
                <Box sx={{ mt: 2 }}>
                    <ExportButton data={exportData} fileName={`view_${viewId}_data.csv`} />
                </Box>
            )}
        </Box>
    );
};
