import React, { useCallback, useMemo, Suspense, memo } from "react";
import { DashboardLayout } from "@toolpad/core";
import {
	Outlet,
	Link as RouterLink,
	useLocation,
	useNavigate,
} from "react-router";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import MoreHorizIcon from "@mui/icons-material/MoreHoriz";
import TuneIcon from "@mui/icons-material/Tune";
import {
	Box,
	Breadcrumbs,
	Button,
	Link,
	MenuItem,
	Stack,
	Typography,
	Alert,
	type PopoverProps,
} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import { useQueryClient } from "@tanstack/react-query";
import { SearchBar } from "@components/Common/SearchBar";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import ErrorBoundary from "@components/ErrorBoundary";
import {
	LoadingStates,
	useLoadingState,
} from "@components/Loading/LoadingComponents";
import {
	useOptimizedState,
	useOptimizedList,
	useOptimizedDebounce,
	startTransition,
	useDeferredValue,
} from "@hooks/react19";

// Memoized UserInfo component
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
		if (isLoading) {
			return <LoadingStates.Inline text="Loading user info..." />;
		}

		if (error) {
			return (
				<Typography sx={{ color: "#f44336", fontSize: "14px" }}>
					Error loading user
				</Typography>
			);
		}

		return (
			<Box
				sx={{
					display: "flex",
					alignItems: "center",
					gap: 1,
					p: "6px 12px",
					bgcolor: "#f5f7ff",
					borderRadius: "4px",
					transition: "background-color 0.2s ease",
					"&:hover": { bgcolor: "#e8eaf6" },
				}}
			>
				<Typography
					sx={{
						color: "#306DAD",
						fontSize: "14px",
						fontWeight: "500",
					}}
				>
					{data?.data?.username || "Unknown User"}
				</Typography>
				<Typography sx={{ color: "#546e7a", fontSize: "14px" }}>
					({data?.data?.user_id || "N/A"})
				</Typography>
				<Typography sx={{ color: "#90a4ae", fontSize: "12px" }}>
					v{data?.data["backend version"] || "N/A"}
				</Typography>
			</Box>
		);
	},
);

UserInfo.displayName = "UserInfo";

// Memoized BreadcrumbNav component
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
		const deferredHistory = useDeferredValue(history);
		const visibleHistory = useMemo(() => {
			return deferredHistory.length > 3
				? deferredHistory.slice(-2)
				: deferredHistory;
		}, [deferredHistory]);

		return (
			<ErrorBoundary
				fallback={
					<Typography variant="body2" color="error">
						Navigation error
					</Typography>
				}
			>
				<Breadcrumbs
					separator={
						<ChevronRightIcon sx={{ color: "#b0bec5", fontSize: "18px" }} />
					}
					aria-label="navigation history"
					sx={{
						bgcolor: "transparent",
						p: "4px 0",
						"& .MuiBreadcrumbs-ol": { alignItems: "center" },
					}}
				>
					<Link
						component={RouterLink}
						sx={{
							color: "#546e7a",
							fontSize: "14px",
							textDecoration: "none",
							p: "4px 8px",
							borderRadius: "2px",
							"&:hover": {
								color: "#306DAD",
								bgcolor: "#f5f7ff",
							},
						}}
						to="/home"
					>
						Home
					</Link>

					{deferredHistory.length > 3 && (
						<IconButton
							onClick={onMoreClick}
							size="small"
							sx={{
								color: "#90a4ae",
								p: "2px",
								"&:hover": { color: "#306DAD", bgcolor: "#f5f7ff" },
							}}
						>
							<MoreHorizIcon fontSize="small" />
						</IconButton>
					)}

					{visibleHistory.map((hist: any) => (
						<Link
							key={hist.id}
							component="button"
							onClick={() => onHistoryClick(hist)}
							sx={{
								color: hist === currentNode ? "#306DAD" : "#546e7a",
								fontSize: "14px",
								fontWeight: hist === currentNode ? "500" : "400",
								textDecoration: "none",
								p: "4px 8px",
								borderRadius: "2px",
								bgcolor: hist === currentNode ? "#e8eaf6" : "transparent",
								transition: "all 0.2s ease",
								"&:hover": {
									color: "#306DAD",
									bgcolor: "#f5f7ff",
								},
							}}
						>
							{hist.pretty_name}
						</Link>
					))}
				</Breadcrumbs>

				<Menu
					anchorEl={menuAnchor}
					open={Boolean(menuAnchor)}
					onClose={onMenuClose}
					anchorOrigin={{ vertical: "center", horizontal: "center" }}
					transformOrigin={{ vertical: "top", horizontal: "center" }}
					PaperProps={{
						sx: {
							borderRadius: "4px",
							boxShadow: "0 4px 12px rgba(0,0,0,0.15)",
							mt: "5vh",
							ml: "18vw",
						},
					}}
				>
					{deferredHistory.map((hist: any) => (
						<MenuItem
							key={hist.id}
							onClick={() => {
								onHistoryClick(hist);
								onMenuClose();
							}}
							sx={{
								fontSize: "14px",
								color: hist === currentNode ? "#306DAD" : "#546e7a",
								bgcolor: hist === currentNode ? "#f5f7ff" : "transparent",
								"&:hover": {
									bgcolor: "#e8eaf6",
									color: "#306DAD",
								},
							}}
						>
							{hist.pretty_name}
						</MenuItem>
					))}
				</Menu>
			</ErrorBoundary>
		);
	},
);

BreadcrumbNav.displayName = "BreadcrumbNav";

const MainLayout = memo(() => {
	const navigate = useNavigate();
	const { pathname } = useLocation();
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

	const { data: historyData, isLoading: isHistoryLoading } = useApiData(
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
				navigate("/");
			});
		},
		onError: (error: any) => {
			setError(`Logout failed: ${error.message}`);
			setErrorToClear("clear");
		},
	});

	const bnodeClassDistribution = useApiMutation("bnode_class_distribution", {
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
			}
		});
	}, [logoutMutation, withLoading]);

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
								fallback={
									<Typography variant="h6" color="error">
										Navigation Error
									</Typography>
								}
							>
								<Stack direction="row" alignItems="center" spacing={2}>
									<Box
										sx={{
											height: "40px",
											display: "flex",
											alignItems: "center",
										}}
									>
										<img src="/logo.svg" alt="I3S" style={{ height: "100%" }} />
									</Box>

									<Suspense
										fallback={
											<LoadingStates.Inline text="Loading navigation..." />
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
								</Stack>
							</ErrorBoundary>
						),
						toolbarActions: () => (
							<ErrorBoundary
								fallback={
									<Typography variant="body2" color="error">
										Toolbar Error
									</Typography>
								}
							>
								<Stack direction="row" alignItems="center" spacing={2}>
									<IconButton
										onClick={handleClickClass}
										disabled={isPendingAny}
										sx={{
											"&:disabled": { opacity: 0.6 },
										}}
									>
										<TuneIcon />
									</IconButton>

									<Suspense
										fallback={<LoadingStates.Inline text="Search..." />}
									>
										<SearchBar />
									</Suspense>

									<UserInfo
										data={deferredData}
										isLoading={isLoading}
										error={apiError}
									/>

									<Button
										variant="outlined"
										size="small"
										onClick={handleLogout}
										disabled={isPendingAny}
										sx={{
											ml: 2,
											"&:disabled": { opacity: 0.6 },
										}}
									>
										Se deconnecter
									</Button>
								</Stack>
							</ErrorBoundary>
						),
					}}
				>
					<Box
						sx={{
							mt: 3,
						}}
					>
						<ErrorBoundary
							fallback={
								<Box sx={{ p: 3, textAlign: "center" }}>
									<Alert severity="error">
										Page content failed to load. Please try refreshing.
									</Alert>
								</Box>
							}
						>
							<Suspense
								fallback={
									<Box sx={{ p: 3, textAlign: "center" }}>
										<LoadingStates.Component message="Loading page content..." />
									</Box>
								}
							>
								<Outlet />
							</Suspense>
						</ErrorBoundary>
					</Box>
				</DashboardLayout>

				{/* Loading indicator for global operations */}
				{isPendingAny && (
					<Box
						sx={{
							position: "fixed",
							bottom: 16,
							right: 16,
							zIndex: 9999,
						}}
					>
						<LoadingStates.Inline text="Processing..." />
					</Box>
				)}
			</Box>
		</ErrorBoundary>
	);
});

MainLayout.displayName = "MainLayout";

export default MainLayout;
