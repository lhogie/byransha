import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { Button, StyledEngineProvider } from "@mui/material";
import { RouterProvider } from "react-router";
import { router } from "@global/router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import ErrorBoundary from "@components/ErrorBoundary";

// Enhanced QueryClient configuration for React 19
const queryClient = new QueryClient({
	defaultOptions: {
		queries: {
			staleTime: 30000,
			gcTime: 5 * 60 * 1000,
			refetchOnWindowFocus: false,
			refetchOnReconnect: true,
			retry: (failureCount, _error) => {
				if (failureCount < 3) {
					const delay = Math.min(1000 * 2 ** failureCount, 30000);
					console.log(`Query failed, retrying in ${delay}ms...`);
					return true;
				}
				return false;
			},
			retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
		},
		mutations: {
			retry: (failureCount, error) => {
				if (failureCount < 2 && error.message.includes("network")) {
					return true;
				}
				return false;
			},
			retryDelay: 1000,
		},
	},
});

window.addEventListener("unhandledrejection", (event) => {
	console.error("Unhandled promise rejection:", event.reason);

	event.preventDefault();
});

window.addEventListener("error", (event) => {
	console.error("Uncaught error:", event.error);
});

// App-level error fallback
const AppErrorFallback = () => (
	<div
		style={{
			display: "flex",
			flexDirection: "column",
			alignItems: "center",
			justifyContent: "center",
			height: "100vh",
			padding: "24px",
			textAlign: "center",
			backgroundColor: "#f5f5f5",
			fontFamily: "IBM Plex Sans, sans-serif",
		}}
	>
		<div style={{ fontSize: "4rem", marginBottom: "24px" }}>💥</div>
		<h1 style={{ color: "#d32f2f", marginBottom: "16px", fontSize: "2rem" }}>
			Critical Application Error
		</h1>
		<p
			style={{
				color: "#666",
				marginBottom: "32px",
				maxWidth: "600px",
				fontSize: "1.1rem",
				lineHeight: "1.6",
			}}
		>
			The application encountered a critical error and cannot continue. This
			might be due to a configuration issue, network problem, or browser
			compatibility issue.
		</p>
		<div
			style={{
				display: "flex",
				gap: "16px",
				flexWrap: "wrap",
				justifyContent: "center",
			}}
		>
			<Button
				onClick={() => window.location.reload()}
				style={{
					padding: "12px 24px",
					backgroundColor: "#1976d2",
					color: "white",
					border: "none",
					borderRadius: "8px",
					cursor: "pointer",
					fontSize: "1rem",
					fontWeight: "500",
					transition: "background-color 0.2s",
				}}
				onMouseOver={(e) => (e.currentTarget.style.backgroundColor = "#1565c0")}
				onMouseOut={(e) => (e.currentTarget.style.backgroundColor = "#1976d2")}
			>
				Reload Application
			</Button>
			<Button
				onClick={() => {
					localStorage.clear();
					sessionStorage.clear();
					window.location.reload();
				}}
				style={{
					padding: "12px 24px",
					backgroundColor: "#f57c00",
					color: "white",
					border: "none",
					borderRadius: "8px",
					cursor: "pointer",
					fontSize: "1rem",
					fontWeight: "500",
					transition: "background-color 0.2s",
				}}
				onMouseOver={(e) => (e.currentTarget.style.backgroundColor = "#ef6c00")}
				onMouseOut={(e) => (e.currentTarget.style.backgroundColor = "#f57c00")}
			>
				Clear Cache & Reload
			</Button>
		</div>
		<details style={{ marginTop: "32px", maxWidth: "800px" }}>
			<summary style={{ cursor: "pointer", color: "#666", fontSize: "0.9rem" }}>
				Troubleshooting Information
			</summary>
			<div
				style={{
					marginTop: "16px",
					textAlign: "left",
					fontSize: "0.875rem",
					color: "#555",
				}}
			>
				<p>
					<strong>Browser:</strong> {navigator.userAgent}
				</p>
				<p>
					<strong>Time:</strong> {new Date().toISOString()}
				</p>
				<p>
					<strong>URL:</strong> {window.location.href}
				</p>
				<p>
					<strong>React Version:</strong> 19.1.0
				</p>
			</div>
		</details>
	</div>
);

// Get container and ensure it exists
const container = document.getElementById("root");
if (!container) {
	throw new Error(
		"Root container not found. Make sure there's a div with id='root' in your HTML.",
	);
}

// Create root with React 19 concurrent features
const root = createRoot(container);

// Enhanced render with comprehensive error boundaries
root.render(
	<StrictMode>
		<ErrorBoundary
			fallback={<AppErrorFallback />}
			onError={(error, errorInfo) => {
				// Log critical application errors
				console.error("Critical application error:", error, errorInfo);

				// In production, send to monitoring service
				if (process.env.NODE_ENV === "production") {
					// Example: Sentry.captureException(error, {
					//   contexts: { react: errorInfo },
					//   tags: { location: 'app-root' }
					// });
				}
			}}
		>
			<StyledEngineProvider injectFirst>
				<QueryClientProvider client={queryClient}>
					<ErrorBoundary
						fallback={
							<div
								style={{
									display: "flex",
									flexDirection: "column",
									alignItems: "center",
									justifyContent: "center",
									height: "100vh",
									padding: "24px",
									textAlign: "center",
								}}
							>
								<h2 style={{ color: "#d32f2f", marginBottom: "16px" }}>
									Router Error
								</h2>
								<p style={{ color: "#666", marginBottom: "24px" }}>
									There was a problem with the application routing.
								</p>
								<Button
									onClick={() => window.location.reload()}
									style={{
										padding: "12px 24px",
										backgroundColor: "#1976d2",
										color: "white",
										border: "none",
										borderRadius: "6px",
										cursor: "pointer",
									}}
								>
									Reload Application
								</Button>
							</div>
						}
						onError={(error, errorInfo) => {
							console.error("Router error:", error, errorInfo);
						}}
					>
						<RouterProvider router={router} />
					</ErrorBoundary>

					{/* React Query DevTools - only in development */}
					{process.env.NODE_ENV === "development" && (
						<ReactQueryDevtools
							buttonPosition="bottom-left"
							initialIsOpen={false}
						/>
					)}
				</QueryClientProvider>
			</StyledEngineProvider>
		</ErrorBoundary>
	</StrictMode>,
);

// Performance monitoring
if (process.env.NODE_ENV === "development") {
	// Log performance metrics
	const observer = new PerformanceObserver((list) => {
		list.getEntries().forEach((entry) => {
			if (entry.entryType === "navigation") {
				const navEntry = entry as PerformanceNavigationTiming;
				console.log("Navigation performance:", {
					domContentLoaded:
						navEntry.domContentLoadedEventEnd -
						navEntry.domContentLoadedEventStart,
					loadComplete: navEntry.loadEventEnd - navEntry.loadEventStart,
					totalTime: navEntry.loadEventEnd - navEntry.fetchStart,
				});
			}
		});
	});

	observer.observe({ entryTypes: ["navigation"] });
}

// Export for testing purposes
export { queryClient };
