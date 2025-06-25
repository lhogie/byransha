import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import { Box, Button, Stack, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import FormField from './FormField';
import {useApiData, useApiMutation} from "../../../hooks/useApiData.js";
import ExistingNodeSelector from "./ExistingNodeSelector.jsx";
import {createKey, shortenAndFormatLabel, typeComponent} from "../../../utils/utils.js";
import {useNavigate} from "react-router";
import {useDebouncedCallback} from "use-debounce";
import {useQueryClient} from "@tanstack/react-query";
import dayjs from "dayjs";

const NestedFields = ({
                          fieldKey,
                          field,
                          rootId,
                          isRoot = false,
                          isToggle = false,
                      }) => {
    const { data: rawApiData, isLoading: loading, error, refetch } = useApiData(`class_attribute_field`, {
        node_id: typeof field.id === 'string' ? Number.parseInt(field.id) : field.id,
    }, {
        enabled: isToggle || isRoot,
    });

    const navigate = useNavigate();
    const queryClient = useQueryClient()

    const jumpToId = useApiMutation('jump');
    const addNode = useApiMutation('add_node');
    const listExistingNode = useApiMutation('list_existing_node');
    const addExistingNode = useApiMutation('add_existing_node');

    const [expandedFields, setExpandedFields] = useState({});
    const [currentToggleNodeId, setCurrentToggleNodeId] = useState(null);
    const [showExistingNodeCard, setShowExistingNodeCard] = useState(false);
    const [existingNodeList, setExistingNodeList] = useState([]);
    const [selectedField, setSelectedField] = useState(null);
    const [loadingExistingNodes, setLoadingExistingNodes] = useState(false);

    const stringifyData = useCallback((data, indent = 2) => {
        if (!data) return "";
        return JSON.stringify(data, null, indent === "tab" ? "\t" : indent);
    }, []);

    // Handle functions - memoized to prevent unnecessary re-renders

    const handleChangingForm = useCallback(async(name, id) => {
        if(id === rootId) return console.log("return from handleChangingForm");
        try{
            await jumpToId.mutateAsync({ node_id: id });
            await navigate(`/add-node/form/${name}`);
            window.location.reload();
        } catch (error) {
            console.error("Error changing form:", error);
        }
    }, [jumpToId, navigate, rootId]);

    const { id, type, name } = field;

    // Use a ref to store the expanded fields state to prevent unnecessary rerenders
    const expandedFieldsRef = useRef(expandedFields);

    // Update the ref whenever expandedFields changes
    useEffect(() => {
        expandedFieldsRef.current = expandedFields;
    }, [expandedFields]);

    const toggleField = useCallback(async(fieldName, nodeId) => {
        // Get the current expanded state from the ref
        const currentExpandedState = expandedFieldsRef.current[fieldName];

        // Only update the specific field that was toggled
        setExpandedFields(prev => {
            // Create a new object that only updates the specific field
            return {
                ...prev,
                [fieldName]: !currentExpandedState
            };
        });
    }, []);

    const handleAddNewNode = useCallback(async (field) => {
        const id = field?.id;
        const fullName = field?.listNodeType;

        setCurrentToggleNodeId(id);
        try {
            const data = await addNode.mutateAsync({
                BNodeClass: fullName,
                node_id: id
            }, {
                onMutate: async(variables) => {
                    await queryClient.cancelQueries({
                        queryKey: ['apiData', 'class_attribute_field', {
                            node_id: id
                        }]
                    })
                },
                onSuccess: async () => {
                    await queryClient.invalidateQueries({
                        queryKey: ['apiData', 'class_attribute_field', {
                            node_id: id
                        }]
                    })
                }
            });
        } catch (error) {console.error("Error adding new node:", error);}
    }, [jumpToId, addNode, rootId, createKey, stringifyData]);

    const handleListExistingNode = useCallback(async (field) => {
        setSelectedField(field);
        setShowExistingNodeCard(true);
        setLoadingExistingNodes(true);

        const shortName = field.listNodeType.split('.').pop();

        try {
            const data = await listExistingNode.mutateAsync({ type: shortName });
            const result = data?.data?.results?.[0]?.result?.data || [];
            setExistingNodeList(result);
        } catch (error) {
            console.error("Error fetching class attribute field:", error);
        } finally {
            setLoadingExistingNodes(false);
        }
    }, [jumpToId, listExistingNode]);


    const handleAddExistingNode = useCallback(async (node) => {
        try{
            await addExistingNode.mutateAsync({
                id: node.id,
                node_id: field.id,
            }, {
                onMutate: async(variables) => {
                    await queryClient.cancelQueries({
                        queryKey: ['apiData', 'class_attribute_field', {
                            node_id: field.id
                        }]
                    });
                },
                onSuccess: async () => {
                    await queryClient.invalidateQueries({
                        queryKey: ['apiData', 'class_attribute_field', {
                            node_id: field.id
                        }]
                    });
                    jumpToId.mutate({ node_id: rootId });
                }
            });
        } catch (error) { console.error("Error adding existing node:", error); }
    }, [addExistingNode, jumpToId, rootId])

    const handleSelectExistingNode = useCallback((node, selectedField) => {
        handleAddExistingNode(node);
        setShowExistingNodeCard(false);
    }, [createKey, handleAddExistingNode])

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
        }
    }, [loading, rawApiData]);




    // Memoize the renderFields function to prevent recreation on every render
    const renderFields = useCallback((parentId, fields, visited = new Set()) => {
        if (!fields || !Array.isArray(fields)) return null;

        return fields.map(subField => {
            const { id, name, type } = subField;
            if (!name || name == "graph") return null;
            const subFieldKey = createKey(subField.id, subField.name);

            if (visited.has(id)) {
                return <Typography key={`cycle-${id}`} color="error">Circular reference detected for {name}</Typography>;
            }

            const isFieldExpanded = expandedFields[subFieldKey] || false;

            return (
                <React.Fragment key={subFieldKey}>
                    <FormField
                        field={subField}
                        fieldKey={subFieldKey}
                        isExpanded={isFieldExpanded}
                        onToggleField={toggleField}
                        onChangingForm={handleChangingForm}
                        defaultValue={ subField.value }
                        parentId={parentId}
                    />

                    {!typeComponent.includes(type) && (
                        <NestedFields
                            fieldKey={subFieldKey}
                            field={subField}
                            rootId={rootId}
                            isToggle={isFieldExpanded}
                        />
                    )}
                </React.Fragment>
            );
        });
    }, [
        createKey,
        expandedFields,
        toggleField,
        handleChangingForm,
        shortenAndFormatLabel
    ]);

    const subfieldData = rawApiData?.data?.results?.[0]?.result?.data || []

    return (
        <React.Fragment>
            {
                isRoot ? (
                    <Box component="form" className="form-fields" onSubmit={e => e.preventDefault()} sx={{ mt: 3 }}>
                        {subfieldData.length > 0 ? (
                            renderFields(field.id, subfieldData)) : (<Typography>No fields available.</Typography>)}
                    </Box>
                ) : (
                    <Box className="nested-fields" sx={{ mt: 2, pl: 2, borderLeft: '1px solid #e0e0e0' }}>
                        {isToggle && (
                            subfieldData.length > 0 ? (
                                renderFields(field.id, subfieldData)) : (
                                <Typography variant="body2" color="text.secondary">No subfields available for this.</Typography>
                            )
                        )}

                        {["ListNode", "SetNode"].includes(type) && isToggle && (
                            <Stack direction="row" spacing={2} className="add-new-node" sx={{ mt: 2 }}>
                                <Button
                                    variant="outlined"
                                    startIcon={<AddIcon />}
                                    onClick={() => handleAddNewNode(field)}
                                >
                                    Add new {name}
                                </Button>
                                <Button
                                    variant="outlined"
                                    startIcon={<AddIcon />}
                                    onClick={() => handleListExistingNode(field)}
                                >
                                    Add {name} from Existing Node
                                </Button>
                            </Stack>
                        )}
                    </Box>
                )
            }
            <ExistingNodeSelector
                show={showExistingNodeCard}
                selectedField={selectedField}
                existingNodeList={existingNodeList}
                loadingExistingNodes={loadingExistingNodes}
                onClose={() => {
                    setShowExistingNodeCard(false);
                    jumpToId.mutate({ node_id: rootId });
                }}
                onSelectNode={handleSelectExistingNode}
            />
        </React.Fragment>
    );
};

export default React.memo(NestedFields, (prevProps, nextProps) => {
    return (
        prevProps.fieldKey === nextProps.fieldKey && 
        prevProps.rootId === nextProps.rootId &&
        prevProps.isToggle === nextProps.isToggle
    );
})
