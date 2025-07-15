import { useEffect, useRef, useCallback } from "react";
import Cytoscape, { type ElementDefinition, type Core } from "cytoscape";
import fcose from "cytoscape-fcose";

Cytoscape.use(fcose);

export interface CytoscapeGraphProps {
	elements: ElementDefinition[];
	style?: React.CSSProperties;
	stylesheet?: any[];
	layout?: any;
	onNodeClick?: (node: any, event: any) => void;
}

export const CytoscapeGraph = ({
	elements,
	style,
	stylesheet,
	layout,
	onNodeClick,
}: CytoscapeGraphProps) => {
	const containerRef = useRef<HTMLDivElement>(null);
	const cyRef = useRef<Core | null>(null);

	const handleNodeClick = useCallback(
		(event: any) => {
			const node = event.target;
			if (onNodeClick) {
				onNodeClick({ id: node.id() }, event);
			}
		},
		[onNodeClick],
	);

	const handleNodeMouseOver = useCallback((event: any) => {
		const node = event.target;
		node.style("label", node.data("label"));
	}, []);

	const handleNodeMouseOut = useCallback((event: any) => {
		const node = event.target;
		node.style("label", "");
	}, []);

	useEffect(() => {
		if (!containerRef.current) return;

		// Initialize Cytoscape instance
		const cy = Cytoscape({
			container: containerRef.current,
			elements,
			style: stylesheet || [
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
			layout: layout || {
				name: "fcose",
				animate: false,
				samplingType: false,
				animationDuration: 1500,
				fit: true,
				padding: 30,
			},
		});

		cyRef.current = cy;

		// Set up event listeners
		cy.on("tap", "node", handleNodeClick);
		cy.on("mouseover", "node", handleNodeMouseOver);
		cy.on("mouseout", "node", handleNodeMouseOut);

		// Cleanup function
		return () => {
			if (cyRef.current) {
				cyRef.current.destroy();
				cyRef.current = null;
			}
		};
	}, [
		elements,
		stylesheet,
		layout,
		handleNodeClick,
		handleNodeMouseOver,
		handleNodeMouseOut,
	]);

	// Update elements when they change
	useEffect(() => {
		if (cyRef.current && elements) {
			cyRef.current.elements().remove();
			cyRef.current.add(elements);

			// Re-run layout
			const layoutOptions = layout || {
				name: "fcose",
				animate: false,
				samplingType: false,
				animationDuration: 1500,
				fit: true,
				padding: 30,
			};

			cyRef.current.layout(layoutOptions).run();
		}
	}, [elements, layout]);

	return (
		<div
			ref={containerRef}
			style={{ width: "100%", height: "100%", ...style }}
		/>
	);
};
