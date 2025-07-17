import React, { type ComponentType, type ReactNode } from "react";
import ErrorBoundary from "./ErrorBoundary";

export interface WithErrorBoundaryOptions {
  fallback?: ReactNode;
  onError?: (error: Error, errorInfo: React.ErrorInfo) => void;
  isolate?: boolean; // Whether to isolate this component's errors
}

/**
 * Higher-order component that wraps a component with an ErrorBoundary
 */
export function withErrorBoundary<P extends object>(
  Component: ComponentType<P>,
  options: WithErrorBoundaryOptions = {},
) {
  const WrappedComponent = React.forwardRef<unknown, P>((props, ref) => {
    const { fallback, onError, isolate = true } = options;

    const errorBoundaryProps = {
      fallback,
      onError: (error: Error, errorInfo: React.ErrorInfo) => {
        // Call custom error handler if provided
        if (onError) {
          onError(error, errorInfo);
        }

        // If not isolating, re-throw to parent boundary
        if (!isolate) {
          throw error;
        }
      },
    };

    return (
      <ErrorBoundary {...errorBoundaryProps}>
        <Component {...(props as P)} />
      </ErrorBoundary>
    );
  });

  // Preserve component name for debugging
  WrappedComponent.displayName = `withErrorBoundary(${
    Component.displayName || Component.name || "Component"
  })`;

  return WrappedComponent;
}

/**
 * Decorator version for class components
 */
export const ErrorBoundaryDecorator = (
  options: WithErrorBoundaryOptions = {},
) => {
  return <P extends object>(Component: ComponentType<P>) => {
    return withErrorBoundary(Component, options);
  };
};

/**
 * Utility to create an error boundary with specific fallback
 */
export const createErrorBoundary = (
  fallback: ReactNode,
  onError?: (error: Error, errorInfo: React.ErrorInfo) => void,
) => {
  return <P extends object>(Component: ComponentType<P>) => {
    return withErrorBoundary(Component, { fallback, onError });
  };
};

export default withErrorBoundary;
