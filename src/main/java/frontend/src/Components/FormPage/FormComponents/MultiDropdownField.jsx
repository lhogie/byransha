import React, {useEffect} from 'react';
import {useApiData} from "../../../hooks/useApiData.js";
import DropdownField from "./DropdownField.jsx";

const MultiDropdownField = ({ field, fieldKey, value, onFocus, onChange, onFirstChange, error, helperText, ...rest }) => {
    const { data: rawApiData, isLoading, isError } = useApiData(`class_attribute_field`, {
        node_id: field.id
    });

    useEffect(() => {
        if (!isLoading && !isError && rawApiData?.data?.results?.[0]?.result?.data?.length !== 0) {
           onFirstChange(rawApiData?.data?.results?.[0]?.result?.data.map((data) => ({
               firstLetter: data.name.split('. ')[1][0],
               label: data.name.split('. ')[1],
               value: data.id,
           })))
        }
    }, [isLoading, isError]);

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
