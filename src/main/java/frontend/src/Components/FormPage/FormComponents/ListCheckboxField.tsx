import { useEffect, useMemo, useState } from "react";
import {
	Checkbox,
	CircularProgress,
	FormControl,
	FormControlLabel,
	FormGroup,
	FormHelperText,
} from "@mui/material";
import { useInfiniteApiData, useApiMutation } from "@hooks/useApiData";
import { useDebouncedCallback } from "use-debounce";
import toast from "react-hot-toast";
import { shortenAndFormatLabel } from "@/utils/utils";
import { useQueryClient } from "@tanstack/react-query";
import { useInView } from "react-intersection-observer";

const ListCheckboxComponent = ({
	option,
	fieldId,
}: {
	option: any;
	fieldId: string;
}) => {
	const [value, setValue] = useState(option.value);
	const setValueMutation = useApiMutation("set_value");
	const queryClient = useQueryClient();

	const handleSaveChanges = useDebouncedCallback(
		async (field) => {
			if (!field) return console.warn("No field provided for saving changes");
			try {
				await toast.promise(
					setValueMutation.mutateAsync(
						{
							id: field.id,
							value: value,
						},
						{
							onSuccess: async () => {
								await queryClient.invalidateQueries({
									queryKey: [
										"apiData",
										"class_attribute_field",
										{
											node_id: Number.parseInt(fieldId),
										},
									],
								});
							},
						},
					),
					{
						loading: `Enregistrement de ${shortenAndFormatLabel(field.name)}...`,
						success: `Changements enregistrés pour ${shortenAndFormatLabel(field.name)}`,
						error: `Erreur lors de l'enregistrement des changements pour ${shortenAndFormatLabel(field.name)}`,
					},
				);
			} catch (error) {
				console.error("Error saving changes:", error);
			}
		},
		500,
		{ maxWait: 2000 },
	);

	return (
		<FormControlLabel
			label={option.name}
			control={
				<Checkbox
					checked={value || false}
					onChange={(e) => {
						setValue(e.target.checked);
						handleSaveChanges(option);
					}}
				/>
			}
		/>
	);
};

export type ListCheckboxFieldProps = {
	field: any;
	error: boolean;
	helperText: string;
};

const ListCheckboxField = ({
	field,
	error,
	helperText,
}: ListCheckboxFieldProps) => {
	const { ref, inView } = useInView();
	const { data, fetchNextPage, hasNextPage, isFetchingNextPage } =
		useInfiniteApiData("class_attribute_field", {
			node_id: field.id,
		});

	useEffect(() => {
		if (inView && hasNextPage) {
			fetchNextPage();
		}
	}, [inView, hasNextPage, fetchNextPage]);

	const options = useMemo(
		() =>
			data?.pages
				.flatMap((page) => page.data.results[0].result.data.data)
				.filter(Boolean) || [],
		[data],
	);

	return (
		<FormControl fullWidth error={error}>
			<FormGroup row>
				{options.map((option: any, index: any) => (
					<ListCheckboxComponent
						key={index}
						option={option}
						fieldId={field.id}
					/>
				))}
			</FormGroup>
			{hasNextPage && (
				<div ref={ref}>
					{isFetchingNextPage ? <CircularProgress /> : "Load more"}
				</div>
			)}
			{helperText && <FormHelperText>{helperText}</FormHelperText>}
		</FormControl>
	);
};

export default ListCheckboxField;
