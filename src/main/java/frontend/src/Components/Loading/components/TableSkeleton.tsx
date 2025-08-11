import { Box, Skeleton } from "@mui/material";

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
