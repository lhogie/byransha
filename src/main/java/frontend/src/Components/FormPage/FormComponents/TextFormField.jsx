import React from 'react';
import {CircularProgress, InputAdornment, TextField} from '@mui/material';

const TextFormField = ({ field, fieldKey, value, onFocus, onChange, ...rest }) => {
    const getInputType = (type) => {
        switch (type) {
            case "EmailNode": return "email";
            case "PhoneNumberNode": return "tel";
            case "IntNode": return "number";
            default: return "text";
        }
    };

    return (
        <TextField
            fullWidth
            id={fieldKey}
            variant="outlined"
            type={getInputType(field.type)}
            value={value || ""}
            onFocus={onFocus}
            onChange={(e) => onChange(e.target.value)}
            {...rest}
        />
    );
};

export default TextFormField;