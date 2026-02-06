import ErrorBoundary from "@components/ErrorBoundary";

export const AppErrorElement = () => (
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
);

export const LayoutErrorElement = () => (
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
);
