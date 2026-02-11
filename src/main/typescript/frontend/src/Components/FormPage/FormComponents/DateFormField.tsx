import { FormControl, FormHelperText } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs, { type Dayjs } from "dayjs";
import { useEffect, useState } from "react";

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
	const [internalValue, setInternalValue] = useState<Dayjs | null>(
		value ? dayjs(value) : null,
	);
	const [internalError, setInternalError] = useState<boolean>(false);

	useEffect(() => {
		setInternalValue(value ? dayjs(value) : null);
	}, [value]);

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

	const handleDateChange = (newValue: Dayjs | null, _context: any) => {
		setInternalValue(newValue);

		const hasError = newValue !== null && !newValue.isValid();
		setInternalError(hasError);

		if (newValue === null || newValue?.isValid()) {
			onChange(newValue?.toISOString());
		}
	};

	const displayError = error || internalError;
	const displayHelperText =
		internalError && !helperText ? "Date invalide" : helperText;

	return (
		<FormControl fullWidth error={displayError}>
			<DatePicker
				value={internalValue}
				onChange={handleDateChange}
				sx={{ width: "100%" }}
				minDate={minDate}
				maxDate={maxDate}
				{...rest}
				slotProps={{
					textField: {
						size: "small",
						error: displayError,
					},
				}}
			/>
			{displayHelperText && (
				<FormHelperText>{displayHelperText}</FormHelperText>
			)}
		</FormControl>
	);
};

export default DateFormField;
