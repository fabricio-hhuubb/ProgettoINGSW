import React, { useState, useEffect } from 'react';
import { getIssues } from '../services/issueService';
import { Link, useNavigate } from 'react-router-dom';
import { getCurrentUser, logout } from '../services/authService';


function IssueListPage() {

    const navigate = useNavigate();

    const [loggingOut, setLoggingOut] = useState(false);
    const [logoutError, setLogoutError] = useState('');

    const [issues, setIssues] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [type, setType] = useState('');
    const [status, setStatus] = useState('');
    const [priority, setPriority] = useState('');

    const [isAdmin, setIsAdmin] = useState(false);

    // Controlla se l'utente collegato è un amministratore
    useEffect(() => {
        const checkUser = async () => {
            try {
                const user = await getCurrentUser();
                setIsAdmin(user.role === 'ADMIN');
            } catch (err) {
                setIsAdmin(false);
            }
        };

        checkUser();
    }, []);

    const handleLogout = async () => {
        setLoggingOut(true);
        setLogoutError('');

        try {
            // Il backend invalida la sessione dell'utente
            await logout();

            // Torna alla pagina di accesso
            navigate('/login', { replace: true });
        } catch (err) {
            setLogoutError('Impossibile uscire. Riprova.');
        } finally {
            setLoggingOut(false);
        }
    };



    const handleFilter = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        // Inseriamo solo i filtri che l'utente ha selezionato
        const filters = {};

        if (type !== '') {
            filters.type = type;
        }

        if (status !== '') {
            filters.status = status;
        }

        if (priority !== '') {
            filters.priority = priority;
        }

        try {
            const data = await getIssues(filters);
            setIssues(data);
        } catch (err) {
            setError('Errore nel caricamento delle issue');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const fetchIssues = async () => {
            try {
                const data = await getIssues();
                setIssues(data);
            } catch (err) {
                setError('Errore nel caricamento delle issue');
            } finally {
                setLoading(false);
            }
        };

        fetchIssues();
    }, []);


    return (
        <div>
            <h2>Lista Issue</h2>

            <button
                type="button"
                onClick={handleLogout}
                disabled={loggingOut}
            >
                {loggingOut ? 'Uscita in corso...' : 'Esci'}
            </button>

            {logoutError && (
                <p role="alert">{logoutError}</p>
            )}

            <Link to="/issues/new">+ Nuova Issue</Link>
            
            {/* Questo significa che questo isAdmin è true, allora mostra quello che c è qui dentro */}
            {isAdmin && (
                <p>
                    <Link to="/users/new">Crea utente</Link>
                </p>
            )}
    
            <form onSubmit={handleFilter}>
                <div>
                    <label htmlFor="type-filter">Tipo: </label>
                    <select
                        id="type-filter"
                        value={type}
                        onChange={(e) => setType(e.target.value)}
                    >
                        <option value="">Tutti</option>
                        <option value="QUESTION">Question</option>
                        <option value="BUG">Bug</option>
                        <option value="DOCUMENTATION">Documentation</option>
                        <option value="FEATURE">Feature</option>
                    </select>
                </div>

                <div>
                    <label htmlFor="status-filter">Stato: </label>
                    <select
                        id="status-filter"
                        value={status}
                        onChange={(e) => setStatus(e.target.value)}
                    >
                        <option value="">Tutti</option>
                        <option value="TODO">Da fare</option>
                        <option value="IN_PROGRESS">In corso</option>
                        <option value="RESOLVED">Risolto</option>
                    </select>
                </div>

                <div>
                    <label htmlFor="priority-filter">Priorità: </label>
                    <select
                        id="priority-filter"
                        value={priority}
                        onChange={(e) => setPriority(e.target.value)}
                    >
                        <option value="">Tutte</option>
                        <option value="LOW">Bassa</option>
                        <option value="MEDIUM">Media</option>
                        <option value="HIGH">Alta</option>
                    </select>
                </div>

                <button type="submit" disabled={loading}>
                    Applica filtri
                </button>
            </form>

            {loading && <p>Caricamento...</p>}

            {error && (
                <p role="alert" style={{ color: 'red' }}>
                    {error}
                </p>
            )}

            {!loading && !error && (
                issues.length === 0 ? (
                    <p>Nessuna issue trovata.</p>
                ) : (
                    <ul>
                        {issues.map((issue) => (
                            <li key={issue.id}>
                                <Link to={`/issues/${issue.id}`}>
                                    <strong>{issue.title}</strong>
                                </Link>

                                <p>{issue.description}</p>

                                <small>
                                    Tipo: {issue.type}
                                    {' — '}Stato: {issue.status}
                                    {' — '}Priorità: {issue.priority || 'Non specificata'}
                                </small>
                            </li>
                        ))}
                    </ul>
                )
            )}
        </div>
    );
}

export default IssueListPage;