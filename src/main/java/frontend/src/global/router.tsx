import { createBrowserRouter } from "react-router";
import { lazy, Suspense } from "react";
import App from "./App.js";
import MainLayout from "./MainLayout.js";
import ErrorBoundary from "@components/ErrorBoundary";
import { LoadingStates } from "@components/Loading/LoadingComponents";

// Lazy load components for better code splitting and performance
const LoginForm = lazy(() => import("@components/LoginForm/LoginForm"));

const HomePage = lazy(() => import("@components/HomePage/HomePage"));

const InformationPage = lazy(
	() => import("@components/InformationPage/InformationPage"),
);

const AddNodePage = lazy(() => import("@components/AddNode/AddNodePage"));

const FormPage = lazy(() => import("@components/FormPage/FormPage"));

// Component wrapper that includes both Suspense and Error Boundary
const LazyComponentWrapper = ({
	children,
	fallback = <LoadingStates.Page />,
	errorMessage = "Failed to load component",
}: {
	children: React.ReactNode;
	fallback?: React.ReactNode;
	errorMessage?: string;
}) => (
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
			// Log to console in development
			if (process.env.NODE_ENV === "development") {
				console.error("Route component error:", error, errorInfo);
			}
			// In production, you could send this to an error tracking service
		}}
	>
		<Suspense fallback={fallback}>{children}</Suspense>
	</ErrorBoundary>
);

export const router = createBrowserRouter([
	{
		Component: App,
		errorElement: (
			<ErrorBoundary>
				<div
					style={{
						display: "flex",
						justifyContent: "center",
						alignItems: "center",
						height: "100vh",
						flexDirection: "column",
						gap: "16px",
					}}
				>
					<h2>Application Error</h2>
					<p>Something went wrong with the application.</p>
					<button type="button" onClick={() => window.location.reload()}>
						Reload Page
					</button>
				</div>
			</ErrorBoundary>
		),
		children: [
			{
				path: "/",
				element: (
					<LazyComponentWrapper
						fallback={<LoadingStates.Component message="Loading login..." />}
						errorMessage="Failed to load login form"
					>
						<LoginForm />
					</LazyComponentWrapper>
				),
			},
			{
				element: <MainLayout />,
				errorElement: (
					<ErrorBoundary>
						<div
							style={{
								display: "flex",
								justifyContent: "center",
								alignItems: "center",
								height: "50vh",
								flexDirection: "column",
								gap: "16px",
							}}
						>
							<h3>Layout Error</h3>
							<p>There was a problem loading the main layout.</p>
						</div>
					</ErrorBoundary>
				),
				children: [
					{
						path: "/home",
						element: (
							<LazyComponentWrapper
								fallback={<LoadingStates.Grid columns={2} count={4} />}
								errorMessage="Failed to load home page"
							>
								<HomePage />
							</LazyComponentWrapper>
						),
					},
					{
						path: "/information/:viewId",
						element: (
							<LazyComponentWrapper
								fallback={
									<LoadingStates.Component message="Loading information..." />
								}
								errorMessage="Failed to load information page"
							>
								<InformationPage />
							</LazyComponentWrapper>
						),
					},
					{
						path: "/add-node",
						element: (
							<LazyComponentWrapper
								fallback={
									<LoadingStates.Component message="Loading add node page..." />
								}
								errorMessage="Failed to load add node page"
							>
								<AddNodePage />
							</LazyComponentWrapper>
						),
					},
					{
						path: "/add-node/form/:rootId",
						element: (
							<LazyComponentWrapper
								fallback={<LoadingStates.Component message="Loading form..." />}
								errorMessage="Failed to load form page"
							>
								<FormPage />
							</LazyComponentWrapper>
						),
					},
				],
			},
		],
	},
]);
