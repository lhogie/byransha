import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import {
	Box,
	Card,
	CardContent,
	CardHeader,
	CircularProgress,
	IconButton,
	Modal,
	Stack,
	TextField,
	Typography,
} from "@mui/material";
import React, { useCallback, useMemo, useState } from "react";

export type ExistingNodeSelectorProps = {
	show: boolean;
	selectedField: any;
	existingNodeList: any[];
	loadingExistingNodes: boolean;
	onClose: () => void;
	onSelectNode: (node: any, selectedField: any) => void;
};

const ExistingNodeSelector = ({
	show,
	selectedField,
	existingNodeList,
	loadingExistingNodes,
	onClose,
	onSelectNode,
}: ExistingNodeSelectorProps) => {
	const [searchQuery, setSearchQuery] = useState("");

	// Memoize handlers to prevent recreating functions on each render
	const handleSearchChange = useCallback((e: any) => {
		setSearchQuery(e.target.value);
	}, []);

	const handleClose = useCallback(() => {
		onClose();
	}, [onClose]);

	// Memoize the filtered list to avoid recalculation on every render
	const filteredNodeList = useMemo(() => {
		return existingNodeList.filter((node) =>
			node.name.toLowerCase().includes(searchQuery.toLowerCase()),
		);
	}, [existingNodeList, searchQuery]);

	// Create a memoized handler for node selection
	const handleSelectNode = useCallback(
		(node: any) => {
			onSelectNode(node, selectedField);
		},
		[onSelectNode, selectedField],
	);

	return (
		<Modal
			open={show}
			onClose={handleClose}
			aria-labelledby="existing-node-modal-title"
			aria-describedby="existing-node-modal-description"
		>
			<Box
				sx={{
					position: "absolute",
					top: "50%",
					left: "50%",
					transform: "translate(-50%, -50%)",
					width: { xs: "95vw", sm: "90%", md: "80%" },
					maxWidth: { xs: "none", sm: 600 },
					maxHeight: "90vh",
					bgcolor: "background.paper",
					boxShadow: 24,
					outline: "none",
				}}
			>
				<Card
					className="existing-node-card"
					sx={{
						width: "100%",
						maxHeight: { xs: "85vh", sm: "80vh" },
						overflow: "auto",
						borderRadius: { xs: 1, sm: 2 },
					}}
				>
					<CardHeader
						id="existing-node-modal-title"
						title="Select Existing Node"
						action={
							<IconButton onClick={handleClose} aria-label="close">
								<CloseRoundedIcon />
							</IconButton>
						}
					/>
					<CardContent>
						<TextField
							fullWidth
							variant="outlined"
							placeholder="Search nodes..."
							value={searchQuery}
							onChange={handleSearchChange}
							sx={{ mb: 2 }}
						/>

						<Box
							className="node-list"
							sx={{ maxHeight: "50vh", overflow: "auto" }}
						>
							{loadingExistingNodes ? (
								<Box display="flex" justifyContent="center" p={2}>
									<CircularProgress size={24} />{" "}
									<Typography sx={{ ml: 1 }}>Loading nodes...</Typography>
								</Box>
							) : (
								<Stack spacing={1}>
									{filteredNodeList.map((node) => (
										<Box
											key={node.id}
											sx={{
												p: 2,
												cursor: "pointer",
												"&:hover": { backgroundColor: "action.hover" },
											}}
											onClick={() => handleSelectNode(node)}
										>
											<Typography fontWeight="bold">{node.name}</Typography>
										</Box>
									))}
								</Stack>
							)}
						</Box>
					</CardContent>
				</Card>
			</Box>
		</Modal>
	);
};

// Wrap with React.memo to prevent unnecessary re-renders
export default React.memo(ExistingNodeSelector);
