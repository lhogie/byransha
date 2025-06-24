import React, {useEffect} from 'react';
import {Autocomplete, CircularProgress, InputAdornment, TextField} from '@mui/material';
import {useApiData, useApiMutation} from "../../../hooks/useApiData.js";

const DropdownField = ({ field, fieldKey, value, defaultValue, onFocus, onChange, ...rest }) => {
    const shortName = field.listNodeType.split('.').pop();

    const {
        data: listData = [],
        isLoading,
        isError,
        error
    } = useApiData('list_existing_node', {
        type: shortName
    });

    useEffect(() => {
        if (!isLoading && !isError && listData?.data?.results?.[0]?.result?.data?.length !== 0 && defaultValue) {
            const id = Number.parseInt(defaultValue.split('@')[1]);
            const existingOption = listData?.data?.results?.[0]?.result?.data.find(option => option.id === id);

            if (existingOption) {
                onChange({
                    label: existingOption.name,
                    value: existingOption.id
                });
            } else {
                onChange(null);
            }
        }
    }, [isLoading, isError]);

    return (
        <Autocomplete
            disablePortal
            renderInput={(params) => (
                <TextField
                    {...params}
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
            size="small"
            id={fieldKey}
            value={value}
            onChange={(event, newValue) => {
                onChange(newValue);
            }}
        />
    );
};

export default DropdownField;
