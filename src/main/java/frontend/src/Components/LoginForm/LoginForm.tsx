import { type FormEventHandler, useState } from "react";
import "./LoginForm.css";
import { FaEye, FaEyeSlash, FaUser } from "react-icons/fa";
import logo from "../Assets/i3S_RVB_Couleur.png";
import { useNavigate } from "react-router";
import { useTitle } from "@global/useTitle";
import { useApiMutation } from "@hooks/useApiData";
import { useQueryClient } from "@tanstack/react-query";

const LoginForm = () => {
	const [username, setUsername] = useState<string>("");
	const [password, setPassword] = useState<string>("");
	const [passwordVisible, setPasswordVisible] = useState<boolean>(false);
	const [error, setError] = useState<string>("");

	const navigate = useNavigate();
	const queryClient = useQueryClient();

	const authMutation = useApiMutation("authenticate", {
		onSuccess: async () => {
			await queryClient.invalidateQueries();
		},
	});

	useTitle("Login");

	const togglePasswordVisibility = () => {
		setPasswordVisible((prevState) => !prevState);
	};

	const handleSubmit: FormEventHandler<HTMLFormElement> = async (e) => {
		e.preventDefault();
		try {
			authMutation.mutate(
				{
					username: username,
					password: password,
				},
				{
					onSuccess: (_data) => {
						navigate("/home"); // Redirect to HomePage
					},
					onError: (error) => {
						console.error(error);
						setError("Failed to connect to the server");
					},
				},
			);
		} catch (err) {
			console.error(err);
			setError("Failed to connect to the server");
		}
	};

	return (
		<div className="container">
			<div className="wrapper">
				<form onSubmit={handleSubmit}>
					<div className="image-container">
						<img src={logo} alt="Logo" />
					</div>
					<div className={`input-box ${username ? "not-empty" : ""}`}>
						<input
							type="text"
							className="input-field"
							placeholder="Identifiant"
							value={username}
							onChange={(e) => setUsername(e.target.value)}
							required
						/>
						<FaUser className="icon" />
					</div>
					<div className={`input-box ${password ? "not-empty" : ""}`}>
						<input
							type={passwordVisible ? "text" : "password"}
							className="input-field"
							placeholder="Mot de passe"
							value={password}
							onChange={(e) => setPassword(e.target.value)}
							required
						/>
						{passwordVisible ? (
							<FaEyeSlash
								className="toggle-icon icon"
								onClick={togglePasswordVisibility}
							/>
						) : (
							<FaEye
								className="toggle-icon icon"
								onClick={togglePasswordVisibility}
							/>
						)}
					</div>
					{error && <p className="error-message">{error}</p>}

					<button type="submit">
						<span>Se connecter</span>
					</button>
				</form>
			</div>
		</div>
	);
};
export default LoginForm;
