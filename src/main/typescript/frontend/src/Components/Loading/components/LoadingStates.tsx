import { Box } from "@mui/material";
import { InlineLoading } from "./InlineLoading";
import { LoadingSpinner } from "./LoadingSpinner";
import { TableSkeleton } from "./TableSkeleton";
import { ViewGridSkeleton } from "./ViewGridSkeleton";

export const LoadingStates = {
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

	Grid: ({ columns = 2, count = 4 }: { columns?: number; count?: number }) => (
		<ViewGridSkeleton columns={columns} count={count} />
	),

	Table: ({ rows = 5, columns = 4 }: { rows?: number; columns?: number }) => (
		<TableSkeleton rows={rows} columns={columns} />
	),

	Inline: ({ text }: { text?: string }) => <InlineLoading text={text} />,

	Component: ({ message }: { message?: string }) => (
		<LoadingSpinner message={message || "Loading component..."} />
	),
};

export default LoadingStates;
