import React, { useState, useEffect } from 'react';
import { getIssues } from '../services/issueService';
import { Link } from 'react-router-dom';


function IssueListPage() {
    const [issues, setIssues] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

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

    if (loading) return <p>Caricamento...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div>
            <h2>Lista Issue</h2>
            <Link to="/issues/new">+ Nuova Issue</Link>
            {issues.length === 0 ? (
                <p>Nessuna issue presente.</p>
            ) : (
                <ul>
                    {issues.map((issue) => (
                        <li key={issue.id}>
                            <Link to={"/issues/${issue.id}"}>
                                <strong>{issue.title}</strong>
                            </Link>
                            {issue.priority && ` — Priorità: ${issue.priority}`}
                            <br />
                            <small>{issue.description}</small>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default IssueListPage;