import { Box, Button } from "@mui/material";
import { useCallback } from "react";

interface BNodeNavigatorDisplayProps {
	content: any;
	jumpToNode: (nodeId: number | string) => void;
}

export const BNodeNavigatorDisplay = ({
	content,
	jumpToNode,
}: BNodeNavigatorDisplayProps) => {
	const handleButtonClick = useCallback(
		(event: React.MouseEvent<HTMLButtonElement>, nodeId: number | string) => {
			event.stopPropagation();
			event.preventDefault();
			jumpToNode(nodeId);
		},
		[jumpToNode],
	);

	return (
		<>
			<Box sx={{ mb: 2 }}>
				{Object.keys(content.ins).map((inNode) => (
					<Button
						key={inNode}
						onClick={(e) => handleButtonClick(e, content.ins[inNode])}
						variant="contained"
						sx={{
							bgcolor: "#3949ab",
							color: "#fff",
							mr: 1,
							mb: 1,
							"&:hover": { bgcolor: "#5c6bc0" },
						}}
					>
						{inNode} ({content.ins[inNode]})
					</Button>
				))}
			</Box>
			<Box sx={{ paddingY: "10px" }}>
				{Object.keys(content.outs).map((outNode) => (
					<Button
						key={outNode}
						onClick={(e) => handleButtonClick(e, content.outs[outNode])}
						variant="contained"
						sx={{
							bgcolor: "#00897b",
							color: "#fff",
							mr: 1,
							mb: 1,
							"&:hover": { bgcolor: "#26a69a" },
						}}
					>
						{outNode} ({content.outs[outNode]})
					</Button>
				))}
			</Box>
		</>
	);
};
