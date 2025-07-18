import { Box, CircularProgress, Skeleton, Typography } from "@mui/material";
import React, { Suspense } from "react";

// Basic loading spinner with customizable size and color
export const LoadingSpinner = ({
	size = 40,
	color = "primary",
	message,
	sx = {},
}: {
	size?: number;
	color?: "primary" | "secondary" | "inherit";
	message?: string;
	sx?: any;
}) => (
	<Box
		sx={{
			display: "flex",
			flexDirection: "column",
			alignItems: "center",
			justifyContent: "center",
			gap: 2,
			padding: 2,
			...sx,
		}}
	>
		<CircularProgress size={size} color={color} />
		{message && (
			<Typography variant="body2" color="text.secondary" textAlign="center">
				{message}
			</Typography>
		)}
	</Box>
);

// Card loading skeleton for view cards
export const ViewCardSkeleton = () => (
	<Box
		sx={{
			aspectRatio: "1",
			border: "1px solid #e0e0e0",
			borderRadius: 2,
			display: "flex",
			flexDirection: "column",
			bgcolor: "#ffffff",
		}}
	>
		<Box
			sx={{
				height: "40px",
				width: "100%",
				bgcolor: "#f5f5f5",
				borderBottom: "1px solid #e0e0e0",
				display: "flex",
				alignItems: "center",
				justifyContent: "center",
			}}
		>
			<Skeleton variant="text" width="80%" height={20} />
		</Box>
		<Box
			sx={{
				padding: 2,
				height: "calc(100% - 40px)",
				display: "flex",
				flexDirection: "column",
				gap: 1,
			}}
		>
			<Skeleton variant="text" width="100%" height={20} />
			<Skeleton variant="text" width="90%" height={20} />
			<Skeleton variant="text" width="75%" height={20} />
			<Skeleton variant="rectangular" width="100%" height={60} />
			<Skeleton variant="text" width="60%" height={20} />
		</Box>
	</Box>
);

// Grid loading skeleton for multiple cards
export const ViewGridSkeleton = ({
	columns = 2,
	count = 4,
}: {
	columns?: number;
	count?: number;
}) => (
	<Box
		sx={{
			display: "flex",
			flexWrap: "wrap",
			gap: 4,
		}}
	>
		{Array.from({ length: count }).map((_, index) => (
			<Box
				key={index}
				sx={{
					width: {
						xs: "100%",
						sm: `calc(${100 / Math.min(columns, 2)}% - 16px)`,
						md: `calc(${100 / columns}% - 32px)`,
					},
				}}
			>
				<ViewCardSkeleton />
			</Box>
		))}
	</Box>
);

// Table loading skeleton
export const TableSkeleton = ({
	rows = 5,
	columns = 4,
}: {
	rows?: number;
	columns?: number;
}) => (
	<Box sx={{ width: "100%" }}>
		<Box sx={{ display: "flex", gap: 1, marginBottom: 1 }}>
			{Array.from({ length: columns }).map((_, index) => (
				<Skeleton key={index} variant="text" width="100%" height={40} />
			))}
		</Box>
		{Array.from({ length: rows }).map((_, rowIndex) => (
			<Box key={rowIndex} sx={{ display: "flex", gap: 1, marginBottom: 1 }}>
				{Array.from({ length: columns }).map((_, colIndex) => (
					<Skeleton key={colIndex} variant="text" width="100%" height={30} />
				))}
			</Box>
		))}
	</Box>
);

// Page loading overlay
export const PageLoadingOverlay = ({
	message = "Loading...",
}: {
	message?: string;
}) => (
	<Box
		sx={{
			position: "fixed",
			top: 0,
			left: 0,
			right: 0,
			bottom: 0,
			backgroundColor: "rgba(46, 59, 85, 0.9)",
			display: "flex",
			alignItems: "center",
			justifyContent: "center",
			zIndex: 9999,
		}}
	>
		<LoadingSpinner size={60} message={message} sx={{ color: "#90caf9" }} />
	</Box>
);

// Inline loading for smaller components
export const InlineLoading = ({ text = "Loading..." }: { text?: string }) => (
	<Box sx={{ display: "flex", alignItems: "center", gap: 1, padding: 1 }}>
		<CircularProgress size={16} />
		<Typography variant="body2" color="text.secondary">
			{text}
		</Typography>
	</Box>
);

// Enhanced Suspense wrapper with error boundary integration
export const SuspenseWrapper = ({
	children,
	fallback,
	errorFallback,
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

// Loading states for different scenarios
export const LoadingStates = {
	// For entire pages
	Page: () => (
		<Box
			sx={{
				display: "flex",
				justifyContent: "center",
				alignItems: "center",
				height: "100vh",
				bgcolor: "#2e3b55",
			}}
		>
			<LoadingSpinner
				size={60}
				message="Loading page..."
				sx={{ color: "#90caf9" }}
			/>
		</Box>
	),

	// For card grids
	Grid: ({ columns = 2, count = 4 }: { columns?: number; count?: number }) => (
		<ViewGridSkeleton columns={columns} count={count} />
	),

	// For data tables
	Table: ({ rows = 5, columns = 4 }: { rows?: number; columns?: number }) => (
		<TableSkeleton rows={rows} columns={columns} />
	),

	// For inline content
	Inline: ({ text }: { text?: string }) => <InlineLoading text={text} />,

	// For views/components
	Component: ({ message }: { message?: string }) => (
		<LoadingSpinner message={message || "Loading component..."} />
	),
};

// Hook for managing loading states with React 19 features
export const useLoadingState = (initialState = false) => {
	const [isLoading, setIsLoading] = React.useState(initialState);

	const startLoading = React.useCallback(() => {
		React.startTransition(() => {
			setIsLoading(true);
		});
	}, []);

	const stopLoading = React.useCallback(() => {
		React.startTransition(() => {
			setIsLoading(false);
		});
	}, []);

	const withLoading = React.useCallback(
		async <T,>(asyncFn: () => Promise<T>): Promise<T> => {
			startLoading();
			try {
				const result = await asyncFn();
				return result;
			} finally {
				stopLoading();
			}
		},
		[startLoading, stopLoading],
	);

	return {
		isLoading,
		startLoading,
		stopLoading,
		withLoading,
	};
};

export default LoadingStates;
