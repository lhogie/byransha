import React, { useCallback, useEffect, useMemo, Suspense, memo } from "react";
import { useParams, useNavigate } from "react-router";
import { Box, IconButton, Typography, Fade, Zoom, Button } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import FullscreenIcon from "@mui/icons-material/Fullscreen";
import FullscreenExitIcon from "@mui/icons-material/FullscreenExit";
import RefreshIcon from "@mui/icons-material/Refresh";
import "./InformationPage.css";
import { useTitle } from "@global/useTitle";
import { View } from "@common/View";
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

// Enhanced InformationPage with React 19 optimizations
const InformationPage = memo(() => {
  const { viewId } = useParams<{ viewId: string }>();
  const navigate = useNavigate();
  const { isLoading: isTransitioning, withLoading } = useLoadingState();

  // React 19 optimized state management
  const [isFullscreen, setIsFullscreen, isFullscreenUpdating] =
    useOptimizedState(false, {
      transitionUpdates: true,
    });

  const [isVisible, setIsVisible, isVisibleUpdating] = useOptimizedState(
    false,
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

  // Deferred viewId for better performance during navigation
  const deferredViewId = useDeferredValue(viewId);

  // Debounced error clearing
  const [errorToClear, setErrorToClear] = useOptimizedState<string | null>(
    null,
    {
      debounceMs: 5000,
      transitionUpdates: true,
    },
  );
  const [debouncedErrorClear] = useOptimizedDebounce(errorToClear, 5000);

  // Clear error after debounce
  useEffect(() => {
    if (debouncedErrorClear) {
      setError(null);
      setErrorToClear(null);
    }
  }, [debouncedErrorClear, setError, setErrorToClear]);

  // Memoized title computation
  const pageTitle = useMemo(() => {
    if (!deferredViewId) return "Information";
    return `Information - ${deferredViewId.replaceAll("_", " ")}`;
  }, [deferredViewId]);

  useTitle(pageTitle);

  // Enhanced navigation handler with startTransition
  const handleNavigation = useCallback(
    (path: string) => {
      withLoading(async () => {
        startTransition(() => {
          navigate(path);
        });
      });
    },
    [navigate, withLoading],
  );

  // Optimized close handler with error handling
  const handleClose = useCallback(() => {
    startTransition(() => {
      setIsVisible(false);
      // Add a small delay for the exit animation
      setTimeout(() => {
        handleNavigation("/home");
      }, 200);
    });
  }, [handleNavigation, setIsVisible]);

  // Fullscreen toggle handler with error boundary
  const handleFullscreenToggle = useCallback(() => {
    try {
      startTransition(() => {
        setIsFullscreen((prev) => !prev);
      });
    } catch (err) {
      setError("Failed to toggle fullscreen mode");
      setErrorToClear("clear");
    }
  }, [setIsFullscreen, setError, setErrorToClear]);

  // Refresh handler with loading state
  const handleRefresh = useCallback(() => {
    withLoading(async () => {
      try {
        startTransition(() => {
          window.location.reload();
        });
      } catch (err) {
        setError("Failed to refresh page");
        setErrorToClear("clear");
      }
    });
  }, [withLoading, setError, setErrorToClear]);

  // Enhanced keyboard handler with proper cleanup and error handling
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      try {
        switch (event.key) {
          case "Escape":
            event.preventDefault();
            handleClose();
            break;
          case "F11":
            event.preventDefault();
            handleFullscreenToggle();
            break;
          case "F5":
            if (event.ctrlKey) {
              event.preventDefault();
              handleRefresh();
            }
            break;
          default:
            break;
        }
      } catch (err) {
        setError("Keyboard shortcut failed");
        setErrorToClear("clear");
      }
    };

    window.addEventListener("keydown", handleKeyDown);

    // Set visible state after mount for animation
    const timer = setTimeout(() => {
      startTransition(() => {
        setIsVisible(true);
      });
    }, 50);

    return () => {
      window.removeEventListener("keydown", handleKeyDown);
      clearTimeout(timer);
    };
  }, [
    handleClose,
    handleFullscreenToggle,
    handleRefresh,
    setIsVisible,
    setError,
    setErrorToClear,
  ]);

  // Redirect if no viewId with error handling
  useEffect(() => {
    if (!viewId) {
      setError("No view ID provided");
      setTimeout(() => {
        handleNavigation("/home");
      }, 3000);
    }
  }, [viewId, handleNavigation, setError]);

  // Error fallback component with retry functionality
  const InformationErrorFallback = useCallback(
    ({
      error: errorMsg,
      resetError,
    }: {
      error?: Error;
      resetError?: () => void;
    }) => (
      <ErrorBoundary>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            height: "100vh",
            padding: 3,
            backgroundColor: "#f5f5f5",
          }}
        >
          <Box sx={{ textAlign: "center", mb: 3 }}>
            <Box
              component="span"
              sx={{ fontSize: "3rem", mb: 2, display: "block" }}
            >
              📄
            </Box>
            <Typography variant="h4" color="error" gutterBottom>
              Information Page Error
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
              Unable to load the information page for "{viewId}".
            </Typography>
            {errorMsg && (
              <Typography variant="body2" color="error" sx={{ mb: 3 }}>
                {errorMsg.message}
              </Typography>
            )}
          </Box>

          <Box
            sx={{
              display: "flex",
              gap: 2,
              flexWrap: "wrap",
              justifyContent: "center",
            }}
          >
            <Button
              variant="contained"
              onClick={resetError || handleRefresh}
              startIcon={<RefreshIcon />}
              color="primary"
            >
              Retry
            </Button>
            <Button
              variant="outlined"
              onClick={handleClose}
              startIcon={<CloseIcon />}
              color="secondary"
            >
              Go Back
            </Button>
          </Box>
        </Box>
      </ErrorBoundary>
    ),
    [viewId, handleRefresh, handleClose],
  );

  // Loading component with enhanced styling
  const LoadingComponent = useCallback(
    () => (
      <ErrorBoundary>
        <Box
          sx={{
            height: "100vh",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            backgroundColor: "#f8f9fa",
          }}
        >
          <LoadingStates.Component message={`Loading ${deferredViewId}...`} />
        </Box>
      </ErrorBoundary>
    ),
    [deferredViewId],
  );

  // Don't render anything if no viewId
  if (!viewId) {
    return (
      <InformationErrorFallback
        error={new Error("No view ID provided")}
        resetError={() => handleNavigation("/home")}
      />
    );
  }

  const isPendingAny =
    isTransitioning ||
    isFullscreenUpdating ||
    isVisibleUpdating ||
    isErrorUpdating;

  return (
    <ErrorBoundary
      fallback={<InformationErrorFallback />}
      onError={(error, errorInfo) => {
        console.error(
          `Information page error for ${viewId}:`,
          error,
          errorInfo,
        );
        setError(`Error: ${error.message}`);
        setErrorToClear("clear");
      }}
      resetKeys={[viewId]} // Reset error boundary when viewId changes
    >
      <Fade in={isVisible} timeout={300}>
        <Box
          className={`information-page ${isFullscreen ? "fullscreen" : ""}`}
          sx={{
            height: "100vh",
            display: "flex",
            flexDirection: "column",
            backgroundColor: "#ffffff",
            position: "relative",
            overflow: "hidden",
            opacity: isPendingAny ? 0.8 : 1,
            transition: "opacity 0.2s ease-in-out",
          }}
        >
          {/* Error display */}
          {error && (
            <Box
              sx={{
                position: "absolute",
                top: 16,
                left: "50%",
                transform: "translateX(-50%)",
                zIndex: 10000,
                bgcolor: "error.main",
                color: "white",
                px: 2,
                py: 1,
                borderRadius: 1,
              }}
            >
              <Typography variant="caption">{error}</Typography>
            </Box>
          )}

          {/* Header with controls */}
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              padding: { xs: 1, sm: 2 },
              borderBottom: "1px solid #e0e0e0",
              backgroundColor: "#f8f9fa",
              minHeight: "60px",
            }}
          >
            <Typography
              variant="h5"
              component="h1"
              sx={{
                flex: 1,
                fontWeight: 600,
                color: "#1976d2",
                fontSize: { xs: "1.25rem", sm: "1.5rem" },
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
              }}
            >
              {deferredViewId?.replaceAll("_", " ") || "Information"}
            </Typography>

            <Box sx={{ display: "flex", gap: 1 }}>
              <Zoom
                in={isVisible}
                timeout={400}
                style={{ transitionDelay: "100ms" }}
              >
                <IconButton
                  onClick={handleRefresh}
                  aria-label="refresh"
                  size="small"
                  disabled={isPendingAny}
                  sx={{
                    color: "#666",
                    "&:hover": { color: "#1976d2", backgroundColor: "#e3f2fd" },
                  }}
                >
                  <RefreshIcon />
                </IconButton>
              </Zoom>

              <Zoom
                in={isVisible}
                timeout={400}
                style={{ transitionDelay: "200ms" }}
              >
                <IconButton
                  onClick={handleFullscreenToggle}
                  aria-label={isFullscreen ? "exit fullscreen" : "fullscreen"}
                  size="small"
                  disabled={isPendingAny}
                  sx={{
                    color: "#666",
                    "&:hover": { color: "#1976d2", backgroundColor: "#e3f2fd" },
                  }}
                >
                  {isFullscreen ? <FullscreenExitIcon /> : <FullscreenIcon />}
                </IconButton>
              </Zoom>

              <Zoom
                in={isVisible}
                timeout={400}
                style={{ transitionDelay: "300ms" }}
              >
                <IconButton
                  onClick={handleClose}
                  aria-label="close"
                  size="small"
                  disabled={isPendingAny}
                  sx={{
                    color: "#666",
                    "&:hover": { color: "#d32f2f", backgroundColor: "#ffebee" },
                  }}
                >
                  <CloseIcon />
                </IconButton>
              </Zoom>
            </Box>
          </Box>

          {/* Main content area */}
          <Box
            sx={{
              flex: 1,
              overflow: "hidden",
              display: "flex",
              flexDirection: "column",
            }}
          >
            <ErrorBoundary
              fallback={
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    height: "100%",
                    flexDirection: "column",
                    gap: 2,
                  }}
                >
                  <Typography color="error">
                    Failed to load view content
                  </Typography>
                  <Button
                    onClick={handleRefresh}
                    startIcon={<RefreshIcon />}
                    variant="contained"
                    color="primary"
                  >
                    Retry
                  </Button>
                </Box>
              }
              onError={(error, errorInfo) => {
                console.error("View content error:", error, errorInfo);
                setError(`View error: ${error.message}`);
                setErrorToClear("clear");
              }}
            >
              <Suspense fallback={<LoadingComponent />}>
                <View
                  viewId={deferredViewId || ""}
                  sx={{
                    height: "100%",
                    overflow: "auto",
                    backgroundColor: "#ffffff",
                  }}
                />
              </Suspense>
            </ErrorBoundary>
          </Box>

          {/* Loading indicator for transitions */}
          {isPendingAny && (
            <Box
              sx={{
                position: "absolute",
                top: 0,
                left: 0,
                right: 0,
                height: "3px",
                backgroundColor: "#e0e0e0",
                overflow: "hidden",
              }}
            >
              <Box
                sx={{
                  height: "100%",
                  backgroundColor: "#1976d2",
                  animation: "loading-bar 1s ease-in-out infinite",
                  "@keyframes loading-bar": {
                    "0%": { transform: "translateX(-100%)" },
                    "100%": { transform: "translateX(100%)" },
                  },
                }}
              />
            </Box>
          )}
        </Box>
      </Fade>
    </ErrorBoundary>
  );
});

InformationPage.displayName = "InformationPage";

export default InformationPage;
