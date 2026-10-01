package com.bugboard26.backend.controller;

import com.bugboard26.backend.dto.CommentCreateRequest;
import com.bugboard26.backend.model.Comment;
import com.bugboard26.backend.model.Issue;
import com.bugboard26.backend.model.IssueType;
import com.bugboard26.backend.model.Role;
import com.bugboard26.backend.model.User;
import com.bugboard26.backend.repository.CommentRepository;
import com.bugboard26.backend.repository.IssueRepository;
import com.bugboard26.backend.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @Test // Controlla che l'aggiunta di un commento sia corretta
    void utentePuoAggiungereCommento() {
        
        // Con mockito creiamo i repository senza usare il database
        CommentRepository commentRepository = mock(CommentRepository.class);
        IssueRepository issueRepository = mock(IssueRepository.class);

        CommentController controller = new CommentController(
                commentRepository,
                issueRepository
        );

        User utente = new User(
                "utente@test.it",
                "password-test",
                Role.USER
        );

        Issue issue = new Issue(
                "Errore nel login",
                "Non riesco ad accedere",
                IssueType.BUG,
                utente
        );

        CommentCreateRequest request = new CommentCreateRequest(
                "Il problema si verifica anche sul mio computer"
        );

        // La issue con ID 1 esiste
        when(issueRepository.findById(1L))
                .thenReturn(Optional.of(issue));


        // Esecuzione
        var response = controller.addComment(
                1L,
                request,
                new UserDetailsImpl(utente)
        );


        // Controllo che i risultati siano corretti
        assertEquals(200, response.getStatusCode().value());

        // Recuperiamo il commento passato al repository tramite Captor
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(captor.capture());

        Comment commentoCreato = captor.getValue();

        assertEquals("Il problema si verifica anche sul mio computer", commentoCreato.getText());

        assertSame(utente, commentoCreato.getAuthor());
        assertSame(issue, commentoCreato.getIssue());
        assertNotNull(commentoCreato.getCreatedAt());
    }
}