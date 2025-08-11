import { Box } from "@mui/material";
import { LoadingSpinner } from "./LoadingSpinner";

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
