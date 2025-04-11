import axios from "axios";
import React, {useCallback, useEffect, useRef} from "react";
import {ResponsiveLine, ResponsiveLineCanvas} from "@nivo/line";
import {ResponsiveBar, ResponsiveBarCanvas} from "@nivo/bar";
import CircularProgress from "@mui/material/CircularProgress";
import {graphviz} from "d3-graphviz";
import CustomCodeBlock from "../../global/CustomCodeBlock.jsx";
import {ResponsiveNetwork, ResponsiveNetworkCanvas} from "@nivo/network";
import './View.css'
import {useApiData, useApiMutation} from "../../hooks/useApiData.js";
import {useQueryClient} from "@tanstack/react-query";
import {Box, Button} from "@mui/material";

export const View = ({viewId}) => {
    const { data, isLoading: loading, error, refetch } = useApiData(viewId);
    const graphvizRef = useRef(null);
    const queryClient = useQueryClient()

    const jumpMutation = useApiMutation('jump', {
        onSuccess: async () => {
            await queryClient.invalidateQueries()
        },
    });

    const jumpToNode = useCallback((nodeId) => {
        jumpMutation.mutate(`node_id=${nodeId}`);
    }, []);

    useEffect(() => {
        if (!data) return;
        const {data: content, headers} = data;

        if (!content.results[0].result) return;

        const contentType = content.results[0].result.contentType;

        if (contentType === 'text/dot' && graphvizRef.current) {
            graphviz(graphvizRef.current).renderDot(content.results[0].result.data)
        }
    }, [data]);

    const displayContent = useCallback((content, contentType) => {
        if (!content) {
            return <div className="error-message">No content available.</div>;
        }

        if (contentType === 'text/json') {
            if (viewId === 'char_example_xy') {
                const parsedChartData = parseNivoChartData(content);

                return (
                    <div className="graph">
                        <ResponsiveLineCanvas
                            data={parsedChartData}
                            margin={{top: 50, right: 110, bottom: 50, left: 60}}
                            xScale={{type: 'linear'}}
                            yScale={{type: 'linear', min: 'auto', max: 'auto', stacked: false}}
                            axisBottom={{
                                legend: 'X Axis',
                                legendOffset: 36,
                                legendPosition: 'middle',
                            }}
                            axisLeft={{
                                legend: 'Y Axis',
                                legendOffset: -40,
                                legendPosition: 'middle',
                            }}
                            colors={{scheme: 'set2'}}
                            pointSize={10}
                            pointColor={{theme: 'background'}}
                            pointBorderWidth={2}
                            useMesh={true}
                            legends={[
                                {
                                    anchor: 'bottom-right',
                                    direction: 'column',
                                    justify: false,
                                    translateX: 100,
                                    translateY: 0,
                                    itemsSpacing: 0,
                                    itemDirection: 'left-to-right',
                                    itemWidth: 80,
                                    itemHeight: 20,
                                    itemOpacity: 0.75,
                                    symbolSize: 12,
                                    symbolShape: 'circle',
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
                            margin={{top: 50, right: 130, bottom: 50, left: 60}}
                            padding={0.3}
                            groupMode="grouped"
                            valueScale={{type: 'linear'}}
                            indexScale={{type: 'band', round: true}}
                            colors={{scheme: 'nivo'}}
                            defs={[
                                {
                                    id: 'dots',
                                    type: 'patternDots',
                                    background: 'inherit',
                                    color: '#38bcb2',
                                    size: 4,
                                    padding: 1,
                                    stagger: true
                                },
                                {
                                    id: 'lines',
                                    type: 'patternLines',
                                    background: 'inherit',
                                    color: '#eed312',
                                    rotation: -45,
                                    lineWidth: 6,
                                    spacing: 10
                                }
                            ]}
                            borderColor={{
                                from: 'color',
                                modifiers: [
                                    [
                                        'darker',
                                        1.6
                                    ]
                                ]
                            }}
                            labelSkipWidth={12}
                            labelSkipHeight={12}
                            labelTextColor={{
                                from: 'color',
                                modifiers: [
                                    [
                                        'darker',
                                        1.6
                                    ]
                                ]
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
                                    effects: [
                                        {
                                            on: 'hover',
                                            style: {
                                                itemOpacity: 1
                                            }
                                        }
                                    ]
                                }
                            ]}
                            role="application"
                            ariaLabel="Nivo bar chart demo"
                            barAriaLabel={e => e.id + ": " + e.formattedValue + " in country: " + e.indexValue}
                        />
                    </div>
                )
            } else if (viewId.endsWith('nivo_view')) {
                return (
                    <div
                        className="graph"
                        onClick={(e) => {
                            e.stopPropagation()
                            e.preventDefault()
                        }}
                    >
                        <ResponsiveNetworkCanvas
                            data={{
                                nodes: content.nodes.map((node) => ({
                                    ...node,
                                    id: node.label
                                })).reduce((accumulator, current) => {
                                    if (!accumulator.find((item) => item.id === current.id)) {
                                        accumulator.push(current);
                                    }
                                    return accumulator;
                                }, []),
                                links: content.links.map((link) => ({
                                    ...link,
                                    target: link.target.label,
                                    source: link.source.label
                                }))
                            }}
                            onClick={(node, event) => {
                                event.preventDefault()
                                event.stopPropagation()
                                jumpToNode(node.id.split('@')[1])
                            }}
                            margin={{top: 0, right: 0, bottom: 0, left: 0}}
                            linkDistance={e => e.distance}
                            centeringStrength={0.3}
                            repulsivity={6}
                            nodeSize={n => n.size}
                            activeNodeSize={n => 1.5 * n.size}
                            nodeColor={e => e.color}
                            nodeBorderWidth={1}
                            nodeBorderColor={{
                                from: 'color',
                                modifiers: [
                                    [
                                        'darker',
                                        0.8
                                    ]
                                ]
                            }}
                            linkThickness={n => 2 + 2 * n.target.data.height}
                            linkBlendMode="multiply"
                            motionConfig="wobbly"
                        />
                    </div>
                );
            } else if (viewId === 'bnode_nav2') {
                return <>
                    <Box sx={{ mb: 2 }}>
                        {
                            Object.keys(content.ins).map((inNode) => (
                                <Button
                                    key={inNode}
                                    onClick={(event) => {
                                        event.stopPropagation()
                                        event.preventDefault()
                                        jumpToNode(content.ins[inNode])
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
                        {
                            Object.keys(content.outs).map((outNode) => (
                                <Button
                                    key={outNode}
                                    onClick={(event) => {
                                        event.stopPropagation()
                                        event.preventDefault()
                                        jumpToNode(content.outs[outNode])
                                    }}                                    variant="contained"
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
            } else {
                return (
                    <div className="content-container">
                        <CustomCodeBlock language="json" code={JSON.stringify(content, null, "\t")}/>
                    </div>
                );
            }
        } else if (contentType === 'text/dot') {
            return (
                <div className="content-container graphviz-container">
                    <div ref={graphvizRef}/>
                </div>
            );
        } else if (contentType === 'text/html') {
            return (
                <div className="content-container html-content">
                    <div dangerouslySetInnerHTML={{__html: content}}/>
                </div>
            );
        } else if (contentType === 'image/svg') {
            return (
                <div className="content-container">
                    <div dangerouslySetInnerHTML={{__html: content}}/>
                </div>
            );
        } else if (contentType === 'image/svg+xml') {
            return (
                <div className="content-container">
                    <img src={`data:image/svg+xml;base64,${content}`} alt="Graphviz" />
                </div>
            );
        } else if (contentType === 'text/plain') {
            return (
                <div className="content-container">
                    <pre>{content}</pre>
                </div>
            );
        } else if (contentType === 'image/png' || contentType === 'image/jpeg') {
            return (
                <div className="content-container">
                    <img src={`data:${contentType};base64,${content}`} alt="Content"/>
                </div>
            );
        }  else if (contentType === 'image/jsondot') {
            return <div className="content-container">
                <CustomCodeBlock language="json" code={JSON.stringify(content, null, "\t")}/>
            </div>
        } else if (contentType === 'text/java') {
            return <div className="content-container">
                <CustomCodeBlock language="java" code={content}/>
            </div>
        } else {
            return (
                <div className="error-message">
                    Unsupported content type: {contentType}
                </div>
            );
        }
    }, [data, viewId]);

    const parseNivoChartData = (content) => {
        const result = [];
        for (let key of Object.keys(content)) {
            const cosData = content?.[key] || {};
            const cosLine = {
                id: key,
                data: cosData.map(val => {
                    const key = Object.keys(val)[0]

                    return {
                        x: parseFloat(key),
                        y: parseFloat(val[key])
                    }
                })
            };
            result.push(cosLine)
        }

        return result;
    };

    const parseBarChartData = (content) => {
        const result = [];
        for (let group of Object.keys(content)) {
            const cosData = content?.[group] || {};
            const cosLine = {
                group: group,
                ...cosData
            };
            result.push(cosLine)
        }
        return result;
    };

    if (loading) {
        return (
            <div className="loading-container">
                <CircularProgress/>
            </div>
        );
    }

    if (error) {
        return (
            <div className="information-page">
                <div className="error-message">Error: {error.message}</div>
            </div>
        );
    }

    if (!data) {
        return <div className="error-message">No data available.</div>;
    }

    const {data: dataContent, headers} = data;

    if (dataContent.results[0].error !== undefined) {
        return <div className="error-message">Error: {dataContent.results[0].error}</div>;
    }

    return displayContent(dataContent.results[0].result.data, dataContent.results[0].result.contentType);
}