import { View } from "@common/View";
import ErrorBoundary from "@components/ErrorBoundary";
import {
	LoadingStates,
	useLoadingState,
} from "@components/Loading/LoadingComponents";
import { useTitle } from "@global/useTitle";
import { useOptimizedDebounce, useOptimizedState } from "@hooks/react19";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import {
	Add as AddIcon,
	Close as CloseIcon,
	AspectRatio as ExpandIcon,
	Remove as RemoveIcon,
	ViewModule as ViewModuleIcon,
} from "@mui/icons-material";
import {
	Alert,
	Box,
	Button,
	ButtonGroup,
	Card,
	CardContent,
	CardHeader,
	Checkbox,
	CircularProgress,
	Dialog,
	DialogContent,
	DialogTitle,
	Divider,
	FormControlLabel,
	IconButton,
	ListItemText,
	Menu,
	MenuItem,
	Paper,
	Skeleton,
	Stack,
	Switch, TextField,
	Tooltip,
	Typography,
	useTheme,
} from "@mui/material";
import React, {
	type MouseEventHandler,
	memo,
	Suspense,
	startTransition,
	useCallback,
	useDeferredValue,
	useMemo,
	useState,
	useTransition,
} from "react";
import { useNavigate } from "react-router";
import { Slider } from "@mui/material";
import dayjs from "dayjs";
import {DatePicker} from "@mui/x-date-pickers/DatePicker";

// Memoized ViewCard component with enhanced accessibility
const ViewCard = memo(
	({
		view,
		onExpand,
		handleViewToggle,
	}: {
		view: any;
		onExpand: (view: any) => void;
		handleViewToggle: (endpoint: string) => void;
	}) => {
		const theme = useTheme();
		const handleToggle = useCallback(
			(e: React.MouseEvent) => {
				e.preventDefault();
				e.stopPropagation();
				handleViewToggle(view.name);
			},
			[handleViewToggle, view.name],
		);

		const handleExpand = useCallback(
			(e: React.MouseEvent) => {
				e.preventDefault();
				e.stopPropagation();
				onExpand(view);
			},
			[onExpand, view],
		);

		const isTechnical = view.type === "technical";

		return (
			<ErrorBoundary
				fallback={
					<Card
						sx={{
							aspectRatio: "1",
							border: `1px solid ${theme.palette.error.main}`,
							borderRadius: 2,
							display: "flex",
							alignItems: "center",
							justifyContent: "center",
							bgcolor: "error.light",
						}}
						role="alert"
					>
						<Typography color="error" variant="body2">
							Erreur de chargement de la vue
						</Typography>
					</Card>
				}
			>
				<Card
					sx={{
						aspectRatio: "1",
						border: `1px solid ${theme.palette.divider}`,
						borderRadius: 2,
						display: "flex",
						flexDirection: "column",
						bgcolor: isTechnical ? "warning.light" : "background.paper",
						transition:
							"transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out",
						"&:hover": {
							transform: "translateY(-2px)",
							boxShadow: theme.shadows[4],
						},
						"&:focus-within": {
							outline: `2px solid ${theme.palette.primary.main}`,
							outlineOffset: "2px",
						},
					}}
					aria-label={`Vue ${view.pretty_name}: ${view.description}`}
				>
					<CardHeader
						sx={{
							height: { xs: 50, sm: 60 },
							bgcolor: isTechnical ? "warning.main" : "grey.100",
							borderBottom: `1px solid ${theme.palette.divider}`,
							p: { xs: 0.5, sm: 1 },
							position: "relative",
							"& .MuiCardHeader-content": {
								overflow: "hidden",
							},
						}}
						title={
							<Typography
								variant="caption"
								sx={{
									color: isTechnical
										? "warning.contrastText"
										: "text.secondary",
									fontWeight: 500,
									display: "-webkit-box",
									WebkitLineClamp: 2,
									WebkitBoxOrient: "vertical",
									overflow: "hidden",
									textOverflow: "ellipsis",
									lineHeight: 1.2,
								}}
							>
								{view.pretty_name}
							</Typography>
						}
						subheader={
							<Typography
								variant="caption"
								sx={{
									color: isTechnical ? "warning.contrastText" : "text.disabled",
									fontSize: "0.7rem",
									display: "-webkit-box",
									WebkitLineClamp: 1,
									WebkitBoxOrient: "vertical",
									overflow: "hidden",
									textOverflow: "ellipsis",
								}}
							>
								{view.description}
							</Typography>
						}
						action={
							<Stack direction="row" spacing={0.5}>
								<Tooltip title="Agrandir la vue">
									<IconButton
										onClick={handleExpand}
										size="small"
										aria-label={`Agrandir la vue ${view.pretty_name}`}
										sx={{
											color: isTechnical
												? "warning.contrastText"
												: "action.active",
											"&:hover": {
												bgcolor: "action.hover",
											},
											"&:focus": {
												outline: `2px solid ${theme.palette.primary.main}`,
												outlineOffset: "1px",
											},
										}}
									>
										<ExpandIcon fontSize="small" />
									</IconButton>
								</Tooltip>
								<Tooltip title="Fermer la vue">
									<IconButton
										onClick={handleToggle}
										size="small"
										aria-label={`Fermer la vue ${view.pretty_name}`}
										sx={{
											color: isTechnical
												? "warning.contrastText"
												: "action.active",
											"&:hover": {
												bgcolor: "action.hover",
											},
											"&:focus": {
												outline: `2px solid ${theme.palette.primary.main}`,
												outlineOffset: "1px",
											},
										}}
									>
										<CloseIcon fontSize="small" />
									</IconButton>
								</Tooltip>
							</Stack>
						}
					/>
					<CardContent
						sx={{
							p: { xs: 1.5, sm: 2 },
							height: "calc(100% - 60px)",
							display: "flex",
							flexDirection: "column",
							overflow: "hidden",
							bgcolor: isTechnical ? "warning.light" : "background.paper",
							"&:last-child": { pb: { xs: 1.5, sm: 2 } },
						}}
					>
						<Box
							sx={{
								flex: 1,
								overflow: "auto",
								color: "text.primary",
								scrollbarWidth: "thin",
								scrollbarColor: `${theme.palette.primary.main} ${theme.palette.grey[200]}`,
								"&::-webkit-scrollbar": { width: "6px" },
								"&::-webkit-scrollbar-thumb": {
									bgcolor: "primary.main",
									borderRadius: "3px",
								},
								"&::-webkit-scrollbar-track": {
									bgcolor: "grey.200",
								},
								wordBreak: "break-word",
								overflowWrap: "break-word",
								whiteSpace: "pre-wrap",
								maxWidth: "100%",
								fontSize: { xs: "0.75rem", sm: "0.875rem" },
							}}
							aria-label={`Contenu de la vue ${view.pretty_name}`}
						>
							{view.error ? (
								<Alert severity="error" variant="outlined" sx={{ mt: 1 }}>
									<Typography variant="body2">{view.error}</Typography>
								</Alert>
							) : (
								<ErrorBoundary
									fallback={
										<Alert severity="error" variant="outlined" sx={{ mt: 1 }}>
											<Typography variant="body2">
												Erreur de chargement de la vue
											</Typography>
										</Alert>
									}
								>
									<Suspense
										fallback={
											<Stack spacing={1} sx={{ mt: 1 }}>
												<Skeleton variant="text" width="80%" />
												<Skeleton variant="text" width="60%" />
												<Skeleton variant="rectangular" height={60} />
											</Stack>
										}
									>
										<View
											viewId={view.name.replaceAll(" ", "_")}
											sx={{
												bgcolor: isTechnical
													? "warning.light"
													: "background.paper",
												width: "100%",
											}}
										/>
									</Suspense>
								</ErrorBoundary>
							)}
						</Box>
					</CardContent>
				</Card>
			</ErrorBoundary>
		);
	},
);

const ViewGrid = memo(
	({
		views,
		selectedViews,
		columns,
		onViewToggle,
		onViewExpand,
	}: {
		views: any[];
		selectedViews: string[];
		columns: number;
		onViewToggle: (endpoint: string) => void;
		onViewExpand: (view: any) => void;
	}) => {
		// Use regular filtered views
		const visibleViews = views.filter((view) =>
			selectedViews.includes(view.name),
		);
		const deferredVisibleViews = useDeferredValue(visibleViews);
		const rowColumns = Math.min(columns, deferredVisibleViews.length);

		return (
			<ErrorBoundary
				fallback={
					<Box sx={{ textAlign: "center", p: 3, color: "error.main" }}>
						<Typography>Error loading view grid</Typography>
					</Box>
				}
			>
				<Box
					sx={{
						display: "grid",
						gridTemplateColumns: {
							xs: "1fr",
							sm: `repeat(${Math.min(columns, 2)}, 1fr)`,
							md: `repeat(${rowColumns}, 1fr)`,
						},
						gap: { xs: 2, sm: 4 },
						opacity: 1,
						transition: "opacity 0.2s ease-in-out",
					}}
				>
					{deferredVisibleViews.map((view) => (
						<ViewCard
							key={view.name}
							view={view}
							onExpand={onViewExpand}
							handleViewToggle={onViewToggle}
						/>
					))}
				</Box>
			</ErrorBoundary>
		);
	},
);

const HomePage = memo(() => {
	const theme = useTheme();
	const navigate = useNavigate();
	const [selectedDate, setSelectedDate] = useState<number>(Date.now());
	const dateRangeStart = new Date("1920-01-01").getTime();
	const dateRangeEnd = Date.now();
	const handleDateChange = (_: Event, value: number | number[]) => {
		setSelectedDate(value as number);
	};
	const { data, isLoading, error } = useApiData(
		"endpoints?only_applicable&type=byransha.web.View",
		{},
		{
			staleTime: 30000,
			gcTime: 5 * 60 * 1000,
			refetchOnWindowFocus: false,
			refetchOnReconnect: true,
		},
	);
	const { isLoading: isTransitioning, withLoading } = useLoadingState();

	// State for expanded view modal with optimization for faster loading
	const [expandedView, setExpandedView] = useOptimizedState<any>(null, {
		transitionUpdates: true,
	});
	const [isModalOpen, setIsModalOpen] = useState(false);

	useTitle("Accueil");

	// Column management with auto mode toggle
	const [columns, setColumns, isColumnsUpdating] = useOptimizedState(2, {
		transitionUpdates: true,
		deferUpdates: false,
	});

	const [autoColumns, setAutoColumns] = useState<boolean>(() =>
		JSON.parse(localStorage.getItem("autoColumns") || "true"),
	);

	const [selectMenuAnchor, setSelectMenuAnchor, isMenuUpdating] =
		useOptimizedState<HTMLButtonElement | null>(null, {
			transitionUpdates: true,
		});

	const [selectedViews, setSelectedViews] = useState<string[]>([]);

	const [showTechnicalViews, setShowTechnicalViews] = useState<boolean>(() =>
		JSON.parse(localStorage.getItem("showTechnicalViews") || "false"),
	);

	// Debounced search functionality (even though not visible in UI, prepared for future)
	const [searchQuery] = useOptimizedState("", {
		transitionUpdates: true,
		debounceMs: 300,
	});
	const [debouncedSearchQuery] = useOptimizedDebounce(searchQuery, 300);

	const jumpToId = useApiMutation("jump");

	// Optimized list management for views
	const [views, setViews] = useState<any[]>([]);
	const [isPending] = useTransition();

	// Memoized function to calculate auto column count
	const getAutoColumnCount = useCallback(() => {
		const width = window.innerWidth;
		if (width < 900) return 1;
		else if (width < 1600) return 2;
		else if (width < 2100) return 3;
		return 4;
	}, []);

	// Deferred values for better performance during rapid changes
	const deferredViews = useDeferredValue(views);
	const deferredSelectedViews = useDeferredValue(selectedViews);

	// Memoized filtered views with search functionality
	const filteredViews = useMemo(() => {
		let filtered = showTechnicalViews
			? deferredViews
			: deferredViews.filter((view: any) => view.type !== "technical");

		// Apply search filter if query exists
		if (debouncedSearchQuery.trim()) {
			filtered = filtered.filter(
				(view: any) =>
					view.pretty_name
						?.toLowerCase()
						.includes(debouncedSearchQuery.toLowerCase()) ||
					view.description
						?.toLowerCase()
						.includes(debouncedSearchQuery.toLowerCase()),
			);
		}

		return filtered;
	}, [deferredViews, showTechnicalViews, debouncedSearchQuery]);

	// Effect for handling data changes with startTransition
	React.useEffect(() => {
		if (data?.data?.results?.[0]?.result?.data) {
			startTransition(() => {
				const apiViews = data.data.results[0].result.data;
				const filteredApiViews = showTechnicalViews
					? apiViews
					: apiViews.filter((view: any) => view.type !== "technical");

				// Handle view ordering first
				const savedOrder =
					JSON.parse(localStorage.getItem("viewOrder") as string) || [];
				let finalViews = filteredApiViews;
				if (savedOrder.length > 0) {
					finalViews = [...filteredApiViews].sort((a, b) => {
						const indexA = savedOrder.indexOf(a.endpoint);
						const indexB = savedOrder.indexOf(b.endpoint);
						return (
							(indexA === -1 ? Infinity : indexA) -
							(indexB === -1 ? Infinity : indexB)
						);
					});
				}

				// Set views only once
				setViews(finalViews);

				// Handle selected views with preserved state
				const saved = JSON.parse(
					localStorage.getItem("selectedViewsSaved") as string,
				);
				let newSelected: any;
				if (!saved || saved.length === 0) {
					newSelected = filteredApiViews.map((view: any) => view.name);
				} else {
					newSelected = saved.filter((endpoint: any) =>
						filteredApiViews.some((view: any) => view.name === endpoint),
					);
				}
				localStorage.setItem("selectedViewsSaved", JSON.stringify(newSelected));
				setSelectedViews(newSelected);
			});
		}
	}, [data, showTechnicalViews]);

	// Effect for handling window resize with auto columns
	React.useEffect(() => {
		const handleResize = () => {
			if (autoColumns) {
				startTransition(() => {
					setColumns(getAutoColumnCount());
				});
			}
		};

		if (autoColumns) {
			window.addEventListener("resize", handleResize);
			handleResize();
		}

		return () => window.removeEventListener("resize", handleResize);
	}, [getAutoColumnCount, setColumns, autoColumns]);

	// Optimized handlers using React 19 features
	const handleSelectMenuOpen = useCallback<
		MouseEventHandler<HTMLButtonElement>
	>(
		(event) => {
			setSelectMenuAnchor(event.currentTarget);
		},
		[setSelectMenuAnchor],
	);

	const handleSelectMenuClose = useCallback<
		MouseEventHandler<HTMLButtonElement>
	>(() => {
		setSelectMenuAnchor(null);
	}, [setSelectMenuAnchor]);

	const handleViewToggle = useCallback(
		(endpoint: string) => {
			startTransition(() => {
				setSelectedViews((prev) => {
					const newSelected = prev.includes(endpoint)
						? prev.filter((id) => id !== endpoint)
						: [...prev, endpoint];

					localStorage.setItem(
						"selectedViewsSaved",
						JSON.stringify(newSelected),
					);

					const technicalViews = views.filter(
						(view) => view.type === "technical",
					);
					const openTechnicalViews = technicalViews.filter((view) =>
						newSelected.includes(view.name),
					);
					if (openTechnicalViews.length === 0 && showTechnicalViews) {
						setShowTechnicalViews(false);
						localStorage.setItem("showTechnicalViews", JSON.stringify(false));
					}
					return newSelected;
				});
			});
		},
		[views, showTechnicalViews],
	);

	const handleTechnicalViewsToggle = useCallback(() => {
		startTransition(() => {
			setShowTechnicalViews((prev) => {
				const newValue = !prev;
				localStorage.setItem("showTechnicalViews", JSON.stringify(newValue));

				const techViews =
					data?.data?.results?.[0]?.result?.data?.filter(
						(view: any) => view.type === "technical",
					) || [];
				const techEndpoints = techViews.map((view: any) => view.name);

				setSelectedViews((prevSelected) => {
					let updated: any;
					if (newValue) {
						updated = [...new Set([...prevSelected, ...techEndpoints])];
					} else {
						updated = prevSelected.filter(
							(endpoint) => !techEndpoints.includes(endpoint),
						);
					}
					localStorage.setItem("selectedViewsSaved", JSON.stringify(updated));
					return updated;
				});

				return newValue;
			});
		});
	}, [data]);

	const handleAutoColumnsToggle = useCallback(() => {
		const newAutoValue = !autoColumns;
		setAutoColumns(newAutoValue);
		localStorage.setItem("autoColumns", JSON.stringify(newAutoValue));

		if (newAutoValue) {
			// Apply auto sizing immediately when enabling
			startTransition(() => {
				setColumns(getAutoColumnCount());
			});
		}
	}, [autoColumns, getAutoColumnCount, setColumns]);

	const handleViewExpand = useCallback(
		(view: any) => {
			withLoading(async () => {
				if (view.name.endsWith("class_attribute_field")) {
					await jumpToId.mutateAsync({
						node: data?.data?.node_id,
					});
					navigate(`/add-node/form/${data?.data?.node_id}`);
				} else {
					navigate(`/home/${view.name}`)
					console.log("Expanding view:", view.name);
				}
			});
		},
		[setExpandedView, withLoading, jumpToId, data, navigate],
	);

	const handleCloseExpandedView = useCallback(() => {
		setIsModalOpen(false);
		// Delay clearing the view to allow modal close animation
		setTimeout(() => setExpandedView(null), 200);
	}, [setExpandedView]);

	const incrementColumns = useCallback(() => {
		startTransition(() => {
			setColumns((prev) => Math.min(prev + 1, filteredViews.length));
		});
	}, [filteredViews.length, setColumns]);

	const decrementColumns = useCallback(() => {
		startTransition(() => {
			setColumns((prev) => Math.max(prev - 1, 1));
		});
	}, [setColumns]);

	// Loading state with React 19 optimizations
	if (isLoading) {
		return (
			<ErrorBoundary>
				<LoadingStates.Page />
			</ErrorBoundary>
		);
	}

	// Error state with recovery
	if (error || !data || !data.data || !data.data.results) {
		return (
			<ErrorBoundary>
				<Box
					sx={{
						display: "flex",
						flexDirection: "column",
						alignItems: "center",
						justifyContent: "center",
						height: "100vh",
						padding: 3,
						bgcolor: "#2e3b55",
					}}
				>
					<Box
						sx={{
							bgcolor: "#fff3e0",
							p: 3,
							borderRadius: 2,
							color: "#ef6c00",
							textAlign: "center",
							maxWidth: "500px",
						}}
					>
						<Typography variant="h6" gutterBottom>
							Failed to Load Data
						</Typography>
						<Typography variant="body2" sx={{ mb: 2 }}>
							{error?.message || "Data is null or unavailable."}
						</Typography>
						<Button
							variant="contained"
							onClick={() => window.location.reload()}
							sx={{ mt: 1 }}
						>
							Retry
						</Button>
					</Box>
				</Box>
			</ErrorBoundary>
		);
	}

	const isPendingAny =
		isTransitioning || isColumnsUpdating || isMenuUpdating || isPending;

	return (
		<ErrorBoundary>
			<Box
				sx={{
					padding: { xs: "8px", sm: "16px", md: "40px" },
					maxWidth: "100%",
					margin: "0 auto",
					bgcolor: "#2e3b55",
					minHeight: "100vh",
					zIndex: 1,
					opacity: isPendingAny ? 0.8 : 1,
					transition: "opacity 0.2s ease-in-out",
				}}
				className="home-page"
			>
				{/* Header avec design amélioré */}
				<Paper
					elevation={2}
					sx={{
						p: { xs: 2, sm: 3 },
						mb: { xs: 2, sm: 4 },
						borderRadius: 3,
						bgcolor: "rgba(255, 255, 255, 0.95)",
						backdropFilter: "blur(10px)",
						border: "1px solid rgba(255, 255, 255, 0.2)",
					}}
				>
					<Box
						sx={{
							display: "flex",
							flexDirection: { xs: "column", lg: "row" },
							justifyContent: "space-between",
							alignItems: { xs: "flex-start", lg: "center" },
							gap: { xs: 2, sm: 3 },
						}}
					>
						{/* Section gauche - Contrôles principaux */}
						<Box
							sx={{
								display: "flex",
								flexDirection: { xs: "column", sm: "row" },
								gap: { xs: 2, sm: 3 },
								alignItems: { xs: "flex-start", sm: "center" },
								flex: 1,
							}}
						>
							{/* Sélection des vues */}
							<Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
								<Button
									variant="outlined"
									startIcon={<ViewModuleIcon />}
									onClick={handleSelectMenuOpen}
									disabled={isPendingAny}
									sx={{
										borderColor: "#90caf9",
										color: "#1976d2",
										textTransform: "none",
										borderRadius: 2,
										"&:hover": {
											borderColor: "#42a5f5",
											bgcolor: "rgba(25, 118, 210, 0.04)",
										},
									}}
								>
									Sélection des vues
								</Button>
								<Menu
									anchorEl={selectMenuAnchor}
									open={Boolean(selectMenuAnchor)}
									onClose={handleSelectMenuClose}
									PaperProps={{
										sx: {
											maxHeight: { xs: 300, sm: 400 },
											maxWidth: { xs: "90vw", sm: 350 },
											mt: 1,
											borderRadius: 2,
											boxShadow: theme.shadows[8],
										},
									}}
								>
									{filteredViews.map((view) => (
										<MenuItem
											key={view.name}
											sx={{
												py: 1,
												px: 2,
												borderRadius: 1,
												mx: 1,
												mb: 0.5,
												"&:hover": {
													bgcolor: "primary.50",
												},
											}}
										>
											<Box
												sx={{
													display: "flex",
													alignItems: "center",
													width: "100%",
												}}
												onClick={() => handleViewToggle(view.name)}
											>
												<Checkbox
													checked={deferredSelectedViews.includes(view.name)}
													sx={{
														color: "#90caf9",
														"&.Mui-checked": { color: "#90caf9" },
													}}
												/>
												<ListItemText
													primary={view.pretty_name}
													secondary={
														view.type === "technical" ? "Technique" : ""
													}
												/>
											</Box>
										</MenuItem>
									))}
								</Menu>
							</Box>

							{/* Toggle vues techniques */}
							<FormControlLabel
								control={
									<Switch
										checked={showTechnicalViews}
										onChange={handleTechnicalViewsToggle}
										disabled={isPendingAny}
										color="primary"
									/>
								}
								label="Vues techniques"
								sx={{
									"& .MuiFormControlLabel-label": {
										fontSize: { xs: "0.875rem", sm: "1rem" },
										color: "text.primary",
									},
								}}
							/>

							<Divider
								orientation="vertical"
								flexItem
								sx={{ display: { xs: "none", sm: "block" } }}
							/>

							{/* Contrôles de colonnes améliorés */}
							<Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
								<FormControlLabel
									control={
										<Switch
											checked={autoColumns}
											onChange={handleAutoColumnsToggle}
											color="primary"
										/>
									}
									label="Auto colonnes"
									sx={{
										"& .MuiFormControlLabel-label": {
											fontSize: { xs: "0.875rem", sm: "1rem" },
											color: "text.primary",
											fontWeight: autoColumns ? 600 : 400,
										},
										px: 2,
										py: 1,
										borderRadius: 2,
										bgcolor: autoColumns ? "primary.50" : "grey.50",
										transition: "all 0.2s ease-in-out",
									}}
								/>

								{!autoColumns && (
									<ButtonGroup
										variant="outlined"
										size="small"
										disabled={isPendingAny}
										sx={{
											"& .MuiButton-root": {
												minWidth: { xs: 32, sm: 40 },
												fontSize: { xs: "0.75rem", sm: "0.875rem" },
												borderColor: "#90caf9",
												color: "#1976d2",
												"&:hover": {
													borderColor: "#42a5f5",
													bgcolor: "rgba(25, 118, 210, 0.04)",
												},
											},
										}}
									>
										<Button onClick={decrementColumns} disabled={columns === 1}>
											<RemoveIcon fontSize="small" />
										</Button>
										<Button disabled sx={{ cursor: "default" }}>
											{columns}
										</Button>
										<Button
											onClick={incrementColumns}
											disabled={columns === filteredViews.length}
										>
											<AddIcon fontSize="small" />
										</Button>
									</ButtonGroup>
								)}
							</Box>
						</Box>

						<Box sx={{ minWidth: 220 }}>

							{/* Input manuel */}
							<DatePicker
								disableFuture
								value={dayjs(selectedDate)}
								onChange={(value) => {
									const newDate = dayjs(value).valueOf();
									if (!isNaN(newDate)) {
										setSelectedDate(newDate);
									}
								}}
								sx={{ mb: 1 }}
							/>

							{/* Slider */}
							<Slider
								size="small"
								min={dateRangeStart}
								max={dateRangeEnd}
								step={1000 * 60 * 60 * 24}
								value={selectedDate}
								onChange={handleDateChange}
								valueLabelDisplay="off"
							/>
						</Box>


						{/* Bouton Ajouter nouveau noeud */}
						<Button
							variant="contained"
							startIcon={<AddIcon />}
							onClick={() => navigate("/add-node")}
							disabled={isPendingAny}
							sx={{
								px: 3,
								py: 1.5,
								borderRadius: 2,
								textTransform: "none",
								fontSize: "1rem",
								fontWeight: 600,
								boxShadow: theme.shadows[3],
								"&:hover": {
									boxShadow: theme.shadows[6],
									transform: "translateY(-1px)",
								},
								transition: "all 0.2s ease-in-out",
							}}
						>
							Nouveau nœud
						</Button>
					</Box>
				</Paper>

				<Suspense fallback={<LoadingStates.Grid columns={columns} count={6} />}>
					<ViewGrid
						views={filteredViews}
						selectedViews={deferredSelectedViews}
						columns={columns}
						onViewToggle={handleViewToggle}
						onViewExpand={handleViewExpand}
					/>
				</Suspense>

				{/* Loading Indicator */}
				{isPendingAny && (
					<Box
						sx={{
							position: "fixed",
							bottom: 24,
							right: 24,
							zIndex: 1400,
						}}
					>
						<CircularProgress />
					</Box>
				)}

				{/* Modal optimisé pour expanded view */}
				<Dialog
					open={isModalOpen}
					onClose={handleCloseExpandedView}
					maxWidth="lg"
					fullWidth
					aria-labelledby="expanded-view-title"
					PaperProps={{
						sx: {
							height: "90vh",
							maxHeight: "90vh",
							borderRadius: 3,
							overflow: "hidden",
						},
					}}
					TransitionProps={{
						timeout: 300,
					}}
				>
					<DialogTitle
						id="expanded-view-title"
						sx={{
							display: "flex",
							justifyContent: "space-between",
							alignItems: "center",
							borderBottom: "1px solid #e0e0e0",
							bgcolor: "primary.50",
							py: 2,
						}}
					>
						<Typography variant="h6" sx={{ fontWeight: 600 }}>
							{expandedView?.pretty_name || "Vue agrandie"}
						</Typography>
						<IconButton
							onClick={handleCloseExpandedView}
							aria-label="Fermer la vue agrandie"
							sx={{
								"&:hover": {
									bgcolor: "primary.100",
								},
							}}
						>
							<CloseIcon />
						</IconButton>
					</DialogTitle>
					<DialogContent sx={{ p: 0, height: "100%" }}>
						{expandedView && isModalOpen && (
							<Suspense
								fallback={
									<Box
										sx={{
											display: "flex",
											flexDirection: "column",
											justifyContent: "center",
											alignItems: "center",
											height: "100%",
											gap: 2,
										}}
									>
										<CircularProgress size={48} />
										<Typography variant="body1" color="text.secondary">
											Chargement de {expandedView.pretty_name}...
										</Typography>
									</Box>
								}
							>
								<View
									viewId={expandedView.name}
									sx={{
										height: "100%",
										overflow: "auto",
									}}
								/>
							</Suspense>
						)}
					</DialogContent>
				</Dialog>
			</Box>
		</ErrorBoundary>
	);
});

export default HomePage;
