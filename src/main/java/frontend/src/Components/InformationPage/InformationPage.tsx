import { useParams } from "react-router";
import "./InformationPage.css";
import { useTitle } from "../../global/useTitle.js";
import { View } from "../Common/View";
import { IconButton } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { useNavigate } from "react-router";

const InformationPage = () => {
	const { viewId } = useParams();

	const navigate = useNavigate();

	useTitle(`Information for View ${viewId}`);

	if (!viewId) {
		navigate("/home");
		return null;
	}

	return (
		<div className="information-page">
			<h1>Content:</h1>
			<View viewId={viewId} />
			<IconButton
				className="close-button"
				onClick={() => {
					navigate("/home");
				}}
				aria-label="close"
			>
				<CloseIcon />
			</IconButton>
		</div>
	);
};
export default InformationPage;
