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
import "./HomePage.css";
import { View } from "@common/View";
import ErrorBoundary from "@components/ErrorBoundary";
import {
	LoadingStates,
	useLoadingState,
} from "@components/Loading/LoadingComponents";
import { useTitle } from "@global/useTitle";
import { useOptimizedDebounce, useOptimizedState } from "@hooks/react19";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import AddIcon from "@mui/icons-material/Add";
import Expand from "@mui/icons-material/AspectRatio";
import CloseIcon from "@mui/icons-material/Close";
import RemoveIcon from "@mui/icons-material/Remove";
import {
	Box,
	Button,
	Card,
	CardContent,
	Checkbox,
	CircularProgress,
	ListItemText,
	Menu,
	MenuItem,
	Typography,
} from "@mui/material";
import { useNavigate } from "react-router";

// Memoized ViewCard component with React 19 optimizations
const ViewCard = memo(
	({
		view,
		onClick,
		handleViewToggle,
	}: {
		view: any;
		onClick: MouseEventHandler<any>;
		handleViewToggle: (endpoint: string) => void;
	}) => {
		const handleToggle = useCallback(
			(e: React.MouseEvent) => {
				e.preventDefault();
				startTransition(() => {
					handleViewToggle(view.name);
				});
			},
			[handleViewToggle, view.name],
		);

		return (
			<ErrorBoundary
				fallback={
					<Card
						sx={{
							aspectRatio: "1",
							border: "1px solid #f44336",
							borderRadius: 2,
							display: "flex",
							alignItems: "center",
							justifyContent: "center",
							bgcolor: "#ffebee",
						}}
					>
						<Typography color="error">Error loading view</Typography>
					</Card>
				}
			>
				<Card
					sx={{
						cursor: "pointer",
						aspectRatio: "1",
						border: "1px solid #e0e0e0",
						borderRadius: 2,
						display: "flex",
						flexDirection: "column",
						bgcolor: view.type === "technical" ? "#fff9c4" : "#ffffff",
						transition: "all 0.2s ease-in-out",
						"&:hover": {
							transform: "translateY(-2px)",
							boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
						},
					}}
				>
					<Box
						sx={{
							height: "40px",
							width: "100%",
							bgcolor: view.type === "technical" ? "#fff8b0" : "#f5f5f5",
							borderBottom: "1px solid #e0e0e0",
							display: "flex",
							alignItems: "center",
							justifyContent: "center",
							cursor: "grab",
							position: "relative",
						}}
					>
						<Typography
							className="DragHere"
							variant="caption"
							sx={{
								color: "#757575",
								textAlign: "center",
								flex: 1,
								px: 1,
							}}
						>
							{`${view.pretty_name} - ${view.description}`}
						</Typography>
						<Box sx={{ display: "flex", position: "absolute", right: 4 }}>
							<button
								type="button"
								className="expand-card"
								onClick={onClick}
								aria-label="expand"
								style={{
									background: "none",
									border: "none",
									cursor: "pointer",
									padding: "2px",
									display: "flex",
									alignItems: "center",
								}}
							>
								<Expand fontSize="small" />
							</button>
							<button
								type="button"
								className="erased-card"
								onClick={handleToggle}
								aria-label="close"
								style={{
									background: "none",
									border: "none",
									cursor: "pointer",
									padding: "2px",
									display: "flex",
									alignItems: "center",
								}}
							>
								<CloseIcon fontSize="small" />
							</button>
						</Box>
					</Box>
					<CardContent
						sx={{
							padding: { xs: "12px", sm: "16px" },
							height: "calc(100% - 40px)",
							display: "flex",
							flexDirection: "column",
							overflow: "hidden",
							bgcolor: view.type === "technical" ? "#fff9c4" : "#ffffff",
						}}
					>
						<Box
							sx={{
								flex: 1,
								overflow: "auto",
								color: "#424242",
								msOverflowStyle: "none",
								scrollbarWidth: "thin",
								scrollbarColor: "#3f51b5 #e8eaf6",
								"&::-webkit-scrollbar": { width: "6px" },
								"&::-webkit-scrollbar-thumb": {
									bgcolor: "#3f51b5",
									borderRadius: "3px",
								},
								"&::-webkit-scrollbar-track": { bgcolor: "#e8eaf6" },
								wordBreak: "break-word",
								overflowWrap: "break-word",
								whiteSpace: "pre-wrap",
								maxWidth: "100%",
								fontSize: { xs: "0.75rem", sm: "0.875rem" },
							}}
						>
							{view.error ? (
								<Typography color="error" variant="body2">
									{view.error}
								</Typography>
							) : (
								<ErrorBoundary
									fallback={
										<Typography color="error" variant="body2">
											Error loading view
										</Typography>
									}
								>
									<Suspense
										fallback={<LoadingStates.Inline text="Loading view..." />}
									>
										<View
											viewId={view.name.replaceAll(" ", "_")}
											sx={{
												bgcolor:
													view.type === "technical" ? "#fff9c4" : "#ffffff",
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

ViewCard.displayName = "ViewCard";

// Memoized ViewGrid component using React 19 hooks
const ViewGrid = memo(
	({
		views,
		selectedViews,
		columns,
		onViewClick,
		onViewToggle,
	}: {
		views: any[];
		selectedViews: string[];
		columns: number;
		onViewClick: (view: any) => void;
		onViewToggle: (endpoint: string) => void;
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
						display: "flex",
						flexWrap: "wrap",
						gap: { xs: 2, sm: 4 },
						opacity: 1,
						transition: "opacity 0.2s ease-in-out",
					}}
				>
					{deferredVisibleViews.map((view) => (
						<Box
							key={view.name}
							sx={{
								width: {
									xs: "100%",
									sm: `calc(${100 / Math.min(columns, 2)}% - 16px)`,
									md: `calc(${100 / rowColumns}% - 32px)`,
								},
							}}
						>
							<ViewCard
								view={view}
								onClick={(e) => {
									if (e.defaultPrevented) return;
									onViewClick(view);
								}}
								handleViewToggle={onViewToggle}
							/>
						</Box>
					))}
				</Box>
			</ErrorBoundary>
		);
	},
);

ViewGrid.displayName = "ViewGrid";

const HomePage = memo(() => {
	const navigate = useNavigate();
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

	useTitle("Home");

	// Use React 19 optimized state management
	const [columns, setColumns, isColumnsUpdating] = useOptimizedState(2, {
		transitionUpdates: true,
		deferUpdates: false,
	});

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

	// Effect for handling window resize with startTransition
	React.useEffect(() => {
		const handleResize = () => {
			startTransition(() => {
				setColumns(getAutoColumnCount());
			});
		};

		window.addEventListener("resize", handleResize);
		handleResize();

		return () => window.removeEventListener("resize", handleResize);
	}, [getAutoColumnCount, setColumns]);

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

	const handleViewClick = useCallback(
		(view: any) => {
			withLoading(async () => {
				if (view.name.endsWith("class_attribute_field")) {
					await jumpToId.mutateAsync({
						node: data?.data?.node_id,
					});
					navigate(`/add-node/form/${data?.data?.node_id}`);
				} else {
					navigate(`/information/${view.name.replaceAll(" ", "_")}`);
				}
			});
		},
		[jumpToId, data, navigate, withLoading],
	);

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
				<Box
					sx={{
						display: "flex",
						flexDirection: { xs: "column", sm: "row" },
						justifyContent: "space-between",
						alignItems: { xs: "flex-start", sm: "center" },
						mb: { xs: 2, sm: 4 },
						gap: { xs: 2, sm: 0 },
					}}
				>
					<Box
						sx={{
							display: "flex",
							flexDirection: { xs: "column", sm: "row" },
							gap: { xs: 1, sm: 2 },
							alignItems: { xs: "flex-start", sm: "center" },
							width: { xs: "100%", sm: "auto" },
						}}
					>
						<Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
							<Button
								variant="outlined"
								onClick={handleSelectMenuOpen}
								disabled={isPendingAny}
								sx={{
									minWidth: { xs: 36, sm: 40 },
									borderWidth: "2px",
									borderColor: "#90caf9",
									color: "#90caf9",
									fontSize: { xs: "0.75rem", sm: "0.875rem" },
									padding: { xs: "4px 8px", sm: "6px 12px" },
									"&:hover": {
										borderColor: "#42a5f5",
										bgcolor: "#37474f",
									},
									"&:disabled": {
										opacity: 0.6,
									},
								}}
							>
								Views
							</Button>
							<Menu
								anchorEl={selectMenuAnchor}
								open={Boolean(selectMenuAnchor)}
								onClose={handleSelectMenuClose}
								PaperProps={{
									sx: {
										maxHeight: 500,
										maxWidth: 400,
										overflowY: "auto",
										width: "auto",
										padding: 1,
										borderRadius: "8px",
									},
								}}
							>
								{filteredViews.map((view) => (
									<MenuItem
										key={view.name}
										sx={{
											display: "flex",
											alignItems: "center",
											justifyContent: "space-between",
											paddingRight: 1,
											fontSize: "14px",
											color: view.type === "technical" ? "#283593" : "#424242",
											backgroundColor:
												view.type === "technical" ? "#fff9c4" : "transparent",
											borderRadius: "8px",
											"&:hover": {
												backgroundColor:
													view.type === "technical" ? "#fff8b0" : "#e8eaf6",
											},
										}}
									>
										<Box
											sx={{
												display: "flex",
												alignItems: "center",
												flexGrow: 1,
												borderRadius: "18px",
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
											<ListItemText primary={view.pretty_name} />
										</Box>
									</MenuItem>
								))}
							</Menu>
						</Box>

						<Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
							<Checkbox
								checked={showTechnicalViews}
								onChange={handleTechnicalViewsToggle}
								disabled={isPendingAny}
								sx={{
									color: "#90caf9",
									"&.Mui-checked": { color: "#90caf9" },
									padding: { xs: "4px", sm: "6px" },
								}}
							/>
							<Typography
								sx={{
									color: "#90caf9",
									fontSize: { xs: "0.75rem", sm: "0.875rem" },
								}}
							>
								Show Technical Views
							</Typography>
						</Box>

						<Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
							<Button
								variant="outlined"
								onClick={decrementColumns}
								disabled={columns === 1 || isPendingAny}
								sx={{
									minWidth: { xs: 36, sm: 40 },
									borderWidth: "2px",
									borderColor: "#90caf9",
									color: "#90caf9",
									fontSize: { xs: "0.75rem", sm: "0.875rem" },
									padding: { xs: "4px", sm: "6px" },
									"&:hover": {
										borderColor: "#42a5f5",
										bgcolor: "#37474f",
									},
								}}
							>
								<RemoveIcon fontSize="small" />
							</Button>
							<Typography
								sx={{
									color: "#ffffff",
									fontWeight: "bold",
									fontSize: { xs: "0.875rem", sm: "1rem" },
								}}
							>
								{columns}
							</Typography>
							<Button
								variant="outlined"
								onClick={incrementColumns}
								disabled={columns === filteredViews.length || isPendingAny}
								sx={{
									minWidth: { xs: 36, sm: 40 },
									borderWidth: "2px",
									borderColor: "#90caf9",
									color: "#90caf9",
									fontSize: { xs: "0.75rem", sm: "0.875rem" },
									padding: { xs: "4px", sm: "6px" },
									"&:hover": {
										borderColor: "#42a5f5",
										bgcolor: "#37474f",
									},
								}}
							>
								<AddIcon fontSize="small" />
							</Button>
						</Box>
					</Box>

					<Button
						variant="outlined"
						onClick={() => navigate("/add-node")}
						disabled={isPendingAny}
						sx={{
							minWidth: { xs: 36, sm: 40 },
							borderWidth: "2px",
							borderColor: "#90caf9",
							color: "#90caf9",
							fontSize: { xs: "0.75rem", sm: "0.875rem" },
							padding: { xs: "4px 8px", sm: "6px 12px" },
							"&:hover": {
								borderColor: "#42a5f5",
								bgcolor: "#37474f",
							},
						}}
					>
						Add new node
					</Button>
				</Box>

				<Suspense fallback={<LoadingStates.Grid columns={columns} count={6} />}>
					<ViewGrid
						views={filteredViews}
						selectedViews={deferredSelectedViews}
						columns={columns}
						onViewClick={handleViewClick}
						onViewToggle={handleViewToggle}
					/>
				</Suspense>

				{isPendingAny && (
					<Box
						sx={{
							position: "fixed",
							bottom: 16,
							right: 16,
							zIndex: 9999,
						}}
					>
						<CircularProgress size={24} sx={{ color: "#90caf9" }} />
					</Box>
				)}
			</Box>
		</ErrorBoundary>
	);
});

HomePage.displayName = "HomePage";

export default HomePage;
