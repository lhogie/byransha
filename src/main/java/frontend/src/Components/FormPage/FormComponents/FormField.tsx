import ColorPickerField from "@components/FormPage/FormComponents/ColorPickerField";
import { useApiMutation } from "@hooks/useApiData";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
	Box,
	Button,
	FormControl,
	Grid,
	IconButton,
	Typography,
} from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import React, { useCallback, useEffect, useState } from "react";
import toast from "react-hot-toast";
import { useDebouncedCallback } from "use-debounce";
import {
	checkboxField,
	colorField,
	dateField,
	fileField,
	getErrorMessage,
	imageField,
	inputTextField,
	listField,
	shortenAndFormatLabel,
	typeComponent,
	validateFieldValue,
} from "@/utils/utils";
import CheckboxFormField from "./CheckboxFormField.js";
import DateFormField from "./DateFormField.js";
import DropdownField from "./DropdownField";
import ImageFormField from "./ImageFormField";
import ListCheckboxField from "./ListCheckboxField.js";
import MultiDropdownField from "./MultiDropdownField.js";
import PdfFormField from "./PdfFormField.js";
import RadioField from "./RadioField.js";
import TextFormField from "./TextFormField";

const FormField = ({
	field,
	fieldKey,
	isExpanded,
	onToggleField,
	onChangingForm,
	parentId,
	defaultValue = "",
}: {
	field: any;
	fieldKey: string;
	isExpanded: boolean;
	onToggleField: (fieldKey: string, id: string) => void;
	onChangingForm: (name: string, id: string) => void;
	parentId: string;
	defaultValue?: any;
}) => {
	const { id, name, type, validations, isValid, choices, source } = field;
	const [value, setValue] = useState(
		listField.includes(type)
			? field.listType === "RADIO"
				? field.value
				: []
			: defaultValue,
	);
	const [error, setError] = useState(false);
	const [errorMessage, setErrorMessage] = useState("");
	const queryClient = useQueryClient();

	const setValueMutation = useApiMutation("set_value");
	const addExistingNodeMutation = useApiMutation("add_existing_node");
	const removeFromListMutation = useApiMutation("remove_from_list");

	const validateAndSetError = useDebouncedCallback(
		(currentValue: any) => {
			const validationResult = validateFieldValue(
				type,
				currentValue,
				validations,
			);
			setError(!validationResult.isValid);
			setErrorMessage(validationResult.message);
			return validationResult.isValid;
		},
		300,
		{ maxWait: 1000 },
	);

	useEffect(() => {
		validateAndSetError(value);
	}, [value, validateAndSetError]);

	const handleSaveChanges = useDebouncedCallback(
		async (field) => {
			if (!field) return console.warn("No field provided for saving changes");
			const isValid = validateAndSetError(value);

			if (!isValid) {
				console.warn(
					`Invalid value for ${field.name} (${field.type}): ${value}`,
				);
				return;
			}

			try {
				await toast.promise(
					setValueMutation.mutateAsync(
						{
							id: field.id,
							value: value,
							parentId,
						},
						{
							onSuccess: async () => {
								await queryClient.invalidateQueries({
									queryKey: [
										"apiData",
										"class_attribute_field",
										{
											node_id: Number.parseInt(parentId),
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
			} catch (error: any) {
				console.error("Error saving changes:", error);
				setError(true);
				setErrorMessage(`Error saving changes: ${error.message}`);
			}
		},
		500,
		{ maxWait: 2000 },
	);

	const handleFileChange = useCallback(
		async (fileData: string) => {
			if (!fileData) {
				setValue(null);
				return;
			}

			setValue(fileData);

			try {
				await toast.promise(
					setValueMutation.mutateAsync(
						{
							id: field.id,
							value: fileData,
						},
						{
							onSuccess: async () => {
								await queryClient.invalidateQueries({
									queryKey: [
										"apiData",
										"class_attribute_field",
										{
											node_id: Number.parseInt(parentId),
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
			} catch (error: any) {
				console.error("Error saving changes:", error);
				setError(true);
				setErrorMessage(`Error saving changes: ${error.message}`);
			}
		},
		[
			field.id,
			field.name,
			parentId,
			queryClient.invalidateQueries,
			setValueMutation.mutateAsync,
		],
	);

	const handleSaveDropdownChanges = useDebouncedCallback(
		async (field, value, added: boolean = true) => {
			if (!field) return console.warn("No field provided for saving changes");
			const isValid = validateAndSetError(value);

			if (!isValid) {
				console.warn(
					`Invalid value for ${field.name} (${field.type}): ${value}`,
				);
				return;
			}

			try {
				await toast.promise(
					(added
						? addExistingNodeMutation
						: removeFromListMutation
					).mutateAsync(
						{
							node_id: field.id,
							id: value,
						},
						{
							onSuccess: async () => {
								await queryClient.invalidateQueries({
									queryKey: [
										"apiData",
										"class_attribute_field",
										{
											node_id: Number.parseInt(parentId),
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
			} catch (error: any) {
				console.error("Error saving changes:", error);
				setError(true);
				setErrorMessage(`Error saving changes: ${error.message}`);
			}
		},
	);

	const handleValueChange = useCallback(
		(value: any, _f = undefined) => {
			const isValid = validateFieldValue(field.type, value, validations);
			setError(!isValid);
			if (!isValid) {
				setErrorMessage(getErrorMessage(field.type, value, validations));
			} else {
				setErrorMessage("");
			}

			handleSaveChanges(field);
			setValue(value);
		},
		[field, handleSaveChanges, validations],
	);

	const handleDropdownValueChange = useCallback(
		(value: any) => {
			// Validate immediately for user feedback
			const isValid = validateFieldValue(field.type, value?.value, validations);
			setError(!isValid);
			if (!isValid) {
				setErrorMessage(getErrorMessage(field.type, value?.value, validations));
			} else {
				setErrorMessage("");
			}

			handleSaveDropdownChanges(field, value?.value);
			setValue(value);
		},
		[field, handleSaveDropdownChanges, validations],
	);

	const handleFirstValueChange = useCallback((value: any) => {
		setValue(value);
	}, []);

	const handleMultiDropdownValueChange = useCallback(
		(newValue: any) => {
			const isValid = validateAndSetError(newValue);

			if (!isValid) {
				return;
			}

			const oldValues = value || [];
			const newValues = newValue || [];

			const addedValues = newValues.filter(
				(newItem: any) =>
					!oldValues.some((oldItem: any) => oldItem.value === newItem.value),
			);

			if (addedValues.length > 0) {
				addedValues.forEach((addedItem: any) => {
					handleSaveDropdownChanges(field, addedItem.value);
				});
			}

			const removedValues = oldValues.filter(
				(oldItem: any) =>
					!newValues.some((newItem: any) => newItem.value === oldItem.value),
			);

			if (removedValues.length > 0) {
				removedValues.forEach((removedItem: any) => {
					handleSaveDropdownChanges(field, removedItem.value, false);
				});
			}

			setValue(newValue);
		},
		[field, handleSaveDropdownChanges, value, validateAndSetError],
	);

	const handleMultiDropdownFirstChange = useCallback(
		(newValue: any) => setValue(newValue),
		[],
	);

	const handleChangingForm = useCallback(() => {
		onChangingForm(name, id);
	}, [onChangingForm, name, id]);

	const handleToggleField = useCallback(() => {
		onToggleField(fieldKey, id);
	}, [onToggleField, fieldKey, id]);

	useEffect(() => {
		return () => {
			handleSaveChanges.flush();
		};
	}, [handleSaveChanges]);

	return (
		<Box key={fieldKey} className="form-field-wrapper" sx={{ p: 1 }}>
			<Grid container className="form-field" spacing={2} alignItems="center">
				<Grid size={{ xs: 12, sm: 4 }}>
					<FormControl
						component="fieldset"
						sx={{
							display: "flex",
							flexDirection: "row",
						}}
					>
						<Button
							tabIndex={-1}
							variant="text"
							color="primary"
							onClick={handleChangingForm}
							title={fieldKey}
							sx={{ textAlign: "left", justifyContent: "flex-start" }}
						>
							<Typography
								fontWeight="medium"
								color={isValid === false ? "error" : "primary"}
								sx={{ fontWeight: isValid === false ? "bold" : "regular" }}
							>
								{shortenAndFormatLabel(name)}
							</Typography>
						</Button>
						{!(
							typeComponent.includes(type) ||
							(listField.includes(type) && field.listType !== "LIST")
						) && (
							<Box className="toggle-wrapper" textAlign="right">
								<IconButton onClick={handleToggleField} size="small">
									{isExpanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
								</IconButton>
							</Box>
						)}
					</FormControl>
				</Grid>

				<Grid size={{ xs: 12, sm: 6 }}>
					{listField.includes(type) && field.listType === "CHECKBOX" && (
						<ListCheckboxField
							field={field}
							error={error}
							helperText={error ? errorMessage : ""}
						/>
					)}

					{inputTextField.includes(type) && (
						<TextFormField
							field={field}
							fieldKey={fieldKey}
							value={value}
							onChange={handleValueChange}
							size="small"
							error={error}
							helperText={error ? errorMessage : ""}
						/>
					)}

					{dateField.includes(type) && (
						<DateFormField
							fieldKey={fieldKey}
							field={field}
							value={value}
							onChange={handleValueChange}
							error={error}
							helperText={error ? errorMessage : ""}
						/>
					)}

					{checkboxField.includes(type) && (
						<CheckboxFormField
							fieldKey={fieldKey}
							value={value}
							onChange={handleValueChange}
						/>
					)}

					{imageField.includes(type) && isExpanded && (
						<ImageFormField
							field={field}
							fieldKey={fieldKey}
							value={value}
							onChange={handleValueChange}
							size="small"
						/>
					)}

					{listField.includes(type) && field.listType === "DROPDOWN" && (
						<DropdownField
							field={field}
							fieldKey={fieldKey}
							value={value ?? null}
							onChange={handleDropdownValueChange}
							onFirstChange={handleFirstValueChange}
							defaultValue={defaultValue}
							error={error}
							helperText={error ? errorMessage : ""}
						/>
					)}

					{listField.includes(type) && field.isDropdown && (
						<MultiDropdownField
							field={field}
							fieldKey={fieldKey}
							value={value ?? []}
							onChange={handleMultiDropdownValueChange}
							onFirstChange={handleMultiDropdownFirstChange}
							error={error}
							helperText={error ? errorMessage : ""}
						/>
					)}

					{listField.includes(type) && field.listType === "RADIO" && (
						<RadioField
							field={field}
							fieldKey={fieldKey}
							value={value}
							onChange={handleValueChange}
							onFirstChange={handleFirstValueChange}
							defaultValue={defaultValue}
							error={error}
							helperText={error ? errorMessage : ""}
						/>
					)}

					{fileField.includes(type) && (
						<PdfFormField
							value={value}
							onChange={handleFileChange}
							error={error}
							helperText={errorMessage}
						/>
					)}

					{colorField.includes(type) && (
						<ColorPickerField
							value={value}
							onChange={handleValueChange}
							error={error}
							helperText={errorMessage}
						/>
					)}
				</Grid>
			</Grid>
		</Box>
	);
};

// Wrap with React.memo to prevent unnecessary re-renders
export default React.memo(FormField, (prevProps, nextProps) => {
	// Only re-render if this specific field's data has changed
	return (
		prevProps.fieldKey === nextProps.fieldKey &&
		prevProps.isExpanded === nextProps.isExpanded &&
		prevProps.onToggleField === nextProps.onToggleField &&
		prevProps.defaultValue === nextProps.defaultValue
	);
});
