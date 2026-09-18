import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getComments, addComment } from '../services/commentService';

function IssueDetailPage() {
    const { id } = useParams();
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    const [loading, setLoading] = useState(true);

    const fetchComments = async () => {
        try {
            const data = await getComments(id);
            setComments(data);
        } catch (err) {
            console.error('Errore nel caricamento commenti', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchComments();
    }, [id]);

    const handleAddComment = async (e) => {
        e.preventDefault();
        if (!newComment.trim()) return;

        try {
            await addComment(id, newComment);
            setNewComment('');
            fetchComments(); // ricarica la lista dopo l'aggiunta
        } catch (err) {
            console.error('Errore nell\'aggiunta del commento', err);
        }
    };

    if (loading) return <p>Caricamento...</p>;

    return (
        <div>
            <h2>Dettaglio Issue #{id}</h2>

            <h3>Commenti</h3>
            {comments.length === 0 ? (
                <p>Nessun commento ancora.</p>
            ) : (
                <ul>
                    {comments.map((comment) => (
                        <li key={comment.id}>
                            <strong>{comment.author.email}</strong>: {comment.text}
                            <br />
                            <small>{new Date(comment.createdAt).toLocaleString()}</small>
                        </li>
                    ))}
                </ul>
            )}

            <form onSubmit={handleAddComment}>
                <textarea
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="Scrivi un commento..."
                    required
                />
                <button type="submit">Invia</button>
            </form>
        </div>
    );
}

export default IssueDetailPage;