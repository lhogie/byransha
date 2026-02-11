import View from "@common/View";
import { useTitle } from "@global/useTitle";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import { Box, Container, IconButton, Paper } from "@mui/material";
import { useNavigate, useParams } from "react-router";

const Expand = () => {
	const navigate = useNavigate();
	const { typeEndpoint: name } = useParams();
	const nameString = name ? decodeURIComponent(name) : "";
	useTitle(nameString);

	return (
		<Box sx={{ padding: { xs: "8px", sm: "16px", md: "40px" } }}>
			<Container
				component={Paper}
				elevation={3}
				maxWidth={false}
				sx={{ p: 3, position: "relative", minHeight: "85vh", overflow: "auto" }}
			>
				<IconButton
					className="close-button"
					onClick={() => navigate(-1)}
					aria-label="close"
					sx={{ position: "absolute", top: 16, right: 16, zIndex: 10 }}
				>
					<CloseRoundedIcon />
				</IconButton>

				<View
					viewId={nameString}
					sx={{
						height: "100%",
						overflow: "auto",
					}}
				/>
			</Container>
		</Box>
	);
};

export default Expand;
