import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/authService';

function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            await login(email, password);
            navigate('/issues'); // reindirizza alla lista issue
        } catch (err) {
            setError('Email o password non corretti');
        }
    };

    return (
        <div className="login-page">
            <div className="login-card">
                <p className="login-brand">BugBoard26</p>

                <h2>Accedi</h2>
                <p className="login-description">
                    Inserisci le tue credenziali per gestire le issue.
                </p>

                <form onSubmit={handleSubmit}>
                    <div className="login-field">
                        <label htmlFor="login-email">Email</label>
                        <input
                            id="login-email"
                            type="email"
                            autoComplete="username"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="nome@esempio.it"
                            required
                        />
                    </div>

                    <div className="login-field">
                        <label htmlFor="login-password">Password</label>
                        <input
                            id="login-password"
                            type="password"
                            autoComplete="current-password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    {error && (
                        <p className="login-error" role="alert">
                            {error}
                        </p>
                    )}

                    <button className="login-button" type="submit">
                        Accedi
                    </button>
                </form>

                <p className="login-help">
                    Non hai un account? Rivolgiti all’amministratore.
                </p>
            </div>
        </div>
    );
}

export default LoginPage;