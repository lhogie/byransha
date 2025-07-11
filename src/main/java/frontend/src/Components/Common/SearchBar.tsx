import {
  TextField,
  ListItem,
  ListItemAvatar,
  Avatar,
  ListItemText,
  Badge,
  List,
  Paper,
  CircularProgress,
  Box,
  ClickAwayListener,
  Fade,
  ListItemButton,
} from "@mui/material";
import { useApiMutation, useInfiniteApiData } from "@hooks/useApiData";
import { useRef, useState, useEffect, useCallback } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import { useDebounce } from "use-debounce";
import WarningIcon from "@mui/icons-material/Warning";
import { useVirtualizer } from "@tanstack/react-virtual";

export const SearchBar = ({ key }: { key?: string }) => {
  const [query, setQuery] = useState("");
  const [debounceQuery] = useDebounce(query, 250, { maxWait: 500 });
  const [isOpen, setIsOpen] = useState(false);
  const [scrollPosition, setScrollPosition] = useState(0);
  const [focusedIndex, setFocusedIndex] = useState(-1);

  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const inputRef = useRef<HTMLInputElement>(null);
  const parentRef = useRef<HTMLDivElement>(null);

  const jumpMutation = useApiMutation("jump", {
    onSuccess: async () => {
      // Only invalidate queries that might be affected by the jump, not the search query
      await queryClient.invalidateQueries({
        predicate: (query) => {
          // Don't invalidate search queries to prevent SearchBar reset
          return !query.queryKey.includes("search_node");
        },
      });
    },
  });

  const {
    isLoading,
    data,
    isFetching,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useInfiniteApiData(
    "search_node",
    {
      query: debounceQuery,
    },
    {
      enabled: debounceQuery.length > 0,
      gcTime: 30000,
    },
  );

  const results: {
    id: string;
    name: string;
    type: string;
    img?: string;
    imgMimeType?: string;
    isValid: boolean;
  }[] =
    data?.pages.flatMap((d) => d.data?.results?.[0]?.result?.data?.data) || [];

  console.log("SearchBar Debug:", {
    query,
    debounceQuery,
    isOpen,
    isLoading,
    results: results.length,
    data: data?.pages?.length,
  });

  const rowVirtualizer = useVirtualizer({
    count: hasNextPage ? results.length + 1 : results.length,
    estimateSize: () => 60,
    getScrollElement: () => parentRef.current,
    overscan: 5,
  });

  const handleScroll = useCallback(() => {
    const scrollElement = parentRef.current;
    if (!scrollElement) return;

    const { scrollTop, scrollHeight, clientHeight } = scrollElement;
    const scrollPercentage = (scrollTop + clientHeight) / scrollHeight;

    // Save scroll position
    setScrollPosition(scrollTop);

    // Only fetch when scrolled to 80% of the content
    if (scrollPercentage > 0.8 && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  // Restore scroll position after data changes
  useEffect(() => {
    const scrollElement = parentRef.current;
    if (scrollElement && scrollPosition > 0) {
      scrollElement.scrollTop = scrollPosition;
    }
  }, [scrollPosition]);

  // Handle scroll events
  useEffect(() => {
    const scrollElement = parentRef.current;
    if (!scrollElement) return;

    scrollElement.addEventListener("scroll", handleScroll);
    return () => scrollElement.removeEventListener("scroll", handleScroll);
  }, [handleScroll]);

  // Handle keyboard navigation
  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (!isOpen || results.length === 0) return;

    switch (event.key) {
      case "ArrowDown":
        event.preventDefault();
        setFocusedIndex((prev) =>
          prev < results.length - 1 ? prev + 1 : prev,
        );
        break;
      case "ArrowUp":
        event.preventDefault();
        setFocusedIndex((prev) => (prev > 0 ? prev - 1 : prev));
        break;
      case "Enter":
        event.preventDefault();
        if (focusedIndex >= 0 && focusedIndex < results.length) {
          handleSelectOption(results[focusedIndex]);
        }
        break;
      case "Escape":
        setIsOpen(false);
        setFocusedIndex(-1);
        break;
    }
  };

  const handleSelectOption = (option: (typeof results)[0]) => {
    jumpMutation.mutate({
      node_id: option.id,
    });
    setIsOpen(false);
    setFocusedIndex(-1);
    navigate("/home", { replace: true });
  };

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setQuery(value);
    setIsOpen(true);
    setFocusedIndex(-1);
  };

  const handleInputFocus = () => {
    if (query.length > 0) {
      setIsOpen(true);
    }
  };

  const handleClickAway = () => {
    setIsOpen(false);
    setFocusedIndex(-1);
  };

  return (
    <ClickAwayListener onClickAway={handleClickAway}>
      <Box sx={{ position: "relative", width: 300 }}>
        <TextField
          ref={inputRef}
          label="Rechercher"
          variant="outlined"
          size="small"
          value={query}
          onChange={handleInputChange}
          onFocus={handleInputFocus}
          onKeyDown={handleKeyDown}
          fullWidth
          InputProps={{
            endAdornment: isLoading ? (
              <CircularProgress color="inherit" size={20} />
            ) : null,
            sx: { pr: 0.5 },
          }}
          sx={{
            display: { xs: "none", md: "inline-block" },
            "& .MuiOutlinedInput-root": {
              borderBottomLeftRadius: isOpen ? 0 : undefined,
              borderBottomRightRadius: isOpen ? 0 : undefined,
            },
          }}
        />

        <Fade in={isOpen && debounceQuery.length > 0}>
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
            {isLoading && results.length === 0 ? (
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                  p: 2,
                }}
              >
                <CircularProgress size={20} />
              </Box>
            ) : results.length === 0 ? (
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                  p: 2,
                }}
              >
                No results found
              </Box>
            ) : (
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
                    const result = results[virtualItem.index];
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
                        <ListItemButton
                          onClick={() => result && handleSelectOption(result)}
                          sx={{
                            height: "100%",
                            backgroundColor: isFocused
                              ? "action.hover"
                              : "transparent",
                            "&:hover": {
                              backgroundColor: "action.hover",
                            },
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
                              <CircularProgress size={20} />
                            </Box>
                          ) : result ? (
                            <>
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
                            </>
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
                              No results found
                            </Box>
                          )}
                        </ListItemButton>
                      </ListItem>
                    );
                  })}
                </List>
              </Box>
            )}
          </Paper>
        </Fade>
      </Box>
    </ClickAwayListener>
  );
};
