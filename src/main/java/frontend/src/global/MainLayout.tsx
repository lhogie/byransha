import { SearchBar } from "@components/Common/SearchBar";
import ErrorBoundary from "@components/ErrorBoundary";
import {
	LoadingStates,
	useLoadingState,
} from "@components/Loading/LoadingComponents";
import {
	startTransition,
	useDeferredValue,
	useOptimizedDebounce,
	useOptimizedList,
	useOptimizedState,
} from "@hooks/react19";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import {
	ChevronRight as ChevronRightIcon,
	Home as HomeIcon,
	Logout as LogoutIcon,
	MoreHoriz as MoreHorizIcon,
	Tune as TuneIcon,
} from "@mui/icons-material";
import {
	Alert,
	Avatar,
	Box,
	Breadcrumbs,
	Button,
	Chip,
	Link,
	MenuItem,
	type PopoverProps,
	Stack,
	Tooltip,
	Typography,
	useTheme,
} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import { useQueryClient } from "@tanstack/react-query";
import { DashboardLayout } from "@toolpad/core";
import React, { memo, Suspense, useCallback, useMemo } from "react";
import { Outlet, Link as RouterLink, useNavigate } from "react-router";

// Memoized UserInfo component with enhanced accessibility
const UserInfo = memo(
	({
		data,
		isLoading,
		error,
	}: {
		data: any;
		isLoading: boolean;
		error: any;
	}) => {
		const _theme = useTheme();

		if (isLoading) {
			return (
				<LoadingStates.Inline text="Chargement des informations utilisateur..." />
			);
		}

		if (error) {
			return (
				<Alert severity="error" sx={{ fontSize: "0.75rem" }}>
					Erreur de chargement utilisateur
				</Alert>
			);
		}

		const username = data?.data?.username || "Utilisateur inconnu";
		const userId = data?.data?.user_id || "N/A";
		const version = data?.data["backend version"] || "N/A";

		return (
			<Box
				sx={{
					display: "flex",
					alignItems: "center",
					gap: 1.5,
					p: 1.5,
					bgcolor: "background.paper",
					transition: "all 0.2s ease",
				}}
				aria-label="Informations utilisateur"
			>
				<Avatar
					sx={{
						width: 32,
						height: 32,
						bgcolor: "primary.main",
						fontSize: "0.875rem",
					}}
					aria-label={`Avatar de ${username}`}
				>
					{username.charAt(0).toUpperCase()}
				</Avatar>
				<Box sx={{ display: "flex", flexDirection: "column", minWidth: 0 }}>
					<Typography
						variant="body2"
						sx={{
							fontWeight: 600,
							color: "text.primary",
							fontSize: "0.875rem",
							whiteSpace: "nowrap",
							overflow: "hidden",
							textOverflow: "ellipsis",
						}}
						title={username}
					>
						{username}
					</Typography>
					<Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
						<Typography
							variant="caption"
							sx={{
								color: "text.secondary",
								fontSize: "0.75rem",
							}}
							title={`ID utilisateur: ${userId}`}
						>
							ID: {userId}
						</Typography>
						<Chip
							label={`v${version}`}
							size="small"
							variant="outlined"
							sx={{
								height: 16,
								fontSize: "0.6rem",
								"& .MuiChip-label": {
									px: 0.5,
								},
							}}
						/>
					</Box>
				</Box>
			</Box>
		);
	},
);

UserInfo.displayName = "UserInfo";

// Enhanced BreadcrumbNav component with better accessibility
const BreadcrumbNav = memo(
	({
		history,
		currentNode,
		onHistoryClick,
		onMoreClick,
		menuAnchor,
		onMenuClose,
	}: {
		history: any[];
		currentNode: any;
		onHistoryClick: (hist: any) => void;
		onMoreClick: (event: React.MouseEvent<HTMLButtonElement>) => void;
		menuAnchor: PopoverProps["anchorEl"];
		onMenuClose: () => void;
	}) => {
		const theme = useTheme();
		const deferredHistory = useDeferredValue(history);
		const visibleHistory = useMemo(() => {
			return deferredHistory.length > 3
				? deferredHistory.slice(-2)
				: deferredHistory;
		}, [deferredHistory]);

		return (
			<ErrorBoundary
				fallback={<Alert severity="error">Erreur de navigation</Alert>}
			>
				<Breadcrumbs
					separator={
						<ChevronRightIcon
							sx={{ color: "text.disabled", fontSize: "18px" }}
						/>
					}
					aria-label="Fil d'Ariane de navigation"
					sx={{
						bgcolor: "transparent",
						p: 0.5,
						"& .MuiBreadcrumbs-ol": { alignItems: "center" },
					}}
				>
					<Link
						component={RouterLink}
						to="/home"
						sx={{
							display: "flex",
							alignItems: "center",
							gap: 0.5,
							color: "text.secondary",
							fontSize: "0.875rem",
							textDecoration: "none",
							p: 1,
							borderRadius: 1,
							transition: "all 0.2s ease",
							"&:hover": {
								color: "primary.main",
								bgcolor: "action.hover",
							},
							"&:focus": {
								outline: `2px solid ${theme.palette.primary.main}`,
								outlineOffset: "2px",
							},
						}}
						aria-label="Retour à l'accueil"
					>
						<HomeIcon fontSize="small" />
						Accueil
					</Link>

					{deferredHistory.length > 3 && (
						<Tooltip title="Afficher tout l'historique">
							<IconButton
								onClick={onMoreClick}
								size="small"
								aria-label="Afficher l'historique complet de navigation"
								aria-expanded={Boolean(menuAnchor)}
								aria-haspopup="true"
								sx={{
									color: "text.disabled",
									"&:hover": {
										color: "primary.main",
										bgcolor: "action.hover",
									},
									"&:focus": {
										outline: `2px solid ${theme.palette.primary.main}`,
										outlineOffset: "2px",
									},
								}}
							>
								<MoreHorizIcon fontSize="small" />
							</IconButton>
						</Tooltip>
					)}

					{visibleHistory.map((hist: any) => {
						const isCurrentNode = hist === currentNode;
						return (
							<Link
								key={hist.id}
								component="button"
								onClick={() => onHistoryClick(hist)}
								sx={{
									color: isCurrentNode ? "primary.main" : "text.secondary",
									fontSize: "0.875rem",
									fontWeight: isCurrentNode ? 600 : 400,
									textDecoration: "none",
									p: 1,
									borderRadius: 1,
									bgcolor: isCurrentNode ? "action.selected" : "transparent",
									transition: "all 0.2s ease",
									border: "none",
									cursor: "pointer",
									maxWidth: 150,
									overflow: "hidden",
									textOverflow: "ellipsis",
									whiteSpace: "nowrap",
									"&:hover": {
										color: "primary.main",
										bgcolor: "action.hover",
									},
									"&:focus": {
										outline: `2px solid ${theme.palette.primary.main}`,
										outlineOffset: "2px",
									},
								}}
								aria-label={`Naviguer vers ${hist.pretty_name}`}
								aria-current={isCurrentNode ? "page" : undefined}
								title={hist.pretty_name}
							>
								{hist.pretty_name}
							</Link>
						);
					})}
				</Breadcrumbs>

				<Menu
					anchorEl={menuAnchor}
					open={Boolean(menuAnchor)}
					onClose={onMenuClose}
					anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
					transformOrigin={{ vertical: "top", horizontal: "center" }}
					aria-label="Menu de l'historique de navigation"
					PaperProps={{
						sx: {
							borderRadius: 2,
							boxShadow: theme.shadows[8],
							mt: 1,
							minWidth: 200,
							maxWidth: 300,
						},
					}}
				>
					{deferredHistory.map((hist: any) => {
						const isCurrentNode = hist === currentNode;
						return (
							<MenuItem
								key={hist.id}
								onClick={() => {
									onHistoryClick(hist);
									onMenuClose();
								}}
								sx={{
									fontSize: "0.875rem",
									color: isCurrentNode ? "primary.main" : "text.primary",
									bgcolor: isCurrentNode ? "action.selected" : "transparent",
									"&:hover": {
										bgcolor: "action.hover",
										color: "primary.main",
									},
									"&:focus": {
										bgcolor: "action.focus",
									},
								}}
								role="menuitem"
								aria-current={isCurrentNode ? "page" : undefined}
							>
								<Typography
									variant="body2"
									sx={{
										overflow: "hidden",
										textOverflow: "ellipsis",
										whiteSpace: "nowrap",
									}}
								>
									{hist.pretty_name}
								</Typography>
							</MenuItem>
						);
					})}
				</Menu>
			</ErrorBoundary>
		);
	},
);

BreadcrumbNav.displayName = "BreadcrumbNav";

const MainLayout = memo(() => {
	const theme = useTheme();
	const navigate = useNavigate();
	const queryClient = useQueryClient();
	const { isLoading: isTransitioning, withLoading } = useLoadingState();

	// React 19 optimized state management
	const [menuAnchor, setMenuAnchor, isMenuUpdating] = useOptimizedState<
		PopoverProps["anchorEl"]
	>(null, {
		transitionUpdates: true,
	});

	const [error, setError, isErrorUpdating] = useOptimizedState<string | null>(
		null,
		{
			transitionUpdates: true,
		},
	);

	// Debounced error clearing
	const [errorToClear, setErrorToClear] = useOptimizedState<string | null>(
		null,
		{
			debounceMs: 5000,
			transitionUpdates: true,
		},
	);
	const [debouncedErrorClear] = useOptimizedDebounce(errorToClear, 5000);

	// API data with React 19 optimizations
	const {
		data,
		isLoading,
		error: apiError,
	} = useApiData(
		"endpoints?only_applicable&type=byransha.web.View",
		{},
		{
			staleTime: 60000,
			gcTime: 5 * 60 * 1000,
			refetchOnWindowFocus: false,
			refetchOnReconnect: true,
		},
	);

	const { data: historyData } = useApiData(
		"user_history",
		{},
		{
			staleTime: 30000,
			gcTime: 2 * 60 * 1000,
			refetchOnWindowFocus: false,
		},
	);

	// Optimized list management for history
	const {
		items: history,
		setItems: setHistory,
		isPending: isHistoryUpdating,
	} = useOptimizedList(
		historyData?.data?.results?.[0]?.result?.data ?? [],
		(item: any) => item.id,
	);

	// Deferred values for better performance
	const deferredHistory = useDeferredValue(history);
	const deferredData = useDeferredValue(data);

	// Mutations with error handling
	const jumpMutation = useApiMutation("jump", {
		onSuccess: async () => {
			startTransition(() => {
				queryClient.invalidateQueries({ queryKey: ["apiData"] });
				setError(null);
			});
		},
		onError: (error: any) => {
			setError(`Navigation failed: ${error.message}`);
			setErrorToClear("clear");
		},
	});

	const logoutMutation = useApiMutation("logout", {
		onSuccess: async () => {
			startTransition(() => {
				queryClient.clear();
			});
		},
		onError: (error: any) => {
			setError(`Logout failed: ${error.message}`);
			setErrorToClear("clear");
		},
	});

	const bnodeClassDistribution = useApiMutation("class_distribution", {
		onError: (error: any) => {
			setError(`Failed to load classes: ${error.message}`);
			setErrorToClear("clear");
		},
	});

	const addNodeMutation = useApiMutation("add_node", {
		onError: (error: any) => {
			setError(`Failed to create node: ${error.message}`);
			setErrorToClear("clear");
		},
	});

	// Clear error after debounce
	React.useEffect(() => {
		if (debouncedErrorClear) {
			setError(null);
			setErrorToClear(null);
		}
	}, [debouncedErrorClear, setError, setErrorToClear]);

	// Update history when data changes
	React.useEffect(() => {
		if (historyData?.data?.results?.[0]?.result?.data) {
			setHistory(historyData.data.results[0].result.data);
		}
	}, [historyData, setHistory]);

	// Optimized handlers
	const jumpToNode = useCallback(
		(nodeId: number) => {
			withLoading(async () => {
				try {
					await jumpMutation.mutateAsync({ node_id: nodeId });
				} catch (err) {
					console.error("Jump to node failed:", err);
				}
			});
		},
		[jumpMutation, withLoading],
	);

	const handleHistoryClick = useCallback(
		(hist: { id: number }) => {
			jumpToNode(hist.id);
		},
		[jumpToNode],
	);

	const handleLogout = useCallback(async () => {
		await withLoading(async () => {
			try {
				await logoutMutation.mutateAsync({});
			} catch (err) {
				console.error("Logout failed:", err);
			} finally {
				startTransition(() => {
					navigate("/");
				});
			}
		});
	}, [logoutMutation, withLoading, navigate]);

	const handleMoreClick = useCallback(
		(event: React.MouseEvent<HTMLButtonElement>) => {
			setMenuAnchor(event.currentTarget);
		},
		[setMenuAnchor],
	);

	const handleMenuClose = useCallback(() => {
		setMenuAnchor(null);
	}, [setMenuAnchor]);

	const handleCreateAndJump = useCallback(
		async (name: string) => {
			const fullName = name || "byransha.labmodel.model.v0.SearchForm";
			if (!fullName) return;

			try {
				const response = await addNodeMutation.mutateAsync({
					BNodeClass: fullName,
				});

				const nodeId = response?.data?.results?.[0]?.result?.data.id;
				await jumpMutation.mutateAsync({ node_id: nodeId });
				return nodeId;
			} catch (err) {
				console.error(`Error during handleCreateAndJump for ${fullName}:`, err);
				throw err;
			}
		},
		[addNodeMutation, jumpMutation],
	);

	const handleClickClass = useCallback(async () => {
		await withLoading(async () => {
			try {
				const response = await bnodeClassDistribution.mutateAsync({});
				const classList = response?.data?.results?.[0]?.result?.data || [];

				const searchFormClass = classList.find((item: any) => {
					const fullName = Object.keys(item)[0];
					return fullName?.includes("SearchForm");
				});

				const nodeId = await handleCreateAndJump(
					Object.keys(searchFormClass)[0],
				);
				navigate(`/add-node/form/${nodeId}`);
			} catch (err) {
				console.error("Navigation skipped due to error:", err);
			}
		});
	}, [bnodeClassDistribution, handleCreateAndJump, navigate, withLoading]);

	// Memoized current node calculation
	const currentNode = useMemo(() => {
		return deferredHistory[deferredHistory.length - 1];
	}, [deferredHistory]);

	const isPendingAny =
		isTransitioning || isMenuUpdating || isErrorUpdating || isHistoryUpdating;

	return (
		<ErrorBoundary
			fallback={
				<Box sx={{ p: 3, textAlign: "center" }}>
					<Alert severity="error">
						Main layout failed to load. Please refresh the page.
					</Alert>
				</Box>
			}
			onError={(error, errorInfo) => {
				console.error("MainLayout error:", error, errorInfo);
				setError(`Layout error: ${error.message}`);
			}}
		>
			<Box
				sx={{
					'& .MuiDrawer-root .MuiDrawer-paper, & [role="navigation"]': {
						"&::-webkit-scrollbar": { display: "none" },
						msOverflowStyle: "none",
						scrollbarWidth: "none",
					},
					opacity: isPendingAny ? 0.9 : 1,
					transition: "opacity 0.2s ease-in-out",
				}}
			>
				{/* Error display */}
				{error && (
					<Alert
						severity="error"
						onClose={() => setError(null)}
						sx={{
							position: "fixed",
							top: 16,
							left: "50%",
							transform: "translateX(-50%)",
							zIndex: 10000,
							maxWidth: "500px",
						}}
					>
						{error}
					</Alert>
				)}

				<DashboardLayout
					hideNavigation={true}
					disableCollapsibleSidebar={true}
					slots={{
						appTitle: () => (
							<ErrorBoundary
								fallback={<Alert severity="error">Erreur de navigation</Alert>}
							>
								<Stack
									direction="row"
									alignItems="center"
									spacing={2}
									sx={{ minWidth: 0, flex: 1 }}
								>
									<Box
										sx={{
											height: 40,
											display: "flex",
											alignItems: "center",
											flexShrink: 0,
										}}
									>
										<img
											src="/logo.svg"
											alt="Logo I3S"
											style={{ height: "100%" }}
										/>
									</Box>

									<Box sx={{ minWidth: 0, flex: 1 }}>
										<Suspense
											fallback={
												<LoadingStates.Inline text="Chargement navigation..." />
											}
										>
											<BreadcrumbNav
												history={deferredHistory}
												currentNode={currentNode}
												onHistoryClick={handleHistoryClick}
												onMoreClick={handleMoreClick}
												menuAnchor={menuAnchor}
												onMenuClose={handleMenuClose}
											/>
										</Suspense>
									</Box>
								</Stack>
							</ErrorBoundary>
						),
						toolbarActions: () => (
							<ErrorBoundary
								fallback={
									<Alert severity="error">Erreur de la barre d'outils</Alert>
								}
							>
								<Stack
									direction="row"
									alignItems="center"
									spacing={1.5}
									sx={{ flexWrap: "wrap", gap: 1 }}
								>
									<Tooltip title="Ouvrir la recherche avancée">
										<span>
											<IconButton
												onClick={handleClickClass}
												disabled={isPendingAny}
												size="medium"
												aria-label="Ouvrir la recherche avancée"
												sx={{
													"&:disabled": { opacity: 0.6 },
													"&:focus": {
														outline: `2px solid ${theme.palette.primary.main}`,
														outlineOffset: "2px",
													},
												}}
											>
												<TuneIcon />
											</IconButton>
										</span>
									</Tooltip>

									<Box sx={{ display: { xs: "none", md: "block" } }}>
										<Suspense
											fallback={<LoadingStates.Inline text="Recherche..." />}
										>
											<SearchBar />
										</Suspense>
									</Box>

									<Box sx={{ display: { xs: "none", sm: "block" } }}>
										<UserInfo
											data={deferredData}
											isLoading={isLoading}
											error={apiError}
										/>
									</Box>

									<Tooltip title="Se déconnecter de l'application">
										<Button
											variant="outlined"
											size="small"
											startIcon={<LogoutIcon />}
											onClick={handleLogout}
											disabled={isPendingAny}
											aria-label="Se déconnecter"
											sx={{
												"&:disabled": { opacity: 0.6 },
												fontSize: { xs: "0.75rem", sm: "0.875rem" },
												"&:focus": {
													outline: `2px solid ${theme.palette.primary.main}`,
													outlineOffset: "2px",
												},
											}}
										>
											<Box sx={{ display: { xs: "none", sm: "block" } }}>
												Déconnexion
											</Box>
										</Button>
									</Tooltip>
								</Stack>
							</ErrorBoundary>
						),
					}}
				>
					<Box
						component="main"
						sx={{
							mt: 2,
							px: { xs: 1, sm: 2 },
						}}
						aria-label="Contenu principal"
					>
						<ErrorBoundary
							fallback={
								<Box sx={{ p: 3, textAlign: "center" }}>
									<Alert
										severity="error"
										action={
											<Button
												color="inherit"
												size="small"
												onClick={() => window.location.reload()}
											>
												Actualiser
											</Button>
										}
									>
										<Typography variant="h6" gutterBottom>
											Erreur de chargement du contenu
										</Typography>
										<Typography variant="body2">
											Le contenu de la page n'a pas pu être chargé. Veuillez
											actualiser la page.
										</Typography>
									</Alert>
								</Box>
							}
						>
							<Suspense
								fallback={
									<Box sx={{ p: 3, textAlign: "center" }} aria-live="polite">
										<LoadingStates.Component message="Chargement du contenu..." />
									</Box>
								}
							>
								<Outlet />
							</Suspense>
						</ErrorBoundary>
					</Box>
				</DashboardLayout>

				{/* Enhanced loading indicator for global operations */}
				{isPendingAny && (
					<Box
						sx={{
							position: "fixed",
							bottom: 24,
							right: 24,
							zIndex: theme.zIndex.snackbar,
						}}
						aria-live="polite"
						aria-label="Opération en cours"
					>
						<Alert
							severity="info"
							sx={{
								alignItems: "center",
								"& .MuiAlert-icon": {
									mr: 1,
								},
							}}
						>
							<LoadingStates.Inline text="Traitement en cours..." />
						</Alert>
					</Box>
				)}
			</Box>
		</ErrorBoundary>
	);
});

MainLayout.displayName = "MainLayout";

export default MainLayout;
