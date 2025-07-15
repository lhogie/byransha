import {
	Box,
	Card,
	CardContent,
	CardMedia,
	Typography,
	Grid,
} from "@mui/material";
import { useVirtualizer } from "@tanstack/react-virtual";
import { useRef, useMemo, useCallback, useEffect, useState } from "react";

interface ClassAttributeFieldDisplayProps {
	content: any;
}

export const ClassAttributeFieldDisplay = ({
	content,
}: ClassAttributeFieldDisplayProps) => {
	const parentRef = useRef<HTMLDivElement>(null);
	const [containerWidth, setContainerWidth] = useState(0);

	const filteredAttributes = useMemo(
		() =>
			content?.attributes?.filter((node: any) => node.name !== "graph") || [],
		[content?.attributes],
	);

	useEffect(() => {
		const updateWidth = () => {
			if (parentRef.current) {
				setContainerWidth(parentRef.current.offsetWidth);
			}
		};

		updateWidth();
		window.addEventListener("resize", updateWidth);
		return () => window.removeEventListener("resize", updateWidth);
	}, []);

	const itemsPerRow = useMemo(() => {
		if (containerWidth === 0) return 1;
		if (containerWidth >= 600) return 2;
		return 1;
	}, [containerWidth]);

	const rows = useMemo(() => {
		const result: any[][] = [];
		for (let i = 0; i < filteredAttributes.length; i += itemsPerRow) {
			result.push(filteredAttributes.slice(i, i + itemsPerRow));
		}
		return result;
	}, [filteredAttributes, itemsPerRow]);

	const rowVirtualizer = useVirtualizer({
		count: rows.length,
		getScrollElement: () => parentRef.current,
		estimateSize: useCallback(() => 300, []),
		overscan: 2,
	});

	const renderCard = useCallback((outNode: any) => {
		const isImage = outNode.mimeType?.startsWith("image/") && outNode.value;
		const hasValue =
			Object.hasOwn(outNode, "value") &&
			outNode.value !== null &&
			outNode.value !== undefined;

		return (
			<Card
				sx={{
					width: "100%",
					display: "flex",
					flexDirection: "column",
					height: 260,
					maxHeight: 260,
				}}
			>
				<CardContent
					sx={{ flexGrow: 1, display: "flex", flexDirection: "column" }}
				>
					<Typography
						variant="h6"
						component="div"
						sx={{ wordBreak: "break-word" }}
					>
						{outNode.name}
					</Typography>
					{isImage && (
						<CardMedia
							component="img"
							sx={{
								maxHeight: 200,
								width: "auto",
								objectFit: "contain",
								mt: 1,
								border: "1px solid #eee",
							}}
							image={`data:${outNode.mimeType};base64,${outNode.value}`}
							alt={`Output value for ${outNode.name}`}
						/>
					)}
					{!isImage && hasValue && (
						<Typography
							variant="body2"
							component="pre"
							sx={{
								whiteSpace: "pre-wrap",
								wordBreak: "break-all",
								mt: 1,
								p: 1,
								backgroundColor: "#f5f5f5",
								borderRadius: 1,
								overflow: "auto",
								maxHeight: 160,
							}}
						>
							{typeof outNode.value === "object"
								? JSON.stringify(outNode.value, null, 2)
								: String(outNode.value)}
						</Typography>
					)}
					{!hasValue && !isImage && (
						<Typography
							variant="body2"
							sx={{
								fontStyle: "italic",
								mt: 1,
								p: 1,
								backgroundColor: "#f5f5f5",
								borderRadius: 1,
								flexGrow: 1,
								display: "flex",
								alignItems: "center",
								justifyContent: "center",
								minHeight: 40,
							}}
						>
							(No displayable value)
						</Typography>
					)}
				</CardContent>
			</Card>
		);
	}, []);

	if (!Array.isArray(content?.attributes)) {
		return (
			<Typography sx={{ p: 2 }} color="error">
				Error: Expected an array for 'class_attribute_field' data, but received
				type {typeof content?.attributes}.
			</Typography>
		);
	}

	if (content?.attributes.length === 0) {
		return <Typography sx={{ p: 2 }}>No output nodes connected.</Typography>;
	}

	return (
		<Box
			ref={parentRef}
			sx={{
				height: "400px",
				overflow: "auto",
				p: 1,
				width: "100%",
			}}
		>
			<Box
				sx={{
					height: `${rowVirtualizer.getTotalSize()}px`,
					width: "100%",
					position: "relative",
				}}
			>
				{rowVirtualizer.getVirtualItems().map((virtualRow) => {
					const rowItems = rows[virtualRow.index];
					if (!rowItems) return null;

					return (
						<Box
							key={virtualRow.key}
							sx={{
								position: "absolute",
								top: 0,
								left: 0,
								width: "100%",
								height: `${virtualRow.size}px`,
								transform: `translateY(${virtualRow.start}px)`,
							}}
						>
							<Grid container spacing={2} sx={{ width: "100%" }}>
								{rowItems.map((outNode) => (
									<Grid
										key={outNode.id}
										size={{ xs: 12, sm: 6 }}
										sx={{ display: "flex" }}
									>
										{renderCard(outNode)}
									</Grid>
								))}
							</Grid>
						</Box>
					);
				})}
			</Box>
		</Box>
	);
};
