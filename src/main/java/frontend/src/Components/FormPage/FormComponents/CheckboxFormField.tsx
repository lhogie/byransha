import type { FocusEventHandler } from "react";
import { Checkbox, FormControlLabel } from "@mui/material";

const CheckboxFormField = ({
	fieldKey,
	value,
	onFocus,
	onChange,
	...rest
}: {
	fieldKey: string;
	value: boolean | undefined;
	onFocus?: FocusEventHandler<HTMLButtonElement>;
	onChange: (v: boolean) => void;
}) => {
	return (
		<FormControlLabel
			control={
				<Checkbox
					id={fieldKey}
					checked={!!value}
					onChange={(e) => onChange(e.target.checked!)}
					{...rest}
				/>
			}
			label=""
		/>
	);
};

export default CheckboxFormField;
