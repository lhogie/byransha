import React from 'react';
import { Box, Button, TextField, Typography } from '@mui/material';

const ImageFormField = ({ field, fieldKey, value, onFocus, onChange, ...rest }) => {
    return (
        <Box className="image-preview-wrapper" sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 2 }}>
            {value && (
                <Box
                    component="img"
                    className="image-preview"
                    src={`data:${field.mimeType};base64,${value}`}
                    alt={field.name}
                    sx={{
                        maxHeight: '150px',
                        width: 'auto',
                        objectFit: 'contain',
                        mt: 1,
                        border: '1px solid #eee'
                    }}
                />
            )}

            <Button
                variant="contained"
                component="label"
                sx={{ mt: 2 }}
            >
                Upload New Image
                <TextField
                    type="file"
                    accept="image/*"
                    sx={{ display: 'none' }}
                    onClick={onFocus}
                    onChange={async (e) => {
                        const file = e.target.files[0];
                        if (!file) return;
                        const reader = new FileReader();
                        reader.onloadend = async () => {
                            const base64String = reader.result.split(',')[1];
                            onChange(base64String);
                        };
                        reader.readAsDataURL(file);
                    }}
                    {...rest}
                />
            </Button>
        </Box>
    );
};

export default ImageFormField;