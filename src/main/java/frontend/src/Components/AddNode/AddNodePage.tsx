import { useTitle } from "@global/useTitle";
import { useApiData, useApiMutation } from "@hooks/useApiData";
import CloseIcon from "@mui/icons-material/Close";
import ReloadIcon from "@mui/icons-material/Refresh";
import SearchIcon from "@mui/icons-material/Search";
import StarIcon from "@mui/icons-material/Star";
import StarBorderIcon from "@mui/icons-material/StarBorder";
import {
	Box,
	Card,
	CardContent,
	Container,
	Fade,
	Grid,
	IconButton,
	InputAdornment,
	Paper,
	TextField,
	Typography,
} from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router";

const AddNodePage = () => {
	useTitle(`Add node`);

	const navigate = useNavigate();
	const { data: rawApiData, refetch } = useApiData("class_distribution");
	const queryClient = useQueryClient();

	const [className, setClassName] = useState<string[]>([]);
	const [fullClassName, setFullClassName] = useState<string[]>([]);
	const [persistingClasses, setPersistingClasses] = useState<Set<string>>(
		new Set(),
	);
	const [searchTerm, setSearchTerm] = useState<string>("");
	const [exitAnim, setExitAnim] = useState<boolean>(false);
	const [favorites, setFavorites] = useState<string[]>(() => {
		try {
			const stored = localStorage.getItem("favorites");
			if (stored != null) {
				const parsed = JSON.parse(stored);
				return Array.isArray(parsed) ? parsed : [];
			} else {
				return [];
			}
		} catch {
			return [];
		}
	});

	const handleClose = () => {
		setExitAnim(true);
		setTimeout(() => navigate("/home"), 300);
	};

	const jumpMutation = useApiMutation("jump", {
		onSuccess: async () => {
			await queryClient.invalidateQueries();
		},
	});

	const addNodeMutation = useApiMutation("add_node");
	const classInformationMutation = useApiMutation("class_information");

	const handleCreateAndJump = async (name: string) => {
		const fullName = fullClassName.find((item) => item.endsWith(name));
		if (!fullName) return;
		try {
			const response = await addNodeMutation.mutateAsync({
				BNodeClass: fullName,
			});

			const data = response?.data?.results?.[0]?.result?.data.id;

			await jumpMutation.mutateAsync({ node_id: data });

			return data;
		} catch (err) {
			console.error(`Error during handleCreateAndJump for ${fullName}:`, err);
			throw err;
		}
	};

	const handleClickClass = async (name: string) => {
		try {
			const data = await handleCreateAndJump(name);
			navigate(`/add-node/form/${data}`);
		} catch (err) {
			console.error("Navigation skipped due to error:", err);
		}
	};

	const toggleFavorite = (name: string) => {
		setFavorites((prev) =>
			prev.includes(name) ? prev.filter((n) => n !== name) : [...prev, name],
		);
	};

	useEffect(() => {
		localStorage.setItem("favorites", JSON.stringify(favorites));
	}, [favorites]);

	useEffect(() => {
		if (!rawApiData) return;
		try {
			const classList = rawApiData?.data?.results?.[0]?.result?.data || [];

			const filteredList = classList.filter((item: any) => {
				const fullName = Object.keys(item)[0];
				return fullName;
			});

			const shortName = filteredList.map((item: any) => {
				const fullName = Object.keys(item)[0];
				return fullName.split(".").pop();
			});

			const fullName = filteredList.map((item: any) => {
				return Object.keys(item)[0];
			});

			setClassName(shortName);
			setFullClassName(fullName);
		} catch (err) {
			console.error("Failed to parse class names:", err);
		}
	}, [rawApiData]);

	const fetchClassInfo = useCallback(
		async (fullName: string) => {
			const cacheKey = `persisting:${fullName}`;
			const cached = localStorage.getItem(cacheKey);
			if (cached !== null) {
				return cached === "true";
			}

			try {
				const response = await classInformationMutation.mutateAsync({
					classForm: fullName,
				});
				const data = response?.data?.results?.[0]?.result?.data;

				return data?.BusinessNode !== undefined;
			} catch (err) {
				console.error(`Error fetching info for ${fullName}:`, err);
				return false;
			}
		},
		[classInformationMutation.mutateAsync],
	);

	useEffect(() => {
		if (!fullClassName || fullClassName.length === 0) return;

		const checkPersistingNodes = async () => {
			const persistingSet = new Set<string>();

			await Promise.all(
				fullClassName.map(async (name) => {
					const hasPersisting = await fetchClassInfo(name);
					if (hasPersisting) {
						persistingSet.add(name);
					}
				}),
			);

			setPersistingClasses(persistingSet);
		};

		checkPersistingNodes();
	}, [fullClassName, fetchClassInfo]);

	return (
		<Fade in={!exitAnim} timeout={300}>
			<Container
				component={Paper}
				elevation={3}
				sx={{
					p: 4,
					position: "relative",
					bgcolor: "#f8f9fa",
					maxWidth: "100%",
					width: "100%",
					minHeight: "80vh",
					overflow: "hidden",
				}}
			>
				<Typography
					variant="h3"
					component="h1"
					gutterBottom
					sx={{
						color: "#2c3e50",
						textAlign: "center",
						fontWeight: 600,
						pb: 2,
						borderBottom: "3px solid #3498db",
					}}
				>
					Add a new node
				</Typography>

				<Box
					sx={{
						display: "flex",
						justifyContent: "center",
						mb: 3,
						width: "100%",
					}}
				>
					<TextField
						fullWidth
						variant="outlined"
						placeholder="Search class name..."
						value={searchTerm}
						onChange={(e) => setSearchTerm(e.target.value)}
						sx={{ maxWidth: { xs: "100%", sm: 400 } }}
						InputProps={{
							startAdornment: (
								<InputAdornment position="start">
									<SearchIcon />
								</InputAdornment>
							),
						}}
					/>
				</Box>

				{favorites.length > 0 && (
					<>
						<Typography
							variant="h4"
							component="h2"
							sx={{
								color: "#34495e",
								my: 3,
								fontWeight: 500,
							}}
						>
							Favorites (Persistent Only)
						</Typography>
						<Grid
							container
							spacing={2}
							sx={{ mt: 2, justifyContent: "center" }}
						>
							{favorites
								.map((name) => {
									const fullName = fullClassName.find((f) => f.endsWith(name));
									return { short: name, full: fullName };
								})
								.filter(({ full }) => full && persistingClasses.has(full))
								.filter(({ short }) =>
									short.toLowerCase().includes(searchTerm.toLowerCase()),
								)
								.map(({ short }) => (
									<Grid key={short}>
										<Card
											onClick={() => handleClickClass(short)}
											sx={{
												minWidth: { xs: 80, sm: 120 },
												cursor: "pointer",
												transition: "all 0.2s ease",
												"&:hover": {
													bgcolor: "#f0f8ff",
													transform: "translateY(-2px)",
													boxShadow: 3,
												},
											}}
										>
											<CardContent
												sx={{
													display: "flex",
													alignItems: "center",
													justifyContent: "flex-start",
													p: 2,
													"&:last-child": { pb: 2 },
												}}
											>
												<Box
													onClick={(e) => {
														e.stopPropagation();
														toggleFavorite(short);
													}}
													sx={{
														mr: 1,
														display: "inline-flex",
														cursor: "pointer",
													}}
												>
													<StarIcon sx={{ color: "#f1c40f" }} />
												</Box>
												<Box>{short}</Box>
											</CardContent>
										</Card>
									</Grid>
								))}
						</Grid>
					</>
				)}

				<Typography
					variant="h4"
					component="h2"
					sx={{
						color: "#34495e",
						my: 3,
						fontWeight: 500,
					}}
				>
					All persisting classes
				</Typography>
				<Grid container spacing={2} sx={{ mt: 2, justifyContent: "center" }}>
					{className
						.map((name, index) => ({
							short: name,
							full: fullClassName[index],
						}))
						.filter(({ full }) => persistingClasses.has(full))
						.filter(({ short }) =>
							short.toLowerCase().includes(searchTerm.toLowerCase()),
						)
						.map(({ short }) => (
							<Grid key={short}>
								<Card
									sx={{
										minWidth: { xs: 80, sm: 120 },
										cursor: "pointer",
										transition: "all 0.2s ease",
										"&:hover": {
											bgcolor: "#f0f8ff",
											transform: "translateY(-2px)",
											boxShadow: 3,
										},
									}}
								>
									<CardContent
										onClick={() => handleClickClass(short)}
										sx={{
											display: "flex",
											alignItems: "center",
											justifyContent: "flex-start",
											p: 2,
											"&:last-child": { pb: 2 },
										}}
									>
										<Box
											onClick={(e) => {
												e.stopPropagation();
												toggleFavorite(short);
											}}
											sx={{
												mr: 1,
												display: "inline-flex",
												cursor: "pointer",
											}}
										>
											{favorites.includes(short) ? (
												<StarIcon sx={{ color: "#f1c40f" }} />
											) : (
												<StarBorderIcon sx={{ color: "#ccc" }} />
											)}
										</Box>
										<Box>{short}</Box>
									</CardContent>
								</Card>
							</Grid>
						))}
				</Grid>

				<Box
					sx={{
						position: "absolute",
						top: 10,
						right: 10,
						display: "flex",
						gap: 1,
						zIndex: 1000,
					}}
				>
					<IconButton
						onClick={() => {
							Object.keys(localStorage)
								.filter((key) => key.startsWith("persisting:"))
								.forEach((key) => localStorage.removeItem(key));
							setPersistingClasses(new Set());
							refetch();
						}}
						aria-label="reload"
						title="Reload all classes"
						sx={{
							"&:hover": {
								color: "#3498db",
							},
						}}
					>
						<ReloadIcon />
					</IconButton>
					<IconButton
						onClick={handleClose}
						aria-label="close"
						title="Close"
						sx={{
							"&:hover": {
								color: "#e74c3c",
							},
						}}
					>
						<CloseIcon />
					</IconButton>
				</Box>
			</Container>
		</Fade>
	);
};

export default AddNodePage;
