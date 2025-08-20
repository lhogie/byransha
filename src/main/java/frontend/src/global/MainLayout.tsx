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
	Close as CloseIcon,
	Home as HomeIcon,
	Logout as LogoutIcon,
	MoreHoriz as MoreHorizIcon,
	Search as SearchIcon,
	ManageSearchRounded as TuneIcon,
} from "@mui/icons-material";
import {
	Alert,
	Avatar,
	Box,
	Breadcrumbs,
	Button,
	Chip,
	Dialog,
	DialogContent,
	DialogTitle,
	Drawer,
	IconButton,
	Link,
	MenuItem,
	type PopoverProps, Slider,
	Stack,
	Tooltip,
	Typography,
	useMediaQuery,
	useTheme,
} from "@mui/material";
import Menu from "@mui/material/Menu";
import { useQueryClient } from "@tanstack/react-query";
import { DashboardLayout } from "@toolpad/core";
import React, { memo, Suspense, useCallback, useMemo, useState } from "react";
import { Outlet, Link as RouterLink, useNavigate } from "react-router";
import {DatePicker} from "@mui/x-date-pickers/DatePicker";
import dayjs from "dayjs";

// Mobile-optimized UserInfo component
const MobileUserInfo = memo(
	({
		data,
		isLoading,
		error,
	}: {
		data: any;
		isLoading: boolean;
		error: any;
	}) => {
		if (isLoading) {
			return (
				<Avatar sx={{ width: 32, height: 32, bgcolor: "grey.300" }}>
					<Typography variant="caption">...</Typography>
				</Avatar>
			);
		}

		if (error) {
			return (
				<Avatar sx={{ width: 32, height: 32, bgcolor: "error.main" }}>
					<Typography variant="caption" color="white">
						!
					</Typography>
				</Avatar>
			);
		}

		const username = data?.data?.username || "User";

		return (
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
		);
	},
);

MobileUserInfo.displayName = "MobileUserInfo";

// Full UserInfo component for larger screens
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

// Mobile Search Dialog
const MobileSearchDialog = memo(
	({ open, onClose }: { open: boolean; onClose: () => void }) => {
		return (
			<Dialog
				open={open}
				onClose={onClose}
				fullScreen
				sx={{
					"& .MuiDialog-paper": {
						bgcolor: "background.default",
					},
				}}
			>
				<DialogTitle
					sx={{
						display: "flex",
						alignItems: "center",
						justifyContent: "space-between",
						p: 2,
					}}
				>
					<Typography variant="h6">Rechercher</Typography>
					<IconButton onClick={onClose} aria-label="Fermer">
						<CloseIcon />
					</IconButton>
				</DialogTitle>
				<DialogContent sx={{ p: 2 }}>
					<SearchBar />
				</DialogContent>
			</Dialog>
		);
	},
);

MobileSearchDialog.displayName = "MobileSearchDialog";

// Responsive BreadcrumbNav component
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
		const isMobile = useMediaQuery(theme.breakpoints.down("md"));
		const deferredHistory = useDeferredValue(history);

		const visibleHistory = useMemo(() => {
			// Show fewer items on mobile
			const maxItems = isMobile ? 1 : 3;
			return deferredHistory.length > maxItems
				? deferredHistory.slice(-1) // Show only last item on mobile
				: deferredHistory;
		}, [deferredHistory, isMobile]);

		return (
			<ErrorBoundary
				fallback={<Alert severity="error">Erreur de navigation</Alert>}
			>
				<Breadcrumbs
					separator={
						<ChevronRightIcon
							sx={{
								color: "text.disabled",
								fontSize: { xs: "16px", md: "18px" },
							}}
						/>
					}
					aria-label="Fil d'Ariane de navigation"
					sx={{
						bgcolor: "transparent",
						p: 0.5,
						"& .MuiBreadcrumbs-ol": {
							alignItems: "center",
							flexWrap: "nowrap",
							overflow: "hidden",
						},
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
							fontSize: { xs: "0.75rem", md: "0.875rem" },
							textDecoration: "none",
							p: { xs: 0.5, md: 1 },
							borderRadius: 1,
							transition: "all 0.2s ease",
							minWidth: 0,
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
						{!isMobile && "Accueil"}
					</Link>

					{deferredHistory.length > (isMobile ? 1 : 3) && (
						<Tooltip title="Afficher tout l'historique">
							<IconButton
								onClick={onMoreClick}
								size="small"
								aria-label="Afficher l'historique complet de navigation"
								aria-expanded={Boolean(menuAnchor)}
								aria-haspopup="true"
								sx={{
									color: "text.disabled",
									p: { xs: 0.25, md: 0.5 },
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
									fontSize: { xs: "0.75rem", md: "0.875rem" },
									fontWeight: isCurrentNode ? 600 : 400,
									textDecoration: "none",
									p: { xs: 0.5, md: 1 },
									borderRadius: 1,
									bgcolor: isCurrentNode ? "action.selected" : "transparent",
									transition: "all 0.2s ease",
									border: "none",
									cursor: "pointer",
									maxWidth: { xs: 100, sm: 120, md: 150 },
									overflow: "hidden",
									textOverflow: "ellipsis",
									whiteSpace: "nowrap",
									minWidth: 0,
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
							maxWidth: { xs: "90vw", sm: 300 },
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

// Mobile navigation drawer
const MobileNavDrawer = memo(
	({
		open,
		onClose,
		data,
		isLoading,
		error,
		onLogout,
	}: {
		open: boolean;
		onClose: () => void;
		data: any;
		isLoading: boolean;
		error: any;
		onLogout: () => void;
	}) => {
		return (
			<Drawer
				anchor="right"
				open={open}
				onClose={onClose}
				PaperProps={{
					sx: {
						width: { xs: "100%", sm: 320 },
						maxWidth: "100vw",
					},
				}}
			>
				<Box sx={{ p: 2 }}>
					<Box
						sx={{
							display: "flex",
							justifyContent: "space-between",
							alignItems: "center",
							mb: 3,
						}}
					>
						<Typography variant="h6">Menu</Typography>
						<IconButton onClick={onClose} aria-label="Fermer le menu">
							<CloseIcon />
						</IconButton>
					</Box>

					{/* User info section */}
					<Box
						sx={{ mb: 3, p: 2, bgcolor: "background.paper", borderRadius: 1 }}
					>
						<UserInfo data={data} isLoading={isLoading} error={error} />
					</Box>

					{/* Navigation actions */}
					<Stack spacing={2}>
						<Button
							variant="outlined"
							fullWidth
							startIcon={<LogoutIcon />}
							onClick={() => {
								onLogout();
								onClose();
							}}
							aria-label="Se déconnecter"
						>
						</Button>
					</Stack>
				</Box>
			</Drawer>
		);
	},
);

MobileNavDrawer.displayName = "MobileNavDrawer";

const MainLayout = memo(() => {
	const theme = useTheme();
	const navigate = useNavigate();
	const queryClient = useQueryClient();
	const { isLoading: isTransitioning, withLoading } = useLoadingState();

	const [selectedDate, setSelectedDate] = useState<number>(Date.now());
	const dateRangeStart = new Date("1920-01-01").getTime();
	const dateRangeEnd = Date.now();
	const handleDateChange = (_: Event, value: number | number[]) => {
		setSelectedDate(value as number);
	};

	// Media queries for responsive behavior
	const isMobile = useMediaQuery(theme.breakpoints.down("md"));
	const _isTablet = useMediaQuery(theme.breakpoints.down("lg"));

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

	// Mobile-specific state
	const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
	const [searchDialogOpen, setSearchDialogOpen] = useState(false);

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
							maxWidth: { xs: "90vw", sm: "500px" },
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
									spacing={{ xs: 1, md: 2 }}
									sx={{
										minWidth: 0,
										flex: 1,
										overflow: "hidden",
									}}
								>
									{/* Logo - responsive sizing */}
									<Box
										sx={{
											height: { xs: 32, md: 40 },
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

									{/* Navigation breadcrumbs */}
									<Box sx={{ minWidth: 0, flex: 1, overflow: "hidden" }}>
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
									spacing={{ xs: 0.5, md: 1.5 }}
									sx={{
										flexWrap: "nowrap",
										minWidth: 0,
									}}
								>

								<DatePicker
									disableFuture
									value={dayjs(selectedDate)}
									onChange={(value) => {
										const newDate = dayjs(value).valueOf();
										if (!isNaN(newDate)) setSelectedDate(newDate);
									}}
									sx={{ mb: 1 }}
								/>

									{/* Barre de recherche + bouton avancé */}
									{isMobile ? (
										<Tooltip title="Rechercher / Options avancées">
											<IconButton
												onClick={() => setSearchDialogOpen(true)}
												size="small"
												aria-label="Ouvrir la recherche"
												sx={{
													"&:focus": {
														outline: `2px solid ${theme.palette.primary.main}`,
														outlineOffset: "2px",
													},
												}}
											>
												<SearchIcon fontSize="small" />
											</IconButton>
										</Tooltip>
									) : (
										<Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
											<Suspense fallback={<LoadingStates.Inline text="Recherche..." />}>
												<SearchBar />
											</Suspense>
											<Tooltip title="Recherche avancée">
												<IconButton
													onClick={handleClickClass}
													size="medium"
													aria-label="Ouvrir la recherche avancée"
													sx={{
														"&:focus": {
															outline: `2px solid ${theme.palette.primary.main}`,
															outlineOffset: "2px",
														},
													}}
												>
													<TuneIcon />
												</IconButton>
											</Tooltip>
										</Box>
									)}

									{/* Dialog mobile pour recherche et avancée */}
									<Dialog open={searchDialogOpen} onClose={() => setSearchDialogOpen(false)} fullWidth>
										<DialogContent>
											<Suspense fallback={<LoadingStates.Inline text="Recherche..." />}>
												<SearchBar />
												<Box mt={2}>
													<IconButton onClick={handleClickClass}>
														<TuneIcon /> Options avancées
													</IconButton>
												</Box>
											</Suspense>
										</DialogContent>
									</Dialog>


									{/* User info - different implementations for different screen sizes */}
									{isMobile ? (
										<Tooltip title="Menu utilisateur">
											<IconButton
												onClick={() => setMobileMenuOpen(true)}
												size="small"
												aria-label="Ouvrir le menu utilisateur"
												sx={{
													"&:focus": {
														outline: `2px solid ${theme.palette.primary.main}`,
														outlineOffset: "2px",
													},
												}}
											>
												<MobileUserInfo
													data={deferredData}
													isLoading={isLoading}
													error={apiError}
												/>
											</IconButton>
										</Tooltip>
									) : (
										<>
											{/* Tablet: Show avatar + logout */}
											<Box
												sx={{ display: { xs: "none", sm: "flex", lg: "none" } }}
											>
												<Stack direction="row" alignItems="center" spacing={1}>
													<MobileUserInfo
														data={deferredData}
														isLoading={isLoading}
														error={apiError}
													/>
													<Tooltip title="Se déconnecter de l'application">
														<IconButton
															onClick={handleLogout}
															disabled={isPendingAny}
															size="small"
															aria-label="Se déconnecter"
															sx={{
																"&:disabled": { opacity: 0.6 },
																"&:focus": {
																	outline: `2px solid ${theme.palette.primary.main}`,
																	outlineOffset: "2px",
																},
															}}
														>
															<LogoutIcon fontSize="small" />
														</IconButton>
													</Tooltip>
												</Stack>
											</Box>

											{/* Desktop: Full user info + logout button */}
											<Box sx={{ display: { xs: "none", lg: "block" } }}>
												<UserInfo
													data={deferredData}
													isLoading={isLoading}
													error={apiError}
												/>
											</Box>
										</>
									)}

									{/* Logout button for desktop */}
									{!isMobile && (
										<Tooltip title="Se déconnecter de l'application">
											<IconButton
												size="small"
												onClick={handleLogout}
												disabled={isPendingAny}
												aria-label="Se déconnecter"
												sx={{
													"&:disabled": { opacity: 0.6 },
													fontSize: { xs: "0.75rem", sm: "0.875rem" },
													display: { xs: "none", sm: "flex" },
													minWidth: { sm: "auto", md: "auto" },
													"&:focus": {
														outline: `2px solid ${theme.palette.primary.main}`,
														outlineOffset: "2px",
													},
												}}
											>
												<LogoutIcon />
											</IconButton>
										</Tooltip>
									)}
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

				{/* Mobile Search Dialog */}
				<MobileSearchDialog
					open={searchDialogOpen}
					onClose={() => setSearchDialogOpen(false)}
				/>

				{/* Mobile Navigation Drawer */}
				<MobileNavDrawer
					open={mobileMenuOpen}
					onClose={() => setMobileMenuOpen(false)}
					data={deferredData}
					isLoading={isLoading}
					error={apiError}
					onLogout={handleLogout}
				/>

				{/* Enhanced loading indicator for global operations */}
				{isPendingAny && (
					<Box
						sx={{
							position: "fixed",
							bottom: { xs: 16, md: 24 },
							right: { xs: 16, md: 24 },
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
