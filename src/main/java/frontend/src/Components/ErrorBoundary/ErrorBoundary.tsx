import React, { Component, type ReactNode } from "react";
import { Box, Typography, Button, Alert, AlertTitle } from "@mui/material";
import { ErrorOutline, Refresh } from "@mui/icons-material";

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
  onError?: (error: Error, errorInfo: React.ErrorInfo) => void;
  resetKeys?: (string | number)[];
}

interface State {
  hasError: boolean;
  error: Error | null;
  errorInfo: React.ErrorInfo | null;
}

class ErrorBoundary extends Component<Props, State> {
  private prevResetKeys: (string | number)[] | undefined;

  constructor(props: Props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    };
    this.prevResetKeys = props.resetKeys;
  }

  static getDerivedStateFromError(error: Error): Partial<State> {
    return {
      hasError: true,
      error,
    };
  }

  componentDidUpdate(prevProps: Props) {
    const { resetKeys } = this.props;
    const { hasError } = this.state;

    if (hasError && resetKeys && this.prevResetKeys) {
      // Check if any reset key has changed
      const hasResetKeyChanged =
        resetKeys.length !== this.prevResetKeys.length ||
        resetKeys.some((key, idx) => key !== this.prevResetKeys![idx]);

      if (hasResetKeyChanged) {
        this.setState({
          hasError: false,
          error: null,
          errorInfo: null,
        });
      }
    }

    this.prevResetKeys = resetKeys;
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    this.setState({
      error,
      errorInfo,
    });

    // Call the optional error handler
    if (this.props.onError) {
      this.props.onError(error, errorInfo);
    }

    // Log error to console in development
    if (process.env.NODE_ENV === "development") {
      console.error("ErrorBoundary caught an error:", error, errorInfo);
    }
  }

  handleRetry = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
    });
  };

  render() {
    if (this.state.hasError) {
      // If a custom fallback is provided, use it
      if (this.props.fallback) {
        return this.props.fallback;
      }

      // Default error UI
      return (
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            minHeight: "200px",
            padding: 3,
            bgcolor: "background.paper",
            borderRadius: 2,
            border: "1px solid",
            borderColor: "error.light",
          }}
        >
          <Alert
            severity="error"
            sx={{
              width: "100%",
              maxWidth: "600px",
              mb: 2,
            }}
            icon={<ErrorOutline />}
          >
            <AlertTitle>Something went wrong</AlertTitle>
            <Typography variant="body2" sx={{ mb: 2 }}>
              {this.state.error?.message || "An unexpected error occurred"}
            </Typography>

            {process.env.NODE_ENV === "development" && this.state.errorInfo && (
              <Box
                sx={{
                  mt: 2,
                  p: 2,
                  bgcolor: "grey.100",
                  borderRadius: 1,
                  maxHeight: "200px",
                  overflow: "auto",
                }}
              >
                <Typography
                  variant="caption"
                  component="pre"
                  sx={{
                    fontFamily: "monospace",
                    fontSize: "0.75rem",
                    whiteSpace: "pre-wrap",
                    wordBreak: "break-word",
                  }}
                >
                  {this.state.error?.stack}
                </Typography>
              </Box>
            )}

            <Button
              variant="outlined"
              startIcon={<Refresh />}
              onClick={this.handleRetry}
              sx={{ mt: 2 }}
              size="small"
            >
              Try Again
            </Button>
          </Alert>
        </Box>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
