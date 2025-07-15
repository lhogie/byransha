import { useApiData } from "@hooks/useApiData";
import type React from "react";
import { useEffect, useRef } from "react";
import {
	Autocomplete,
	CircularProgress,
	FormControl,
	FormHelperText,
	TextField,
} from "@mui/material";

export type DropdownFieldProps = {
	field: any;
	fieldKey: string;
	value: any;
	defaultValue?: string;
	onFocus?: (event: React.FocusEvent<HTMLInputElement>) => void;
	onChange: (value: any) => void;
	onFirstChange: (value: any) => void;
	error: boolean;
	helperText: string;
	multiple?: boolean;
	disabled?: boolean;
	required?: boolean;
	options?: any[];
	onLoadMore?: () => void;
	hasMore?: boolean;
};

const DropdownField = ({
	field,
	fieldKey,
	value,
	defaultValue,
	onFocus,
	onChange,
	onFirstChange,
	error,
	helperText,
	multiple = false,
	options,
	onLoadMore,
	hasMore,
	...rest
}: DropdownFieldProps) => {
	const {
		data: listData,
		isLoading,
		isError,
		error: apiError,
	} = useApiData("list_existing_node", {
		type: field.listNodeType.split(".").pop(),
		enabled: !options,
	});

	const observer = useRef<IntersectionObserver | null>(null);
	const lastOptionRef = (node: HTMLLIElement) => {
		if (isLoading) return;
		if (observer.current) observer.current.disconnect();
		observer.current = new IntersectionObserver((entries) => {
			if (entries[0].isIntersecting && hasMore && onLoadMore) {
				onLoadMore();
			}
		});
		if (node) observer.current.observe(node);
	};

	useEffect(() => {
		if (
			!isLoading &&
			!isError &&
			listData?.data?.results?.[0]?.result?.data?.length !== 0 &&
			!multiple
		) {
			if (defaultValue) {
				const id = Number.parseInt(defaultValue.split("@")[1]);
				const existingOption = listData?.data?.results?.[0]?.result?.data.find(
					(option: any) => option.id === id,
				);

				if (existingOption) {
					onFirstChange({
						label: existingOption.name,
						value: existingOption.id,
					});
				} else {
					onFirstChange(undefined);
				}
			} else {
				onFirstChange(undefined);
			}
		}
	}, [
		isLoading,
		isError,
		defaultValue,
		listData?.data?.results?.[0]?.result?.data.find,
		listData?.data?.results?.[0]?.result?.data?.length,
		multiple,
		onFirstChange,
	]);

	const data = options || listData?.data?.results?.[0]?.result?.data || [];

	return (
		<FormControl fullWidth error={error}>
			<Autocomplete
				disablePortal
				renderInput={(params) => <TextField {...params} error={error} />}
				options={data
					.map((option: any) => {
						const firstLetter = option.name[0].toUpperCase();

						return {
							label: option.name,
							value: option.id,
							firstLetter:
								option.name === "France(FR)"
									? " "
									: /[0-9]/.test(firstLetter)
										? "0-9"
										: firstLetter,
						};
					})
					.sort(
						(
							a: {
								label: string;
								value: string;
								firstLetter: string;
							},
							b: {
								label: string;
								value: string;
								firstLetter: string;
							},
						) => {
							// Sort by firstLetter first to ensure correct grouping
							// Space " " comes before "0-9" and letters, so France(FR) will be at the top
							if (a.firstLetter !== b.firstLetter) {
								return a.firstLetter.localeCompare(b.firstLetter);
							}
							// Then sort by label for items within the same group
							return a.label.localeCompare(b.label);
						},
					)}
				groupBy={(option) => option.firstLetter}
				getOptionKey={(option) => option.value ?? ""}
				getOptionLabel={(option) => option.label ?? ""}
				isOptionEqualToValue={(option, value) => {
					return option.value === value.value;
				}}
				size="small"
				id={fieldKey}
				value={value}
				onChange={(_event, newValue) => {
					onChange(newValue);
				}}
				multiple={multiple}
				loading={isLoading}
				ListboxProps={{
					onScroll: (event) => {
						const listboxNode = event.currentTarget;
						if (
							listboxNode.scrollTop + listboxNode.clientHeight ===
							listboxNode.scrollHeight
						) {
							if (hasMore && onLoadMore) {
								onLoadMore();
							}
						}
					},
				}}
				renderOption={(props, option, { index }) => {
					if (index === data.length - 1 && hasMore) {
						return (
							<li {...props} ref={lastOptionRef} key={option.value}>
								{option.label}
							</li>
						);
					}
					return (
						<li {...props} key={option.value}>
							{option.label}
						</li>
					);
				}}
				{...rest}
			/>
			{helperText && <FormHelperText>{helperText}</FormHelperText>}
		</FormControl>
	);
};

export default DropdownField;