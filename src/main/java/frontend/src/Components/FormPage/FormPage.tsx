import { useParams } from "react-router";
import "./FormPage.css";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import AddIcon from "@mui/icons-material/Add";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import DeleteIcon from "@mui/icons-material/Delete";
import DownloadIcon from "@mui/icons-material/Download";
import {
	Box,
	Button,
	CircularProgress,
	Container,
	IconButton,
	Paper,
	SpeedDial,
	SpeedDialAction,
	SpeedDialIcon,
	Typography,
} from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import { saveAs } from "file-saver";
import { useNavigate } from "react-router";
import { createKey, shortenAndFormatLabel } from "@/utils/utils";
import NestedFields from "./FormComponents/NestedFields";

const FormPage = () => {
	const { rootId: rawRootId } = useParams();

	const rootId = rawRootId ? parseInt(rawRootId) : 0;

	const queryClient = useQueryClient();
	const navigate = useNavigate();
	const {
		data: rawApiData,
		isLoading: loading,
		error,
		refetch,
	} = useApiData(`class_attribute_field`, {
		node_id: rootId,
	});
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
		<>
			<Container
				className="form-page"
				component={Paper}
				elevation={3}
				sx={{ p: 3, position: "relative" }}
			>
				<IconButton
					className="close-button"
					onClick={() => navigate(-1)}
					aria-label="close"
					sx={{ position: "absolute", top: 16, right: 16 }}
				>
					<CloseRoundedIcon />
				</IconButton>

				<Typography variant="h4" component="h1" gutterBottom>
					Form for: {shortenAndFormatLabel(pageName)}
				</Typography>

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
			</Container>
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
														await refetch();
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
		</>
	);
};

export default FormPage;
