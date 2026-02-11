import { Typography } from "@mui/material";
import Sketch from "@uiw/react-color-sketch";
import type React from "react";

export type ColorPickerFieldProps = {
	value: any;
	onFocus?: (event: React.FocusEvent<HTMLInputElement>) => void;
	onChange: (value: any) => void;
	error: boolean;
	helperText: string;
};

const ColorPickerField = ({
	value,
	onFocus,
	onChange,
	error,
	helperText,
	...rest
}: ColorPickerFieldProps) => {
	return (
		<>
			<Sketch
				style={{ marginLeft: 20 }}
				color={value || ""}
				onFocus={onFocus}
				onChange={(color) => {
					onChange(color.hex);
				}}
				{...rest}
			/>
			{helperText && (
				<Typography
					color={error ? "error" : "textSecondary"}
					variant="caption"
					sx={{ mt: 1 }}
				>
					{helperText}
				</Typography>
			)}
		</>
	);
};

export default ColorPickerField;
