import { Box } from "@mui/material";
import type { ElementDefinition } from "cytoscape";
import { memo, useMemo } from "react";
import { CytoscapeGraph } from "../CytoscapeGraph";

interface NetworkData {
	nodes: Array<{
		id: string;
		label: string;
		size?: number;
		color?: string;
		x?: number;
		y?: number;
	}>;
	links: Array<{
		source: string;
		target: string;
	}>;
}

interface MemoizedNetworkChartProps {
	data: NetworkData;
	onNodeClick: (node: any, event: any) => void;
}

export const MemoizedNetworkChart = memo(
	({ data, onNodeClick }: MemoizedNetworkChartProps) => {
		const processedData = useMemo(() => {
			return data;
		}, [data]);

		const elements = useMemo(
			() => [
				...processedData.nodes.map((node: any) => ({
					data: {
						id: node.id,
						label: node.label,
						size: node.size || 30,
						color: node.color || "#1f77b4",
						x: node.x,
						y: node.y,
					},
				})),
				...processedData.links.map((link: any) => ({
					data: {
						id: `${link.source}-${link.target}`,
						source: link.source,
						target: link.target,
					},
				})),
			],
			[processedData],
		);

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
			<Box sx={{ height: "300px", width: "100%" }}>
				<CytoscapeGraph
					elements={elements as ElementDefinition[]}
					style={{ width: "100%", height: "100%" }}
					stylesheet={cytoscapeStyles}
					layout={{
						name: processedData.nodes.length > 1000 ? "random" : "fcose",
						animate: false,
						samplingType: false,
						animationDuration: 1500,
						fit: true,
						padding: 30,
					}}
					onNodeClick={onNodeClick}
				/>
			</Box>
		);
	},
);

MemoizedNetworkChart.displayName = "MemoizedNetworkChart";

