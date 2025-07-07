import type React from "react";
import Sketch from '@uiw/react-color-sketch';
import {Typography} from "@mui/material";

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
					onChange(color.hexa);
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
