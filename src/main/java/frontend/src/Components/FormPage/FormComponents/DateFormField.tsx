import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import dayjs from "dayjs";
import { FormHelperText, FormControl } from "@mui/material";

const DateFormField = ({
	fieldKey,
	value,
	onChange,
	error,
	helperText,
	...rest
}: {
	fieldKey: string;
	value: any;
	onChange: (value: any) => void;
	error: boolean;
	helperText: string;
}) => {
	return (
		<FormControl fullWidth error={error}>
			<DatePicker
				value={value ? dayjs(value) : null}
				onChange={(newValue) => onChange(newValue)}
				sx={{ width: "100%" }}
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
