import { CircularProgress } from "@mui/material";
import { Suspense, useCallback } from "react";
import {
	MemoizedBarChart,
	MemoizedLineChart,
	MemoizedNetworkChart,
} from "./Charts";

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
					// @ts-expect-error
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
				Object.keys(obj).forEach((key) => {
					keySet.add(key);
				});
			});
			return Array.from(keySet).sort();
		},
		[],
	);

	const getNetworkData = useCallback((chartContent: any) => {
		if (!chartContent || !chartContent.nodes) return { nodes: [], links: [] };

		const uniqueNodesMap = new Map();

		chartContent.nodes.forEach((node: any) => {
			const nodeId = node.id || node.label;

			const transformedNode = {
				...node,
				id: nodeId,
				label: node.label || nodeId,
				size: node.size || 30,
				color: node.color || "#1f77b4",
			};

			if (!uniqueNodesMap.has(transformedNode.id)) {
				uniqueNodesMap.set(transformedNode.id, transformedNode);
			}
		});

		const transformedLinks = chartContent.links.map((link: any) => {
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
			// biome-ignore lint/a11y/noStaticElementInteractions: This is a controlled click handler
			<div
				className="graph"
				onClick={(e) => {
					e.stopPropagation();
					e.preventDefault();
				}}
				onKeyDown={(e) => {
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
			// biome-ignore lint/a11y/noStaticElementInteractions: This is a controlled click handler
			<div
				className="graph"
				onClick={(e) => {
					e.stopPropagation();
					e.preventDefault();
				}}
				onKeyDown={(e) => {
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
			// biome-ignore lint/a11y/noStaticElementInteractions: This is a controlled click handler
			<div
				className="graph"
				onClick={(e) => {
					e.stopPropagation();
					e.preventDefault();
				}}
				onKeyDown={(e) => {
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
