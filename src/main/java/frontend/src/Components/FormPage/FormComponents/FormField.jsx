import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {
    Autocomplete,
    Box,
    Button,
    CircularProgress,
    FormControl,
    FormHelperText,
    Grid,
    IconButton,
    Typography
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import TextFormField from './TextFormField';
import DateFormField from './DateFormField';
import CheckboxFormField from './CheckboxFormField';
import ImageFormField from './ImageFormField';
import {useDebouncedCallback} from "use-debounce";
import {
    checkboxField,
    createKey,
    dateField, dropdownField, getErrorMessage,
    imageField,
    inputTextField, listCheckboxField, radioField,
    shortenAndFormatLabel, typeComponent, validateFieldValue
} from "../../../utils/utils.js";
import dayjs from "dayjs";
import {useApiMutation} from "../../../hooks/useApiData.js";
import TextField from '@mui/material/TextField';
import DropdownField from "./DropdownField.jsx";
import toast from "react-hot-toast";
import RadioField from "./RadioField.jsx";
import ListCheckboxField from "./ListCheckboxField.jsx";
import {useQueryClient} from "@tanstack/react-query";

const FormField = ({
                       field,
                       fieldKey,
                       isExpanded, // Changed from expandedFields to isExpanded
                       onToggleField,
                       onChangingForm,
                       parentId,
                       defaultValue = '', // Default value for the field
                   }) => {
    const { id, name, type } = field;
    const [value, setValue] = useState(dropdownField.includes(type) ? {
        label: defaultValue,
        value: defaultValue?.split('@')[1]
    } : defaultValue);
    const [error, setError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const queryClient = useQueryClient();

    const setValueMutation = useApiMutation('set_value');
    const addExistingNodeMutation = useApiMutation('add_existing_node');


    const handleSaveChanges = useDebouncedCallback(async (field) => {
        if (!field) return console.warn("No field provided for saving changes");
        const isValid = validateFieldValue(field.type, value);

        // Update error state based on validation result
        setError(!isValid);
        if (!isValid) {
            const message = getErrorMessage(field.type, value);
            setErrorMessage(message);
            console.warn(`Invalid value for ${field.name} (${field.type}): ${value}`);
            return;
        } else {
            setErrorMessage('');
        }

        try {
            await toast.promise(setValueMutation.mutateAsync({
                id: field.id,
                value: value,
            }, {
                onSuccess: async () => {
                    await queryClient.invalidateQueries({
                        queryKey: ['apiData', 'class_attribute_field', {
                            node_id: Number.parseInt(parentId)
                        }]
                    })
                    await queryClient.invalidateQueries({
                        queryKey: ['apiData', 'class_attribute_field', {}]
                    })
                }
            }), {
                loading: `Enregistrement de ${shortenAndFormatLabel(field.name)}...`,
                success: `Changements enregistrés pour ${shortenAndFormatLabel(field.name)}`,
                error: `Erreur lors de l'enregistrement des changements pour ${shortenAndFormatLabel(field.name)}`,
            });
        } catch (error) {
            console.error('Error saving changes:', error);
            setError(true);
            setErrorMessage('Error saving changes: ' + error.message);
        }
    }, 500, { maxWait: 2000 });

    const handleSaveDropdownChanges = useDebouncedCallback(async (field, value) => {
        if (!field) return console.warn("No field provided for saving changes");
        const isValid = validateFieldValue(field.type, value);

        // Update error state based on validation result
        setError(!isValid);
        if (!isValid) {
            const message = getErrorMessage(field.type, value);
            setErrorMessage(message);
            console.warn(`Invalid value for ${field.name} (${field.type}): ${value}`);
            return;
        } else {
            setErrorMessage('');
        }

        try {
            await toast.promise(addExistingNodeMutation.mutateAsync({
                node_id: field.id,
                id: value,
            }, {
                onSuccess: async () => {
                    await queryClient.invalidateQueries({
                        queryKey: ['apiData', 'class_attribute_field', {
                            node_id: Number.parseInt(parentId)
                        }]
                    })
                    await queryClient.invalidateQueries({
                        queryKey: ['apiData', 'class_attribute_field', {}]
                    })
                }
            }), {
                loading: `Enregistrement de ${shortenAndFormatLabel(field.name)}...`,
                success: `Changements enregistrés pour ${shortenAndFormatLabel(field.name)}`,
                error: `Erreur lors de l'enregistrement des changements pour ${shortenAndFormatLabel(field.name)}`,
            });
        } catch (error) {
            console.error('Error saving changes:', error);
            setError(true);
            setErrorMessage('Error saving changes: ' + error.message);
        }
    })

    const handleValueChange = useCallback((value, f = undefined) => {
        const isValid = validateFieldValue(field.type, value);
        setError(!isValid);
        if (!isValid) {
            setErrorMessage(getErrorMessage(field.type, value));
        } else {
            setErrorMessage('');
        }

        handleSaveChanges(field);
        setValue(value);
    }, [fieldKey, field]);

    const handleDropdownValueChange = useCallback((value) => {
        // Validate immediately for user feedback
        const isValid = validateFieldValue(field.type, value?.value);
        setError(!isValid);
        if (!isValid) {
            setErrorMessage(getErrorMessage(field.type, value?.value));
        } else {
            setErrorMessage('');
        }

        handleSaveDropdownChanges(field, value?.value);
        setValue(value);
    })

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
                    <FormControl component="fieldset" sx={{
                        display: 'flex',
                        flexDirection: 'row',
                    }}>
                        <Button
                            tabIndex="-1"
                            variant="text"
                            color="primary"
                            onClick={handleChangingForm}
                            title={fieldKey}
                            sx={{ textAlign: 'left', justifyContent: 'flex-start' }}
                        >
                            <Typography fontWeight="medium">{shortenAndFormatLabel(name)}</Typography>
                        </Button>
                        {!typeComponent.includes(type) && (
                            <Box className="toggle-wrapper" textAlign="right">
                                <IconButton
                                    onClick={handleToggleField}
                                    size="small"
                                >
                                    {isExpanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                                </IconButton>
                            </Box>
                        )}
                    </FormControl>
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                    {
                        (listCheckboxField.includes(type) && (
                            <ListCheckboxField
                                field={field}
                                fieldKey={fieldKey}
                                size="small"
                                error={error}
                                helperText={error ? errorMessage : ''}
                            />
                        ))
                    }

                    {(inputTextField.includes(type)) && (
                        <TextFormField
                            field={field}
                            fieldKey={fieldKey}
                            value={value}
                            onChange={handleValueChange}
                            size="small"
                            error={error}
                            helperText={error ? errorMessage : ''}
                        />
                    )}

                    {dateField.includes(type) && (
                        <DateFormField
                            fieldKey={fieldKey}
                            value={value}
                            onChange={handleValueChange}
                            size="small"
                            error={error}
                            helperText={error ? errorMessage : ''}
                        />
                    )}

                    {checkboxField.includes(type) && (
                        <CheckboxFormField
                            fieldKey={fieldKey}
                            value={value}
                            onChange={handleValueChange}
                            size="small"
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

                    {dropdownField.includes(type) && (
                        <DropdownField
                            field={field}
                            fieldKey={fieldKey}
                            value={value}
                            onChange={handleDropdownValueChange}
                            size="small"
                            defaultValue={defaultValue}
                            error={error}
                            helperText={error ? errorMessage : ''}
                        />
                    )}

                    {radioField.includes(type) && (
                        <RadioField
                            field={field}
                            fieldKey={fieldKey}
                            value={value}
                            onChange={handleValueChange}
                            size="small"
                            error={error}
                            helperText={error ? errorMessage : ''}
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
