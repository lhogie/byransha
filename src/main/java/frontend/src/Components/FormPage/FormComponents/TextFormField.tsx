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

	// Extract min and max values from validations if they exist
	const inputProps: any = {};

	if (field.validations) {
		// For number inputs, apply min and max directly
		if (field.type === "IntNode" && field.validations.min !== undefined) {
			inputProps.min = field.validations.min;
		}
		if (field.type === "IntNode" && field.validations.max !== undefined) {
			inputProps.max = field.validations.max;
		}

		// For text inputs with size constraints
		if (field.validations.size) {
			if (field.validations.size.min !== undefined) {
				inputProps.minLength = field.validations.size.min;
			}
			if (field.validations.size.max !== undefined) {
				inputProps.maxLength = field.validations.size.max;
			}
		}
	}

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
			inputProps={inputProps}
			{...rest}
		/>
	);
};

export default TextFormField;
