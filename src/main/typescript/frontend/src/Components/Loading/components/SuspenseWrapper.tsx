import type React from "react";
import { Suspense } from "react";
import { LoadingSpinner } from "./LoadingSpinner";

export const SuspenseWrapper = ({
	children,
	fallback,
}: {
	children: React.ReactNode;
	fallback?: React.ReactNode;
	errorFallback?: React.ReactNode;
}) => (
	<Suspense
		fallback={fallback || <LoadingSpinner message="Loading component..." />}
	>
		{children}
	</Suspense>
);
