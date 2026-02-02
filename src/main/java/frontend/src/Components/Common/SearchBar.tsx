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
import { useApiMutation, useInfiniteApiData } from "@hooks/useApiData";
import {
	Search as SearchIcon,
	Warning as WarningIcon,
} from "@mui/icons-material";
import {
	Alert,
	Avatar,
	Badge,
	Box,
	CircularProgress,
	ClickAwayListener,
	Fade,
	InputAdornment,
	List,
	ListItem,
	ListItemAvatar,
	ListItemButton,
	ListItemText,
	Paper,
	TextField,
	Typography,
	useTheme,
} from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import { useVirtualizer } from "@tanstack/react-virtual";
import type React from "react";
import { memo, Suspense, useCallback, useEffect, useMemo, useRef } from "react";
import { useNavigate } from "react-router";

const SearchResult = memo(
	({
		result,
		isFocused,
		onSelect,
	}: {
		result: {
			id: string;
			name: string;
			type: string;
			img?: string;
			imgMimeType?: string;
			isValid: boolean;
		};
		isFocused: boolean;
		onSelect: () => void;
	}) => {
		return (
			<ListItemButton
				onClick={onSelect}
				role="option"
				aria-selected={isFocused}
				tabIndex={isFocused ? 0 : -1}
				sx={{
					height: "100%",
					backgroundColor: isFocused ? "action.hover" : "transparent",
					"&:hover": {
						backgroundColor: "action.hover",
					},
					"&:focus": {
						backgroundColor: "action.focus",
						outline: "2px solid",
						outlineColor: "primary.main",
						outlineOffset: "-2px",
					},
					transition: "background-color 0.15s ease",
				}}
			>
				<ListItemAvatar>
					<Badge
						invisible={result.isValid}
						badgeContent={
							<WarningIcon
								color="warning"
								fontSize="small"
								aria-label="Élément non valide"
							/>
						}
					>
						<Avatar
							src={
								result.img
									? `data:${result.imgMimeType};base64,${result.img}`
									: ""
							}
							alt={`Image de ${result.name}`}
							sx={{ width: 32, height: 32 }}
						/>
					</Badge>
				</ListItemAvatar>
				<ListItemText
					primary={result.name}
					secondary={result.type}
					primaryTypographyProps={{
						style: { marginRight: 8 },
						noWrap: true,
					}}
					secondaryTypographyProps={{
						noWrap: true,
					}}
				/>
			</ListItemButton>
		);
	},
);

SearchResult.displayName = "SearchResult";

const VirtualizedList = memo(
	({
		results,
		focusedIndex,
		hasNextPage,
		onSelectOption,
		parentRef,
	}: {
		results: any[];
		focusedIndex: number;
		hasNextPage: boolean;
		isFetchingNextPage: boolean;
		onSelectOption: (option: any) => void;
		parentRef: React.RefObject<HTMLDivElement>;
	}) => {
		const deferredResults = useDeferredValue(results);

		const rowVirtualizer = useVirtualizer({
			count: hasNextPage ? deferredResults.length + 1 : deferredResults.length,
			estimateSize: () => 60,
			getScrollElement: () => parentRef.current,
			overscan: 5,
		});

		return (
			<Box
				ref={parentRef}
				role="listbox"
				aria-label="Résultats de recherche"
				sx={{
					height: Math.min(400, rowVirtualizer.getTotalSize()),
					overflowY: "auto",
					position: "relative",
				}}
			>
				<List
					sx={{
						height: `${rowVirtualizer.getTotalSize()}px`,
						position: "relative",
						p: 0,
					}}
					role="presentation"
				>
					{rowVirtualizer.getVirtualItems().map((virtualItem) => {
						const result = deferredResults[virtualItem.index];
						const isLoadingItem = !result && hasNextPage;
						const isFocused = focusedIndex === virtualItem.index;

						return (
							<ListItem
								key={result?.id || `loading-${virtualItem.index}`}
								disablePadding
								sx={{
									height: `${virtualItem.size}px`,
									left: 0,
									position: "absolute",
									top: 0,
									transform: `translateY(${virtualItem.start}px)`,
									width: "100%",
								}}
							>
								{isLoadingItem ? (
									<Box
										sx={{
											display: "flex",
											justifyContent: "center",
											alignItems: "center",
											width: "100%",
											py: 1,
										}}
									>
										<LoadingStates.Inline text="Loading more..." />
									</Box>
								) : result ? (
									<SearchResult
										result={result}
										isFocused={isFocused}
										onSelect={() => onSelectOption(result)}
									/>
								) : (
									<Box
										sx={{
											display: "flex",
											justifyContent: "center",
											alignItems: "center",
											width: "100%",
											py: 1,
										}}
									>
										<Typography variant="body2" color="text.secondary">
											No results found
										</Typography>
									</Box>
								)}
							</ListItem>
						);
					})}
				</List>
			</Box>
		);
	},
);

VirtualizedList.displayName = "VirtualizedList";

export const SearchBar = memo(() => {
	const navigate = useNavigate();
	const queryClient = useQueryClient();
	const theme = useTheme();
	const { isLoading: isNavigating, withLoading } = useLoadingState();

	const [query, setQuery, isQueryUpdating] = useOptimizedState("", {
		transitionUpdates: true,
	});

	const [isOpen, setIsOpen, isOpenUpdating] = useOptimizedState(false, {
		transitionUpdates: true,
	});

	const [scrollPosition, setScrollPosition, isScrollUpdating] =
		useOptimizedState(0, {
			transitionUpdates: true,
			debounceMs: 100,
		});

	const [focusedIndex, setFocusedIndex, isFocusUpdating] = useOptimizedState(
		-1,
		{
			transitionUpdates: true,
		},
	);

	const [error, setError, isErrorUpdating] = useOptimizedState<string | null>(
		null,
		{
			transitionUpdates: true,
		},
	);

	const [debouncedQuery] = useOptimizedDebounce(query, 300);

	const inputRef = useRef<HTMLInputElement>(null);
	const parentRef = useRef<HTMLDivElement>(null);

	const jumpMutation = useApiMutation("jump", {
		onSuccess: async () => {
			startTransition(() => {
				queryClient.invalidateQueries({
					predicate: (query) => {
						return !query.queryKey.includes("search_node");
					},
				});
				setError(null);
			});
		},
		onError: (error: any) => {
			setError(`Navigation failed: ${error.message}`);
		},
	});

	const {
		isLoading,
		data,
		isFetchingNextPage,
		fetchNextPage,
		hasNextPage,
		error: searchError,
	} = useInfiniteApiData(
		"search_node",
		{
			query: debouncedQuery,
		},
		{
			enabled: debouncedQuery.length > 0,
			gcTime: 30000,
			staleTime: 10000,
			refetchOnWindowFocus: false,
			retry: 2,
			retryDelay: 1000,
		},
	);

	const results = useMemo(() => {
		try {
			// Dylan Deb ajout verification
			const allResults =
				data?.pages.flatMap((d) => d.data?.results?.[0]?.result?.data?.data) ||
				[];
			
			// Dédupliquer les résultats par ID
			const uniqueResults = Array.from(
				new Map(allResults.map((item) => [item?.id, item])).values()
			);
			return uniqueResults;
			// Dylan Fin
		} catch (error) {
			console.error("Error processing search results:", error);
			setError("Error processing search results");
			return [];
		}
	}, [data, setError]);

	const deferredResults = useDeferredValue(results);

	useEffect(() => {
		if (searchError) {
			setError(`Search failed: ${searchError.message}`);
		}
	}, [searchError, setError]);

	const handleScroll = useCallback(() => {
		const scrollElement = parentRef.current;
		if (!scrollElement) return;

		const { scrollTop, scrollHeight, clientHeight } = scrollElement;
		const scrollPercentage = (scrollTop + clientHeight) / scrollHeight;

		startTransition(() => {
			setScrollPosition(scrollTop);
		});

		if (scrollPercentage > 0.8 && hasNextPage && !isFetchingNextPage) {
			fetchNextPage().catch((error) => {
				console.error("Failed to fetch next page:", error);
				setError("Failed to load more results");
			});
		}
	}, [
		hasNextPage,
		isFetchingNextPage,
		fetchNextPage,
		setScrollPosition,
		setError,
	]);

	useEffect(() => {
		const scrollElement = parentRef.current;
		if (scrollElement && scrollPosition > 0) {
			scrollElement.scrollTop = scrollPosition;
		}
	}, [scrollPosition]);

	useEffect(() => {
		const scrollElement = parentRef.current;
		if (!scrollElement) {
			const timer = setTimeout(() => {
				if (parentRef.current) {
					parentRef.current.addEventListener("scroll", handleScroll, { passive: true });
				}
			}, 100);
			return () => clearTimeout(timer);
		}

		scrollElement.addEventListener("scroll", handleScroll, { passive: true });
		return () => scrollElement.removeEventListener("scroll", handleScroll);
	}, [handleScroll]);

	const handleSelectOption = useCallback(
		(option: (typeof results)[0]) => {
			withLoading(async () => {
				try {
					await jumpMutation.mutateAsync({
						node_id: option.id,
					});

					startTransition(() => {
						setIsOpen(false);
						setFocusedIndex(-1);
					});

					navigate("/home", { replace: true });
				} catch (error) {
					console.error("Failed to select option:", error);
				}
			});
		},
		[jumpMutation, setIsOpen, setFocusedIndex, navigate, withLoading],
	);

	const handleKeyDown = useCallback(
		(event: React.KeyboardEvent) => {
			if (!isOpen || deferredResults.length === 0) return;

			switch (event.key) {
				case "ArrowDown":
					event.preventDefault();
					startTransition(() => {
						setFocusedIndex((prev) =>
							prev < deferredResults.length - 1 ? prev + 1 : prev,
						);
					});
					break;
				case "ArrowUp":
					event.preventDefault();
					startTransition(() => {
						setFocusedIndex((prev) => (prev > 0 ? prev - 1 : prev));
					});
					break;
				case "Enter":
					event.preventDefault();
					if (focusedIndex >= 0 && focusedIndex < deferredResults.length) {
						handleSelectOption(deferredResults[focusedIndex]);
					}
					break;
				case "Escape":
					startTransition(() => {
						setIsOpen(false);
						setFocusedIndex(-1);
					});
					break;
			}
		},
		[
			isOpen,
			deferredResults,
			focusedIndex,
			setFocusedIndex,
			setIsOpen,
			handleSelectOption,
		],
	);

	const handleInputChange = useCallback(
		(event: React.ChangeEvent<HTMLInputElement>) => {
			const value = event.target.value;
			startTransition(() => {
				setQuery(value);
				setIsOpen(true);
				setFocusedIndex(-1);
				setError(null);
			});
		},
		[setQuery, setIsOpen, setFocusedIndex, setError],
	);

	const handleInputFocus = useCallback(() => {
		if (query.length > 0) {
			startTransition(() => {
				setIsOpen(true);
			});
		}
	}, [query.length, setIsOpen]);

	const handleClickAway = useCallback(() => {
		startTransition(() => {
			setIsOpen(false);
			setFocusedIndex(-1);
		});
	}, [setIsOpen, setFocusedIndex]);

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => {
				setError(null);
			}, 5000);
			return () => clearTimeout(timer);
		}
	}, [error, setError]);

	const isPendingAny =
		isNavigating ||
		isQueryUpdating ||
		isOpenUpdating ||
		isScrollUpdating ||
		isFocusUpdating ||
		isErrorUpdating;

	return (
		<ErrorBoundary
			fallback={
				<Box sx={{ width: { xs: "100%", sm: 250, md: 300 } }}>
					<Alert severity="error" sx={{ fontSize: "0.875rem" }}>
						Recherche indisponible
					</Alert>
				</Box>
			}
			onError={(error, errorInfo) => {
				console.error("SearchBar error:", error, errorInfo);
				setError("Erreur du composant de recherche");
			}}
		>
			<ClickAwayListener onClickAway={handleClickAway}>
				<Box
					sx={{
						position: "relative",
						width: { xs: "100%", sm: 250, md: 300 },
						minWidth: 0,
					}}
					role="combobox"
					aria-expanded={isOpen && debouncedQuery.length > 0}
					aria-haspopup="listbox"
					aria-owns={isOpen ? "search-results" : undefined}
				>
					{/* Error display */}
					{error && (
						<Alert
							severity="error"
							onClose={() => setError(null)}
							sx={{
								position: "absolute",
								top: -50,
								left: 0,
								right: 0,
								zIndex: 1400,
								fontSize: "0.75rem",
							}}
							role="alert"
							aria-live="polite"
						>
							{error}
						</Alert>
					)}

					<TextField
						ref={inputRef}
						label="Rechercher"
						placeholder="Tapez pour rechercher..."
						variant="outlined"
						size="small"
						value={query}
						onChange={handleInputChange}
						onFocus={handleInputFocus}
						onKeyDown={handleKeyDown}
						disabled={isPendingAny}
						fullWidth
						autoComplete="off"
						aria-label="Rechercher dans les éléments"
						aria-describedby={error ? "search-error" : "search-help"}
						aria-autocomplete="list"
						aria-controls={isOpen ? "search-results" : undefined}
						InputProps={{
							startAdornment: (
								<InputAdornment position="start">
									<SearchIcon color="action" />
								</InputAdornment>
							),
							endAdornment:
								isLoading || isPendingAny ? (
									<InputAdornment position="end">
										<CircularProgress
											color="inherit"
											size={20}
											aria-label="Recherche en cours"
										/>
									</InputAdornment>
								) : null,
							sx: { pr: 0.5 },
						}}
						sx={{
							width: "100%",
							"& .MuiOutlinedInput-root": {
								borderBottomLeftRadius: isOpen ? 0 : undefined,
								borderBottomRightRadius: isOpen ? 0 : undefined,
								opacity: isPendingAny ? 0.7 : 1,
								transition: "opacity 0.2s ease-in-out",
								"&:hover fieldset": {
									borderColor: "primary.main",
								},
								"&.Mui-focused fieldset": {
									borderColor: "primary.main",
								},
							},
						}}
					/>

					{/* Hidden helper text for screen readers */}
					<Typography
						component="div"
						sx={{
							position: "absolute",
							left: -10000,
							top: "auto",
							clip: "rect(0 0 0 0)",
							overflow: "hidden",
						}}
					>
						Utilisez les flèches haut et bas pour naviguer dans les résultats,
						Entrée pour sélectionner, Échap pour fermer
					</Typography>

					<Fade in={isOpen && debouncedQuery.length > 0}>
						<Paper
							elevation={8}
							sx={{
								position: "absolute",
								top: "100%",
								left: 0,
								right: 0,
								zIndex: 1300,
								maxHeight: 400,
								borderTopLeftRadius: 0,
								borderTopRightRadius: 0,
								border: "1px solid",
								borderColor: "divider",
								borderTop: "none",
								boxShadow: theme.shadows[8],
							}}
							role="presentation"
						>
							<ErrorBoundary
								fallback={
									<Box sx={{ p: 2, textAlign: "center" }}>
										<Alert severity="error" sx={{ fontSize: "0.875rem" }}>
											Erreur lors du chargement des résultats
										</Alert>
									</Box>
								}
							>
								{isLoading && deferredResults.length === 0 ? (
									<Box
										sx={{
											display: "flex",
											justifyContent: "center",
											alignItems: "center",
											p: 2,
										}}
										aria-live="polite"
									>
										<LoadingStates.Inline text="Recherche en cours..." />
									</Box>
								) : deferredResults.length === 0 ? (
									<Box
										sx={{
											display: "flex",
											justifyContent: "center",
											alignItems: "center",
											p: 2,
										}}
										aria-live="polite"
									>
										<Typography variant="body2" color="text.secondary">
											Aucun résultat trouvé
										</Typography>
									</Box>
								) : (
									<Suspense
										fallback={
											<Box
												sx={{ p: 2, textAlign: "center" }}
												aria-live="polite"
											>
												<LoadingStates.Inline text="Chargement des résultats..." />
											</Box>
										}
									>
										<VirtualizedList
											results={deferredResults}
											focusedIndex={focusedIndex}
											hasNextPage={hasNextPage}
											isFetchingNextPage={isFetchingNextPage}
											onSelectOption={handleSelectOption}
											parentRef={parentRef as React.RefObject<HTMLDivElement>}
										/>
									</Suspense>
								)}
							</ErrorBoundary>
						</Paper>
					</Fade>
				</Box>
			</ClickAwayListener>
		</ErrorBoundary>
	);
});

SearchBar.displayName = "SearchBar";
