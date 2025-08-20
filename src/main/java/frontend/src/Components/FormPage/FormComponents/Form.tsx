import { type useApiData, useApiMutation } from "@hooks/useApiData";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import DownloadIcon from "@mui/icons-material/Download";
import {
	Box,
	Button,
	CircularProgress,
	SpeedDial,
	SpeedDialAction,
	SpeedDialIcon,
	Typography,
} from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import { saveAs } from "file-saver";
import { useNavigate } from "react-router";
import { createKey } from "@/utils/utils";
import NestedFields from "./NestedFields";

export const Form = ({
	rawApiData,
	loading,
	error,
	refetch,
}: {
	rawApiData: ReturnType<typeof useApiData>["data"];
	loading: ReturnType<typeof useApiData>["isLoading"];
	error: ReturnType<typeof useApiData>["error"];
	refetch: ReturnType<typeof useApiData>["refetch"];
}) => {
	const rootId = rawApiData?.data?.node_id;
	const queryClient = useQueryClient();
	const navigate = useNavigate();
	const pageName =
		rawApiData?.data?.results?.[0]?.result?.data?.currentNode?.name;
	const exportCSVMutation = useApiMutation("export_csv");
	const removeNodeMutation = useApiMutation("remove_node");
	const searchNodeMutation = useApiMutation("search_node", {
		onSuccess: async () => {
			await queryClient.invalidateQueries({
				queryKey: [
					"apiData",
					"class_attribute_field",
					{
						node_id:
							rawApiData?.data?.results?.[0]?.result?.data?.attributes?.filter(
								(attribute: any) => attribute.name === "results",
							)[0].id,
					},
				],
			});

			await queryClient.invalidateQueries({
				queryKey: [
					"infinite",
					"apiData",
					"class_attribute_field",
					{
						node_id:
							rawApiData?.data?.results?.[0]?.result?.data?.attributes?.filter(
								(attribute: any) => attribute.name === "results",
							)[0].id,
					},
				],
			});
		},
	});

	if (loading)
		return (
			<Box
				display="flex"
				justifyContent="center"
				alignItems="center"
				height="100vh"
			>
				<CircularProgress />
			</Box>
		);
	if (error)
		return (
			<Typography color="error">
				Error loading form fields: {error.message || "Unknown error"}
			</Typography>
		);

	return (
		<Box>
			<NestedFields
				fieldKey={createKey(rootId, pageName)}
				rootId={rootId}
				isRoot
				field={{
					id: rootId,
				}}
			/>

			{rawApiData?.data?.results?.[0]?.result?.data.currentNode?.type ===
			"SearchForm" ? (
				<Button
					variant="contained"
					color="primary"
					sx={{}}
					onClick={() =>
						searchNodeMutation.mutate({
							node_id: rootId,
							pageSize: 1000,
						})
					}
				>
					Rechercher
				</Button>
			) : (
				""
			)}
			<SpeedDial
				ariaLabel="SpeedDial playground example"
				sx={{ position: "absolute", bottom: 16, right: 16 }}
				hidden={false}
				icon={<SpeedDialIcon />}
			>
				{[
					{
						icon: <DownloadIcon />,
						name: "Exporter",
						onClick: async () => {
							const response = await exportCSVMutation.mutateAsync({
								node_id: rootId,
								includeAllFields: true,
								maxDepth: 4,
							});

							const blob = new Blob(
								[response?.data?.results?.[0]?.result?.data],
								{ type: "text/csv" },
							);
							saveAs(blob, `${pageName}.csv`);
						},
					},
					{
						icon: <AddIcon />,
						name: "Ajouter une nouvelle entrée",
					},
					{
						icon: <DeleteIcon />,
						name: "Supprimer",
						onClick: async () => {
							await removeNodeMutation.mutateAsync(
								{
									node_id: rootId,
									delete: false,
								},
								{
									onSuccess: async (data) => {
										await refetch();
										var li = data?.data?.results?.[0]?.result?.data;
										var chaine = "Those node will be affected : \n";
										for (
											let i = 0;
											i < data?.data?.results?.[0]?.result?.data.length;
											i++
										) {
											chaine += `${li[i].id}@${li[i].class}, `;
										}
										chaine += "\n\nAre you sure you want to delete this node ?";
										if (window.confirm(chaine)) {
											await removeNodeMutation.mutateAsync(
												{
													node_id: rootId,
													delete: true,
												},
												{
													onSuccess: async () => {
														navigate(-1);
													},
												},
											);
										}
									},
								},
							);
						},
					},
				].map((action) => (
					<SpeedDialAction
						key={action.name}
						icon={action.icon}
						tooltipTitle={action.name}
						onClick={action.onClick}
					/>
				))}
			</SpeedDial>
		</Box>
	);
};
