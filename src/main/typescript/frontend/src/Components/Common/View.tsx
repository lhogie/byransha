import ErrorBoundary from "@components/ErrorBoundary";
import {
	LoadingStates,
	useLoadingState,
} from "@components/Loading/LoadingComponents";
import {
	startTransition,
	useDeferredValue,
	useOptimizedDebounce,
	useOptimizedState,
} from "@hooks/react19";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import { Alert, Box, Button, Paper, Typography, useTheme } from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import { memo, Suspense, useCallback, useEffect, useMemo } from "react";
import { ModalComponent } from "../View/ModalComponent";
import { ViewContent } from "../View/ViewContent";

interface ViewProps {
	viewId: string;
	sx?: any;
}

export const View = memo(({ viewId, sx }: ViewProps) => {
	const theme = useTheme();
	const {
		data: rawApiData,
		isLoading: loading,
		error,
		refetch,
	} = useApiData(
		viewId,
		{},
		{
			staleTime: 30000,
			gcTime: 5 * 60 * 1000,
			refetchOnWindowFocus: false,
			refetchOnReconnect: true,
			retry: 3,
			retryDelay: (attemptIndex: number) =>
				Math.min(1000 * 2 ** attemptIndex, 30000),
		},
	);

	const queryClient = useQueryClient();
	const { isLoading: isMutating, withLoading } = useLoadingState();

	const [isModalOpen, setIsModalOpen, isModalUpdating] = useOptimizedState(
		false,
		{
			transitionUpdates: true,
		},
	);

	const [hex, setHex, isColorUpdating] = useOptimizedState("#ffffff", {
		transitionUpdates: true,
		debounceMs: 300,
	});

	const [viewError, setViewError, isViewErrorUpdating] = useOptimizedState<
		string | null
	>(null, {
		transitionUpdates: true,
	});

	const deferredViewId = useDeferredValue(viewId);
	const deferredHex = useDeferredValue(hex);
	const [debouncedColor] = useOptimizedDebounce(hex, 500);

	const saveColour = useApiMutation("update_colour", {
		onSuccess: () => {
			startTransition(() => {
				queryClient.setQueryData(["apiData", viewId], (oldData: unknown) => {
					if (!oldData) return oldData;
					const typedData = oldData as any;
					return {
						...typedData,
						data: {
							...typedData.data,
							results: typedData.data.results?.map((result: any) => ({
								...result,
								color: debouncedColor,
							})),
						},
					};
				});
				setViewError(null);
			});
		},
		onError: (error: unknown) => {
			console.error("Failed to save color:", error);
			startTransition(() => {
				setViewError("Failed to save color changes");
				setHex("#ffffff");
			});
		},
	});

	const jumpMutation = useApiMutation("jump", {
		onSuccess: async () => {
			startTransition(() => {
				queryClient.invalidateQueries({ queryKey: ["apiData"] });
				setViewError(null);
			});
		},
		onError: (error: unknown) => {
			console.error("Failed to jump to node:", error);
			startTransition(() => {
				setViewError("Failed to navigate to node");
			});
		},
	});

	// Optimized color change handler with debouncing
	const handleHexChange = useCallback(
		(colour: { hex: string }) => {
			setHex(colour.hex);
		},
		[setHex],
	);

	// Effect to handle debounced color saves
	useEffect(() => {
		if (debouncedColor !== "#ffffff" && debouncedColor !== hex) {
			withLoading(async () => {
				try {
					await saveColour.mutateAsync({
						view_id: deferredViewId,
						value: debouncedColor,
					});
				} catch (error: unknown) {
					console.error("Color update failed:", error);
				}
			});
		}
	}, [debouncedColor, deferredViewId, saveColour, withLoading, hex]);

	// Optimized jump to node handler
	const jumpToNode = useCallback(
		(nodeId: number | string) => {
			withLoading(async () => {
				try {
					await jumpMutation.mutateAsync({ node_id: nodeId });
				} catch (error: unknown) {
					console.error("Jump to node failed:", error);
				}
			});
		},
		[jumpMutation, withLoading],
	);

	// Memoized modal handlers
	const handleModalChange = useCallback(
		(opened: boolean) => {
			startTransition(() => {
				setIsModalOpen(opened);
			});
		},
		[setIsModalOpen],
	);

	// Retry handler for failed requests
	const handleRetry = useCallback(() => {
		withLoading(async () => {
			try {
				await refetch();
				setViewError(null);
			} catch (error: unknown) {
				console.error("Retry failed:", error);
				setViewError("Retry failed. Please try again.");
			}
		});
	}, [refetch, withLoading, setViewError]);

	// Memoized data extraction with error handling
	const { dataContent, exportData, backgroundColor } = useMemo(() => {
		try {
			const dataContent = rawApiData?.data;
			const exportData = rawApiData?.data?.results?.[0]?.result?.data;
			const backgroundColor = sx?.bgcolor || "transparent";

			return {
				dataContent,
				exportData,
				backgroundColor,
			};
		} catch (error: unknown) {
			console.error("Error processing view data:", error);
			setViewError("Error processing view data");
			return {
				dataContent: undefined,
				exportData: undefined,
				backgroundColor: sx?.bgcolor || "transparent",
			};
		}
	}, [rawApiData, sx?.bgcolor, setViewError]);

	// Enhanced error boundary for view-specific errors
	const ViewErrorFallback = useCallback(
		({
			error: errorObj,
			resetError,
		}: {
			error: Error;
			resetError: () => void;
		}) => (
			<Paper
				elevation={1}
				sx={{
					display: "flex",
					flexDirection: "column",
					alignItems: "center",
					justifyContent: "center",
					minHeight: 200,
					p: 3,
					border: `1px solid ${theme.palette.divider}`,
					borderRadius: 2,
					bgcolor: "background.paper",
				}}
			>
				<Box sx={{ textAlign: "center", mb: 2 }}>
					<Typography variant="h2" sx={{ fontSize: "2rem", mb: 1 }}>
						⚠️
					</Typography>
					<Typography variant="body2" color="text.secondary" gutterBottom>
						Échec du chargement de la vue: {viewId}
					</Typography>
					{process.env.NODE_ENV === "development" && (
						<Typography variant="caption" color="text.disabled" display="block">
							{errorObj.message}
						</Typography>
					)}
					{viewError && (
						<Alert severity="error" sx={{ mt: 1, fontSize: "0.75rem" }}>
							{viewError}
						</Alert>
					)}
				</Box>
				<Box sx={{ display: "flex", gap: 1 }}>
					<Button
						variant="outlined"
						onClick={() => {
							resetError();
							setViewError(null);
						}}
						size="small"
					>
						Réessayer
					</Button>
					<Button variant="contained" onClick={handleRetry} size="small">
						Recharger les données
					</Button>
				</Box>
			</Paper>
		),
		[viewId, viewError, handleRetry, setViewError, theme.palette.divider],
	);

	const isPendingAny =
		isMutating || isModalUpdating || isColorUpdating || isViewErrorUpdating;

	return (
		<ErrorBoundary
			fallback={
				<ViewErrorFallback
					error={new Error("View render error")}
					resetError={() => window.location.reload()}
				/>
			}
			onError={(error, errorInfo) => {
				console.error(`View ${viewId} error:`, error, errorInfo);
				setViewError(`Erreur de rendu: ${error.message}`);
			}}
			resetKeys={[viewId]}
		>
			<Box
				sx={{
					width: "100%",
					height: "100%",
					minHeight: 300,
					position: "relative",
					display: "flex",
					flexDirection: "column",
					opacity: isPendingAny ? 0.8 : 1,
					transition: "opacity 0.2s ease-in-out",
					overflow: "hidden",
					...sx,
				}}
			>
				{/* Error display */}
				{viewError && (
					<Alert
						severity="error"
						onClose={() => setViewError(null)}
						sx={{
							position: "absolute",
							top: 8,
							left: "50%",
							transform: "translateX(-50%)",
							zIndex: 1000,
							fontSize: "0.75rem",
							maxWidth: "90%",
						}}
						aria-live="polite"
					>
						{viewError}
					</Alert>
				)}

				{/* Modal with Suspense boundary */}
				<ErrorBoundary
					fallback={
						<Alert severity="warning" sx={{ m: 1 }}>
							Erreur lors du chargement du modal
						</Alert>
					}
					onError={(error) => {
						console.error("Modal error:", error);
						setViewError("Le modal n'a pas pu être chargé");
					}}
				>
					<Suspense fallback={null}>
						<ModalComponent
							dataForModal={exportData}
							isModalOpen={isModalOpen}
							setIsModalOpen={handleModalChange}
						/>
					</Suspense>
				</ErrorBoundary>

				{/* Main content with error boundary and loading states */}
				<Box
					sx={{
						flex: 1,
						width: "100%",
						overflow: "auto",
						position: "relative",
						display: "flex",
						flexDirection: "column",
						scrollbarWidth: "thin",
						scrollbarColor: `${theme.palette.primary.main} ${theme.palette.grey[200]}`,
						"&::-webkit-scrollbar": { width: "8px", height: "8px" },
						"&::-webkit-scrollbar-track": {
							bgcolor: "grey.100",
							borderRadius: 1,
						},
						"&::-webkit-scrollbar-thumb": {
							bgcolor: "primary.main",
							borderRadius: 1,
							"&:hover": {
								bgcolor: "primary.dark",
							},
						},
					}}
				>
					<ErrorBoundary
						fallback={
							<ViewErrorFallback
								error={new Error("View content error")}
								resetError={handleRetry}
							/>
						}
						onError={(error, errorInfo) => {
							console.error("View content error:", error, errorInfo);
							setViewError(`Erreur de contenu: ${error.message}`);
						}}
					>
						<Suspense
							fallback={
								<Box
									sx={{
										display: "flex",
										justifyContent: "center",
										alignItems: "center",
										minHeight: 200,
										p: 2,
									}}
								>
									<LoadingStates.Component
										message={`Chargement de ${deferredViewId}...`}
									/>
								</Box>
							}
						>
							<ViewContent
								loading={loading}
								error={error}
								rawApiData={rawApiData}
								refetch={refetch}
								dataContent={dataContent}
								backgroundColor={backgroundColor}
								jumpToNode={jumpToNode}
								hexColor={deferredHex}
								onHexColorChange={handleHexChange}
								viewId={deferredViewId}
							/>
						</Suspense>
					</ErrorBoundary>
				</Box>

				{/* Loading indicator for mutations */}
				{isPendingAny && (
					<Box
						sx={{
							position: "absolute",
							top: 8,
							right: 8,
							zIndex: 10,
						}}
						aria-live="polite"
						aria-label="Opération en cours"
					>
						<LoadingStates.Inline text="" />
					</Box>
				)}
			</Box>
		</ErrorBoundary>
	);
});

View.displayName = "View";
export default View;
