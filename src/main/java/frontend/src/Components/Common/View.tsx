import React, {
  useState,
  useMemo,
  useCallback,
  useEffect,
  Suspense,
  memo,
} from "react";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import { Box } from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import { ModalComponent } from "../View/ModalComponent";
import { ViewContent } from "../View/ViewContent";
import ErrorBoundary from "@components/ErrorBoundary";
import {
  LoadingStates,
  useLoadingState,
} from "@components/Loading/LoadingComponents";
import {
  useOptimizedState,
  useOptimizedDebounce,
  startTransition,
  useDeferredValue,
} from "@hooks/react19";
import "./View.css";

interface ViewProps {
  viewId: string;
  sx?: any;
}

export const View = memo(({ viewId, sx }: ViewProps) => {
  const {
    data: rawApiData,
    isLoading: loading,
    error,
    refetch,
  } = useApiData(
    viewId,
    {},
    {
      // React 19 optimizations
      staleTime: 30000, // 30 seconds
      gcTime: 5 * 60 * 1000, // 5 minutes
      refetchOnWindowFocus: false,
      refetchOnReconnect: true,
      retry: 3,
      retryDelay: (attemptIndex: number) =>
        Math.min(1000 * 2 ** attemptIndex, 30000),
    },
  );

  const queryClient = useQueryClient();
  const { isLoading: isMutating, withLoading } = useLoadingState();

  // React 19 optimized state management
  const [isModalOpen, setIsModalOpen, isModalUpdating] = useOptimizedState(
    false,
    {
      transitionUpdates: true,
    },
  );

  const [hex, setHex, isColorUpdating] = useOptimizedState("#ffffff", {
    transitionUpdates: true,
    debounceMs: 300, // Debounce color changes
  });

  const [viewError, setViewError, isViewErrorUpdating] = useOptimizedState<
    string | null
  >(null, {
    transitionUpdates: true,
  });

  // Deferred values for better performance during rapid updates
  const deferredViewId = useDeferredValue(viewId);
  const deferredHex = useDeferredValue(hex);
  const [debouncedColor] = useOptimizedDebounce(hex, 500);

  // Memoized API mutations with error handling
  const saveColour = useApiMutation("update_colour", {
    onSuccess: () => {
      // Optimistically update without full refetch
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
        // Revert color on error
        setHex("#ffffff");
      });
    },
  });

  const jumpMutation = useApiMutation("jump", {
    onSuccess: async () => {
      // Use startTransition for non-urgent updates
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
      // Immediate UI update
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
  const handleModalClose = useCallback(() => {
    startTransition(() => {
      setIsModalOpen(false);
    });
  }, [setIsModalOpen]);

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
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          height: "200px",
          padding: 2,
          border: "1px solid #e0e0e0",
          borderRadius: 1,
          backgroundColor: "#fafafa",
        }}
      >
        <Box sx={{ textAlign: "center", mb: 2 }}>
          <Box component="span" sx={{ fontSize: "2rem", color: "#f44336" }}>
            ⚠️
          </Box>
          <Box sx={{ mt: 1, color: "#666", fontSize: "0.875rem" }}>
            Failed to load view: {viewId}
          </Box>
          {process.env.NODE_ENV === "development" && (
            <Box sx={{ mt: 1, color: "#999", fontSize: "0.75rem" }}>
              {errorObj.message}
            </Box>
          )}
          {viewError && (
            <Box sx={{ mt: 1, color: "#f44336", fontSize: "0.75rem" }}>
              {viewError}
            </Box>
          )}
        </Box>
        <Box sx={{ display: "flex", gap: 1 }}>
          <button
            type="button"
            onClick={() => {
              resetError();
              setViewError(null);
            }}
            style={{
              padding: "8px 16px",
              backgroundColor: "#1976d2",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
              fontSize: "0.875rem",
            }}
          >
            Retry
          </button>
          <button
            type="button"
            onClick={handleRetry}
            style={{
              padding: "8px 16px",
              backgroundColor: "#f57c00",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
              fontSize: "0.875rem",
            }}
          >
            Refetch Data
          </button>
        </Box>
      </Box>
    ),
    [viewId, viewError, handleRetry, setViewError],
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
        // Log error for debugging
        console.error(`View ${viewId} error:`, error, errorInfo);
        setViewError(`Render error: ${error.message}`);
      }}
      resetKeys={[viewId]} // Reset when viewId changes
    >
      <Box
        className="view-container"
        sx={{
          height: "100%",
          display: "flex",
          flexDirection: "column",
          position: "relative",
          opacity: isPendingAny ? 0.8 : 1,
          transition: "opacity 0.2s ease-in-out",
        }}
      >
        {/* Error display */}
        {viewError && (
          <Box
            sx={{
              position: "absolute",
              top: 8,
              left: "50%",
              transform: "translateX(-50%)",
              zIndex: 1000,
              bgcolor: "error.main",
              color: "white",
              px: 2,
              py: 1,
              borderRadius: 1,
              fontSize: "0.75rem",
            }}
          >
            {viewError}
          </Box>
        )}

        {/* Modal with Suspense boundary */}
        <ErrorBoundary
          fallback={<div>Error loading modal</div>}
          onError={(error) => {
            console.error("Modal error:", error);
            setViewError("Modal failed to load");
          }}
        >
          <Suspense fallback={null}>
            <ModalComponent
              dataForModal={exportData}
              isModalOpen={isModalOpen}
              setIsModalOpen={handleModalClose}
            />
          </Suspense>
        </ErrorBoundary>

        {/* Main content with error boundary and loading states */}
        <ErrorBoundary
          fallback={
            <ViewErrorFallback
              error={new Error("View content error")}
              resetError={handleRetry}
            />
          }
          onError={(error, errorInfo) => {
            console.error("View content error:", error, errorInfo);
            setViewError(`Content error: ${error.message}`);
          }}
        >
          <Suspense
            fallback={
              <LoadingStates.Component
                message={`Loading ${deferredViewId}...`}
              />
            }
          >
            <ViewContent
              loading={loading}
              error={error}
              rawApiData={rawApiData}
              dataContent={dataContent}
              backgroundColor={backgroundColor}
              jumpToNode={jumpToNode}
              hexColor={deferredHex}
              onHexColorChange={handleHexChange}
              viewId={deferredViewId}
            />
          </Suspense>
        </ErrorBoundary>

        {/* Loading indicator for mutations */}
        {isPendingAny && (
          <Box
            sx={{
              position: "absolute",
              top: 8,
              right: 8,
              zIndex: 10,
            }}
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
