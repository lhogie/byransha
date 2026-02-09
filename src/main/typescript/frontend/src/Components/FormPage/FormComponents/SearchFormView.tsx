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
	const [appliedFilters, setAppliedFilters] = useState<Array<{ 
		id: number; 
		filterChainId: number; 
		resultsId: number;
		pairId: number; // ID du FilterResultPair backend
		filterType?: string;
		filterValue?: string;
		filterLabel?: string;
		capturedResults?: any;
	}>>([]);
	const [searchValue, setSearchValue] = useState("");

	const exportCSVMutation = useApiMutation("export_csv");
	const removeNodeMutation = useApiMutation("remove_node");
	const setValueMutation = useApiMutation("set_value");
	const addProgressiveFilterMutation = useApiMutation("add_progressive_filter");
	const applyProgressiveFilterMutation = useApiMutation("apply_progressive_filter");
	
	const pageName = rawApiData?.data?.results?.[0]?.result?.data?.currentNode?.name;

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
			// Ne pas lancer la recherche si la valeur est vide et qu'aucun filtre n'est appliqué
			if (!searchValue && appliedFilters.length === 0) return;
            
            searchNodeMutation.mutate({
                node_id: rootId,
                pageSize: 1000,
            });
        }, 1000); // 1 seconde de délai

		return () => clearTimeout(timer);
	}, [searchValue]); // Déclencher uniquement quand searchValue change

	const handleFilterToggle = useCallback(() => {
		setShowFilters((prev) => !prev);
	}, []);

	const addNewFilter = useCallback(async () => {
        // Appeler le backend pour créer une nouvelle paire FilterChain/Results
        const response = await addProgressiveFilterMutation.mutateAsync({
            node_id: rootId,
        });
        
        const newId = Date.now();
        const backendData = response?.data?.results?.[0]?.result?.data;
        
        if (backendData?.filterChainId && backendData?.resultsId && backendData?.pairId) {
            setAppliedFilters(prev => [
                ...prev,
                {
                    id: newId,
                    filterChainId: backendData.filterChainId,
                    resultsId: backendData.resultsId,
                    pairId: backendData.pairId,
                },
            ]);
            
            // Invalider le cache pour que NestedFields charge les filtres de la nouvelle FilterChain
            await queryClient.invalidateQueries({
                queryKey: ["apiData", "class_attribute_field"],
            });
        }
    }, [rootId, addProgressiveFilterMutation, queryClient])

	const removeAppliedFilter = useCallback((filterId: number) => {
        setAppliedFilters(prev => prev.filter((f) => f.id !== filterId));
    }, []);

	const updateFilterInfo = (filterId: number, value: string) => {
        setAppliedFilters(prev => prev.map(f => f.id === filterId ? { ...f, filterValue: value } : f));
    };

	const applySpecificFilter = async (filterId: number, index: number) => {
        const filter = appliedFilters.find(f => f.id === filterId);
        if (!filter) return;
        
        // Appeler le backend pour appliquer ce filtre spécifique
        const response = await applyProgressiveFilterMutation.mutateAsync({
            node_id: rootId,
            pairId: filter.pairId.toString(),
        });
        
        // Mise à jour du label
        setAppliedFilters(prev => prev.map(f => 
            f.id === filterId ? { 
                ...f, 
                filterLabel: `Filtre ${index + 1} appliqué - ${response?.data?.results?.[0]?.result?.data?.resultsCount || 0} résultat(s)`
            } : f
        ));
        
        // Invalider le cache pour rafraîchir l'affichage
        await queryClient.invalidateQueries({
            queryKey: ["infinite", "apiData", "class_attribute_field", { node_id: filter.resultsId }],
        });
    };

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
						setSearchValue(e.target.value);
						if (searchTermAttr?.id) {
							setValueMutation.mutate({
								id: searchTermAttr.id,
								value: e.target.value,
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
					Recherche avancée
				</Typography>
				
				{/* Bouton pour ajouter un filtre */}
				<Box sx={{ display: "flex", gap: 2, mb: 2 }}>
					<Button 
						onClick={addNewFilter} 
						startIcon={<AddIcon />} 
						variant="contained"
					>
						Ajouter un filtre
					</Button>
					<Button
						startIcon={<DownloadIcon />}
						variant="outlined"
						onClick={async () => {
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
						}}
					>
						Extraction
					</Button>
				</Box>

				{/* Affichage des filtres appliqués */}
				{appliedFilters.map((filter, index) => {
					// Chaque filtre utilise sa propre FilterChain et Results créés côté backend
                        const filterChainId = filter.filterChainId;
                        const resultsId = filter.resultsId;
                        
                        // Récupérer les filtres précédents (tous les filtres avant celui-ci)
                        const previousFilters = appliedFilters.slice(0, index);
						return (
					<Paper 
						key={filter.id} 
						elevation={2} 
						sx={{ p: 2, mb: 2, backgroundColor: "#f5f5f5" }}
					>
						<Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }}>
							<Box>
								<Typography variant="subtitle1" fontWeight="bold">
									Filtre {index + 1}
								</Typography>
								{filter.filterLabel && (
									<Typography variant="caption" color="success.main" sx={{ display: "block", mt: 0.5 }}>
										{filter.filterLabel}
										{filter.filterValue && `: "${filter.filterValue}"`}
									</Typography>
								)}
							</Box>
							<IconButton 
								onClick={() => removeAppliedFilter(filter.id)} 
								size="small"
								color="error"
							>
								<DeleteIcon />
							</IconButton>
						</Box>

						{/* Afficher les filtres précédents appliqués */}
						{previousFilters.length > 0 && (
							<Box sx={{ mb: 2, p: 1.5, backgroundColor: "#e8e8e8", borderRadius: 1 }}>
								<Typography variant="caption" fontWeight="bold" gutterBottom>
									Filtres précédents appliqués :
								</Typography>
								{previousFilters.map((prevFilter, prevIndex) => (
									<Typography key={prevFilter.id} variant="caption" sx={{ display: "block", color: "text.secondary" }}>
										• Filtre {prevIndex + 1}
										{prevFilter.filterValue && `: "${prevFilter.filterValue}"`}
									</Typography>
								))}
							</Box>
						)}

						<Box sx={{ mb: 2 }}>
                                    <NestedFields
                                        fieldKey={createKey(filterChainId, `chain-${filter.id}`)}
                                        rootId={rootId}
                                        isRoot={false}
                                        isToggle={true}
                                        field={{ id: filterChainId, name: "filterChain" }}
                                    />
                                    <Box sx={{ mt: 1, display: "flex", gap: 1 }}>
                                        <TextField
                                            size="small"
                                            label="Valeur"
                                            fullWidth
                                            onChange={(e) => updateFilterInfo(filter.id, e.target.value)}
                                        />
                                        <Button 
                                            size="small" 
                                            variant="outlined" 
                                            onClick={() => applySpecificFilter(filter.id, index)}
                                        >
                                            Appliquer
                                        </Button>
                                    </Box>
                                </Box>

                                <Box sx={{ mt: 2, pt: 1, borderTop: "1px solid #ddd" }}>
                                    <Typography variant="caption" fontWeight="bold">Résultats du filtre {index + 1}:</Typography>
                                    <NestedFields
                                        fieldKey={createKey(resultsId, `results-${filter.id}`)}
                                        rootId={rootId}
                                        isRoot={false}
                                        field={{ id: resultsId, name: "results" }}
                                    />
                                </Box>
                            </Paper>
                        );
                    })}
                </Paper>
            </Collapse>

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

