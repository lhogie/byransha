import type React from "react";
import { TextField, type TextFieldProps } from "@mui/material";

export type TextFormFieldProps = {
	field: any;
	fieldKey: string;
	value: any;
	onFocus?: (event: React.FocusEvent<HTMLInputElement>) => void;
	onChange: (value: any) => void;
	error: boolean;
	helperText: string;
} & TextFieldProps;

const TextFormField = ({
	field,
	fieldKey,
	value,
	onFocus,
	onChange,
	error,
	helperText,
	...rest
}: TextFormFieldProps) => {
	const getInputType = (type: string) => {
		switch (type) {
			case "EmailNode":
				return "email";
			case "PhoneNumberNode":
				return "tel";
			case "IntNode":
				return "number";
			default:
				return "text";
		}
	};

	return (
		<TextField
			fullWidth
			id={fieldKey}
			variant="outlined"
			type={getInputType(field.type)}
			value={value || ""}
			onFocus={onFocus}
			onChange={(e) => onChange(e.target.value)}
			error={error}
			helperText={helperText}
			{...rest}
		/>
	);
};

export default TextFormField;
