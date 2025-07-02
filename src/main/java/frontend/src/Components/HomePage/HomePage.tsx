import React, {
	useState,
	memo,
	type MouseEventHandler,
	useCallback,
} from "react";
import "./HomePage.css";
import { useNavigate } from "react-router";
import {
	Box,
	Button,
	Card,
	CardContent,
	CircularProgress,
	Typography,
	Checkbox,
	ListItemText,
	Menu,
	MenuItem,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";
import CloseIcon from "@mui/icons-material/Close";
import Expand from "@mui/icons-material/AspectRatio";
import { useTitle } from "@global/useTitle";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import { View } from "@common/View";

const ViewCard = memo(
	({
		view,
		onClick,
		handleViewToggle,
	}: {
		view: any;
		onClick: MouseEventHandler<any>;
		handleViewToggle: (endpoint: string) => void;
	}) => {
		return (
			<Card
				sx={{
					cursor: "pointer",
					aspectRatio: "1",
					border: "1px solid #e0e0e0",
					borderRadius: 2,
					display: "flex",
					flexDirection: "column",
					bgcolor: view.response_type === "technical" ? "#fff9c4" : "#ffffff",
				}}
			>
				<Box
					sx={{
						height: "40px",
						width: "100%",
						bgcolor: view.response_type === "technical" ? "#fff8b0" : "#f5f5f5",
						borderBottom: "1px solid #e0e0e0",
						display: "flex",
						alignItems: "center",
						justifyContent: "center",
						cursor: "grab",
					}}
				>
					<Typography
						className="DragHere"
						variant="caption"
						sx={{ color: "#757575" }}
					>
						{`${view.endpoint.replace(/_/g, " ").replace(/(?:^|\s)\S/g, (match: any) => match.toUpperCase())} - ${view.what_is_this}`}
					</Typography>
					<button
						type="button"
						className="expand-card"
						onClick={onClick}
						aria-label="expand"
					>
						{" "}
						<Expand />{" "}
					</button>
					<button
						type="button"
						className="erased-card"
						onClick={(_e) => {
							handleViewToggle(view.endpoint);
						}}
					>
						{" "}
						<CloseIcon />{" "}
					</button>
				</Box>
				<CardContent
					sx={{
						padding: { xs: "12px", sm: "16px" },
						height: "calc(100% - 40px)",
						display: "flex",
						flexDirection: "column",
						overflow: "hidden",
						bgcolor: view.response_type === "technical" ? "#fff9c4" : "#ffffff",
					}}
				>
					<Box
						sx={{
							flex: 1,
							overflow: "auto",
							color: "#424242",
							msOverflowStyle: "none",
							scrollbarWidth: "thin",
							scrollbarColor: "#3f51b5 #e8eaf6",
							"&::-webkit-scrollbar": { width: "6px" },
							"&::-webkit-scrollbar-thumb": {
								bgcolor: "#3f51b5",
								borderRadius: "3px",
							},
							"&::-webkit-scrollbar-track": { bgcolor: "#e8eaf6" },
							wordBreak: "break-word",
							overflowWrap: "break-word",
							whiteSpace: "pre-wrap",
							maxWidth: "100%",
							fontSize: { xs: "0.75rem", sm: "0.875rem" },
						}}
					>
						{view.error ? (
							view.error
						) : (
							<React.Suspense fallback={<div>Loading view...</div>}>
								<View
									viewId={view.endpoint.replaceAll(" ", "_")}
									sx={{
										bgcolor:
											view.response_type === "technical"
												? "#fff9c4"
												: "#ffffff",
										width: "100%",
									}}
								/>
							</React.Suspense>
						)}
					</Box>
				</CardContent>
			</Card>
		);
	},
);

const HomePage = () => {
	const navigate = useNavigate();
	const { data, isLoading } = useApiData("");
	useTitle("Home");

	const [views, setViews] = useState<any[]>([]);
	const [columns, setColumns] = useState<number>(2);
	const [selectMenuAnchor, setSelectMenuAnchor] =
		useState<HTMLButtonElement | null>(null);
	const [selectedViews, setSelectedViews] = useState<string[]>([]);
	const [showTechnicalViews, setShowTechnicalViews] = useState<boolean>(() => {
		const saved = localStorage.getItem("showTechnicalViews");
		return saved ? JSON.parse(saved) : false;
	});

	const [_showColorPicker, _setShowColorPicker] = useState<boolean>(false);
	const [_pickerColor, _setPickerColor] = useState<string>("#ffffff");
	const visibleViews = views.filter((view) =>
		selectedViews.includes(view.endpoint),
	);
	const rowColumns = Math.min(columns, visibleViews.length);

	const jumpToId = useApiMutation("jump");

	const getAutoColumnCount = useCallback(() => {
		const width = window.innerWidth;

		if (width < 900) return 1;
		else if (width < 1600) return 2;
		else if (width < 2100) return 3;
		return 4;
	}, []);

	React.useEffect(() => {
		if (data?.data?.results) {
			const filteredViews = showTechnicalViews
				? data.data.results
				: data.data.results.filter(
						(view) => view.response_type !== "technical",
					);
			setViews(filteredViews);
			setSelectedViews(() => {
				const saved = JSON.parse(
					localStorage.getItem("selectedViewsSaved") as string,
				);
				let newSelected: any;
				if (!saved || saved.length === 0) {
					newSelected = filteredViews.map((view) => view.endpoint);
				} else {
					newSelected = saved.filter((endpoint: any) =>
						filteredViews.some((view) => view.endpoint === endpoint),
					);
				}
				localStorage.setItem("selectedViewsSaved", JSON.stringify(newSelected));
				return newSelected;
			});

			const savedOrder =
				JSON.parse(localStorage.getItem("viewOrder") as string) || [];
			let orderedViews = filteredViews;

			if (savedOrder.length > 0) {
				orderedViews = [...filteredViews].sort((a, b) => {
					const indexA = savedOrder.indexOf(a.endpoint);
					const indexB = savedOrder.indexOf(b.endpoint);
					return (
						(indexA === -1 ? Infinity : indexA) -
						(indexB === -1 ? Infinity : indexB)
					);
				});
			}

			setViews(orderedViews);
		}
	}, [data, showTechnicalViews]);

	React.useEffect(() => {
		const handleResize = () => setColumns(getAutoColumnCount());

		window.addEventListener("resize", handleResize);
		handleResize();

		return () => window.removeEventListener("resize", handleResize);
	}, [getAutoColumnCount]);

	if (isLoading) {
		return (
			<Box
				sx={{
					display: "flex",
					justifyContent: "center",
					alignItems: "center",
					height: "100vh",
					bgcolor: "#2e3b55",
				}}
			>
				<CircularProgress sx={{ color: "#1e88e5" }} />
			</Box>
		);
	}

	if (!data || !data.data || !data.data.results) {
		return (
			<Box
				sx={{
					bgcolor: "#fff3e0",
					p: 2,
					borderRadius: 2,
					color: "#ef6c00",
					textAlign: "center",
				}}
			>
				Error: Data is null.
			</Box>
		);
	}

	const handleSelectMenuOpen: MouseEventHandler<HTMLButtonElement> = (event) =>
		setSelectMenuAnchor(event.currentTarget);
	const handleSelectMenuClose: MouseEventHandler<HTMLButtonElement> = () =>
		setSelectMenuAnchor(null);

	const handleViewToggle = (endpoint: string) => {
		setSelectedViews((prev) => {
			const newSelected = prev.includes(endpoint)
				? prev.filter((id) => id !== endpoint)
				: [...prev, endpoint];

			localStorage.setItem("selectedViewsSaved", JSON.stringify(newSelected));

			const technicalViews = views.filter(
				(view) => view.response_type === "technical",
			);
			const openTechnicalViews = technicalViews.filter((view) =>
				newSelected.includes(view.endpoint),
			);
			if (openTechnicalViews.length === 0 && showTechnicalViews) {
				setShowTechnicalViews(false);
				localStorage.setItem("showTechnicalViews", JSON.stringify(false));
			}
			return newSelected;
		});
	};

	const handleTechnicalViewsToggle = () => {
		setShowTechnicalViews((prev) => {
			const newValue = !prev;
			localStorage.setItem("showTechnicalViews", JSON.stringify(newValue));

			const techViews =
				data?.data?.results?.filter(
					(view) => view.response_type === "technical",
				) || [];
			const techEndpoints = techViews.map((view) => view.endpoint);

			setSelectedViews((prevSelected) => {
				let updated: any;
				if (newValue) {
					updated = [...new Set([...prevSelected, ...techEndpoints])];
				} else {
					updated = prevSelected.filter(
						(endpoint) => !techEndpoints.includes(endpoint),
					);
				}
				localStorage.setItem("selectedViewsSaved", JSON.stringify(updated));
				return updated;
			});

			return newValue;
		});
	};

	const incrementColumns = () =>
		setColumns((prev) => Math.min(prev + 1, views.length));
	const decrementColumns = () => setColumns((prev) => Math.max(prev - 1, 1));

	return (
		<Box
			sx={{
				padding: { xs: "8px", sm: "16px", md: "40px" },
				maxWidth: "100%",
				margin: "0 auto",
				bgcolor: "#2e3b55",
				minHeight: "100vh",
				zIndex: 1,
			}}
			className="home-page"
		>
			<Box
				sx={{
					display: "flex",
					flexDirection: { xs: "column", sm: "row" },
					justifyContent: "space-between",
					alignItems: { xs: "flex-start", sm: "center" },
					mb: { xs: 2, sm: 4 },
					gap: { xs: 2, sm: 0 },
				}}
			>
				<Box
					sx={{
						display: "flex",
						flexDirection: { xs: "column", sm: "row" },
						gap: { xs: 1, sm: 2 },
						alignItems: { xs: "flex-start", sm: "center" },
						width: { xs: "100%", sm: "auto" },
					}}
				>
					<Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
						<Button
							variant="outlined"
							onClick={handleSelectMenuOpen}
							sx={{
								minWidth: { xs: 36, sm: 40 },
								borderWidth: "2px",
								borderColor: "#90caf9",
								color: "#90caf9",
								fontSize: { xs: "0.75rem", sm: "0.875rem" },
								padding: { xs: "4px 8px", sm: "6px 12px" },
								"&:hover": {
									borderColor: "#42a5f5",
									bgcolor: "#37474f",
								},
							}}
						>
							Views
						</Button>
						<Menu
							anchorEl={selectMenuAnchor}
							open={Boolean(selectMenuAnchor)}
							onClose={handleSelectMenuClose}
							PaperProps={{
								sx: {
									maxHeight: 500,
									maxWidth: 400,
									overflowY: "auto",
									width: "auto",
									padding: 1,
									borderRadius: "8px",
								},
							}}
						>
							{views.map((view, _index) => (
								<MenuItem
									key={view.id}
									sx={{
										display: "flex",
										alignItems: "center",
										justifyContent: "space-between",
										paddingRight: 1,
										fontSize: "14px",
										color:
											view.response_type === "technical"
												? "#283593"
												: "#424242",
										backgroundColor:
											view.response_type === "technical"
												? "#fff9c4"
												: "transparent",
										borderRadius: "8px",
										"&:hover": {
											backgroundColor:
												view.response_type === "technical"
													? "#fff8b0"
													: "#e8eaf6",
										},
									}}
								>
									<Box
										sx={{
											display: "flex",
											alignItems: "center",
											flexGrow: 1,
											borderRadius: "18px",
										}}
										onClick={() => handleViewToggle(view.endpoint)}
									>
										<Checkbox
											checked={selectedViews.includes(view.endpoint)}
											sx={{
												color: "#90caf9",
												"&.Mui-checked": { color: "#90caf9" },
											}}
										/>
										<ListItemText primary={view.pretty_name} />
									</Box>
									{/*
                                    <Box sx={{ cursor: 'grab', ml: 1, display: 'flex', alignItems: 'center' }}>
                                        <DragIndicatorIcon fontSize="small" sx={{ color: '#90caf9' }} />
                                    </Box>
                                    */}
								</MenuItem>
							))}
						</Menu>
					</Box>
					<Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
						<Checkbox
							checked={showTechnicalViews}
							onChange={handleTechnicalViewsToggle}
							sx={{
								color: "#90caf9",
								"&.Mui-checked": { color: "#90caf9" },
								padding: { xs: "4px", sm: "6px" },
							}}
						/>
						<Typography
							sx={{
								color: "#90caf9",
								fontSize: { xs: "0.75rem", sm: "0.875rem" },
							}}
						>
							Show Technical Views
						</Typography>
					</Box>
					<Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
						<Button
							variant="outlined"
							onClick={decrementColumns}
							disabled={columns === 1}
							sx={{
								minWidth: { xs: 36, sm: 40 },
								borderWidth: "2px",
								borderColor: "#90caf9",
								color: "#90caf9",
								fontSize: { xs: "0.75rem", sm: "0.875rem" },
								padding: { xs: "4px", sm: "6px" },
								"&:hover": {
									borderColor: "#42a5f5",
									bgcolor: "#37474f",
								},
							}}
						>
							<RemoveIcon fontSize="small" />
						</Button>
						<Typography
							sx={{
								color: "#ffffff",
								fontWeight: "bold",
								fontSize: { xs: "0.875rem", sm: "1rem" },
							}}
						>
							{columns}
						</Typography>
						<Button
							variant="outlined"
							onClick={incrementColumns}
							disabled={columns === views.length}
							sx={{
								minWidth: { xs: 36, sm: 40 },
								borderWidth: "2px",
								borderColor: "#90caf9",
								color: "#90caf9",
								fontSize: { xs: "0.75rem", sm: "0.875rem" },
								padding: { xs: "4px", sm: "6px" },
								"&:hover": {
									borderColor: "#42a5f5",
									bgcolor: "#37474f",
								},
							}}
						>
							<AddIcon fontSize="small" />
						</Button>
					</Box>
				</Box>
				<Button
					variant="outlined"
					onClick={() => navigate("/add-node")}
					sx={{
						minWidth: { xs: 36, sm: 40 },
						borderWidth: "2px",
						borderColor: "#90caf9",
						color: "#90caf9",
						fontSize: { xs: "0.75rem", sm: "0.875rem" },
						padding: { xs: "4px 8px", sm: "6px 12px" },
						"&:hover": {
							borderColor: "#42a5f5",
							bgcolor: "#37474f",
						},
					}}
				>
					Add new node
				</Button>
			</Box>

			<Box
				sx={{
					display: "flex",
					flexWrap: "wrap",
					gap: { xs: 2, sm: 4 },
				}}
			>
				{views
					.filter((view) => selectedViews.includes(view.endpoint))
					.map((view, _index) => (
						<Box
							key={view.index}
							sx={{
								width: {
									xs: "100%",
									sm: `calc(${100 / Math.min(columns, 2)}% - 16px)`,
									md: `calc(${100 / rowColumns}% - 32px)`,
								},
							}}
						>
							<ViewCard
								view={view}
								onClick={(e) => {
									if (e.defaultPrevented) return;
									if (view.endpoint.endsWith("class_attribute_field")) {
										jumpToId.mutate({
											node: view.result?.dialect.split("@")[1],
										});
										navigate(
											`/add-node/form/${view.result?.dialect.split("@")[0]}`,
										);
									} else
										navigate(
											`/information/${view.endpoint.replaceAll(" ", "_")}`,
										);
								}}
								handleViewToggle={handleViewToggle}
							/>
						</Box>
					))}
			</Box>
		</Box>
	);
};
export default HomePage;
