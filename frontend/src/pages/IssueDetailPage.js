import React, { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getIssueById, assignIssue } from '../services/issueService';
import { getComments, addComment } from '../services/commentService';
import { getCurrentUser } from '../services/authService';
import { getUsers } from '../services/userService';

function IssueDetailPage() {
    const { id } = useParams();

    const [issue, setIssue] = useState(null);
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');

    const [loading, setLoading] = useState(true);
    const [sending, setSending] = useState(false);
    const [error, setError] = useState('');
    const [commentError, setCommentError] = useState('');

    const [isAdmin, setIsAdmin] = useState(false);
    const [users, setUsers] = useState([]);
    const [selectedUserId, setSelectedUserId] = useState('');

    const [assigning, setAssigning] = useState(false);
    const [assignmentError, setAssignmentError] = useState('');
    const [assignmentSuccess, setAssignmentSuccess] = useState('');   

    const [currentUserEmail, setCurrentUserEmail] = useState('');

    useEffect(() => {
        const loadUsers = async () => {
            try {
                const currentUser = await getCurrentUser();
                // L'email ci permette di conoscere l'assegnatario
                setCurrentUserEmail(currentUser.email);
                const admin = currentUser.role == 'ADMIN';

                setIsAdmin(admin);

                if (admin) {
                    const data = await getUsers();
                    setUsers(data);
                }
            } catch (err){
                setAssignmentError('Impossibile caricare i dati per l assegnazione');
            }
        };
        loadUsers();
    }, []);

    useEffect(() => {
        // Evita di aggiornare questa pagina se nel frattempo la lasci
        let active = true;

        const fetchDetails = async () => {
            setLoading(true);
            setError('');
            setCommentError('');
            setNewComment('');

            try {
                // Carica sia la issue sia i suoi commenti.
                const [issueData, commentsData] = await Promise.all([
                    getIssueById(id),
                    getComments(id),
                ]);

                if (active) {
                    setIssue(issueData);
                    setComments(commentsData);
                }
            } catch (err) {
                if (active) {
                    setError(
                        err.response?.status === 404
                            ? 'Issue non trovata.'
                            : 'Impossibile caricare il dettaglio della issue.'
                    );
                }
            } finally {
                if (active) {
                    setLoading(false);
                }
            }
        };

        fetchDetails();

        return () => {
            active = false;
        };
    }, [id]);

    const handleAddComment = async (e) => {
        e.preventDefault();

        const text = newComment.trim();
        if (!text || sending) return;

        setSending(true);
        setCommentError('');

        try {
            const savedComment = await addComment(id, text);

            // Il backend restituisce il commento salvato:
            // possiamo aggiungerlo alla lista senza ricaricarla.
            setComments((previous) => [...previous, savedComment]);
            setNewComment('');
        } catch (err) {
            setCommentError('Impossibile inviare il commento. Riprova.');
        } finally {
            setSending(false);
        }
    };

    if (loading) return <p>Caricamento...</p>;

    const handleAssign = async (e) => {
        e.preventDefault();

        if (!selectedUserId || assigning) return;

        setAssigning(true);
        setAssignmentError('');
        setAssignmentSuccess('');

        try {
            // Il menu restituisce una stringa: la convertiamo in numero
            const utenteId = Number(selectedUserId);

            await assignIssue(id, utenteId);

            // Aggiorniamo a video l'assegnatario dopo il salvataggio
            const selectedUser = users.find((user) => user.id === utenteId);

            setIssue((previousIssue) => ({
                ...previousIssue,
                assegnato: selectedUser,
            }));

            setAssignmentSuccess('Assegnazione salvata.');
        } catch (err) {
            setAssignmentError('Impossibile salvare l’assegnazione.');
        } finally {
            setAssigning(false);
        }
    };

    if (error) {
        return (
            <div>
                <Link to="/issues">Torna alla lista</Link>
                <p role="alert">{error}</p>
            </div>
        );
    }

    return (
        <div>
            <Link to="/issues">Torna alla lista</Link>

            <h2>{issue.title}</h2>

            {(isAdmin || (
                currentUserEmail !== ''
                && issue.assegnato != null
                && issue.assegnato.email === currentUserEmail
            )) && (
                <p>
                    <Link to={'/issues/' + id + '/edit'}>
                        Modifica issue
                    </Link>
                </p>
            )}

            <p><strong>ID:</strong> {issue.id}</p>
            <p><strong>Tipo:</strong> {issue.type}</p>
            <p><strong>Stato:</strong> {issue.status}</p>
            <p>
                <strong>Priorità:</strong> {issue.priority || 'Non specificata'}
            </p>
            <p><strong>Autore:</strong> {issue.author?.email}</p>

            <p>
                <strong>Assegnato a:</strong>{' '}
                {issue.assegnato ? issue.assegnato.email : 'Nessuno'}
            </p>

            {isAdmin && (
                <form onSubmit={handleAssign}>
                    <label htmlFor="assigned-user">Assegna a: </label>

                    <select
                        id="assigned-user"
                        value={selectedUserId}
                        onChange={(e) => setSelectedUserId(e.target.value)}
                        disabled={assigning}
                        required
                    >
                        <option value="">Seleziona un utente</option>

                        {users.map((user) => (
                            <option key={user.id} value={user.id}>
                                {user.email}
                            </option>
                        ))}
                    </select>

                    <button
                        type="submit"
                        disabled={assigning || !selectedUserId}
                    >
                        {assigning ? 'Salvataggio...' : 'Salva assegnazione'}
                    </button>
                </form>
            )}

            {assignmentError && (
                <p role="alert">{assignmentError}</p>
            )}

            {assignmentSuccess && (
                <p role="status">{assignmentSuccess}</p>
            )}

            <h3>Descrizione</h3>
            <p style={{ whiteSpace: 'pre-wrap' }}>
                {issue.description}
            </p>

            <h3>Commenti</h3>

            {comments.length === 0 ? (
                <p>Nessun commento ancora.</p>
            ) : (
                <ul>
                    {comments.map((comment) => (
                        <li key={comment.id}>
                            <strong>{comment.author?.email}</strong>
                            <p style={{ whiteSpace: 'pre-wrap' }}>
                                {comment.text}
                            </p>
                            <small>
                                {new Date(comment.createdAt).toLocaleString('it-IT')}
                            </small>
                        </li>
                    ))}
                </ul>
            )}

            <form onSubmit={handleAddComment}>
                <label htmlFor="new-comment">Nuovo commento</label>
                <br />
                <textarea
                    id="new-comment"
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="Scrivi un commento..."
                    disabled={sending}
                    required
                />

                {commentError && (
                    <p role="alert">{commentError}</p>
                )}

                <br />
                <button
                    type="submit"
                    disabled={sending || !newComment.trim()}
                >
                    {sending ? 'Invio in corso...' : 'Invia'}
                </button>
            </form>
        </div>
    );
}

export default IssueDetailPage;