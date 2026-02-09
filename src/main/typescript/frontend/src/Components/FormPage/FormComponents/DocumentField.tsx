import { useApiData, useApiMutation } from "@hooks/useApiData";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import DownloadIcon from "@mui/icons-material/Download";
import InsertDriveFileIcon from "@mui/icons-material/InsertDriveFile";
import PictureAsPdfIcon from "@mui/icons-material/PictureAsPdf";
import {
	Box,
	Button,
	CircularProgress,
	Divider,
	Stack,
	Typography,
} from "@mui/material";
import { Document, Page, pdfjs } from "react-pdf";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
import { saveAs } from "file-saver";

pdfjs.GlobalWorkerOptions.workerSrc = new URL(
	"pdfjs-dist/build/pdf.worker.min.mjs",
	import.meta.url,
).toString();

const DocumentField = ({
	field,
	parentId,
}: {
	field: any;
	parentId: string;
}) => {
	const { data: rawApiData, refetch } = useApiData(`class_attribute_field`, {
		node_id: field.id,
	});

	const setValueMutation = useApiMutation("set_value");

	const parsedApiData =
		rawApiData?.data?.results?.[0]?.result?.data?.attributes;
	const data = parsedApiData
		? parsedApiData?.find((attr: any) => attr?.type === "ByteNode")
		: null;
	const title = parsedApiData
		? parsedApiData?.find((attr: any) => attr?.type === "StringNode")
		: null;
	const mimeType = parsedApiData
		? parsedApiData?.find((attr: any) => attr?.type === "MimeTypeNode")
		: null;

	const handleFileChange = (event: any) => {
		const selectedFile = event.target.files[0];
		if (selectedFile) {
			const reader = new FileReader();

			reader.onloadend = async () => {
				if (typeof reader.result === "string") {
					const base64String = reader.result?.split(",")[1];
					console.log(base64String);

					await Promise.all([
						setValueMutation.mutateAsync({
							id: data.id,
							value: base64String,
							parentId,
						}),
						setValueMutation.mutateAsync({
							id: title.id,
							value: selectedFile.name,
							parentId,
						}),
						setValueMutation.mutateAsync({
							id: mimeType.id,
							value: selectedFile.type,
							parentId,
						}),
					]);

					await refetch();
				}
			};

			reader.readAsDataURL(selectedFile);
		} else {
			alert("Veuillez sélectionner un fichier.");
			event.target.value = null;
		}
	};

	return (
		<Box sx={{ mb: 2 }}>
			<Stack spacing={2}>
				{data &&
				title &&
				mimeType &&
				data.value &&
				title.value &&
				mimeType.value ? (
					<Box>
						<Typography
							variant="subtitle2"
							color="text.secondary"
							sx={{ mb: 1 }}
						>
							Aperçu du fichier
						</Typography>
						<Box
							sx={{
								display: "flex",
								justifyContent: "center",
								alignItems: "center",
								p: 2,
								border: 1,
								borderColor: "grey.300",
								borderRadius: 1,
								bgcolor: "grey.50",
								minHeight: 200,
							}}
						>
							{mimeType.value === "application/pdf" ? (
								<Document
									file={`data:${mimeType.value};base64,${data.value}`}
									loading={
										<Box
											sx={{
												display: "flex",
												flexDirection: "column",
												alignItems: "center",
											}}
										>
											<CircularProgress size={40} sx={{ mb: 1 }} />
											<Typography variant="caption" color="text.secondary">
												Chargement du PDF...
											</Typography>
										</Box>
									}
									error={
										<Box
											sx={{
												display: "flex",
												flexDirection: "column",
												alignItems: "center",
												p: 2,
											}}
										>
											<PictureAsPdfIcon
												color="error"
												sx={{ fontSize: 48, mb: 1 }}
											/>
											<Typography variant="caption" color="error">
												Aperçu non disponible
											</Typography>
										</Box>
									}
								>
									<Page
										pageNumber={1}
										width={200}
										renderTextLayer={false}
										renderAnnotationLayer={false}
									/>
								</Document>
							) : mimeType.value.startsWith("image/") ? (
								<Box
									component="img"
									src={`data:${mimeType.value};base64,${data.value}`}
									alt={title.value}
									sx={{
										maxHeight: 180,
										maxWidth: "100%",
										objectFit: "contain",
										borderRadius: 1,
									}}
								/>
							) : (
								<Box
									sx={{
										display: "flex",
										flexDirection: "column",
										alignItems: "center",
										p: 3,
									}}
								>
									<InsertDriveFileIcon
										sx={{ fontSize: 48, color: "text.secondary", mb: 1 }}
									/>
									<Typography
										variant="body2"
										color="text.secondary"
										align="center"
									>
										"{title.value}"
									</Typography>
									<Typography
										variant="caption"
										color="text.secondary"
										align="center"
									>
										Le fichier ne peut pas être prévisualisé
									</Typography>
								</Box>
							)}
						</Box>
						<Typography
							variant="body2"
							color="text.primary"
							sx={{ mt: 1, fontWeight: "medium" }}
						>
							{title.value}
						</Typography>
					</Box>
				) : (
					<Box
						sx={{
							display: "flex",
							flexDirection: "column",
							alignItems: "center",
							p: 4,
							border: 2,
							borderColor: "grey.300",
							borderStyle: "dashed",
							borderRadius: 2,
							bgcolor: "grey.50",
						}}
					>
						<CloudUploadIcon
							sx={{ fontSize: 48, color: "text.secondary", mb: 2 }}
						/>
						<Typography variant="body1" color="text.secondary" align="center">
							Aucun fichier téléchargé
						</Typography>
						<Typography variant="caption" color="text.secondary" align="center">
							Téléchargez un fichier pour commencer
						</Typography>
					</Box>
				)}

				<Divider />

				<Stack direction="row" spacing={2} justifyContent="flex-start">
					{data && title && mimeType && data.value && title.value && (
						<Button
							variant="outlined"
							startIcon={<DownloadIcon />}
							onClick={() => {
								saveAs(
									`data:${mimeType.value};base64,${data.value}`,
									title.value,
								);
							}}
							disabled={
								!(data && mimeType && data.value && title && title.value)
							}
							sx={{ minWidth: 140 }}
						>
							Télécharger
						</Button>
					)}
					<Button
						variant="contained"
						component="label"
						startIcon={<CloudUploadIcon />}
						sx={{ minWidth: 120 }}
					>
						Télécharger un fichier
						<input type="file" hidden onChange={handleFileChange} />
					</Button>
				</Stack>
			</Stack>
		</Box>
	);
};

export default DocumentField;
