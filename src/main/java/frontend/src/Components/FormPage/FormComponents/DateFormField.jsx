import React from 'react';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';

const DateFormField = ({ fieldKey, value, onFocus, onChange, ...rest }) => {
    return (
        <DatePicker
            id={fieldKey}
            value={value ? dayjs(value) : null}
            onChange={(newValue) => onChange(newValue)}
            onFocus={onFocus}
            sx={{ width: '100%' }}
            {...rest}
        />
    );
};

export default DateFormField;