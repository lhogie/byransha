import {useNavigate, useParams} from "react-router";
import "./FormPage.css";
import { useApiData } from "@hooks/useApiData";

import {Form} from "@components/FormPage/FormComponents/Form";
import {Box, Container, IconButton, Paper, Typography} from "@mui/material";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import {shortenAndFormatLabel} from "@/utils/utils";

const FormPage = () => {
	const { rootId: rawRootId } = useParams();
	const navigate = useNavigate();
	const rootId = rawRootId ? parseInt(rawRootId) : 0;

	const {
		data: rawApiData,
		isLoading: loading,
		error,
		refetch,
	} = useApiData(`class_attribute_field`, {
		node_id: rootId,
	});

	const pageName = rawApiData?.data?.results?.[0]?.result?.data?.currentNode?.name;


	return (
		<>
			<Box sx={{padding: { xs: "8px", sm: "16px", md: "40px" },}}>
				<Container
					component={Paper}
					elevation={3}
					maxWidth={false}
					sx={{p: 3, position: "relative", minHeight: "85vh", overflow: "auto"}}
				>
					<IconButton
						className="close-button"
						onClick={() => navigate(-1)}
						aria-label="close"
						sx={{ position: "absolute", top: 16, right: 16 }}
					>
						<CloseRoundedIcon />
					</IconButton>

					<Typography
						variant="h3"
						component="h1"
						gutterBottom
						sx={{
							color: "#2c3e50",
							textAlign: "center",
							fontWeight: 600,
							pb: 2,
							borderBottom: "3px solid #3498db",
						}}
					>
						Form for: {shortenAndFormatLabel(pageName)}
					</Typography>
					<Form rawApiData={rawApiData} loading={loading} error={error} refetch={refetch} />
				</Container>
			</Box>
		</>
	)


};

// @ts-ignore
export default FormPage;