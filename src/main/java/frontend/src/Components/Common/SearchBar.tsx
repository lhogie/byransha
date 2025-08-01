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
import WarningIcon from "@mui/icons-material/Warning";
import {
	Avatar,
	Badge,
	Box,
	CircularProgress,
	ClickAwayListener,
	Fade,
	List,
	ListItem,
	ListItemAvatar,
	ListItemButton,
	ListItemText,
	Paper,
	TextField,
	Typography,
} from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import { useVirtualizer } from "@tanstack/react-virtual";
import type React from "react";
import { memo, Suspense, useCallback, useEffect, useMemo, useRef } from "react";
import { useNavigate } from "react-router";

// Memoized SearchResult component
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
				sx={{
					height: "100%",
					backgroundColor: isFocused ? "action.hover" : "transparent",
					"&:hover": {
						backgroundColor: "action.hover",
					},
					transition: "background-color 0.15s ease",
				}}
			>
				<ListItemAvatar>
					<Badge
						invisible={result.isValid}
						badgeContent={<WarningIcon color="warning" />}
					>
						<Avatar
							src={
								result.img
									? `data:${result.imgMimeType};base64,${result.img}`
									: ""
							}
							alt={result.name}
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

// Memoized VirtualizedList component
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
	const { isLoading: isNavigating, withLoading } = useLoadingState();

	// React 19 optimized state management
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

	// Debounced search query with React 19 optimization
	const [debouncedQuery] = useOptimizedDebounce(query, 300);

	const inputRef = useRef<HTMLInputElement>(null);
	const parentRef = useRef<HTMLDivElement>(null);

	// Enhanced API mutations with error handling
	const jumpMutation = useApiMutation("jump", {
		onSuccess: async () => {
			startTransition(() => {
				// Only invalidate queries that might be affected by the jump
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

	// Enhanced infinite query with React 19 optimizations
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

	// Memoized results processing
	const results = useMemo(() => {
		try {
			return (
				data?.pages.flatMap((d) => d.data?.results?.[0]?.result?.data?.data) ||
				[]
			);
		} catch (error) {
			console.error("Error processing search results:", error);
			setError("Error processing search results");
			return [];
		}
	}, [data, setError]);

	const deferredResults = useDeferredValue(results);

	// Handle search error
	useEffect(() => {
		if (searchError) {
			setError(`Search failed: ${searchError.message}`);
		}
	}, [searchError, setError]);

	// Optimized scroll handler with debouncing
	const handleScroll = useCallback(() => {
		const scrollElement = parentRef.current;
		if (!scrollElement) return;

		const { scrollTop, scrollHeight, clientHeight } = scrollElement;
		const scrollPercentage = (scrollTop + clientHeight) / scrollHeight;

		// Update scroll position with transition
		startTransition(() => {
			setScrollPosition(scrollTop);
		});

		// Fetch next page when scrolled to 80%
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

	// Restore scroll position after data changes
	useEffect(() => {
		const scrollElement = parentRef.current;
		if (scrollElement && scrollPosition > 0) {
			scrollElement.scrollTop = scrollPosition;
		}
	}, [scrollPosition]);

	// Enhanced scroll event handling
	useEffect(() => {
		const scrollElement = parentRef.current;
		if (!scrollElement) return;

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

	// Enhanced keyboard navigation with React 19 optimizations
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

	// Optimized focus handler
	const handleInputFocus = useCallback(() => {
		if (query.length > 0) {
			startTransition(() => {
				setIsOpen(true);
			});
		}
	}, [query.length, setIsOpen]);

	// Optimized click away handler
	const handleClickAway = useCallback(() => {
		startTransition(() => {
			setIsOpen(false);
			setFocusedIndex(-1);
		});
	}, [setIsOpen, setFocusedIndex]);

	// Clear error after 5 seconds
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
				<Box sx={{ width: 300 }}>
					<Typography variant="body2" color="error">
						Search unavailable
					</Typography>
				</Box>
			}
			onError={(error, errorInfo) => {
				console.error("SearchBar error:", error, errorInfo);
				setError("Search component error");
			}}
		>
			<ClickAwayListener onClickAway={handleClickAway}>
				<Box sx={{ position: "relative", width: 300 }}>
					{/* Error display */}
					{error && (
						<Box
							sx={{
								position: "absolute",
								top: -30,
								left: 0,
								right: 0,
								bgcolor: "error.main",
								color: "white",
								px: 1,
								py: 0.5,
								borderRadius: 1,
								fontSize: "0.75rem",
								zIndex: 1400,
							}}
						>
							{error}
						</Box>
					)}

					<TextField
						ref={inputRef}
						label="Rechercher"
						variant="outlined"
						size="small"
						value={query}
						onChange={handleInputChange}
						onFocus={handleInputFocus}
						onKeyDown={handleKeyDown}
						disabled={isPendingAny}
						fullWidth
						InputProps={{
							endAdornment:
								isLoading || isPendingAny ? (
									<CircularProgress color="inherit" size={20} />
								) : null,
							sx: { pr: 0.5 },
						}}
						sx={{
							display: { xs: "none", md: "inline-block" },
							"& .MuiOutlinedInput-root": {
								borderBottomLeftRadius: isOpen ? 0 : undefined,
								borderBottomRightRadius: isOpen ? 0 : undefined,
								opacity: isPendingAny ? 0.7 : 1,
								transition: "opacity 0.2s ease-in-out",
							},
						}}
					/>

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
							}}
						>
							<ErrorBoundary
								fallback={
									<Box sx={{ p: 2, textAlign: "center" }}>
										<Typography variant="body2" color="error">
											Search results error
										</Typography>
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
									>
										<LoadingStates.Inline text="Searching..." />
									</Box>
								) : deferredResults.length === 0 ? (
									<Box
										sx={{
											display: "flex",
											justifyContent: "center",
											alignItems: "center",
											p: 2,
										}}
									>
										<Typography variant="body2" color="text.secondary">
											No results found
										</Typography>
									</Box>
								) : (
									<Suspense
										fallback={
											<Box sx={{ p: 2, textAlign: "center" }}>
												<LoadingStates.Inline text="Loading results..." />
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
