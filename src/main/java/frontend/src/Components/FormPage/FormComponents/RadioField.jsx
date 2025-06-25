import React, {useEffect} from 'react';
import {FormControl, FormHelperText, FormControlLabel, Radio, RadioGroup} from '@mui/material';

const RadioField = ({ field, fieldKey, value, defaultValue, onFocus, onChange, error, helperText, ...rest }) => {
    console.log(value, defaultValue)

    return (
        <FormControl fullWidth error={error}>
            <RadioGroup
                row
                name={fieldKey}
                value={value || defaultValue}
                onChange={(e) => onChange(e.target.value)}
                onFocus={onFocus}
                {...rest}
            >
                {
                    field.options.map((option, index) => (
                        <FormControlLabel
                            key={index}
                            value={index}
                            control={<Radio />}
                            label={option}
                        />
                    ))
                }
            </RadioGroup>
            {helperText && <FormHelperText>{helperText}</FormHelperText>}
        </FormControl>
    );
};

export default RadioField;
