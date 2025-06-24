import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {
    Autocomplete,
    Box,
    Button,
    CircularProgress,
    FormControl,
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
    dateField, dropdownField,
    imageField,
    inputTextField,
    shortenAndFormatLabel, typeComponent
} from "../../../utils/utils.js";
import dayjs from "dayjs";
import {useApiMutation} from "../../../hooks/useApiData.js";
import TextField from '@mui/material/TextField';
import DropdownField from "./DropdownField.jsx";

const FormField = ({
                       field,
                       fieldKey,
                       isExpanded, // Changed from expandedFields to isExpanded
                       onToggleField,
                       onChangingForm,
                       defaultValue = '', // Default value for the field
                   }) => {
    const { id, name, type } = field;
    const [value, setValue] = useState(dropdownField.includes(type) ? {
        label: defaultValue,
        value: defaultValue.split('@')[1]
    } : defaultValue);

    const setValueMutation = useApiMutation('set_value');
    const addExistingNodeMutation = useApiMutation('add_existing_node');

    const validateFieldValue = (type, value) => {
        // If value is null or undefined, it's valid (empty is allowed)
        if (value === null || value === undefined) return true;

        // For empty strings, consider them valid
        if (typeof value === 'string' && value.trim() === '') return true;

        // Validate based on field type
        switch (type) {
            case 'EmailNode':
                // Email validation using regex pattern from EmailNode.java
                const emailRegex = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;
                return emailRegex.test(value);

            case 'IntNode':
                // Check if value is a valid integer
                return /^-?\d+$/.test(value);

            case 'PhoneNumberNode':
                // Check if value is a valid phone number (digits only)
                return /^\d+$/.test(value);

            case 'DateNode':
                // Check if value is a valid dayjs date object
                return dayjs(value).isValid();

            default:
                // For other field types, consider them valid
                return true;
        }
    };

    const handleSaveChanges = useDebouncedCallback(async (field) => {
        if (!field) return console.warn("No field provided for saving changes");
        const isValid = validateFieldValue(field.type, value);
        if (!isValid) {
            console.warn(`Invalid value for ${field.name} (${field.type}): ${value}`);
            return;
        }

        try {
            const data = await setValueMutation.mutateAsync( {
                id: field.id,
                value: value,
            });
        } catch (error) {
            console.error('Error saving changes:', error);
        }
    }, 500, { maxWait: 2000 });

    const handleSaveDropdownChanges = useDebouncedCallback(async (field, value) => {
        if (!field) return console.warn("No field provided for saving changes");
        const isValid = validateFieldValue(field.type, value);
        if (!isValid) {
            console.warn(`Invalid value for ${field.name} (${field.type}): ${value}`);
            return;
        }

        try {
            const data = await addExistingNodeMutation.mutateAsync({
                node_id: field.id,
                id: value,
            });
        } catch (error) {
            console.error('Error saving changes:', error);
        }
    })

    // Memoize handlers to prevent recreating functions on each render
    const handleValueChange = useCallback((value) => {
        handleSaveChanges(field);
        setValue(value);
    }, [fieldKey, field]);

    const handleDropdownValueChange = useCallback((value) => {
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
                    {(inputTextField.includes(type)) && (
                        <TextFormField
                            field={field}
                            fieldKey={fieldKey}
                            value={value}
                            onChange={handleValueChange}
                            size="small"
                        />
                    )}

                    {dateField.includes(type) && (
                        <DateFormField
                            fieldKey={fieldKey}
                            value={value}
                            onChange={handleValueChange}
                            size="small"
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
