import { useParams } from 'react-router';
import './FormPage.css';
import React, { useEffect, useState, useCallback, useRef } from 'react';
import { IconButton } from '@mui/material';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import { useNavigate } from "react-router";
import {useApiData, useApiMutation} from "../../hooks/useApiData.js";

const FormPage = () => {
  const { classForm } = useParams();
  const navigate = useNavigate();
  const { data: rawApiData, isLoading: loading, error, refetch } = useApiData(`class_attribute_field`);
  const rootId = rawApiData?.data?.node_id || null;
  const pageName = classForm.split('.').pop();

  const [formValues, setFormValues] = useState({});
  const [expandedFields, setExpandedFields] = useState({});
  const [subfieldData, setSubfieldData] = useState({});
  const lastExpandedNode = useRef(null);
  const [currentToggleNodeId, setCurrentToggleNodeId] = useState(null);


  const addFieldToggleNode = useApiMutation('class_attribute_field', {
    onSuccess: async (data) => {
      const id = data?.data?.node_id;
      if (id === rootId) return console.log("return from addFieldToggleNode");

      const allFields = data?.data?.results?.[0]?.result?.data || [];
      const subfields = allFields.filter(field => field?.name !== "graph");

      const newValues = {};
      subfields.forEach(field => {
        if (field.name && field.value !== undefined && field.value !== "null") {
          const fieldKey = field.id + "@" + field.name;
          newValues[fieldKey] = field.value;
        }
      });

      setFormValues(prev => ({
        ...prev,
        ...newValues,
      }));

      setSubfieldData(prev => ({
        ...prev,
        [lastExpandedNode.current]: subfields,
      }));

      console.log("Set subfields for", lastExpandedNode.current, subfields);
    }
  });



  const jumpToToggleNode = useApiMutation('jump', {
    onSuccess: async (data) => {
        console.log("Subfield data fetched successfully");
        addFieldToggleNode.mutate();
        goBackToRootNode.mutate({'node_id': rootId});
    },
  });

  const goBackToRootNode = useApiMutation('jump', {
      onSuccess: async (data) => {
          console.log("Returned to root node successfully -------------------");
      }
  })


const toggleField = (fieldName, nodeId) => {
  const isExpanded = expandedFields[fieldName];

  if (!isExpanded && !subfieldData[nodeId] && nodeId !== rootId && !loading) {
    setCurrentToggleNodeId(nodeId);
    lastExpandedNode.current = nodeId;
    jumpToToggleNode.mutate(`node_id=${nodeId}`);
  }

  setExpandedFields(prev => ({
    ...prev,
    [fieldName]: !prev[fieldName],
  }));
};

  const shortenAndFormatLabel = (label) => {
    if (!label) return '';
    const spaced = label
      .replace(/([a-z])([A-Z])/g, '$1 $2')
      .replace(/_/g, ' ')
      .trim();

    return spaced.charAt(0).toUpperCase() + spaced.slice(1);
  };



  const stringifyData = useCallback((data, indent = 2) => {
    if (!data) return "";
    return JSON.stringify(data, null, indent === "tab" ? "\t" : indent);
  }, []);

  useEffect(() => {
    if (!loading && rawApiData) {
      const initialValues = {};

      const allFields =
        rawApiData?.data?.results?.[0]?.result?.data ||
        rawApiData?.results?.[0]?.result?.data ||
        [];

      allFields.forEach(field => {
        if (field.name && field.value !== "null" && field.value !== undefined) {
          const fieldKey = field.id + "@" + field.name;
          initialValues[fieldKey] = field.value;
        }
      });

      setFormValues(initialValues);
      setExpandedFields({});
      setSubfieldData({});
    }
  }, [loading, rawApiData]);



  const formFields = (
    rawApiData?.data?.results?.[0]?.result?.data ||
    rawApiData?.results?.[0]?.result?.data ||
    []
  ).filter(field => field?.name !== "graph");


  const renderFields = (fields, visited = new Set()) => {
    if (!fields || !Array.isArray(fields)) return null;

    return fields.map(field => {
      const { id, name, type } = field;
      if (!name) return null;
      const fieldKey = field.id + "@" + field.name;

      if (visited.has(id)) {
        return <p key={`cycle-${id}`}>Circular reference detected for {name}</p>;
      }

      const newVisited = new Set(visited);
      newVisited.add(id);

      renderFields(subfieldData[id], newVisited)


      return (
        <div key={fieldKey} className="form-field-wrapper">
          <div className="form-field">
            <label htmlFor={name} className="main-label">{shortenAndFormatLabel(name)}</label>

            {(type === "StringNode") && (
              <input
                id={fieldKey}
                type="text"
                value={formValues[fieldKey] || ""}
                onChange={(e) =>
                  setFormValues(prev => ({
                    ...prev,
                    [fieldKey]: e.target.value,
                  }))
                }
              />
            )}

            {(type === "BooleanNode") && (
              <input
                id={fieldKey}
                type="checkbox"
                checked={!!formValues[fieldKey]}
                onChange={(e) =>
                  setFormValues(prev => ({
                    ...prev,
                    [fieldKey]: e.target.checked,
                  }))
                }
              />
            )}

            {!["StringNode", "BooleanNode"].includes(type) && (
                <div className="toggle-wrapper">
                      <button
                        type="button"
                        onClick={() => toggleField(name, id)}
                        className="toggle-button"
                      >
                        <span className="toggle-icon">{expandedFields[name] ? '▼' : '▶'}</span>
                      </button>
                    </div>
                  )}
                </div>

          {!["StringNode", "BooleanNode"].includes(type) && (
            <div className="nested-fields">
              {expandedFields[name] ? (
                subfieldData[id] ? (
                  subfieldData[id].length > 0 ? (
                    renderFields(subfieldData[id], new Set(visited))
                  ) : (
                    <p>No subfields available for this.</p>
                  )
                ) : (
                  <p>Loading subfields or error, please reload...</p>
                )
              ) : null}
            </div>
          )}
        </div>
      );
    });
  };


  if (loading) return <p>Loading form fields...</p>;
  if (error) return <p>Error loading form fields: {error.message || 'Unknown error'}</p>;

  return (
    <div className="form-page">
      <IconButton
        className="close-button"
        onClick={() => navigate("/add-node")}
        aria-label="close"
      >
        <CloseRoundedIcon />
      </IconButton>

      <h1>Form for: {shortenAndFormatLabel(pageName)}</h1>

      <form className="form-fields" onSubmit={e => e.preventDefault()}>
        {formFields.length > 0 ? (
          renderFields(formFields)
        ) : (
          <p>No fields available.</p>
        )}
      </form>
    </div>
  );
};

export default FormPage;
