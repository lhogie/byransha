import { useApiMutation, useInfiniteApiData } from "@hooks/useApiData";
import AddIcon from "@mui/icons-material/Add";
import { Box, Button, Stack, Typography } from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import React, { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router";
import { createKey, listField } from "@/utils/utils";
import ExistingNodeSelector from "./ExistingNodeSelector";
import FieldRenderer from "./FieldRenderer";

const NestedFields = ({
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
		fetchNextPage,
		hasNextPage,
		isFetchingNextPage,
		error,
		isError,
	} = useInfiniteApiData(
		`class_attribute_field`,
		{
			node_id:
				typeof field.id === "string" ? Number.parseInt(field.id) : field.id,
		},
		{
			enabled: isToggle || isRoot,
		},
	);

	console.log(hasNextPage);

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

	const { type, name } = field;

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
									"infinite",
									"apiData",
									"class_attribute_field",
									{
										node_id: id,
									},
								],
							});

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
		[addNode, queryClient],
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
									"infinite",
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
		[addExistingNode, jumpToId, rootId, field.id, queryClient],
	);

	const handleSelectExistingNode = useCallback(
		(node: any, _selectedField: any) => {
			handleAddExistingNode(node);
			setShowExistingNodeCard(false);
		},
		[handleAddExistingNode],
	);

	// Flatten paginated data from infinite query
	const subfieldData = React.useMemo(() => {
		if (!rawApiData?.pages || rawApiData.pages.length === 0) return [];

		return rawApiData.pages.reduce((acc: any[], page: any) => {
			try {
				// Handle the response structure with attributes array
				const pageData =
					page?.data?.results?.[0]?.result?.data?.attributes || [];
				acc.push(...pageData);
				return acc;
			} catch (error) {
				console.warn("Error extracting page data:", error);
				return acc;
			}
		}, []);
	}, [rawApiData]);

	useEffect(() => {
		if (!loading && rawApiData?.pages && subfieldData.length > 0) {
			const initialValues: {
				[key: string]: any;
			} = {};
			const initialFieldData: {
				[key: string]: any;
			} = {};

			subfieldData.forEach((field: any) => {
				if (field.name && field.value !== "null" && field.value !== undefined) {
					const fieldKey = createKey(field.id, field.name);
					initialValues[fieldKey] = field.value;
					initialFieldData[fieldKey] = field;
				}
			});
		}
	}, [loading, rawApiData?.pages, subfieldData]);

	return (
		<React.Fragment>
			{isRoot ? (
				<Box
					component="form"
					className="form-fields"
					onSubmit={(e) => e.preventDefault()}
					sx={{ mt: 3 }}
				>
					{isError ? (
						<Typography color="error">
							Error loading fields: {error?.message || "Unknown error"}
						</Typography>
					) : loading && subfieldData.length === 0 ? (
						<Typography>Loading fields...</Typography>
					) : subfieldData.length > 0 ? (
						<>
							<FieldRenderer
								parentId={field.id}
								fields={subfieldData}
								expandedFields={expandedFields}
								toggleField={toggleField}
								handleChangingForm={handleChangingForm}
								rootId={rootId}
							/>
							{hasNextPage && (
								<Box sx={{ mt: 2, textAlign: "center" }}>
									<Button
										variant="outlined"
										onClick={() => fetchNextPage()}
										disabled={isFetchingNextPage}
										size="small"
									>
										{isFetchingNextPage ? "Loading..." : "Load More Fields"}
									</Button>
								</Box>
							)}
						</>
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
						(isError ? (
							<Typography variant="body2" color="error">
								Error loading subfields: {error?.message || "Unknown error"}
							</Typography>
						) : loading && subfieldData.length === 0 ? (
							<Typography variant="body2" color="text.secondary">
								Loading subfields...
							</Typography>
						) : subfieldData.length > 0 ? (
							<>
								<FieldRenderer
									parentId={field.id}
									fields={subfieldData}
									expandedFields={expandedFields}
									toggleField={toggleField}
									handleChangingForm={handleChangingForm}
									rootId={rootId}
								/>
								{hasNextPage && (
									<Box sx={{ mt: 1, textAlign: "center" }}>
										<Button
											variant="outlined"
											size="small"
											onClick={() => fetchNextPage()}
											disabled={isFetchingNextPage}
											sx={{ fontSize: "0.75rem" }}
										>
											{isFetchingNextPage ? "Loading..." : "Load More"}
										</Button>
									</Box>
								)}
							</>
						) : (
							<Typography variant="body2" color="text.secondary">
								No subfields available for this.
							</Typography>
						))}

					{listField.includes(type) && isToggle && field.canAddNewNode && (
						<Stack
							direction="row"
							spacing={2}
							className="add-new-node"
							sx={{ mt: 2 }}
						>
							{field.allowCreation ?? (
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
		prevProps.isToggle === nextProps.isToggle &&
		prevProps.field?.id === nextProps.field?.id
	);
});
