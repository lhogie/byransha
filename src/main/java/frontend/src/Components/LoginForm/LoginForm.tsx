import { useTitle } from "@global/useTitle";
import { yupResolver } from "@hookform/resolvers/yup";
import { useApiMutation } from "@hooks/useApiData";
import {
	Person as PersonIcon,
	Visibility,
	VisibilityOff,
} from "@mui/icons-material";
import { LoadingButton } from "@mui/lab";
import {
	Alert,
	Box,
	Container,
	IconButton,
	InputAdornment,
	Paper,
	TextField,
	Typography,
	useTheme,
} from "@mui/material";
import { useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useNavigate } from "react-router";
import * as yup from "yup";
import logo from "../Assets/i3S_RVB_Couleur.png";

// Validation schema using Yup
const validationSchema = yup.object({
	username: yup
		.string()
		.required("L'identifiant est requis")
		.min(2, "L'identifiant doit contenir au moins 2 caractères")
		.max(50, "L'identifiant ne peut pas dépasser 50 caractères"),
	password: yup
		.string()
		.required("Le mot de passe est requis")
		.min(1, "Le mot de passe est requis"),
});

// Form data interface
interface LoginFormData {
	username: string;
	password: string;
}

const LoginForm = () => {
	const [passwordVisible, setPasswordVisible] = useState<boolean>(false);
	const [authError, setAuthError] = useState<string>("");

	const theme = useTheme();
	const navigate = useNavigate();
	const queryClient = useQueryClient();

	// React Hook Form setup with Yup validation
	const {
		control,
		handleSubmit,
		formState: { errors, isSubmitting },
		setError,
	} = useForm<LoginFormData>({
		resolver: yupResolver(validationSchema),
		defaultValues: {
			username: "",
			password: "",
		},
		mode: "onBlur", // Validate on blur for better UX
	});

	const authMutation = useApiMutation("authenticate", {
		onSuccess: async () => {
			await queryClient.invalidateQueries();
		},
	});

	useTitle("Connexion");

	const togglePasswordVisibility = () => {
		setPasswordVisible((prevState) => !prevState);
	};

	const onSubmit = async (data: LoginFormData) => {
		try {
			setAuthError(""); // Clear previous errors

			await authMutation.mutateAsync(
				{
					username: data.username,
					password: data.password,
				},
				{
					onSuccess: () => {
						navigate("/home", { replace: true });
					},
					onError: (error: any) => {
						console.error("Authentication error:", error);

						// Set specific field errors if available
						if (error.field === "username") {
							setError("username", {
								type: "server",
								message: error.message || "Identifiant invalide",
							});
						} else if (error.field === "password") {
							setError("password", {
								type: "server",
								message: error.message || "Mot de passe invalide",
							});
						} else {
							// Set general error
							setAuthError(
								error.message ||
									"Échec de la connexion au serveur. Veuillez vérifier vos identifiants.",
							);
						}
					},
				},
			);
		} catch (err) {
			console.error("Unexpected error:", err);
			setAuthError("Une erreur inattendue s'est produite. Veuillez réessayer.");
		}
	};

	const isLoading = isSubmitting || authMutation.isPending;

	return (
		<Container
			component="main"
			maxWidth="sm"
			sx={{
				minHeight: "100vh",
				display: "flex",
				alignItems: "center",
				justifyContent: "center",
				py: 3,
			}}
		>
			<Paper
				elevation={6}
				sx={{
					p: { xs: 2, sm: 3, md: 4 },
					width: "100%",
					maxWidth: { xs: "90vw", sm: 400 },
					borderRadius: 2,
					backgroundColor: "background.paper",
					border: `1px solid ${theme.palette.divider}`,
				}}
			>
				{/* Logo Section */}
				<Box
					sx={{
						display: "flex",
						justifyContent: "center",
						mb: 3,
					}}
				>
					<Box
						component="img"
						src={logo}
						alt="Logo I3S"
						sx={{
							maxWidth: { xs: 150, sm: 200 },
							width: "100%",
							height: "auto",
							borderRadius: 1,
						}}
					/>
				</Box>

				{/* Error Alert */}
				{authError && (
					<Alert
						severity="error"
						sx={{ mb: 2 }}
						onClose={() => setAuthError("")}
						role="alert"
						aria-live="polite"
					>
						{authError}
					</Alert>
				)}

				{/* Login Form */}
				<Box
					component="form"
					onSubmit={handleSubmit(onSubmit)}
					noValidate
					aria-label="Formulaire de connexion"
				>
					{/* Username Field */}
					<Controller
						name="username"
						control={control}
						render={({ field }) => (
							<TextField
								{...field}
								margin="normal"
								required
								fullWidth
								id="username"
								label="Identifiant"
								name="username"
								autoComplete="username"
								autoFocus
								disabled={isLoading}
								error={!!errors.username}
								helperText={errors.username?.message}
								InputProps={{
									startAdornment: (
										<InputAdornment position="start">
											<PersonIcon color="action" />
										</InputAdornment>
									),
								}}
								sx={{
									"& .MuiOutlinedInput-root": {
										"&:hover fieldset": {
											borderColor: "primary.main",
										},
									},
								}}
								inputProps={{
									"aria-describedby": errors.username
										? "username-error"
										: undefined,
								}}
							/>
						)}
					/>

					{/* Password Field */}
					<Controller
						name="password"
						control={control}
						render={({ field }) => (
							<TextField
								{...field}
								margin="normal"
								required
								fullWidth
								name="password"
								label="Mot de passe"
								type={passwordVisible ? "text" : "password"}
								id="password"
								autoComplete="current-password"
								disabled={isLoading}
								error={!!errors.password}
								helperText={errors.password?.message}
								InputProps={{
									endAdornment: (
										<InputAdornment position="end">
											<IconButton
												aria-label={
													passwordVisible
														? "Masquer le mot de passe"
														: "Afficher le mot de passe"
												}
												onClick={togglePasswordVisibility}
												onMouseDown={(e) => e.preventDefault()}
												edge="end"
												disabled={isLoading}
												size="small"
											>
												{passwordVisible ? <VisibilityOff /> : <Visibility />}
											</IconButton>
										</InputAdornment>
									),
								}}
								sx={{
									"& .MuiOutlinedInput-root": {
										"&:hover fieldset": {
											borderColor: "primary.main",
										},
									},
								}}
								inputProps={{
									"aria-describedby": errors.password
										? "password-error"
										: undefined,
								}}
							/>
						)}
					/>

					{/* Submit Button */}
					<LoadingButton
						type="submit"
						fullWidth
						variant="contained"
						loading={isLoading}
						loadingPosition="start"
						disabled={isLoading}
						sx={{
							mt: 3,
							mb: 2,
							py: 1.5,
							borderRadius: 2,
							fontWeight: 600,
							textTransform: "none",
							fontSize: "1rem",
							"&:hover": {
								backgroundColor: "primary.dark",
							},
							"&:disabled": {
								backgroundColor: "action.disabledBackground",
							},
						}}
						aria-describedby={authError ? "auth-error" : undefined}
					>
						{isLoading ? "Connexion en cours..." : "Se connecter"}
					</LoadingButton>
				</Box>

				{/* Footer */}
				<Box sx={{ mt: 2, textAlign: "center" }}>
					<Typography
						variant="body2"
						color="text.secondary"
						sx={{ fontSize: "0.875rem" }}
					>
						Veuillez vous connecter avec vos identifiants
					</Typography>
				</Box>
			</Paper>
		</Container>
	);
};

export default LoginForm;
