import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getIssueById, updateIssue } from '../services/issueService';
import { getCurrentUser } from '../services/authService';

function IssueEditPage() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [type, setType] = useState('BUG');
    const [priority, setPriority] = useState('');

    const [canEdit, setCanEdit] = useState(false);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const loadIssue = async () => {
            setLoading(true);
            setError('');
            setCanEdit(false);

            try {
                const issue = await getIssueById(id);
                const user = await getCurrentUser();

                // /auth/me restituisce email e ruolo.
                const isAdmin = user.role === 'ADMIN';
                const isAssegnato = issue.assegnato != null
                    && issue.assegnato.email === user.email;

                if (!isAdmin && !isAssegnato) {
                    setError('Non hai i permessi per modificare questa issue.');
                    return;
                }

                setCanEdit(true);

                // Compiliamo il modulo con i valori già salvati.
                setTitle(issue.title);
                setDescription(issue.description);
                setType(issue.type);
                setPriority(issue.priority || '');
            } catch (err) {
                setError('Impossibile caricare la issue o verificare la sessione.');
            } finally {
                setLoading(false);
            }
        };

        loadIssue();
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (saving) return;

        if (!title.trim() || !description.trim()) {
            setError('Titolo e descrizione non possono essere vuoti.');
            return;
        }

        setSaving(true);
        setError('');

        try {
            await updateIssue(id, {
                title: title.trim(),
                description: description.trim(),
                type: type,
                priority: priority || null
            });

            // Dopo il salvataggio torniamo al dettaglio.
            navigate('/issues/' + id);
        } catch (err) {
            if (err.response?.status === 403) {
                setError('Non sei autorizzato a modificare questa issue.');
            } else {
                setError('Impossibile salvare le modifiche.');
            }
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return <p>Caricamento...</p>;
    }

    return (
        <div className="issue-form-page">
            <h2>Modifica issue</h2>

            {error && <p role="alert">{error}</p>}

            {canEdit && (
                <form className="issue-form" onSubmit={handleSubmit}>
                    <div>
                        <Link to={'/issues/' + id} className="back-link">
                            Torna al dettaglio
                        </Link>

                        <label htmlFor="edit-title">Titolo:</label>
                        <input
                            id="edit-title"
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            required
                        />
                    </div>

                    <div>
                        <label htmlFor="edit-description">Descrizione:</label>
                        <textarea
                            id="edit-description"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            required
                            rows={6}
                        />
                    </div>

                    <div>
                        <label htmlFor="edit-type">Tipo:</label>
                        <select
                            id="edit-type"
                            value={type}
                            onChange={(e) => setType(e.target.value)}
                        >
                            <option value="QUESTION">Question</option>
                            <option value="BUG">Bug</option>
                            <option value="DOCUMENTATION">Documentation</option>
                            <option value="FEATURE">Feature</option>
                        </select>
                    </div>

                    <div>
                        <label htmlFor="edit-priority">Priorità:</label>
                        <select
                            id="edit-priority"
                            value={priority}
                            onChange={(e) => setPriority(e.target.value)}
                        >
                            <option value="">Non specificata</option>
                            <option value="LOW">Bassa</option>
                            <option value="MEDIUM">Media</option>
                            <option value="HIGH">Alta</option>
                        </select>
                    </div>

                    <button type="submit" disabled={saving}>
                        {saving ? 'Salvataggio...' : 'Salva modifiche'}
                    </button>
                </form>
            )}
        </div>
    );
}

export default IssueEditPage;