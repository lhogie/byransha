import {
	useCallback,
	useMemo,
	type MouseEventHandler,
	Suspense,
	useTransition,
} from "react";
import {
	Box,
	IconButton,
	Modal,
	Tooltip,
	Typography,
	CircularProgress,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import CodeIcon from "@mui/icons-material/Code";
import { JsonView } from "react-json-view-lite";

const modalStyle = {
	position: "absolute",
	top: "50%",
	left: "50%",
	transform: "translate(-50%, -50%)",
	width: "80%",
	maxWidth: "800px",
	maxHeight: "80vh",
	bgcolor: "background.paper",
	border: "2px solid #000",
	boxShadow: 24,
	p: 4,
	display: "flex",
	flexDirection: "column",
	overflow: "hidden",
};

const modalHeaderStyle = {
	display: "flex",
	justifyContent: "space-between",
	alignItems: "center",
	mb: 2,
};

const modalContentStyle = {
	overflowY: "auto",
	maxHeight: "calc(80vh - 100px)",
	width: "100%",
	scrollbarWidth: "thin",
	scrollbarColor: "#888 #f1f1f1",
	"&::-webkit-scrollbar": {
		width: "8px",
		height: "8px",
	},
	"&::-webkit-scrollbar-track": {
		background: "#f1f1f1",
		borderRadius: "4px",
	},
	"&::-webkit-scrollbar-thumb": {
		background: "#888",
		borderRadius: "4px",
	},
	"&::-webkit-scrollbar-thumb:hover": {
		background: "#555",
	},
};

interface ModalComponentProps {
	dataForModal: any;
	isModalOpen: boolean;
	setIsModalOpen: (isOpen: boolean) => void;
}

export const ModalComponent = ({
	dataForModal,
	isModalOpen,
	setIsModalOpen,
}: ModalComponentProps) => {
	const [_isPending, startTransition] = useTransition();

	const handleOpenModal: MouseEventHandler<HTMLButtonElement> = useCallback(
		(event) => {
			event.stopPropagation();
			startTransition(() => {
				setIsModalOpen(true);
			});
		},
		[setIsModalOpen],
	);

	const handleCloseModal: MouseEventHandler<HTMLButtonElement> = useCallback(
		(event) => {
			event.stopPropagation();
			startTransition(() => {
				setIsModalOpen(false);
			});
		},
		[setIsModalOpen],
	);

	return (
		<>
			<Tooltip title="Show Raw Backend Response">
				<span>
					<IconButton
						onClick={handleOpenModal}
						size="small"
						sx={{
							position: "absolute",
							top: 5,
							right: 5,
							zIndex: 10,
							width: 30,
							height: 30,
							color: "primary.main",
						}}
						aria-label="Show raw JSON"
						disabled={dataForModal === null || dataForModal === undefined}
					>
						<CodeIcon />
					</IconButton>
				</span>
			</Tooltip>
			<Modal
				open={isModalOpen}
				onClose={handleCloseModal}
				aria-labelledby="raw-json-modal-title"
				aria-describedby="raw-json-modal-description"
			>
				<Box sx={modalStyle}>
					<Box sx={modalHeaderStyle}>
						<Typography id="raw-json-modal-title" variant="h6" component="h2">
							Raw Backend Response
						</Typography>
						<IconButton onClick={handleCloseModal} aria-label="close">
							<CloseIcon />
						</IconButton>
					</Box>
					<Box sx={modalContentStyle}>
						{dataForModal ? (
							<Suspense fallback={<CircularProgress />}>
								<JsonView data={dataForModal} />
							</Suspense>
						) : (
							<Typography>No raw data available to display.</Typography>
						)}
					</Box>
				</Box>
			</Modal>
		</>
	);
};
