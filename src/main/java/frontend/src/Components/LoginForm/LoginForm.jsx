import React, {useState} from 'react';
import './LoginForm.css';
import {FaEye, FaEyeSlash, FaUser} from "react-icons/fa";
import logo from '../Assets/i3S_RVB_Couleur.png';
import {useNavigate} from 'react-router-dom';
import {useTitle} from "../../global/useTitle";

const LoginForm = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [passwordVisible, setPasswordVisible] = useState(false);
    const [error, setError] = useState(""); // Error handling

    const navigate = useNavigate();

    useTitle("Login");

    const togglePasswordVisibility = () => {
        setPasswordVisible((prevState) => !prevState);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // TODO: Add back the login request
            /*
            // Send the login request to the backend
            const response = await axios.get('https://dronic.i3s.unice.fr:8080/api', {
                params: { username, password },
            });

            if (response.data && response.data['session ID']) {
                // Save the session details in local storage or state
                localStorage.setItem("sessionData", JSON.stringify(response.data));
                navigate('/home'); // Redirect to HomePage
            } else {
                setError("Invalid username or password");
            }
             */

            localStorage.setItem("sessionData", JSON.stringify({"session ID": "1234"}));
            navigate('/grid'); // Redirect to HomePage but gridview
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
                        <img src={logo} alt="Logo"/>
                    </div>
                    <div className={`input-box ${username ? 'not-empty' : ''}`}>
                        <input
                            type="text"
                            className="input-field"
                            placeholder="Identifiant"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                        <FaUser className="icon"/>
                    </div>
                    <div className={`input-box ${password ? 'not-empty' : ''}`}>
                        <input
                            type={passwordVisible ? "text" : "password"}
                            className="input-field"
                            placeholder="Mot de passe"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        {passwordVisible ? (
                            <FaEyeSlash className="toggle-icon icon" onClick={togglePasswordVisibility}/>
                        ) : (
                            <FaEye className="toggle-icon icon" onClick={togglePasswordVisibility}/>
                        )}
                    </div>
                    {error && <p className="error-message">{error}</p>}

                    <button type="submit"><span>Se connecter</span></button>
                </form>
            </div>
        </div>
    );
};

export default LoginForm;
