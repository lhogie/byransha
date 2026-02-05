import { useApiMutation } from "@hooks/useApiData";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import DownloadIcon from "@mui/icons-material/Download";
import FilterListIcon from "@mui/icons-material/FilterList";
import SearchIcon from "@mui/icons-material/Search";
import {
	Box,
	Button,
	Collapse,
	IconButton,
	InputAdornment,
	Paper,
	SpeedDial,
	SpeedDialAction,
	SpeedDialIcon,
	TextField,
	Tooltip,
	Typography,
} from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import { saveAs } from "file-saver";
import { useNavigate } from "react-router";
import { createKey } from "@/utils/utils";
import { useCallback, useEffect, useState } from "react";
import NestedFields from "./NestedFields";

interface SearchFormViewProps {
	rootId: number;
	rawApiData: any;
	refetch: () => void;
}

export const SearchFormView = ({
	rootId,
	rawApiData,
	refetch,
}: SearchFormViewProps) => {
	const queryClient = useQueryClient();
	const navigate = useNavigate();
	const [showFilters, setShowFilters] = useState(false);
	const [searchValue, setSearchValue] = useState("");

	const exportCSVMutation = useApiMutation("export_csv");
	const removeNodeMutation = useApiMutation("remove_node");
	const setValueMutation = useApiMutation("set_value");
	
	const pageName =
		rawApiData?.data?.results?.[0]?.result?.data?.currentNode?.name;

	const searchNodeMutation = useApiMutation("search_node", {
		onSuccess: async () => {
			// Invalider le cache pour rafraîchir les résultats
			await queryClient.invalidateQueries({
				queryKey: [
					"apiData",
					"class_attribute_field",
					{
						node_id:
							rawApiData?.data?.results?.[0]?.result?.data?.attributes?.filter(
								(attribute: any) => attribute.name === "results",
							)[0]?.id,
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
							)[0]?.id,
					},
				],
			});
		},
	});

	// Récupérer les attributs du SearchForm
	const attributes =
		rawApiData?.data?.results?.[0]?.result?.data?.attributes || [];
	const searchTermAttr = attributes.find(
		(attr: any) => attr.name === "searchTerm",
	);
	const filterChainAttr = attributes.find(
		(attr: any) => attr.name === "filterChain",
	);
	const resultsAttr = attributes.find((attr: any) => attr.name === "results");

	// Initialiser la valeur de recherche
	useEffect(() => {
		if (searchTermAttr?.value) {
			setSearchValue(searchTermAttr.value);
		}
	}, [searchTermAttr?.value]);

	// Recherche automatique avec debounce
	useEffect(() => {
		const timer = setTimeout(async () => {
			// Invalider d'abord le cache du SearchForm pour obtenir les dernières valeurs des filtres
			await queryClient.invalidateQueries({
				queryKey: [
					"infinite",
					"apiData",
					"class_attribute_field",
					{
						node_id: rootId,
					},
				],
			});

			// Ensuite déclencher la recherche
			searchNodeMutation.mutate({
				node_id: rootId,
				pageSize: 1000,
			});
		}, 1500); // Augmenter le délai pour laisser le temps aux mutations de se terminer

		return () => clearTimeout(timer);
	}, [searchValue]); // Déclencher uniquement quand searchValue change

	const handleFilterToggle = useCallback(() => {
		setShowFilters((prev) => !prev);
	}, []);

	return (
		<Box sx={{ p: 2 }}>
			{/* Barre de recherche simple */}
			<Box sx={{ mb: 2 }}>
				<TextField
					fullWidth
					variant="outlined"
					placeholder="Rechercher..."
					value={searchValue}
					onChange={(e) => {
						const newValue = e.target.value;
						setSearchValue(newValue);
						if (searchTermAttr?.id) {
							setValueMutation.mutate({
								id: searchTermAttr.id,
								value: newValue,
							});
						}
					}}
					InputProps={{
						startAdornment: (
							<InputAdornment position="start">
								<SearchIcon />
							</InputAdornment>
						),
					}}
				/>
				{}
				<Box sx={{ mt: 1, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
					<Typography variant="caption" color="text.secondary">
						Les résultats se mettent à jour automatiquement
					</Typography>
					<Button
						size="small"
						startIcon={<FilterListIcon />}
						onClick={handleFilterToggle}
						variant={showFilters ? "contained" : "outlined"}
					>
						{showFilters ? "Masquer les filtres" : "Afficher les filtres"}
					</Button>
				</Box>
				
			
		</Box>

			
			{/* Section déroulante pour les filtres avancés */}
			<Collapse in={showFilters}>
				<Paper elevation={1} sx={{ p: 2, mb: 2 }}>
					<Typography variant="h6" gutterBottom>
						Filtres avancés
					</Typography>
					{filterChainAttr && (
						<NestedFields
							fieldKey={createKey(filterChainAttr.id, filterChainAttr.name)}
							rootId={rootId}
							isRoot={false}
							isToggle={true}
							field={filterChainAttr}
						/>
					)}
					<Box sx={{ mt: 2, display: "flex", justifyContent: "flex-end" }}>
						<Button
							variant="contained"
							onClick={async () => {
								// Invalider le cache du SearchForm
								await queryClient.invalidateQueries({
									queryKey: [
										"infinite",
										"apiData",
										"class_attribute_field",
										{
											node_id: rootId,
										},
									],
								});

								// Invalider aussi le cache du filterChain pour obtenir les dernières valeurs
								if (filterChainAttr?.id) {
									await queryClient.invalidateQueries({
										queryKey: [
											"infinite",
											"apiData",
											"class_attribute_field",
											{
												node_id: filterChainAttr.id,
											},
										],
									});
								}

								// Appeler refetch pour forcer le rechargement
								await refetch();

								// Attendre que les données se rechargent complètement
								setTimeout(() => {
									searchNodeMutation.mutate({
										node_id: rootId,
										pageSize: 1000,
									});
								}, 1000);
							}}
						>
							Appliquer les filtres
						</Button>
					</Box>
				</Paper>
			</Collapse>
			

			{/* Résultats en temps réel */}
			{resultsAttr && (
				<Paper elevation={1} sx={{ p: 2 }}>
					<Typography variant="h6" gutterBottom>
						Résultats
					</Typography>
					<NestedFields
						fieldKey={createKey(resultsAttr.id, resultsAttr.name)}
						rootId={rootId}
						isRoot={false}
						isToggle={true}
						field={resultsAttr}
					/>
				</Paper>
			)}

			{/* SpeedDial pour actions (Export, Ajouter, Supprimer) */}
			<SpeedDial
				ariaLabel="Actions SearchForm"
				sx={{ position: "fixed", bottom: 16, right: 80 }}
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
