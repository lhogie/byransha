import type React from "react";
import { useEffect, useMemo } from "react";
import { useInfiniteApiData } from "@hooks/useApiData";
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
		fetchNextPage,
		hasNextPage,
	} = useInfiniteApiData(`class_attribute_field`, {
		node_id: field.id,
	});

	const options = useMemo(
		() =>
			rawApiData?.pages
				.flatMap((page) => page.data.results[0].result.data.data)
				.map((data: any) => ({
					firstLetter: data.name.split(". ")[1][0],
					label: data.name.split(". ")[1],
					value: data.id,
				})) || [],
		[rawApiData],
	);

	useEffect(() => {
		if (!isLoading && !isError && options.length > 0) {
			onFirstChange(options);
		}
	}, [isLoading, isError, onFirstChange, options]);

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
			options={options}
			onLoadMore={fetchNextPage}
			hasMore={hasNextPage}
			{...rest}
		/>
	);
};

export default MultiDropdownField;
