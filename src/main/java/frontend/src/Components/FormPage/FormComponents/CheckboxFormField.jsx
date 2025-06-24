import React from 'react';
import { Checkbox, FormControlLabel } from '@mui/material';

const CheckboxFormField = ({ fieldKey, value, onFocus, onChange, ...rest }) => {
    return (
        <FormControlLabel
            control={
                <Checkbox
                    id={fieldKey}
                    checked={!!value}
                    onFocus={onFocus}
                    onChange={(e) => onChange(e.target.checked)}
                    {...rest}
                />
            }
            label=""
        />
    );
};

export default CheckboxFormField;