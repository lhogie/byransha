import { useParams } from 'react-router';
import './FormPage.css';
import React, { useEffect, useState } from 'react';
import { useTitle } from "../../global/useTitle";
import { IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useNavigate } from "react-router";
import { useApiData } from "../../hooks/useApiData.js";
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';


const FormPage = () => {
  const { classForm } = useParams();
  const navigate = useNavigate();

  const { data: rawApiData, isLoading: loading, error, refetch } = useApiData('add_node', {
    classForm: classForm,
  });

  const [formValues, setFormValues] = useState({});
  const stringifyData = useCallback((data, indent = 2) => {
      if (!data) return "";
      return JSON.stringify(data, null, indent === "tab" ? "\t" : indent);
  }, []);
  const formatLabel = (label) => {
    if (!label) return '';
    const spaced = label.replace(/([a-z])([A-Z])/g, '$1 $2');
    return spaced.charAt(0).toUpperCase() + spaced.slice(1);
  };
  const shortenAndFormatLabel = (fullKey) => {
    if (!fullKey) return '';
    const shortKey = fullKey.split('.').pop(); // Take the last part after dot
    const spaced = shortKey.replace(/([a-z])([A-Z])/g, '$1 $2');
    return spaced.charAt(0).toUpperCase() + spaced.slice(1); // Capitalize first word
  };

  useTitle(`${shortenAndFormatLabel(classForm)}`);



  useEffect(() => {
    const fieldData = rawApiData?.data?.results?.[0]?.result?.data;
    if (fieldData) {
      const initialValues = {};
      for (const [key, fullType] of Object.entries(fieldData)) {
        const type = fullType.split('.').pop();
        initialValues[key] = type === "BooleanNode" ? false : "";
      }
      setFormValues(initialValues);
    }
  }, [rawApiData]);

  return (
    <div className="form-page">
      <h1>Form for: {shortenAndFormatLabel(classForm)}</h1>

      <form className="form-fields">
        {rawApiData?.data?.results?.[0]?.result?.data &&
          Object.entries(rawApiData.data.results[0].result.data).map(([key, fullType]) => {
              // type = fullType;
            const type = fullType.split('.').pop(); // normalize type name

            return (
              <div key={key} className="form-field">
                <label>{formatLabel(key)}</label>

                {(type === "StringNode" || type === "string" || type === "DateNode") && (
                  <input
                    type="text"
                    value={formValues[key] || ""}
                    onChange={(e) =>
                      setFormValues((prev) => ({ ...prev, [key]: e.target.value }))
                    }
                  />
                )}

                {(type === "BooleanNode"  || type === "boolean" )&& (
                  <input
                    type="checkbox"
                    checked={formValues[key] || false}
                    onChange={(e) =>
                      setFormValues((prev) => ({ ...prev, [key]: e.target.checked }))
                    }
                  />
                )}

                {type !== "StringNode" && type !== "BooleanNode" && type !== "boolean" &&  type !== "string" && (
                  <div className="form-field unsupported">
                    <span>{type}</span>
                    <CloseRoundedIcon className="unsupported-icon" />
                  </div>
                )}

              </div>
            );
          })}
      </form>

      <IconButton className="close-button" onClick={() => { navigate("/add-node") }} aria-label="close">
        <CloseIcon />
      </IconButton>
    </div>
  );
};

export default FormPage;
