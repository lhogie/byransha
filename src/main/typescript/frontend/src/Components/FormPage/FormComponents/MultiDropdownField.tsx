import { useApiData } from "@hooks/useApiData";
import type React from "react";
import { useEffect } from "react";
import DropdownField from "./DropdownField";

export type MultiDropdownFieldProps = {
	field: any;
	fieldKey: string;
	value: any;
	onFocus?: (event: React.FocusEvent<HTMLInputElement>) => void;
	onChange: (value: any) => void;
	onFirstChange: (value: any) => void;
	error: boolean;
	helperText: string;
};

const MultiDropdownField = ({
	field,
	fieldKey,
	value,
	onFocus,
	onChange,
	onFirstChange,
	error,
	helperText,
	...rest
}: MultiDropdownFieldProps) => {
	const {
		data: rawApiData,
		isLoading,
		isError,
	} = useApiData(`class_attribute_field`, {
		node_id: field.id,
	});

	useEffect(() => {
		if (
			!isLoading &&
			!isError &&
			rawApiData?.data?.results?.[0]?.result?.data?.attributes?.length !== 0
		) {
			onFirstChange(
				rawApiData?.data?.results?.[0]?.result?.data?.attributes?.map(
					(data: any) => ({
						firstLetter: data.name.split(". ")[1][0],
						label: data.name.split(". ")[1],
						value: data.id,
					}),
				),
			);
		}
	}, [
		isLoading,
		isError,
		onFirstChange,
		rawApiData?.data?.results?.[0]?.result?.data?.attributes?.length,
		rawApiData?.data?.results?.[0]?.result?.data?.attributes?.map,
	]);

	return (
		<DropdownField
			field={field}
			fieldKey={fieldKey}
			value={value}
			onChange={onChange}
			onFirstChange={onFirstChange}
			error={error}
			helperText={helperText}
			multiple
			{...rest}
		/>
	);
};

export default MultiDropdownField;
