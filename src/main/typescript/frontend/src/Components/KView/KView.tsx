import { useNavigate, useParams } from "react-router";
import { useApiData } from "@hooks/useApiData";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import { Box, Container, IconButton, Paper, Typography } from "@mui/material";
import { shortenAndFormatLabel } from "@/utils/utils";
import { KViewContent } from "./KViewContent";

/**
 * KView - Composant réutilisable pour afficher/éditer un nœud
 * Basé sur FormPage mais extrait comme composant générique
 */
const KView = () => {
	const { rootId: rawRootId } = useParams();
	const navigate = useNavigate();
	const rootId = rawRootId ? parseInt(rawRootId, 10) : 0;

	const {
		data: rawApiData,
		isLoading: loading,
		error,
		refetch,
	} = useApiData(`class_attribute_field`, {
		node_id: rootId,
	});

	const pageName =
		rawApiData?.data?.results?.[0]?.result?.data?.currentNode?.name;

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
					onClick={() => navigate(`/home`)}
					aria-label="Fermer"
					sx={{ position: "absolute", top: 16, right: 16 }}
				>
					<CloseRoundedIcon />
				</IconButton>

				<Typography
					variant="h3"
					component="h1"
					gutterBottom
					sx={{
						color: "primary.main",
						textAlign: "center",
						fontWeight: 600,
						pb: 3,
						borderBottom: "3px solid",
						borderColor: "primary.main",
					}}
				>
					Fiche : {shortenAndFormatLabel(pageName)}
				</Typography>
				
				<KViewContent
					rawApiData={rawApiData}
					loading={loading}
					error={error}
					refetch={refetch}
				/>
			</Container>
		</Box>
	);
};

export default KView;
