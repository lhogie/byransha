import { useParams } from 'react-router';
import './FormPage.css';
import React, { useEffect, useState, useCallback } from 'react';
import { useTitle } from "../../global/useTitle";
import { IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useNavigate } from "react-router";
import { useApiData } from "../../hooks/useApiData.js";
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';

const FormPage = () => {
  const { classForm } = useParams();
  const navigate = useNavigate();
  const { data: rawApiData } = useApiData('class_attribute_field', {
    classForm: classForm,
  });

  const [formValues, setFormValues] = useState({});
  const [nestedFormData, setNestedFormData] = useState(null);
  const [expandedFields, setExpandedFields] = useState({});

  const toggleField = async (key) => {
    setExpandedFields((prev) => ({
      ...prev,
      [key]: !prev[key],
    }));

    // Only fetch if expanding and subfields not loaded
    if (expandedFields[key]) return;  // already expanded, no fetch needed

    const keys = key.split(".");
    let current = nestedFormData;

    for (let i = 0; i < keys.length - 1; i++) {
      current = current?.[keys[i]]?.subfields;
      if (!current) return;
    }

    const field = current?.[keys[keys.length - 1]];
    if (!field || field.subfields !== null) return;

    try {
      const subData = await fetchNestedFields(field.fullType);
      if (!subData) return;

      const newSubfields = {};
      for (const [subKey, subTypeFull] of Object.entries(subData)) {
        const subType = subTypeFull?.split(".").pop();
        newSubfields[subKey] = {
          type: subType,
          subfields: null,
          fullType: subTypeFull,
        };
      }

      setNestedFormData((prevData) => {
        const clone = structuredClone(prevData);
        let pointer = clone;
        for (let i = 0; i < keys.length - 1; i++) {
          pointer = pointer[keys[i]].subfields;
        }
        pointer[keys[keys.length - 1]].subfields = newSubfields;
        return clone;
      });
    } catch (e) {
      console.error("Error lazy-loading subfields", e);
    }
  };



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
    const shortKey = fullKey.split('.').pop();
    const spaced = shortKey.replace(/([a-z])([A-Z])/g, '$1 $2');
    return spaced.charAt(0).toUpperCase() + spaced.slice(1);
  };

  useTitle(`${shortenAndFormatLabel(classForm)}`);

  const fetchNestedFields = async (typeName) => {
    if (!typeName) return null;

    try {
      const response = await fetch(`https://localhost:8080/api/class_attribute_field?classForm=${encodeURIComponent(typeName)}`);
      const data = await response.json();
      return data?.results?.[0]?.result?.data || null;
    } catch (error) {
      console.error("Error fetching nested fields:", error);
      return null;
    }
  };

  const buildFieldStructure = async (fieldData) => {
    const result = {};

    for (const [key, fullType] of Object.entries(fieldData)) {
      const type = fullType.split('.').pop();

      if (["StringNode", "BooleanNode", "DateNode", "string", "boolean"].includes(type)) {
        result[key] = { type, subfields: null };
      } else {
        const subData = await fetchNestedFields(fullType);
        const subfields = subData ? await buildFieldStructure(subData) : null;

        result[key] = {
          type,
          subfields
        };
      }
    }

    return result;
  };


  useEffect(() => {
    const loadNestedFields = async () => {
      const fieldData = rawApiData?.data?.results?.[0]?.result?.data;
      if (!fieldData) return;

      const result = {};

      for (const [key, fullType] of Object.entries(fieldData)) {
        const type = fullType.split('.').pop();

        if (["StringNode", "BooleanNode", "DateNode", "string", "boolean"].includes(type)) {
          result[key] = {
            type,
            subfields: null,
            fullType
          };
        } else {
          // Fetch ONE LEVEL of subfields
          const subData = await fetchNestedFields(fullType);
          const subfields = {};

          if (subData) {
            for (const [subKey, subTypeFull] of Object.entries(subData)) {
              const subType = subTypeFull?.split?.(".").pop() || "Unknown";
              subfields[subKey] = {
                type: subType,
                subfields: null,
                fullType: subTypeFull
              };
            }
          }

          result[key] = {
            type,
            subfields: subData ? subfields : null,
            fullType
          };
        }
      }

      setNestedFormData(result);
    };

    loadNestedFields();
  }, [rawApiData]);

 const renderFields = (fields, path = "") =>
   Object.entries(fields).map(([key, { type, subfields }]) => {
     const fieldKey = path ? `${path}.${key}` : key;

     return (
       <div key={fieldKey} className="form-field-wrapper">
         <div className="form-field">
           <label htmlFor={fieldKey} className="main-label">{shortenAndFormatLabel(fieldKey)}</label>

           {(type === "StringNode" || type === "string" || type === "DateNode") && (
             <input
               id={fieldKey}
               type="text"
               value={formValues[fieldKey] || ""}
               onChange={(e) =>
                 setFormValues((prev) => ({
                   ...prev,
                   [fieldKey]: e.target.value,
                 }))
               }
             />
           )}

           {(type === "BooleanNode" || type === "boolean") && (
             <input
               id={fieldKey}
               type="checkbox"
               checked={formValues[fieldKey] || false}
               onChange={(e) =>
                 setFormValues((prev) => ({
                   ...prev,
                   [fieldKey]: e.target.checked,
                 }))
               }
             />
           )}

           {!["StringNode", "BooleanNode", "DateNode", "string", "boolean"].includes(type) && (
             <button type="button" onClick={() => toggleField(fieldKey)} className="toggle-button">
               {expandedFields[fieldKey] ? '▼' : '▶'} {shortenAndFormatLabel(type)} Subfields
             </button>
           )}
         </div>

         {!["StringNode", "BooleanNode", "DateNode", "string", "boolean"].includes(type) && (
           subfields !== null && (!subfields || Object.keys(subfields).length === 0) && (
             <div className="unsupported-field">
               <span className="unsupported-icon">⚠️</span>
               <span>{shortenAndFormatLabel(type)} (structure unknown)</span>
             </div>
           )
         )}



         {expandedFields[fieldKey] && (
           <div className="nested-fields content-container">
             <div className="form-fields">
               {subfields
                 ? renderFields(subfields, fieldKey)
                 : <p className="loading-subfields">Loading subfields...</p>}
             </div>
           </div>
         )}

       </div>
     );
    }
    );


  return (
    <div className="form-page">
      <IconButton
        className="close-button"
        onClick={() => navigate("/add-node")}
        aria-label="close"
      >
        <CloseRoundedIcon />
      </IconButton>

      <h1>Form for: {shortenAndFormatLabel(classForm)}</h1>

      <form className="form-fields">
        {nestedFormData ? (
          renderFields(nestedFormData)
        ) : (
          <div className="loading-container">
            <p>Loading form fields...</p>
          </div>
        )}
      </form>
    </div>
  );
};

export default FormPage;
