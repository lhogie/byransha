import { Box, CircularProgress, Typography } from "@mui/material";

export const InlineLoading = ({ text = "Loading..." }: { text?: string }) => (
	<Box sx={{ display: "flex", alignItems: "center", gap: 1, padding: 1 }}>
		<CircularProgress size={16} />
		<Typography variant="body2" color="text.secondary">
			{text}
		</Typography>
	</Box>
);
