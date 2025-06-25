import React, {useEffect, useState} from 'react';
import {
    Autocomplete, Checkbox,
    CircularProgress,
    FormControl, FormControlLabel,
    FormGroup,
    FormHelperText,
    InputAdornment,
    TextField
} from '@mui/material';
import {useApiData, useApiMutation} from "../../../hooks/useApiData.js";
import {useDebouncedCallback} from "use-debounce";
import toast from "react-hot-toast";
import {shortenAndFormatLabel} from "../../../utils/utils.js";
import {useQueryClient} from "@tanstack/react-query";

const ListCheckboxComponent = ({ option, fieldId }) => {
    const [value, setValue] = useState(option.value);
    const setValueMutation = useApiMutation('set_value');
    const queryClient = useQueryClient();

    const handleSaveChanges = useDebouncedCallback(async (field) => {
        if (!field) return console.warn("No field provided for saving changes");
        try {
            await toast.promise(setValueMutation.mutateAsync({
                id: field.id,
                value: value,
            }, {
                onSuccess: async () => {
                    await queryClient.invalidateQueries({
                        queryKey: ['apiData', 'class_attribute_field', {
                            node_id: Number.parseInt(fieldId)
                        }]
                    })
                }
            }), {
                loading: `Enregistrement de ${shortenAndFormatLabel(field.name)}...`,
                success: `Changements enregistrés pour ${shortenAndFormatLabel(field.name)}`,
                error: `Erreur lors de l'enregistrement des changements pour ${shortenAndFormatLabel(field.name)}`,
            });
        } catch (error) {
            console.error('Error saving changes:', error);
        }
    }, 500, { maxWait: 2000 });

    return  <FormControlLabel
        label={option.name}
        control={
            <Checkbox checked={value || false} onChange={(e) => {
                setValue(e.target.checked);
                handleSaveChanges(option);
            }} />
        }
    />
}

const ListCheckboxField = ({ field, fieldKey, onFocus, error, helperText, ...rest }) => {
    const {data} = useApiData('class_attribute_field', {
        node_id: field.id
    });

    return (
        <FormControl fullWidth error={error}>
            <FormGroup row>
                {
                    data?.data?.results?.[0]?.result?.data?.map((option, index) => (
                       <ListCheckboxComponent key={index} option={option} fieldId={field.id} />
                    ))
                }
            </FormGroup>
            {helperText && <FormHelperText>{helperText}</FormHelperText>}
        </FormControl>
    );
};

export default ListCheckboxField;
