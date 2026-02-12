import { type useApiData, useApiMutation } from "@hooks/useApiData";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import DownloadIcon from "@mui/icons-material/Download";
import {
	Box,
	CircularProgress,
	SpeedDial,
	SpeedDialAction,
	SpeedDialIcon,
	Typography,
} from "@mui/material";
import { saveAs } from "file-saver";
import { useNavigate } from "react-router";
import { createKey } from "@/utils/utils";
import NestedFields from "@components/FormPage/FormComponents/NestedFields";
import { SearchFormView } from "@components/FormPage/FormComponents/SearchFormView";

/**
 * KViewContent - Contenu principal de la KView
 * Gère l'affichage des champs, actions (export, ajout, suppression)
 */
export const KViewContent = ({
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
	const navigate = useNavigate();
	const pageName =
		rawApiData?.data?.results?.[0]?.result?.data?.currentNode?.name;
	const currentNodeType =
		rawApiData?.data?.results?.[0]?.result?.data?.currentNode?.type;
	const exportCSVMutation = useApiMutation("export_csv");
	const removeNodeMutation = useApiMutation("remove_node");

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
				Erreur de chargement des champs : {error.message || "Erreur inconnue"}
			</Typography>
		);

	// Vue spéciale pour SearchForm
	if (currentNodeType === "SearchForm") {
		return (
			<SearchFormView
				rootId={rootId}
				rawApiData={rawApiData}
				refetch={refetch}
			/>
		);
	}

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

			{/* SpeedDial  */}
			<SpeedDial
				ariaLabel="Actions rapides"
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
						name: "Ajouter",
						onClick: () => {
							// TODO: Implémenter l'ajout d'une nouvelle entrée
							console.log("Ajouter une nouvelle entrée");
						},
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
										const li = data?.data?.results?.[0]?.result?.data;
										let chaine = "Ces nœuds seront affectés : \n";
										for (
											let i = 0;
											i < data?.data?.results?.[0]?.result?.data.length;
											i++
										) {
											chaine += `${li[i].id}@${li[i].class}, `;
										}
										chaine += "\n\nÊtes-vous sûr de vouloir supprimer ce nœud ?";
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
