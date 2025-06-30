import { useParams } from "react-router";
import "./InformationPage.css";
import { useTitle } from "@global/useTitle";
import { View } from "@common/View";
import { IconButton } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { useNavigate } from "react-router";
import { useEffect } from "react";

const InformationPage = () => {
	const { viewId } = useParams();

	const navigate = useNavigate();

	useTitle(`Information for View ${viewId}`);

	useEffect(() => {
		const handleKeyDown = (event: KeyboardEvent) => {
			if (event.key === "Escape") {
				navigate("/home");
			}
		};
		window.addEventListener("keydown", handleKeyDown);
		return () => {
			window.removeEventListener("keydown", handleKeyDown);
		};
	}, [navigate]);

	if (!viewId) {
		navigate("/home");
		return null;
	}

	return (
		<div className="information-page">
			<h1>{viewId}</h1>
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
