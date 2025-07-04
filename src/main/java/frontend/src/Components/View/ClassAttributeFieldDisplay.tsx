import {
	Box,
	Button,
	Card,
	CardActions,
	CardContent,
	CardMedia,
	Typography,
} from "@mui/material";

interface ClassAttributeFieldDisplayProps {
	content: any;
}

export const ClassAttributeFieldDisplay = ({
	content,
}: ClassAttributeFieldDisplayProps) => {
	if (!Array.isArray(content?.attributes)) {
		return (
			<Typography sx={{ p: 2 }} color="error">
				Error: Expected an array for 'class_attribute_field' data, but received
				type {typeof content?.attributes}.
			</Typography>
		);
	}
	if (content?.attributes.length === 0) {
		return <Typography sx={{ p: 2 }}>No output nodes connected.</Typography>;
	}

	return (
		<Box sx={{ p: 1, display: "flex", flexWrap: "wrap", gap: 2 }}>
			{content?.attributes?.map((outNode: any) => {
				const isImage =
					outNode.mimeType?.startsWith("image/") && outNode.value;
				const hasValue =
					Object.hasOwn(outNode, "value") &&
					outNode.value !== null &&
					outNode.value !== undefined;
				if (outNode.name === "graph") return null;

				return (
					<Card
						key={outNode.id}
						sx={{
							minWidth: 275,
							maxWidth: 350,
							display: "flex",
							flexDirection: "column",
						}}
					>
						<CardContent sx={{ flexGrow: 1 }}>
							<Typography
								variant="h6"
								component="div"
								sx={{ wordBreak: "break-word" }}
							>
								{outNode.name}
							</Typography>
							{isImage && (
								<CardMedia
									component="img"
									sx={{
										maxHeight: 200,
										width: "auto",
										objectFit: "contain",
										mt: 1,
										border: "1px solid #eee",
									}}
									image={`data:${outNode.mimeType};base64,${outNode.value}`}
									alt={`Output value for ${outNode.name}`}
								/>
							)}
							{!isImage && hasValue && (
								<Typography
									variant="body2"
									component="pre"
									sx={{
										whiteSpace: "pre-wrap",
										wordBreak: "break-all",
										mt: 1,
										p: 1,
										backgroundColor: "#f5f5f5",
										borderRadius: 1,
									}}
								>
									{typeof outNode.value === "object"
										? JSON.stringify(outNode.value, null, 2)
										: String(outNode.value)}
								</Typography>
							)}
							{!hasValue && !isImage && (
								<Typography
									variant="body2"
									sx={{ fontStyle: "italic", mt: 1 }}
								>
									(No displayable value)
								</Typography>
							)}
						</CardContent>

						{outNode.editable === "true" && (
							<CardActions>
								<Button
									size="small"
									onClick={(e) => {
										e.stopPropagation();
										alert(
											`Edit action for: ${outNode.name} (ID: ${outNode.id}) - Not implemented yet.`,
										);
									}}
								>
									Edit
								</Button>
							</CardActions>
						)}
					</Card>
				);
			})}
		</Box>
	);
};
