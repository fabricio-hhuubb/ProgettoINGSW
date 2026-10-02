import React, { useState } from 'react';
import { createIssue } from '../services/issueService';
import { Link, useNavigate } from "react-router-dom";

function IssueFormPage() {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [type, setType] = useState('BUG');
    const [priority, setPriority] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            await createIssue({
                title,
                description,
                type,
                priority: priority || null,
            });
            navigate('/issues');
        } catch (err) {
            setError('Errore nella creazione della issue');
        }
    };

    return (
        <div className="issue-form-page">
            <h2>Nuova Issue</h2>
            <form className="issue-form" onSubmit={handleSubmit}>
                <div>
                    <Link to="/issues" className="back-link">
                        Torna alla lista
                    </Link>
                    <label>Titolo:</label>
                    <input
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Descrizione:</label>
                    <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                        rows={6}
                    />
                </div>
                <div>
                    <label>Tipo:</label>
                    <select value={type} onChange={(e) => setType(e.target.value)}>
                        <option value="QUESTION">Question</option>
                        <option value="BUG">Bug</option>
                        <option value="DOCUMENTATION">Documentation</option>
                        <option value="FEATURE">Feature</option>
                    </select>
                </div>
                <div>
                    <label>Priorità (opzionale):</label>
                    <select value={priority} onChange={(e) => setPriority(e.target.value)}>
                        <option value="">-- Nessuna --</option>
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                    </select>
                </div>
                {error && <p style={{ color: 'red' }}>{error}</p>}
                <button type="submit">Crea Issue</button>
            </form>
        </div>
    );
}

export default IssueFormPage;