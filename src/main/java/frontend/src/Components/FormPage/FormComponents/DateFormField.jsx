import React from 'react';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';
import { FormHelperText, FormControl } from '@mui/material';

const DateFormField = ({ fieldKey, value, onFocus, onChange, error, helperText, ...rest }) => {
    return (
        <FormControl fullWidth error={error}>
            <DatePicker
                id={fieldKey}
                value={value ? dayjs(value) : null}
                onChange={(newValue) => onChange(newValue)}
                onFocus={onFocus}
                sx={{ width: '100%' }}
                {...rest}
                slotProps={{
                    textField: {
                        size: 'small',
                        error: error
                    }
                }}
            />
            {helperText && <FormHelperText>{helperText}</FormHelperText>}
        </FormControl>
    );
};

export default DateFormField;
