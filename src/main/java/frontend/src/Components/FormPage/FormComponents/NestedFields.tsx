import React, { useCallback, useEffect, useRef, useState } from "react";
import { Box, Button, Stack, Typography } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import ExistingNodeSelector from "./ExistingNodeSelector";
import { createKey, listField } from "@/utils/utils";
import { useNavigate } from "react-router";
import { useQueryClient } from "@tanstack/react-query";
import FieldRenderer from "./FieldRenderer";

const NestedFields = ({
	fieldKey,
	field,
	rootId,
	isRoot = false,
	isToggle = false,
}: {
	fieldKey: string;
	field: any;
	rootId: number;
	isRoot?: boolean;
	isToggle?: boolean;
}) => {
	const {
		data: rawApiData,
		isLoading: loading,
		error,
		refetch,
	} = useApiData(
		`class_attribute_field`,
		{
			node_id:
				typeof field.id === "string" ? Number.parseInt(field.id) : field.id,
		},
		{
			enabled: isToggle || isRoot,
		},
	);

	const navigate = useNavigate();
	const queryClient = useQueryClient();

	const jumpToId = useApiMutation("jump");
	const addNode = useApiMutation("add_node");
	const listExistingNode = useApiMutation("list_existing_node");
	const addExistingNode = useApiMutation("add_existing_node");

	const [expandedFields, setExpandedFields] = useState<any>({});
	const [showExistingNodeCard, setShowExistingNodeCard] =
		useState<boolean>(false);
	const [existingNodeList, setExistingNodeList] = useState<any[]>([]);
	const [selectedField, setSelectedField] = useState<any>(null);
	const [loadingExistingNodes, setLoadingExistingNodes] =
		useState<boolean>(false);

	const _stringifyData = useCallback(
		(data: any, indent: string | number = 2) => {
			if (!data) return "";
			return JSON.stringify(data, null, indent === "tab" ? "\t" : indent);
		},
		[],
	);

	// Handle functions - memoized to prevent unnecessary re-renders

	const handleChangingForm = useCallback(
		async (_name: string, id: string) => {
			try {
				await jumpToId.mutateAsync({ node_id: id });
				await navigate(`/add-node/form/${id}`);
				window.location.reload();
			} catch (error) {
				console.error("Error changing form:", error);
			}
		},
		[jumpToId, navigate],
	);

	const { id, type, name } = field;

	// Use a ref to store the expanded fields state to prevent unnecessary rerenders
	const expandedFieldsRef = useRef(expandedFields);

	// Update the ref whenever expandedFields changes
	useEffect(() => {
		expandedFieldsRef.current = expandedFields;
	}, [expandedFields]);

	const toggleField = useCallback(
		async (fieldName: string, _nodeId: string) => {
			// Get the current expanded state from the ref
			const currentExpandedState = expandedFieldsRef.current[fieldName];

			// Only update the specific field that was toggled
			setExpandedFields((prev: any) => {
				// Create a new object that only updates the specific field
				return {
					...prev,
					[fieldName]: !currentExpandedState,
				};
			});
		},
		[],
	);

	const handleAddNewNode = useCallback(
		async (field: any) => {
			const id = field?.id;
			const fullName = field?.listNodeType;

			try {
				const _data = await addNode.mutateAsync(
					{
						BNodeClass: fullName,
						node_id: id,
					},
					{
						onSuccess: async () => {
							await queryClient.invalidateQueries({
								queryKey: [
									"apiData",
									"class_attribute_field",
									{
										node_id: id,
									},
								],
							});
						},
					},
				);
			} catch (error) {
				console.error("Error adding new node:", error);
			}
		},
		[addNode, queryClient.invalidateQueries],
	);

	const handleListExistingNode = useCallback(
		async (field: any) => {
			setSelectedField(field);
			setShowExistingNodeCard(true);
			setLoadingExistingNodes(true);

			const shortName = field.listNodeType.split(".").pop();

			try {
				const data = await listExistingNode.mutateAsync({ type: shortName });
				const result = data?.data?.results?.[0]?.result?.data || [];
				setExistingNodeList(result);
			} catch (error) {
				console.error("Error fetching class attribute field:", error);
			} finally {
				setLoadingExistingNodes(false);
			}
		},
		[listExistingNode],
	);

	const handleAddExistingNode = useCallback(
		async (node: any) => {
			try {
				await addExistingNode.mutateAsync(
					{
						id: node.id,
						node_id: field.id,
					},
					{
						onSuccess: async () => {
							await queryClient.invalidateQueries({
								queryKey: [
									"apiData",
									"class_attribute_field",
									{
										node_id: field.id,
									},
								],
							});
							jumpToId.mutate({ node_id: rootId });
						},
					},
				);
			} catch (error) {
				console.error("Error adding existing node:", error);
			}
		},
		[
			addExistingNode,
			jumpToId,
			rootId,
			field.id,
			queryClient.invalidateQueries,
		],
	);

	const handleSelectExistingNode = useCallback(
		(node: any, _selectedField: any) => {
			handleAddExistingNode(node);
			setShowExistingNodeCard(false);
		},
		[handleAddExistingNode],
	);

	useEffect(() => {
		if (!loading && rawApiData) {
			const initialValues: {
				[key: string]: any;
			} = {};
			const initialFieldData: {
				[key: string]: any;
			} = {};

			const allFields =
				rawApiData?.data?.results?.[0]?.result?.data?.attributes || [];

			allFields.forEach((field: any) => {
				if (field.name && field.value !== "null" && field.value !== undefined) {
					const fieldKey = createKey(field.id, field.name);
					initialValues[fieldKey] = field.value;
					initialFieldData[fieldKey] = field;
				}
			});
		}
	}, [loading, rawApiData]);

	const subfieldData =
		rawApiData?.data?.results?.[0]?.result?.data?.attributes || [];

	return (
		<React.Fragment>
			{isRoot ? (
				<Box
					component="form"
					className="form-fields"
					onSubmit={(e) => e.preventDefault()}
					sx={{ mt: 3 }}
				>
					{subfieldData.length > 0 ? (
						<FieldRenderer
							parentId={field.id}
							fields={subfieldData}
							expandedFields={expandedFields}
							toggleField={toggleField}
							handleChangingForm={handleChangingForm}
							rootId={rootId}
						/>
					) : (
						<Typography>No fields available.</Typography>
					)}
				</Box>
			) : (
				<Box
					className="nested-fields"
					sx={{ mt: 2, pl: 2, borderLeft: "1px solid #e0e0e0" }}
				>
					{isToggle &&
						(subfieldData.length > 0 ? (
							<FieldRenderer
								parentId={field.id}
								fields={subfieldData}
								expandedFields={expandedFields}
								toggleField={toggleField}
								handleChangingForm={handleChangingForm}
								rootId={rootId}
							/>
						) : (
							<Typography variant="body2" color="text.secondary">
								No subfields available for this.
							</Typography>
						))}

					{listField.includes(type) && isToggle && !field.isDropdown && (
						<Stack
							direction="row"
							spacing={2}
							className="add-new-node"
							sx={{ mt: 2 }}
						>
							{field.canAddNewNode ?? (
								<Button
									variant="outlined"
									startIcon={<AddIcon />}
									onClick={() => handleAddNewNode(field)}
								>
									Add new {name}
								</Button>
							)}

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
			)}
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
});
