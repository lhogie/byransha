import { FormControl, FormHelperText } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs, { type Dayjs } from "dayjs";

const DateFormField = ({
	fieldKey,
	value,
	onChange,
	error,
	helperText,
	field,
	...rest
}: {
	fieldKey: string;
	value: any;
	onChange: (value: any) => void;
	error: boolean;
	helperText: string;
	field?: any;
}) => {
	// Extract min and max date values from validations if they exist
	let minDate: Dayjs | undefined;
	let maxDate: Dayjs | undefined;

	if (field?.validations) {
		if (field.validations.min !== undefined) {
			minDate = dayjs(field.validations.min);
		}
		if (field.validations.max !== undefined) {
			maxDate = dayjs(field.validations.max);
		}
	}

	return (
		<FormControl fullWidth error={error}>
			<DatePicker
				value={value ? dayjs(value) : null}
				onChange={(newValue) => onChange(newValue)}
				sx={{ width: "100%" }}
				minDate={minDate}
				maxDate={maxDate}
				{...rest}
				slotProps={{
					textField: {
						size: "small",
						error: error,
					},
				}}
			/>
			{helperText && <FormHelperText>{helperText}</FormHelperText>}
		</FormControl>
	);
};

export default DateFormField;
