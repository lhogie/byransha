import { memo, useMemo, useCallback, Suspense } from "react";
import ReactECharts from "echarts-for-react";
import CytoscapeComponent from "react-cytoscapejs";
import Cytoscape, { type ElementDefinition } from "cytoscape";
import fcose, { type FcoseLayoutOptions } from "cytoscape-fcose";
import { Box, CircularProgress } from "@mui/material";

Cytoscape.use(fcose);

const MemoizedLineChart = memo(({ data }: { data: any }) => {
	const option = useMemo(
		() => ({
			tooltip: {
				trigger: "axis",
				confine: true,
			},
			color: [
				"#5470c6",
				"#91cc75",
				"#fac858",
				"#ee6666",
				"#73c0de",
				"#3ba272",
				"#fc8452",
				"#9a60b4",
				"#ea7ccc",
			],
			legend: {
				data: data.map((series: any) => series.id),
				orient: "vertical",
				right: 10,
				top: "center",
			},
			grid: {
				left: "5%",
				right: "15%",
				bottom: "10%",
				top: "10%",
				containLabel: true,
			},
			xAxis: {
				type: "value",
				name: "X Axis",
				nameLocation: "middle",
				nameGap: 30,
			},
			yAxis: {
				type: "value",
				name: "Y Axis",
				min: -1,
				max: 1,
				nameLocation: "middle",
				nameGap: 50,
			},
			series: data.map((series: any) => ({
				name: series.id,
				type: "line",
				data: series.data.map((point: any) => [point.x, point.y]),
				showSymbol: false,
				symbolSize: 8,
				emphasis: {
					focus: "series",
				},
				animationDuration: 300,
			})),
		}),
		[data],
	);

	return (
		<ReactECharts
			lazyUpdate
			option={option}
			style={{ height: "100%", minHeight: "300px", width: "100%" }}
		/>
	);
});

const MemoizedBarChart = memo(
	({
		prettyName,
		data,
		keys,
	}: {
		prettyName: string | undefined;
		data: any;
		keys: string[];
	}) => {
		const option = useMemo(
			() => ({
				tooltip: {
					confine: true,
					trigger: "axis",
					axisPointer: {
						type: "shadow",
					},
				},
				grid: {
					containLabel: false,
				},
				xAxis: {
					data: keys,
					axisLabel: {
						rotate: 45,
						formatter: (value: string) =>
							value.length > 10 ? `${value.substring(0, 10)}...` : value,
					},
				},
				yAxis: {
					type: "value",
				},
				series: {
					name: prettyName,
					type: "bar",
					data: keys.map((key) => {
						return data[key];
					}),
					emphasis: {
						focus: "series",
						blurScope: "coordinateSystem",
					},
					animationDuration: 300,
				},
			}),
			[data, keys, prettyName],
		);

		return (
			<ReactECharts
				lazyUpdate
				option={option}
				style={{ height: "100%", minHeight: "300px", width: "100%" }}
			/>
		);
	},
);

const MemoizedNetworkChart = memo(
	({
		data,
		onNodeClick,
	}: {
		data: any;
		onNodeClick: (node: any, event: any) => void;
	}) => {
		const processedData = useMemo(() => {
			return data;
		}, [data]);

		const elements = useMemo(
			() =>
				CytoscapeComponent.normalizeElements({
					nodes: processedData.nodes.map((node: any) => ({
						data: {
							id: node.id,
							label: node.label,
							size: node.size || 30,
							color: node.color || "#1f77b4",
							x: node.x,
							y: node.y,
						},
					})),
					edges: processedData.links.map((link: any) => ({
						data: {
							source: link.source,
							target: link.target,
						},
					})),
				}),
			[processedData],
		);

		// Define styles for nodes and edges
		const cytoscapeStyles = useMemo(
			() => [
				{
					selector: "node",
					style: {
						"background-color": "data(color)",
						width: "data(size)",
						height: "data(size)",
						"text-valign": "top",
						"text-halign": "center",
						"text-margin-y": -5,
						"font-size": "12px",
						color: "#000000",
						"text-outline-width": 2,
						"text-outline-color": "#ffffff",
						"z-index": 1,
						// Hide label by default - will be shown on hover via event handlers
						label: "",
					},
				},
				{
					selector: "edge",
					style: {
						width: 2,
						"line-color": "#cccccc",
						"target-arrow-color": "#cccccc",
						"target-arrow-shape": "triangle",
						"curve-style": "bezier",
					},
				},
			],
			[],
		);

		return (
			<Box sx={{ height: "100%", minHeight: "300px", width: "100%" }}>
				<CytoscapeComponent
					elements={elements as ElementDefinition[]}
					style={{ width: "100%", height: "100%" }}
					stylesheet={cytoscapeStyles}
					layout={
						{
							name: processedData.nodes.length > 1000 ? "random" : "fcose",
							animate: false,
							samplingType: false,
							animationDuration: 1500,
							fit: true,
							padding: 30,
						} as FcoseLayoutOptions
					}
					cy={(cy) => {
						// Store the Cytoscape instance for potential future use
						cy.on("tap", "node", (event) => {
							const node = event.target;
							if (onNodeClick) {
								onNodeClick({ id: node.id() }, event);
							}
						});

						// Add hover events to show/hide labels
						cy.on("mouseover", "node", (event) => {
							const node = event.target;
							node.style("label", node.data("label"));
						});

						cy.on("mouseout", "node", (event) => {
							const node = event.target;
							node.style("label", "");
						});
					}}
				/>
			</Box>
		);
	},
);

interface ChartDisplayProps {
	viewId: string;
	content: any;
	onNodeClick: (nodeId: number | string) => void;
	prettyName?: string;
}

export const ChartDisplay = ({
	viewId,
	content,
	onNodeClick,
	prettyName,
}: ChartDisplayProps) => {
	const parseNivoChartData = useCallback((chartContent: any) => {
		if (!chartContent) return [];

		const result = [];
		for (const key of Object.keys(chartContent)) {
			const cosData = chartContent?.[key] || {};
			const cosLine = {
				id: key,
				data: cosData.map((val: any) => {
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

	const parseBarChartData = useCallback(
		(chartContent: { [s: string]: { [s2: string]: string } }) => {
			if (!chartContent) return {};

			const result = {};
			for (const value of Object.values(chartContent)) {
				for (const key of Object.keys(value)) {
					// @ts-ignore
					result[key] = value?.[key] || {};
				}
			}
			return result;
		},
		[],
	);

	const getDistributionKeys = useCallback(
		(chartContent: { [s: string]: any } | ArrayLike<unknown>) => {
			if (!chartContent || Object.values(chartContent).length === 0) return [];
			const keySet = new Set<string>();
			Object.values(chartContent).forEach((obj: string) => {
				Object.keys(obj).forEach((key) => keySet.add(key));
			});
			return Array.from(keySet).sort();
		},
		[],
	);

	const getNetworkData = useCallback((chartContent: any) => {
		if (!chartContent || !chartContent.nodes) return { nodes: [], links: [] };

		const uniqueNodesMap = new Map();

		// Process nodes
		chartContent.nodes.forEach((node: any) => {
			// Ensure node has an id, fallback to label if id is missing
			const nodeId = node.id || node.label;

			const transformedNode = {
				...node,
				id: nodeId,
				// Ensure label exists
				label: node.label || nodeId,
				// Add default size and color if not present
				size: node.size || 30,
				color: node.color || "#1f77b4",
			};

			if (!uniqueNodesMap.has(transformedNode.id)) {
				uniqueNodesMap.set(transformedNode.id, transformedNode);
			}
		});

		// Process links
		const transformedLinks = chartContent.links.map((link: any) => {
			// Handle both object references and direct string references
			const sourceId =
				typeof link.source === "object"
					? link.source.id || link.source.label
					: link.source;

			const targetId =
				typeof link.target === "object"
					? link.target.id || link.target.label
					: link.target;

			return {
				...link,
				source: sourceId,
				target: targetId,
			};
		});

		const processedData = {
			nodes: Array.from(uniqueNodesMap.values()),
			links: transformedLinks,
		};

		return processedData;
	}, []);

	const handleNetworkNodeClick = useCallback(
		(node: any, event: MouseEvent) => {
			if (event) {
				event.preventDefault();
				event.stopPropagation();
			}
			if (node?.id) {
				const nodeId = node.id.includes("@") ? node.id.split("@")[1] : node.id;
				onNodeClick(nodeId);
			}
		},
		[onNodeClick],
	);

	if (viewId === "char_example_xy") {
		const parsedChartData = parseNivoChartData(content);

		return (
			<div
				className="graph"
				onClick={(e) => {
					e.stopPropagation();
					e.preventDefault();
				}}
			>
				<Suspense fallback={<CircularProgress />}>
					<MemoizedLineChart data={parsedChartData} />
				</Suspense>
			</div>
		);
	} else if (viewId.endsWith("_distribution")) {
		const barChartData = parseBarChartData(content);
		const keys = getDistributionKeys(content);
		return (
			<div
				className="graph"
				onClick={(e) => {
					e.stopPropagation();
					e.preventDefault();
				}}
			>
				<Suspense fallback={<CircularProgress />}>
					<MemoizedBarChart
						prettyName={prettyName}
						data={barChartData}
						keys={keys}
					/>
				</Suspense>
			</div>
		);
	} else if (viewId.endsWith("nivo_view")) {
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
						onNodeClick={handleNetworkNodeClick}
					/>
				</Suspense>
			</div>
		);
	} else {
		return null;
	}
};
