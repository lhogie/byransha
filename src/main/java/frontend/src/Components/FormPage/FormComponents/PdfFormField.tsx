import { useState, useEffect } from "react";
import { Document, Page, pdfjs } from "react-pdf";
import "react-pdf/dist/esm/Page/AnnotationLayer.css";
import "react-pdf/dist/esm/Page/TextLayer.css";
import {
	Box,
	Button,
	Typography,
	CircularProgress,
	Paper,
} from "@mui/material";
import PictureAsPdfIcon from "@mui/icons-material/PictureAsPdf";

pdfjs.GlobalWorkerOptions.workerSrc = new URL(
	"pdfjs-dist/build/pdf.worker.min.mjs",
	import.meta.url,
).toString();

const PdfFormField = ({
	value,
	onChange,
	error,
	helperText,
}: {
	value: string;
	onChange: (value: string) => void;
	error: boolean;
	helperText: string;
}) => {
	const [fileUrl, setFileUrl] = useState(value);

	useEffect(() => {
		if (value) {
			const byteCharacters = atob(value);
			const byteNumbers = new Array(byteCharacters.length);
			for (let i = 0; i < byteCharacters.length; i++) {
				byteNumbers[i] = byteCharacters.charCodeAt(i);
			}
			const byteArray = new Uint8Array(byteNumbers);
			const blob = new Blob([byteArray], { type: "application/pdf" });
			const blobUrl = URL.createObjectURL(blob);

			setFileUrl(blobUrl);
		}
	}, [value]);

	const handleFileChange = (event: any) => {
		const selectedFile = event.target.files[0];
		if (selectedFile && selectedFile.type === "application/pdf") {
			const reader = new FileReader();

			reader.onloadend = () => {
				if (typeof reader.result === "string") {
					const base64String = reader.result?.split(",")[1];
					onChange(base64String);
				}
			};

			reader.readAsDataURL(selectedFile);
		} else if (selectedFile) {
			alert("Please select a PDF file.");
			event.target.value = null;
		}
	};

	const openPdfInNewTab = (e: any) => {
		e.stopPropagation();
		if (fileUrl) {
			window.open(fileUrl, "_blank", "noopener,noreferrer");
		}
	};

	return (
		<Box
			sx={{
				display: "flex",
				flexDirection: "column",
				alignItems: "flex-start",
				width: "100%",
			}}
		>
			<Paper
				elevation={2}
				onClick={openPdfInNewTab}
				sx={{
					cursor: "pointer",
					border: `2px solid ${error ? "error.main" : "transparent"}`,
					borderRadius: "8px",
					width: "180px",
					height: "254px",
					overflow: "hidden",
					position: "relative",
					display: "flex",
					justifyContent: "center",
					alignItems: "center",
					backgroundColor: "#f8f8f8",
					mb: 2,
					transition: "all 0.2s ease-in-out",
					"&:hover": {
						borderColor: "primary.main",
						boxShadow: 4,
						transform: "scale(1.02)",
					},
				}}
			>
				{fileUrl ? (
					<Document
						file={fileUrl}
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
									Loading PDF...
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
								<PictureAsPdfIcon color="error" sx={{ fontSize: 40, mb: 1 }} />
								<Typography variant="caption" color="error">
									Preview not available
								</Typography>
							</Box>
						}
					>
						<Page
							pageNumber={1}
							width={180}
							renderTextLayer={false}
							renderAnnotationLayer={false}
						/>
					</Document>
				) : (
					<Box
						sx={{
							display: "flex",
							flexDirection: "column",
							alignItems: "center",
							p: 2,
						}}
					>
						<PictureAsPdfIcon color="disabled" sx={{ fontSize: 60, mb: 1 }} />
						<Typography variant="caption" color="text.secondary" align="center">
							No PDF selected
						</Typography>
					</Box>
				)}
			</Paper>

			<Button
				variant="contained"
				component="label"
				color="primary"
				startIcon={<PictureAsPdfIcon />}
				sx={{ mt: 1 }}
			>
				Upload PDF
				<input
					type="file"
					hidden
					accept="application/pdf"
					onChange={handleFileChange}
				/>
			</Button>

			{helperText && (
				<Typography
					color={error ? "error" : "textSecondary"}
					variant="caption"
					sx={{ mt: 1 }}
				>
					{helperText}
				</Typography>
			)}
		</Box>
	);
};

export default PdfFormField;
