import { useParams } from 'react-router';
import './FormPage.css';
import React, { useEffect, useState, useCallback, useRef } from 'react';
import { IconButton } from '@mui/material';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import { useNavigate } from "react-router";
import {useApiData, useApiMutation} from "../../hooks/useApiData.js";
import AddIcon from '@mui/icons-material/Add';

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

  const [showExistingNodeCard, setShowExistingNodeCard] = useState(false);
  const [existingNodeList, setExistingNodeList] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedField, setSelectedField] = useState(null);

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
    }
  });


  const jumpToToggleNode = useApiMutation('jump', {
    onSuccess: async (data) => {
        addFieldToggleNode.mutate();
        goBackToRootNode.mutate({'node_id': rootId});
    },
  });

  const goBackToRootNode = useApiMutation('jump', {
      onSuccess: async (data) => {}
  })

  const moveDeeper = async (name, id) => {
    if (id === rootId) return console.log("return from moveDeeper");
    try {
      await jumpDeeper.mutateAsync(`node_id=${id}`);
      await navigate(`/add-node/form/${name}`);
      window.location.reload();
    } catch (error) {
      console.error("Error jumping deeper:", error);
    }
  };

  const jumpDeeper = useApiMutation('jump', {
      onSuccess: async (data) => {}
  });

  const toggleField = (fieldName, nodeId) => {
    const isExpanded = expandedFields[fieldName];

    if (isExpanded) {
      setSubfieldData(prev => {
        const newData = { ...prev };
        delete newData[nodeId];
        return newData;
      });
    } else {
      if (!subfieldData[nodeId] && nodeId !== rootId && !loading) {
        setCurrentToggleNodeId(nodeId);
        lastExpandedNode.current = nodeId;
        jumpToToggleNode.mutate(`node_id=${nodeId}`);
      }
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

      console.log(stringifyData(rawApiData, "tab"));
    }
  }, [loading, rawApiData]);

  const formFields = (
    rawApiData?.data?.results?.[0]?.result?.data ||
    rawApiData?.results?.[0]?.result?.data ||
    []
  ).filter(field => field?.name !== "graph");

  const saveChanges = () => {
      Object.entries(formValues).forEach(([nodeId, field]) => {
          if(!(field == "" || field == "null"))  {
              const id = nodeId.split('@')[0]
              save.mutateAsync(`${id}=${field}`)
          }
      })
  };

  const save = useApiMutation('set_value', {onSuccess: async (data) => {},})

  const handleAddNewNode = async (field) => {
    console.log("Add new node clicked");
    const id = field?.id;
    const fullName = field?.listNodeType;
    console.log(stringifyData(field, "tab"));

    setCurrentToggleNodeId(id); // For context in addNewNode
    await jumpDeeper.mutateAsync(`node_id=${id}`);
    await addNewNode.mutateAsync(`BNodeClass=${encodeURIComponent(fullName)}`);
  };

  const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));


  const addNewNode = useApiMutation('add_node', {
    onSuccess: async (data, variables) => {
      console.log("New node added successfully");
      const newNode = data?.data?.results?.[0]?.result?.data;
      console.log(stringifyData(newNode, "tab"));
      if (!newNode) return console.warn("No new node returned");

      const parentId = currentToggleNodeId; // or pass it in `variables`
      const fieldKey = newNode.id + "@" + newNode.name;

      setSubfieldData(prev => ({
        ...prev,
        [parentId]: [...(prev[parentId] || []), newNode]
      }));

      if (newNode.name && newNode.value !== undefined && newNode.value !== "null") {
        setFormValues(prev => ({
          ...prev,
          [fieldKey]: newNode.value
        }));
      }

      await goBackToRootNode.mutate({ node_id: rootId });
    }
  });


  const handleAddExistingNode = async (field) => {
    console.log("Add existing node clicked with field:", field);
    setSelectedField(field);
    setShowExistingNodeCard(true);
    const shortName = field.listNodeType.split('.').pop();
    await jumpDeeper.mutateAsync(`node_id=${field.id}`);
    await listExistingNodes.mutate({type : shortName});

  };

    const listExistingNodes = useApiMutation('list_existing_node', {
        onSuccess: async (data) => {
            const result = data?.data?.results?.[0]?.result?.data || [];
            setExistingNodeList(result);
        }
    });

    const addExistingNode = useApiMutation('add_existing_node', {
        onSuccess: async (data) => {
            console.log("Existing node added successfully", stringifyData(data, "tab"));
            await goBackToRootNode.mutate({ node_id: rootId });
        }
    });

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
            <label htmlFor={name} className="main-label">
              <button type="button" className="deeper-button" onClick={() => moveDeeper(name, id)} title={fieldKey}>
                {shortenAndFormatLabel(name)}
              </button>
            </label>

            {(type === "StringNode" || type === "EmailNode") && (
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

            {!["StringNode", "BooleanNode", "EmailNode"].includes(type) && (
              <div className="toggle-wrapper">
                <button
                  type="button"
                  onClick={() => toggleField(fieldKey, id)}
                  className="toggle-button"
                >
                  <span className="toggle-icon">{expandedFields[fieldKey] ? '▼' : '▶'}</span>
                </button>
              </div>
            )}
          </div>

          {!["StringNode", "BooleanNode", "EmailNode"].includes(type) && (
            <div className="nested-fields">
              {expandedFields[fieldKey] && (
                subfieldData[id] ? (
                  subfieldData[id].length > 0 ? (
                    renderFields(subfieldData[id], newVisited)
                  ) : (
                    <p>No subfields available for this.</p>
                  )
                ) : (
                  <p>Loading subfields or error, please reload...</p>
                )
              )}
              {["ListNode", "SetNode"].includes(type) && expandedFields[fieldKey] && (
                  <div className="add-new-node">
                      <IconButton className="add-element" onClick={() => {handleAddNewNode(field)}}>
                          <AddIcon /> <span>Add New Node</span>
                      </IconButton>
                      <IconButton className="add-element-existing" onClick={() => {handleAddExistingNode(field)}}>
                            <AddIcon /> <span>Add from Existing Node</span>
                      </IconButton>
                  </div>
              )}

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
      <div className="save-button-container">
          <button className="save-button" onClick={saveChanges}>
            Save Changes
          </button>
      </div>

      {showExistingNodeCard && (
        <div className="overlay">
          <div className="existing-node-card">
            <div className="card-header">
              <h2>Select Existing Node</h2>
              <IconButton onClick={() => {setShowExistingNodeCard(false); goBackToRootNode.mutate({ node_id: rootId });}} aria-label="close">
                <CloseRoundedIcon />
              </IconButton>
            </div>

            <input
              type="text"
              placeholder="Search nodes..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="search-bar"
            />

            <div className="node-list">
              {existingNodeList
                .filter(node =>
                  node.name.toLowerCase().includes(searchQuery.toLowerCase())
                )
                .map(node => (
                  <div
                        key={node.id}
                        className="node-card-item"
                        onClick={() => {
                          const fieldKey = node.id + "@" + node.name;
                          setSubfieldData(prev => ({
                            ...prev,
                            [selectedField.id]: [...(prev[selectedField.id] || []), node]
                          }));
                          setFormValues(prev => ({
                            ...prev,
                            [fieldKey]: node.value
                          }));
                            addExistingNode.mutateAsync({id:node.id});

                          setShowExistingNodeCard(false);
                        }}
                  >
                    <strong>{node.name}</strong>: {node.value}
                  </div>
                ))}
            </div>
          </div>
        </div>
      )}


    </div>
  );
};

export default FormPage;
