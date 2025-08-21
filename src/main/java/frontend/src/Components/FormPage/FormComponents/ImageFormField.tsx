import { Box, Button, TextField, type TextFieldProps } from "@mui/material";
import type { ChangeEvent, MouseEventHandler } from "react";

export type ImageFormFieldProps = {
	field: any;
	fieldKey: string;
	value: any;
	onFocus?: MouseEventHandler<HTMLInputElement>;
	onChange: (value: any) => void;
} & TextFieldProps;

const ImageFormField = ({
	field,
	fieldKey,
	value,
	onFocus,
	onChange,
	...rest
}: ImageFormFieldProps) => {
	return (
		<Box
			className="image-preview-wrapper"
			sx={{
				display: "flex",
				flexDirection: "column",
				alignItems: "center",
				mt: 2,
			}}
		>
			{value && (
				<Box
					component="img"
					className="image-preview"
					src={`data:${field.mimeType};base64,${value}`}
					alt={field.name}
					sx={{
						maxHeight: "150px",
						width: "auto",
						objectFit: "contain",
						mt: 1,
						border: "1px solid #eee",
					}}
				/>
			)}

			<Button variant="contained" component="label" sx={{ mt: 2 }}>
				Upload New Image
				<TextField
					type="file"
					// @ts-expect-error
					accept="image/*"
					sx={{ display: "none" }}
					onClick={onFocus}
					onChange={async (e: ChangeEvent<HTMLInputElement>) => {
						const file = e.target.files?.[0];
						if (!file) return;
						const reader = new FileReader();
						reader.onloadend = async () => {
							if (typeof reader?.result === "string") {
								const base64String = reader?.result?.split(",")[1];
								onChange(base64String);
							}
						};
						reader.readAsDataURL(file);
					}}
					{...rest}
				></TextField>
			</Button>
		</Box>
	);
};

export default ImageFormField;
