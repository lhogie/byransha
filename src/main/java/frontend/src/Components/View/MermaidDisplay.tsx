import { CenterFocusStrong, ZoomIn, ZoomOut } from "@mui/icons-material";
import {
	Box,
	IconButton,
	Paper,
	Toolbar,
	Tooltip,
	Typography,
} from "@mui/material";
import mermaid from "mermaid";
import type React from "react";
import { useCallback, useEffect, useRef, useState } from "react";

interface MermaidDisplayProps {
	chart: string;
}

const MermaidDisplay: React.FC<MermaidDisplayProps> = ({ chart }) => {
	const containerRef = useRef<HTMLDivElement>(null);
	const mermaidRef = useRef<HTMLDivElement>(null);
	const [zoom, setZoom] = useState(1);
	const [position, setPosition] = useState({ x: 0, y: 0 });
	const [isDragging, setIsDragging] = useState(false);
	const [dragStart, setDragStart] = useState({ x: 0, y: 0 });

	const [mermaidId, setMermaidId] = useState("");

	useEffect(() => {
		mermaid.initialize({
			startOnLoad: false,
			theme: "dark",
			securityLevel: "loose",

			fontFamily: "Fira Code",
		});
	}, []);

	useEffect(() => {
		const renderMermaid = async () => {
			if (mermaidRef.current && chart) {
				try {
					const id = `mermaid-${Date.now()}`;
					setMermaidId(id);

					// Clear previous content
					mermaidRef.current.innerHTML = "";

					// Parse and render the chart
					const { svg } = await mermaid.render(id, chart);
					mermaidRef.current.innerHTML = svg;

					// Reset zoom and position when chart changes
					setZoom(1);
					setPosition({ x: 0, y: 0 });
				} catch (error) {
					console.error("Mermaid rendering error:", error);
					mermaidRef.current.innerHTML = `<div style="color: red; padding: 20px;">Error rendering diagram: ${error}</div>`;
				}
			}
		};

		renderMermaid();
	}, [chart]);

	// Prevent page scrolling when mouse is over the container
	useEffect(() => {
		const container = containerRef.current;
		if (!container) return;

		const preventScroll = (e: WheelEvent) => {
			e.preventDefault();
		};

		container.addEventListener("wheel", preventScroll, { passive: false });

		return () => {
			container.removeEventListener("wheel", preventScroll);
		};
	}, []);

	const handleZoomIn = useCallback(() => {
		setZoom((prev) => Math.min(prev * 1.2, 3));
	}, []);

	const handleZoomOut = useCallback(() => {
		setZoom((prev) => Math.max(prev / 1.2, 0.3));
	}, []);

	const handleResetView = useCallback(() => {
		setZoom(1);
		setPosition({ x: 0, y: 0 });
	}, []);

	const handleMouseDown = useCallback(
		(e: React.MouseEvent) => {
			if (e.button === 0) {
				setIsDragging(true);
				setDragStart({
					x: e.clientX - position.x,
					y: e.clientY - position.y,
				});
			}
		},
		[position],
	);

	const handleMouseMove = useCallback(
		(e: React.MouseEvent) => {
			if (isDragging) {
				setPosition({
					x: e.clientX - dragStart.x,
					y: e.clientY - dragStart.y,
				});
			}
		},
		[isDragging, dragStart],
	);

	const handleMouseUp = useCallback(() => {
		setIsDragging(false);
	}, []);

	const handleWheel = useCallback(
		(e: React.WheelEvent) => {
			if (containerRef.current) {
				const rect = containerRef.current.getBoundingClientRect();
				const centerX = rect.width / 2;
				const centerY = rect.height / 2;

				const delta = e.deltaY > 0 ? 0.9 : 1.1;
				const newZoom = Math.max(0.3, Math.min(10, zoom * delta));

				// Adjust position to zoom towards mouse cursor
				const mouseX = e.clientX - rect.left;
				const mouseY = e.clientY - rect.top;

				const deltaX = (mouseX - centerX) * (1 - delta);
				const deltaY = (mouseY - centerY) * (1 - delta);

				setZoom(newZoom);
				setPosition((prev) => ({
					x: prev.x + deltaX,
					y: prev.y + deltaY,
				}));
			}
		},
		[zoom],
	);

	return (
		<Paper
			elevation={2}
			sx={{
				height: 600,
				width: "100%",
				display: "flex",
				flexDirection: "column",
				overflow: "hidden",
			}}
		>
			<Toolbar
				variant="dense"
				sx={{
					backgroundColor: "background.paper",
					borderBottom: 1,
					borderColor: "divider",
					minHeight: 48,
				}}
			>
				<Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
					Mermaid Diagram
				</Typography>

				<Box sx={{ display: "flex", gap: 1 }}>
					<Tooltip title="Zoom In">
						<IconButton onClick={handleZoomIn} size="small">
							<ZoomIn />
						</IconButton>
					</Tooltip>

					<Tooltip title="Zoom Out">
						<IconButton onClick={handleZoomOut} size="small">
							<ZoomOut />
						</IconButton>
					</Tooltip>

					<Tooltip title="Reset View">
						<IconButton onClick={handleResetView} size="small">
							<CenterFocusStrong />
						</IconButton>
					</Tooltip>
				</Box>
			</Toolbar>

			<Box
				ref={containerRef}
				sx={{
					flex: 1,
					overflow: "hidden",
					cursor: isDragging ? "grabbing" : "grab",
					backgroundColor: "background.default",
					position: "relative",
					userSelect: "none",
				}}
				onMouseDown={handleMouseDown}
				onMouseMove={handleMouseMove}
				onMouseUp={handleMouseUp}
				onMouseLeave={handleMouseUp}
				onWheel={handleWheel}
			>
				<Box
					ref={mermaidRef}
					sx={{
						transform: `translate(${position.x}px, ${position.y}px) scale(${zoom})`,
						transformOrigin: "center center",
						transition: isDragging ? "none" : "transform 0.1s ease-out",
						width: "100%",
						height: "100%",
						display: "flex",
						alignItems: "center",
						justifyContent: "center",
						position: "absolute",
						top: 0,
						left: 0,
						"& svg": {
							maxWidth: "none",
							maxHeight: "none",
							pointerEvents: "none",
						},
					}}
				/>

				{/* Zoom indicator */}
				<Box
					sx={{
						position: "absolute",
						bottom: 16,
						right: 16,
						backgroundColor: "background.paper",
						px: 1,
						py: 0.5,
						borderRadius: 1,
						fontSize: "0.75rem",
						opacity: 0.8,
					}}
				>
					{Math.round(zoom * 100)}%
				</Box>
			</Box>
		</Paper>
	);
};

export default MermaidDisplay;
