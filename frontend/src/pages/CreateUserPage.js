import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getCurrentUser, createUser } from '../services/authService';

function CreateUserPage() {
    const [isAdmin, setIsAdmin] = useState(false);
    const [loading, setLoading] = useState(true);

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('USER');

    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        const checkUser = async () => {
            try {
                const user = await getCurrentUser();
                setIsAdmin(user.role === 'ADMIN');
            } catch (err) {
                setError('Impossibile verificare la sessione. Prova ad accedere di nuovo.');
            } finally {
                setLoading(false);
            }
        };

        checkUser();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setSaving(true);

        try {
            await createUser(email, password, role);

            setSuccess('Utente creato correttamente.');

            // Svuotiamo il modulo dopo una creazione riuscita.
            setEmail('');
            setPassword('');
            setRole('USER');
        } catch (err) {
            // Il backend può restituire un messaggio, ad esempio
            // "Email già registrata".
            if (err.response?.status === 403) {
                setError('Non sei autorizzato. Verifica di essere collegato come amministratore.');
            } else if (typeof err.response?.data === 'string') {
                setError(err.response.data);
            } else {
                setError('Errore nella creazione dell’utente.');
            }
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return <p>Caricamento...</p>;
    }

    if (!isAdmin) {
        return (
            <div>
                <p role="alert">
                    {error || 'Questa pagina è riservata agli amministratori.'}
                </p>
                <Link to="/issues">Torna alle issue</Link>
                <br />
                <Link to="/login">Vai al login</Link>
            </div>
        );
    }

    return (
        <div className="issue-form-page">
            <Link to="/issues" className="back-link">
                Torna alla lista
            </Link>

            <h2>Crea utente</h2>

            <form className="issue-form" onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="user-email">Email:</label>
                    <input
                        id="user-email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="user-password">Password:</label>
                    <input
                        id="user-password"
                        type="password"
                        autoComplete="new-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="user-role">Ruolo:</label>
                    <select
                        id="user-role"
                        value={role}
                        onChange={(e) => setRole(e.target.value)}
                    >
                        <option value="USER">Utente normale</option>
                        <option value="ADMIN">Amministratore</option>
                    </select>
                </div>

                {error && <p role="alert">{error}</p>}
                {success && <p role="status">{success}</p>}

                <button type="submit" disabled={saving}>
                    {saving ? 'Creazione in corso...' : 'Crea utente'}
                </button>
            </form>
        </div>
    );
}

export default CreateUserPage;