import { useApiData } from "@hooks/useApiData";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import { createTheme } from "@mui/material";
import { frFR as corefrFR } from "@mui/material/locale";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { frFR } from "@mui/x-date-pickers/locales";
import { ReactRouterAppProvider } from "@toolpad/core/react-router";
import React, {
	memo,
	Suspense,
	startTransition,
	useDeferredValue,
	useMemo,
} from "react";
import { Outlet } from "react-router";
import "dayjs/locale/fr";
import ErrorBoundary from "@components/ErrorBoundary";
import { LoadingStates } from "@components/Loading/LoadingComponents";
import type { Navigation } from "@toolpad/core";
import { Toaster } from "react-hot-toast";

const theme = createTheme(
	{
		cssVariables: true,
		colorSchemes: {
			dark: false,
		},
		typography: {
			fontFamily: "IBM Plex Sans, sans-serif",
		},
		transitions: {
			duration: {
				shortest: 150,
				shorter: 200,
				short: 250,
				standard: 300,
				complex: 375,
				enteringScreen: 225,
				leavingScreen: 195,
			},
			easing: {
				easeInOut: "cubic-bezier(0.4, 0, 0.2, 1)",
				easeOut: "cubic-bezier(0.0, 0, 0.2, 1)",
				easeIn: "cubic-bezier(0.4, 0, 1, 1)",
				sharp: "cubic-bezier(0.4, 0, 0.6, 1)",
			},
		},
	},
	frFR,
	corefrFR,
);

const brandingConfig = {
	title: "",
	logo: (
		<img
			src="/logo.svg"
			alt="I3S"
			width="100%"
			height="100%"
			style={{ color: "inherit" }}
		/>
	),
	homeUrl: "/home",
};

const NavigationProvider = memo(
	({
		children,
		navigation,
	}: {
		children: React.ReactNode;
		navigation: Navigation;
	}) => (
		<ErrorBoundary
			fallback={
				<div style={{ padding: "16px", textAlign: "center", color: "#666" }}>
					Navigation error occurred. Please refresh the page.
				</div>
			}
		>
			<ReactRouterAppProvider
				navigation={navigation}
				theme={theme}
				branding={brandingConfig}
			>
				{children}
			</ReactRouterAppProvider>
		</ErrorBoundary>
	),
);

NavigationProvider.displayName = "NavigationProvider";

const LoadingNavigation = () => [
	{
		kind: "page" as const,
		title: "Loading...",
		segment: "home",
		icon: <MenuOutlinedIcon />,
	},
];

const ErrorNavigation = () => [
	{
		kind: "page" as const,
		title: "Error - Retry",
		segment: "home",
		icon: <MenuOutlinedIcon />,
	},
];

function App() {
	const { data, isLoading, error, refetch } = useApiData(
		"endpoints?only_applicable&type=byransha.web.View",
		{},
		{
			staleTime: 60000,
			gcTime: 5 * 60 * 1000,
			refetchOnWindowFocus: false,
			refetchOnReconnect: true,
			retry: (failureCount: number, _error: any) => {
				if (failureCount < 3) {
					console.log(`Retrying API call, attempt ${failureCount + 1}`);
					return true;
				}
				return false;
			},
			retryDelay: (attemptIndex: number) =>
				Math.min(1000 * 2 ** attemptIndex, 30000),
		},
	);

	const deferredData = useDeferredValue(data);
	const deferredIsLoading = useDeferredValue(isLoading);
	const deferredError = useDeferredValue(error);

	const navigation = useMemo((): Navigation => {
		try {
			if (deferredIsLoading) {
				return LoadingNavigation();
			}

			if (deferredError || !deferredData?.data?.results) {
				console.error("Navigation data error:", deferredError);
				return ErrorNavigation();
			}

			const results = deferredData.data.results;
			const navigationItems = results.map((view) => ({
				kind: "page" as const,
				title: view.endpoint || "Unknown View",
				segment: `information/${(view.endpoint || "unknown").replaceAll(" ", "_")}`,
				icon: <MenuOutlinedIcon />,
			}));

			const hasHome = navigationItems.some((item) => item.segment === "home");
			if (!hasHome) {
				navigationItems.unshift({
					kind: "page" as const,
					title: "Home",
					segment: "home",
					icon: <MenuOutlinedIcon />,
				});
			}

			return navigationItems;
		} catch (error) {
			console.error("Error creating navigation:", error);
			return ErrorNavigation();
		}
	}, [deferredData, deferredIsLoading, deferredError]);

	const handleRetry = React.useCallback(() => {
		startTransition(() => {
			refetch();
		});
	}, [refetch]);

	const AppErrorFallback = React.useCallback(
		() => (
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
				}}
			>
				<div style={{ fontSize: "3rem", marginBottom: "16px" }}>⚠️</div>
				<h1 style={{ color: "#d32f2f", marginBottom: "16px" }}>
					Application Error
				</h1>
				<p style={{ color: "#666", marginBottom: "24px", maxWidth: "500px" }}>
					Something went wrong with the application. This could be due to a
					network issue or a temporary server problem.
				</p>
				<div style={{ display: "flex", gap: "12px" }}>
					<button
						type="button"
						onClick={handleRetry}
						style={{
							padding: "12px 24px",
							backgroundColor: "#1976d2",
							color: "white",
							border: "none",
							borderRadius: "6px",
							cursor: "pointer",
							fontSize: "1rem",
						}}
					>
						Retry
					</button>
					<button
						type="button"
						onClick={() => window.location.reload()}
						style={{
							padding: "12px 24px",
							backgroundColor: "#666",
							color: "white",
							border: "none",
							borderRadius: "6px",
							cursor: "pointer",
							fontSize: "1rem",
						}}
					>
						Reload Page
					</button>
				</div>
				{process.env.NODE_ENV === "development" && deferredError && (
					<details style={{ marginTop: "24px", textAlign: "left" }}>
						<summary style={{ cursor: "pointer", color: "#666" }}>
							Error Details (Development)
						</summary>
						<pre
							style={{
								marginTop: "8px",
								padding: "12px",
								backgroundColor: "#f0f0f0",
								borderRadius: "4px",
								fontSize: "0.875rem",
								overflow: "auto",
								maxWidth: "600px",
							}}
						>
							{deferredError.message}
						</pre>
					</details>
				)}
			</div>
		),
		[handleRetry, deferredError],
	);

	return (
		<ErrorBoundary
			fallback={<AppErrorFallback />}
			onError={(error, errorInfo) => {
				console.error("App-level error:", error, errorInfo);

				if (process.env.NODE_ENV === "production") {
					// Example: Sentry.captureException(error, { contexts: { react: errorInfo } });
				}
			}}
		>
			<LocalizationProvider
				dateAdapter={AdapterDayjs}
				adapterLocale="fr"
				localeText={
					frFR.components.MuiLocalizationProvider.defaultProps.localeText
				}
			>
				<Toaster
					position="top-right"
					toastOptions={{
						duration: 4000,
						style: {
							background: "#333",
							color: "#fff",
						},
						success: {
							iconTheme: {
								primary: "#4caf50",
								secondary: "#fff",
							},
						},
						error: {
							iconTheme: {
								primary: "#f44336",
								secondary: "#fff",
							},
						},
					}}
				/>

				<Suspense fallback={<LoadingStates.Page />}>
					<NavigationProvider navigation={navigation}>
						<Suspense
							fallback={
								<div
									style={{
										display: "flex",
										justifyContent: "center",
										alignItems: "center",
										height: "50vh",
									}}
								>
									<LoadingStates.Component message="Loading page content..." />
								</div>
							}
						>
							<Outlet />
						</Suspense>
					</NavigationProvider>
				</Suspense>
			</LocalizationProvider>
		</ErrorBoundary>
	);
}

export default memo(App);
