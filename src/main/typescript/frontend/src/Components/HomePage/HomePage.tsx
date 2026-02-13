import {
	AccountTree as GraphIcon,
	ArrowBack as ArrowBackIcon,
	ArrowForward as ArrowForwardIcon,
	Search as SearchIcon,
	Add as AddIcon,
	Send as SendIcon,
} from "@mui/icons-material";
import {
	Box,
	Card,
	CardContent,
	Container,
	Grid,
	Paper,
	Typography,
	Button,
	Stack,
	TextField,
	Alert,
	Chip,
} from "@mui/material";
import { useNavigate } from "react-router";
import { useState } from "react";
import { useWebSocket } from "@hooks/useWebSocket";

/**
 * HomePage - Notre page d'accueil
 * 
 */
const HomePage = () => {
	const navigate = useNavigate();

	// ============================================================================
	// TEST WEBSOCKET (ZONE DE DÉMO)
	// ============================================================================
	const [messages, setMessages] = useState<string[]>([]);
	const [inputMessage, setInputMessage] = useState("");

	// Utilisation du hook WebSocket - Serveur d'echo public
	const { sendMessage, isConnected } = useWebSocket<string>(
		"echo-test",
		(data) => {
			// Callback appelé quand un message arrive
			setMessages((prev) => [...prev, ` Reçu: ${data}`]);
		}
	);

	const handleSendTest = () => {
		if (inputMessage.trim()) {
			sendMessage(inputMessage);
			setMessages((prev) => [...prev, ` Envoyé: ${inputMessage}`]);
			setInputMessage("");
		}
	};

	// ==========================Shortcuts==================================================
	
	const shortcuts = [
		{
			icon: <GraphIcon sx={{ fontSize: 40 }} />,
			label: "Afficher le graph",
			description: "Visualiser le graphe de données",
			onClick: () => {
				console.log("Navigation vers le graph"); // a eneleve une fois le graph implemente
				navigate('/graph');  // navigate vers la page du graph (à implémenter)
			},
			color: "primary",
		},
		{
			icon: <ArrowBackIcon sx={{ fontSize: 40 }} />,
			label: "Arrière",
			description: "Revenir en arrière",
			onClick: () => {
				navigate(-1);
			},
			color: "secondary",
		},
		{
			icon: <ArrowForwardIcon sx={{ fontSize: 40 }} />,
			label: "Avant",
			description: "Aller en avant",
			onClick: () => {
				navigate(1);
			},
			color: "secondary",
		},
		{
			icon: <SearchIcon sx={{ fontSize: 40 }} />,
			label: "Rechercher",
			description: "Rechercher des données",
			onClick: () => {
				console.log("Navigation vers la recherche"); // a eneleve une fois la recherche implemente
				navigate('/search');                 // navigate vers la page de recherche (à implémenter)
			},
			color: "primary",
		},
		{
			icon: <AddIcon sx={{ fontSize: 40 }} />,
			label: "Ajouter",
			description: "Ajouter de nouvelles données",
			onClick: () => {
				console.log("Navigation vers l'ajout de données"); // a eneleve une fois l'ajout de données implemente
				navigate('/add');                    // navigate vers la page d'ajout de données (à implémenter)
			},
			color: "primary",
		},
	];

	
	// ====================================Vues========================================

	return (
		<Container maxWidth="xl" sx={{ py: 4 }}>
			{/* En-tête */}
			<Box sx={{ mb: 4 }}>
				<Typography
					variant="h3"
					component="h1"
					sx={{
						fontWeight: 700,
						color: "primary.main",                 // peut etre enleve cette box
						mb: 1,
					}}
				>
					Tableau de bord
				</Typography>
				<Typography variant="body1" color="primary.light">
					Accédez rapidement à vos données et visualisations
				</Typography>
			</Box>

			{/* TEST WEBSOCKET - Zone de démo */}
			<Alert
				severity={isConnected ? "success" : "warning"}
				sx={{ mb: 3 }}
				icon={<Chip label={isConnected ? " Connecté" : " Déconnecté"} size="small" />}
			>
				<Box>
					<Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1 }}>
					</Typography>
					<Typography variant="caption" sx={{ display: "block", mb: 2 }}>	
					</Typography>

					{/* Input + Bouton */}
					<Stack direction="row" spacing={1} sx={{ mb: 2 }}>
						<TextField
							size="small"
							placeholder="Tapez un message..."
							value={inputMessage}
							onChange={(e) => setInputMessage(e.target.value)}
							onKeyPress={(e) => e.key === "Enter" && handleSendTest()}
							disabled={!isConnected}
							sx={{ flexGrow: 1 }}
						/>
						<Button
							variant="contained"
							size="small"
							onClick={handleSendTest}
							disabled={!isConnected || !inputMessage.trim()}
							startIcon={<SendIcon />}
						>
							Envoyer
						</Button>
					</Stack>

					{/* Messages */}
					{messages.length > 0 && (
						<Paper
							sx={{
								p: 2,
								maxHeight: 150,
								overflow: "auto",
								bgcolor: "background.default",
							}}
						>
							{messages.map((msg, index) => (
								<Typography
									key={index}
									variant="caption"
									sx={{ display: "block", fontFamily: "monospace" }}
								>
									{msg}
								</Typography>
							))}
						</Paper>
					)}
				</Box>
			</Alert>

			{/* shortcuts */}
			<Paper
				elevation={0}
				sx={{
					p: 3,
					mb: 4,
					bgcolor: "background.paper",
					borderRadius: 3,
					border: "1px solid",
					borderColor: "divider",
				}}
			>
				<Typography
					variant="h6"
					sx={{ mb: 3, fontWeight: 600, color: "text.primary" }}
				>
					Accès rapide
				</Typography>
				<Stack
					direction="row"
					spacing={2}
					sx={{
						flexWrap: "wrap",
						gap: 2,
					}}
				>
					{shortcuts.map((shortcut) => (
						<Button
							key={shortcut.label}
							variant="contained"
							color={shortcut.color as "primary" | "secondary"}
							onClick={shortcut.onClick}
							sx={{
								minWidth: 160,
								minHeight: 120,
								flexDirection: "column",
								gap: 1.5,
								borderRadius: 2,
								boxShadow: 2,
								transition: "all 0.2s ease-in-out",
								"&:hover": {
									transform: "translateY(-4px)",
									boxShadow: 4,
								},
							}}
						>
							{shortcut.icon}
							<Box sx={{ textAlign: "center" }}>
								<Typography variant="body1" sx={{ fontWeight: 600 }}>
									{shortcut.label}
								</Typography>
								<Typography
									variant="caption"
									sx={{ opacity: 0.9, fontSize: "0.75rem" }}
								>
									{shortcut.description}
								</Typography>
							</Box>
						</Button>
					))}
				</Stack>
			</Paper>

			{/* VUES  */}
			<Grid container spacing={3}>
				{/* Vue 1 : Graphique */}
				<Grid item xs={12} md={6}>
					<Card
						sx={{
							height: 350,
							borderRadius: 3,
							border: "1px solid",
							borderColor: "divider",
						}}
					>
						<CardContent>
							<Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
								📊 Vue Graphique
							</Typography>
							<Box
								sx={{
									height: 250,
									display: "flex",
									alignItems: "center",
									justifyContent: "center",
									bgcolor: "background.default",
									borderRadius: 2,
									border: "2px dashed",
									borderColor: "divider",
								}}
							>
								<Typography color="text.secondary">
									{/* TODO: Ajouter votre composant graphique ici */}
									Zone pour graphique (Chart / Echarts)
								</Typography>
							</Box>
						</CardContent>
					</Card>
				</Grid>

				{/* Vue 2 : Tableau */}
				<Grid item xs={12} md={6}>
					<Card
						sx={{
							height: 350,
							borderRadius: 3,
							border: "1px solid",
							borderColor: "divider",
						}}
					>
						<CardContent>
							<Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
								📋 Vue Tableau
							</Typography>
							<Box
								sx={{
									height: 250,
									display: "flex",
									alignItems: "center",
									justifyContent: "center",
									bgcolor: "background.default",
									borderRadius: 2,
									border: "2px dashed",
									borderColor: "divider",
								}}
							>
								<Typography color="text.secondary">
									{/* TODO: Ajouter votre tableau de données ici */}
									Zone pour tableau (DataGrid / Table)
								</Typography>
							</Box>
						</CardContent>
					</Card>
				</Grid>

				{/* Vue 3 : Résumé / Statistiques */}
				<Grid item xs={12} md={6}>
					<Card
						sx={{
							height: 350,
							borderRadius: 3,
							border: "1px solid",
							borderColor: "divider",
						}}
					>
						<CardContent>
							<Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
								📈 Vue Statistiques
							</Typography>
							<Box
								sx={{
									height: 250,
									display: "flex",
									alignItems: "center",
									justifyContent: "center",
									bgcolor: "background.default",
									borderRadius: 2,
									border: "2px dashed",
									borderColor: "divider",
								}}
							>
								<Typography color="text.secondary">
									{/* TODO: Ajouter vos statistiques ici */}
									Zone pour statistiques (Métriques clés)
								</Typography>
							</Box>
						</CardContent>
					</Card>
				</Grid>

				{/* Vue 4 : Activité récente */}
				<Grid item xs={12} md={6}>
					<Card
						sx={{
							height: 350,
							borderRadius: 3,
							border: "1px solid",
							borderColor: "divider",
						}}
					>
						<CardContent>
							<Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
								🕒 Activité récente
							</Typography>
							<Box
								sx={{
									height: 250,
									display: "flex",
									flexDirection: "column",
									alignItems: "center",
									justifyContent: "center",
									bgcolor: "background.default",
									borderRadius: 2,
									border: "2px dashed",
									borderColor: "divider",
									gap: 2,
								}}
							>
								<Typography color="text.secondary">
									{/* TODO: Ajouter la liste d'activités ici */}
									Zone pour activités récentes (Timeline / List)
								</Typography>
								
								{/* BOUTON DE TEST TEMPORAIRE - À SUPPRIMER APRÈS TESTS */}
								<Box
									sx={{
										mt: 2,
										p: 2,
										bgcolor: "warning.light",
										borderRadius: 2,
										border: "2px solid",
										borderColor: "warning.main",
									}}
								>
									<Typography variant="caption" sx={{ mb: 1, display: "block" }}>
									</Typography>
									<Button
										variant="contained"
										size="small"
										onClick={() => navigate("/kview/1")}
										sx={{ mr: 1 }}
									>
										Tester KView (ID: 1)
									</Button>
									<Button
										variant="outlined"
										size="small"
										onClick={() => navigate("/kview/42")}
									>
										Tester KView (ID: 42)
									</Button>
								</Box>
							</Box>
						</CardContent>
					</Card>
				</Grid>
			</Grid>
		</Container>
	);
};

export default HomePage;
