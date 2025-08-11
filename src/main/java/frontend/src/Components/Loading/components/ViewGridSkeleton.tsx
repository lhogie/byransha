import { Box } from "@mui/material";
import { ViewCardSkeleton } from "./ViewCardSkeleton";

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
