import { Box, CircularProgress, Typography } from "@mui/material";

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
