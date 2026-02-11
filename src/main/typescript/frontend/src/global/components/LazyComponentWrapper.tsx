import ErrorBoundary from "@components/ErrorBoundary";
import { LoadingStates } from "@components/Loading/LoadingComponents";
import { Suspense } from "react";

interface LazyComponentWrapperProps {
	children: React.ReactNode;
	fallback?: React.ReactNode;
	errorMessage?: string;
}

export const LazyComponentWrapper = ({
	children,
	fallback = <LoadingStates.Page />,
	errorMessage = "Failed to load component",
}: LazyComponentWrapperProps) => (
	<ErrorBoundary
		fallback={
			<div
				style={{
					display: "flex",
					justifyContent: "center",
					alignItems: "center",
					height: "50vh",
					color: "#666",
				}}
			>
				{errorMessage}
			</div>
		}
		onError={(error, errorInfo) => {
			if (process.env.NODE_ENV === "development") {
				console.error("Route component error:", error, errorInfo);
			}
		}}
	>
		<Suspense fallback={fallback}>{children}</Suspense>
	</ErrorBoundary>
);

export default LazyComponentWrapper;
