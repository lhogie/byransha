import { useState, useCallback, useEffect } from 'react';

export interface ErrorInfo {
  error: Error | null;
  errorInfo?: React.ErrorInfo | null;
}

export function useErrorBoundary() {
  const [errorInfo, setErrorInfo] = useState<ErrorInfo>({ error: null });

  const resetError = useCallback(() => {
    setErrorInfo({ error: null });
  }, []);

  const captureError = useCallback((error: Error, errorInfo?: React.ErrorInfo) => {
    setErrorInfo({ error, errorInfo });

    // Log error in development
    if (process.env.NODE_ENV === 'development') {
      console.error('Error captured by useErrorBoundary:', error, errorInfo);
    }
  }, []);

  // Auto-reset error after a delay (optional)
  useEffect(() => {
    if (errorInfo.error) {
      const timer = setTimeout(() => {
        // Auto-reset after 10 seconds (optional)
        // resetError();
      }, 10000);

      return () => clearTimeout(timer);
    }
  }, [errorInfo.error]);

  return {
    hasError: !!errorInfo.error,
    error: errorInfo.error,
    errorInfo: errorInfo.errorInfo,
    resetError,
    captureError,
  };
}

// Hook for wrapping async operations with error handling
export function useAsyncErrorHandler() {
  const { captureError } = useErrorBoundary();

  const wrapAsync = useCallback(
    <T extends (...args: any[]) => Promise<any>>(fn: T): T => {
      return ((...args: any[]) => {
        return fn(...args).catch((error: Error) => {
          captureError(error);
          throw error; // Re-throw to maintain promise chain
        });
      }) as T;
    },
    [captureError]
  );

  return { wrapAsync };
}
