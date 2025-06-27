import React, {useEffect} from 'react';
import {Autocomplete, CircularProgress, FormControl, FormHelperText, InputAdornment, TextField} from '@mui/material';
import {useApiData, useApiMutation} from "../../../hooks/useApiData.js";

const DropdownField = ({ field, fieldKey, value, defaultValue, onFocus, onChange, onFirstChange, error, helperText, multiple = false, ...rest }) => {
    const shortName = field.listNodeType.split('.').pop();

    const {
        data: listData = [],
        isLoading,
        isError,
        error: apiError
    } = useApiData('list_existing_node', {
        type: shortName
    })

    useEffect(() => {
        if (!isLoading && !isError && listData?.data?.results?.[0]?.result?.data?.length !== 0 && !multiple) {
            if (defaultValue) {
                const id = Number.parseInt(defaultValue.split('@')[1]);
                const existingOption = listData?.data?.results?.[0]?.result?.data.find(option => option.id === id);

                if (existingOption) {
                    onFirstChange({
                        label: existingOption.name,
                        value: existingOption.id
                    });
                } else {
                    onFirstChange(undefined);
                }
            } else {
                onFirstChange(undefined);
            }
        }
    }, [isLoading, isError]);

    return (
        <FormControl fullWidth error={error}>
            <Autocomplete
                disablePortal
                renderInput={(params) => (
                    <TextField
                        {...params}
                        error={error}
                    />
                )}
                options={
                    listData?.data?.results?.[0]?.result?.data.map(option => {
                        const firstLetter = option.name[0].toUpperCase();

                        return {
                            label: option.name,
                            value: option.id,
                            firstLetter: /[0-9]/.test(firstLetter) ? '0-9' : firstLetter,
                        }
                    }).sort((a, b) => {
                        if (a.label === "France(FR)") return -1;
                        if (b.label === "France(FR)") return 1;

                        if (a.firstLetter !== b.firstLetter) {
                            return a.firstLetter.localeCompare(b.firstLetter);
                        }

                        return a.label.localeCompare(b.label);
                    }) || []
                }
                groupBy={(option) => option.firstLetter}
                getOptionKey={(option) => option.value}
                getOptionLabel={(option) => option.label}
                isOptionEqualToValue={(option, value) => {
                    return option.value === value.value;
                }}
                size="small"
                id={fieldKey}
                value={value}
                onChange={(event, newValue) => {
                    onChange(newValue);
                }}
                multiple={multiple}
                {...rest}
            />
            {helperText && <FormHelperText>{helperText}</FormHelperText>}
        </FormControl>
    );
};

export default DropdownField;
