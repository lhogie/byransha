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
    const [lastChangedField, setLastChangedField] = useState(null);

    const [loadingExistingNodes, setLoadingExistingNodes] = useState(false);


    /* Logic */

    // Hooks
    const jumpToId = useApiMutation('jump', {onSuccess: async (data) => {return data; }});
    const classAttributeField = useApiMutation('class_attribute_field', {onSuccess: async (data) => {return data; }});
    const setValue = useApiMutation('set_value', {onSuccess: async (data) => {return data; }});
    const addNode = useApiMutation('add_node', {onSuccess: async (data) => {return data; }});
    const listExistingNode = useApiMutation('list_existing_node', {onSuccess: async (data) => { return data; }});
    const addExistingNode = useApiMutation('add_existing_node', {onSuccess: async (data) => { return data; }});
    ////////////////////////////////////////////////////////////////

    // Utility functions
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

    const createKey = (id, name) => { return `${id}@${name}`; };
    //////////////////////////////////////////////////////////////////

    // Handle functions

    const handleChangingForm = async(name, id) => {
        if(id === rootId) return console.log("return from handleChangingForm");
        try{
            await jumpToId.mutateAsync({ node_id: id });
            await navigate(`/add-node/form/${name}`);
            window.location.reload();
        } catch (error) {
            console.error("Error changing form:", error);
        }
    };


    const handleSaveChanges = async (field) => {
        if (!field) return console.warn("No field provided for saving changes");
        const fieldKey = createKey(field.id, field.name);
        let value = formValues[fieldKey];
        try {
            const data = await setValue.mutateAsync( {
                id: field.id,
                value: value,
            });
            console.log("Changes saved successfully:", stringifyData(data, "tab"));
        } catch (error) {
            console.error('Error saving changes:', error);
        }
    };


    const toggleField = async(fieldName, nodeId) => {
        console.log(`Toggling field: ${fieldName} with nodeId: ${nodeId}`);
        const isExpanded = expandedFields[fieldName];
        if (isExpanded) {
            setSubfieldData(prev => {
                const newData = { ...prev };
                delete newData[nodeId];
                return newData;
            });}
        else {
            if (!subfieldData[nodeId] && nodeId !== rootId && !loading){
                setCurrentToggleNodeId(nodeId);
                lastExpandedNode.current = nodeId;
                await handleNestedField(nodeId);
            }
        }
        setExpandedFields(prev => ({
            ...prev,
            [fieldName]: !prev[fieldName],
        }));
    };

    const handleNestedField = async (id) => {
        await jumpToId.mutateAsync({node_id: id});
        try{
            const data = await classAttributeField.mutateAsync();
            //console.log(stringifyData(data, "tab"));
            const id = data?.data?.node_id;
            if (id === rootId) return console.log("Same id as the root node, not loading subfields");
            const allFields = data?.data?.results?.[0]?.result?.data || [];
            const subfields = allFields.filter(field => field?.name !== "graph");

            const newValues = {};
            subfields.forEach(field => {
                if (field.name && field.value !== undefined && field.value !== "null") {
                    const fieldKey = createKey(field.id, field.name);
                    newValues[fieldKey] = field.value;
                }});

            setFormValues(prev => ({...prev,...newValues,}));
            setSubfieldData(prev => ({...prev,[lastExpandedNode.current]: subfields,}));

        } catch (error) {console.log("Error in wanting to load field:", error);}
        await jumpToId.mutateAsync({node_id: rootId});
    }

    const handleAddNewNode = async (field) => {
        console.log("Add new node clicked");
        const id = field?.id;
        const fullName = field?.listNodeType;
        console.log(stringifyData(field, "tab"));

        setCurrentToggleNodeId(id);
        await jumpToId.mutateAsync({node_id: id});
        try {
            const data = await addNode.mutateAsync({BNodeClass: fullName});
            const node = data?.data?.results?.[0]?.result?.data;
            if (!node) return console.warn("No node returned from addNode mutation");
            const parentId = id;
            const fieldKey = createKey(node.id, node.name);
            setSubfieldData(prev => ({
                ...prev,
                [parentId]: [...(prev[parentId] || []), node]
            }));
            if (node.name && node.value !== undefined && node.value !== "null") {
                setFormValues(prev => ({
                    ...prev,
                    [fieldKey]: node.value
                }));
            }

        } catch (error) {console.error("Error adding new node:", error);}

        await jumpToId.mutateAsync({ node_id: rootId });
    };

    const handleListExistingNode = async (field) => {
        console.log("Add existing node clicked with field:", field);
        setSelectedField(field);
        setShowExistingNodeCard(true);
        setLoadingExistingNodes(true); // <--- start spinner

        const shortName = field.listNodeType.split('.').pop();
        await jumpToId.mutateAsync({node_id: field.id});

        try {
            const data = await listExistingNode.mutateAsync({ type: shortName });
            const result = data?.data?.results?.[0]?.result?.data || [];
            setExistingNodeList(result);
        } catch (error) {
            console.error("Error fetching class attribute field:", error);
        } finally {
            setLoadingExistingNodes(false); // <--- stop spinner
        }
    };


    const handleAddExistingNode = async (node) => {
        try{
            await addExistingNode.mutateAsync({id: node.id});
            await jumpToId.mutateAsync({ node_id: rootId });
        } catch (error) { console.error("Error adding existing node:", error); }
    }
    ////////////////////////////////////////////////////////////////


    /* useEffect */
    useEffect(() => {
        if (!loading && rawApiData) {
            const initialValues = {};
            const initialFieldData = {};

            const allFields =
                rawApiData?.data?.results?.[0]?.result?.data ||
                rawApiData?.results?.[0]?.result?.data ||
                [];

            allFields.forEach(field => {
                if (field.name && field.value !== "null" && field.value !== undefined) {
                    const fieldKey = createKey(field.id, field.name);
                    initialValues[fieldKey] = field.value;
                    initialFieldData[fieldKey] = field;

                }
            });

            setFormValues(initialValues);
            setExpandedFields({});
            setSubfieldData({});
        }
    }, [loading, rawApiData]);

    useEffect(() => {
        if (!lastChangedField) return;

        const handler = setTimeout(() => {
            handleSaveChanges(lastChangedField);
        }, 500);

        return () => {
            clearTimeout(handler);
        };
    }, [formValues]);


    const formFields = (
        rawApiData?.data?.results?.[0]?.result?.data ||
        rawApiData?.results?.[0]?.result?.data ||
        []
    ).filter(field => field?.name !== "graph");

    const inputTextField = ["StringNode", "EmailNode", "PhoneNumberNode", "IntNode"]
    const checkboxField = ["BooleanNode"];
    const imageField = ["ImageNode"];
    const typeComponent = [...inputTextField, ...checkboxField];

    const renderFields = (fields, visited = new Set()) => {
        if (!fields || !Array.isArray(fields)) return null;

        return fields.map(field => {
            const { id, name, type } = field;
            if (!name) return null;
            const fieldKey = createKey(field.id, field.name);

            if (visited.has(id)) {
                return <p key={`cycle-${id}`}>Circular reference detected for {name}</p>;
            }

            const newVisited = new Set(visited);
            newVisited.add(id);

            renderFields(subfieldData[id], newVisited);

            return (
                <div key={fieldKey} className="form-field-wrapper">
                    <div className="form-field">
                        <label htmlFor={name} className="main-label">
                            <button type="button" className="deeper-button" onClick={() => handleChangingForm(name, id)} title={fieldKey}>
                                {shortenAndFormatLabel(name)}
                            </button>
                        </label>

                        {(inputTextField.includes(type)) && (
                            <input
                                id={fieldKey}
                                type="text"
                                value={formValues[fieldKey] || ""}
                                onChange={(e) =>{
                                    setFormValues(prev => ({
                                        ...prev,
                                        [fieldKey]: e.target.value,
                                    }));
                                    setLastChangedField(field);
                                }}
                            />
                        )}

                        {checkboxField.includes(type) && (
                            <input
                                id={fieldKey}
                                type="checkbox"
                                checked={!!formValues[fieldKey]}
                                onChange={(e) => {
                                    setFormValues(prev => ({
                                        ...prev,
                                        [fieldKey]: e.target.checked,
                                    }));}
                                }
                            />
                        )}

                        {!typeComponent.includes(type) && (
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

                    {!typeComponent.includes(type) && (
                        <div className="nested-fields">
                            {expandedFields[fieldKey] && (
                                subfieldData[id] ? (
                                    subfieldData[id].length > 0 ? (
                                        renderFields(subfieldData[id], newVisited)) : (<p>No subfields available for this.</p>)) : (<p>Loading subfields or error, please reload...</p>)
                            )}

                            {["ListNode", "SetNode"].includes(type) && expandedFields[fieldKey] && (
                                <div className="add-new-node">
                                    <IconButton className="add-element" onClick={() => {handleAddNewNode(field)}}>
                                        <AddIcon /> <span>Add new {field.name}</span>
                                    </IconButton>
                                    <IconButton className="add-element-existing" onClick={() => {handleListExistingNode(field)}}>
                                        <AddIcon /> <span>Add {field.name} from Existing Node</span>
                                    </IconButton>
                                </div>
                            )}

                            {expandedFields[fieldKey] && (
                                <div className="image-preview-wrapper" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                    {formValues[fieldKey] && (

                                        <img
                                            className="image-preview"
                                            src={`data:${field.mimeType};base64,${formValues[fieldKey]}`}
                                            alt={name}
                                            style={{
                                                maxHeight: '150px',
                                                width: 'auto',
                                                objectFit: 'contain',
                                                marginTop: '10px',
                                                border: '1px solid #eee'
                                            }}

                                            onClick={() => {}}
                                        />
                                    )}

                                    <label className="add-element" style={{ marginTop: '10px', cursor: 'pointer' }}>
                                        Upload New Image
                                        <input
                                            type="file"
                                            accept="image/*"
                                            style={{ display: 'none' }}
                                            onChange={async (e) => {
                                                const file = e.target.files[0];
                                                if (!file) return;
                                                const reader = new FileReader();
                                                reader.onloadend = async () => {
                                                    const base64String = reader.result.split(',')[1];
                                                    setFormValues(prev => ({
                                                        ...prev,
                                                        [fieldKey]: base64String
                                                    }));
                                                    setLastChangedField(field);
                                                };
                                                reader.readAsDataURL(file);
                                            }}
                                        />
                                    </label>
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
                    renderFields(formFields)) : (<p>No fields available.</p>)}
            </form>

            {showExistingNodeCard && (
                <div className="overlay">
                    <div className="existing-node-card">
                        <div className="card-header">
                            <h2>Select Existing Node</h2>
                            <IconButton onClick={() => {setShowExistingNodeCard(false); jumpToId.mutate({ node_id: rootId });}} aria-label="close">
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
                            {loadingExistingNodes ? (
                                <div className="loading-spinner">Loading nodes...</div>
                            ) : (
                                existingNodeList
                                    .filter(node => node.name.toLowerCase().includes(searchQuery.toLowerCase()))
                                    .map(node => (
                                        <div
                                            key={node.id}
                                            className="node-card-item"
                                            onClick={() => {
                                                const fieldKey = createKey(node.id, node.name);
                                                setSubfieldData(prev => ({
                                                    ...prev,
                                                    [selectedField.id]: [...(prev[selectedField.id] || []), node]
                                                }));
                                                setFormValues(prev => ({
                                                    ...prev,
                                                    [fieldKey]: node.value
                                                }));
                                                handleAddExistingNode(node);
                                                setShowExistingNodeCard(false);
                                            }}
                                        >
                                            <strong>{node.name}</strong>
                                        </div>
                                    ))
                            )}
                        </div>

                    </div>
                </div>
            )}
        </div>
    );
};

export default FormPage;
