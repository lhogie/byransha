import type React from "react";
import {
	FormControl,
	FormHelperText,
	FormControlLabel,
	Radio,
	RadioGroup,
} from "@mui/material";

export type RadioFieldProps = {
	field: any;
	fieldKey: string;
	value: any;
	defaultValue?: string;
	onFocus?: (event: React.FocusEvent<HTMLInputElement>) => void;
	onChange: (value: any) => void;
	error: boolean;
	helperText: string;
};

const RadioField = ({
	field,
	fieldKey,
	value,
	defaultValue,
	onFocus,
	onChange,
	error,
	helperText,
	...rest
}: RadioFieldProps) => {
	return (
		<FormControl fullWidth error={error}>
			<RadioGroup
				row
				name={fieldKey}
				value={value || defaultValue}
				onChange={(e) => onChange(e.target.value)}
				onFocus={onFocus}
				{...rest}
			>
				{field.options.map((option: any, index: any) => (
					<FormControlLabel
						key={index}
						value={index}
						control={<Radio />}
						label={option}
					/>
				))}
			</RadioGroup>
			{helperText && <FormHelperText>{helperText}</FormHelperText>}
		</FormControl>
	);
};

export default RadioField;
