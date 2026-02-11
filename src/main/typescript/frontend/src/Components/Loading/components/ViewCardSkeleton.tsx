import { Box, Skeleton } from "@mui/material";

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
