import { useState, useMemo, useCallback } from "react";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import { Box } from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import { ModalComponent } from "../View/ModalComponent";
import { ViewContent } from "../View/ViewContent";
import "./View.css";

export const View = ({ viewId, sx }: { viewId: string; sx?: any }) => {
	const { data: rawApiData, isLoading: loading, error } = useApiData(viewId);
	const queryClient = useQueryClient();
	const [isModalOpen, setIsModalOpen] = useState(false);

	const [hex, setHex] = useState("#ffffff");
	const saveColour = useApiMutation("update_colour");
	const handleHexChange = useCallback(
		(colour: { hex: string }) => {
			setHex(colour.hex);
			saveColour.mutate({ view_id: viewId, value: colour.hex });
		},
		[saveColour, viewId],
	);

	const jumpMutation = useApiMutation("jump", {
		onSuccess: async () => {
			await queryClient.invalidateQueries();
		},
	});

	const jumpToNode = useCallback(
		(nodeId: number | string) => {
			jumpMutation.mutate({ node_id: nodeId });
		},
		[jumpMutation],
	);

	const { data: dataContent } = rawApiData ?? { data: undefined };

	const backgroundColor = useMemo(
		() => sx?.bgcolor || "transparent",
		[sx?.bgcolor],
	);

	const exportData = rawApiData?.data?.results?.[0]?.result?.data;

	return (
		<Box
			className="view-container"
			sx={{ height: "100%", display: "flex", flexDirection: "column" }}
		>
			<ModalComponent
				dataForModal={exportData}
				isModalOpen={isModalOpen}
				setIsModalOpen={setIsModalOpen}
			/>
			<ViewContent
				loading={loading}
				error={error}
				rawApiData={rawApiData}
				dataContent={dataContent}
				backgroundColor={backgroundColor}
				jumpToNode={jumpToNode}
				hexColor={hex}
				onHexColorChange={handleHexChange}
				viewId={viewId}
			/>
		</Box>
	);
};
