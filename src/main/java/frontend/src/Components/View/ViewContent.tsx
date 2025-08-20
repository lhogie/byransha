import type { useApiData } from "@hooks/useApiData";
import { Box, CircularProgress } from "@mui/material";
import { useMemo } from "react";
import { ContentDisplay } from "./ContentDisplay";

interface ViewContentProps {
	loading: ReturnType<typeof useApiData>["isLoading"];
	error: ReturnType<typeof useApiData>["error"];
	rawApiData: ReturnType<typeof useApiData>["data"];
	refetch: ReturnType<typeof useApiData>["refetch"];
	dataContent: any;
	backgroundColor: string;
	jumpToNode: (nodeId: number | string) => void;
	hexColor: string;
	onHexColorChange: (color: { hex: string }) => void;
	viewId: string;
}

export const ViewContent = ({
	loading,
	error,
	dataContent,
	refetch,
	rawApiData,
	backgroundColor,
	jumpToNode,
	hexColor,
	onHexColorChange,
	viewId,
}: ViewContentProps) => {
	const resultData = useMemo(
		() => dataContent?.results?.[0]?.result?.data,
		[dataContent],
	);
	const resultContentType = useMemo(
		() => dataContent?.results?.[0]?.result?.contentType?.split(";")?.[0],
		[dataContent],
	);
	const prettyName = useMemo(
		() => dataContent?.results?.[0]?.pretty_name,
		[dataContent],
	);

	if (loading) {
		return (
			<Box
				className="view-container"
				sx={{
					position: "relative",
					padding: 2,
					display: "flex",
					justifyContent: "center",
					alignItems: "center",
					minHeight: "300px",
				}}
			>
				<CircularProgress />
			</Box>
		);
	}

	if (error) {
		return (
			<Box
				className="view-container information-page"
				sx={{ position: "relative", padding: 2 }}
			>
				<div className="error-message" style={{ marginTop: "40px" }}>
					Error fetching data: {error.message}
				</div>
			</Box>
		);
	}

	if (!rawApiData) {
		return (
			<Box
				className="view-container information-page"
				sx={{ position: "relative", padding: 2 }}
			>
				<div className="error-message" style={{ marginTop: "40px" }}>
					No data available.
				</div>
			</Box>
		);
	}

	if (dataContent?.results?.[0]?.error !== undefined) {
		return (
			<Box
				className="view-container information-page"
				sx={{ position: "relative", padding: 2 }}
			>
				<div className="error-message" style={{ marginTop: "40px" }}>
					Backend Error: {dataContent.results[0].error}
				</div>
			</Box>
		);
	}

	if (!resultData || !resultContentType) {
		return (
			<Box
				className="view-container information-page"
				sx={{ position: "relative", padding: 2 }}
			>
				<div className="error-message" style={{ marginTop: "40px" }}>
					Result data or content type missing in the response.
				</div>
			</Box>
		);
	}

	return (
		<Box
			sx={{
				position: "relative",
				padding: 2,
				flex: 1,
				minHeight: "300px",
				display: "flex",
				flexDirection: "column",
				overflow: "hidden",
			}}
		>
			<Box
				sx={{
					mt: 4,
					flex: 1,
					overflow: "auto",
					"&::-webkit-scrollbar": {
						width: "8px",
						height: "8px",
					},
					"&::-webkit-scrollbar-track": {
						background: "#f1f1f1",
						borderRadius: "4px",
					},
					"&::-webkit-scrollbar-thumb": {
						background: "#888",
						borderRadius: "4px",
					},
					"&::-webkit-scrollbar-thumb:hover": {
						background: "#555",
					},
				}}
			>
				<ContentDisplay
					viewId={viewId}
					content={resultData}
					refetch={refetch}
					rawApiData={rawApiData}
					contentType={resultContentType}
					backgroundColor={backgroundColor}
					jumpToNode={jumpToNode}
					hexColor={hexColor}
					onHexColorChange={onHexColorChange}
					prettyName={prettyName}
				/>
			</Box>
		</Box>
	);
};
