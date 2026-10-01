package com.bugboard26.backend.controller;

import com.bugboard26.backend.dto.IssueUpdateRequest;
import com.bugboard26.backend.model.Issue;
import com.bugboard26.backend.model.IssueType;
import com.bugboard26.backend.model.Priority;
import com.bugboard26.backend.model.Role;
import com.bugboard26.backend.model.User;
import com.bugboard26.backend.repository.IssueRepository;
import com.bugboard26.backend.repository.UserRepository;
import com.bugboard26.backend.security.UserDetailsImpl;
import com.bugboard26.backend.dto.IssueCreateRequest;
import com.bugboard26.backend.model.IssueStatus;
import org.mockito.ArgumentCaptor;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IssueControllerTest {

    @Test  // Serve a testare la funziona updateIssue
    void adminPuoModificareIssue() {

        // Creiamo repository simulati senza collegarci al database
        IssueRepository issueRepository = mock(IssueRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        IssueController controller = new IssueController(
                issueRepository,
                userRepository
        );

        User admin = new User(
                "admin@test.it",
                "password-test",
                Role.ADMIN
        );

        Issue issue = new Issue(
                "Titolo originale",
                "Descrizione originale",
                IssueType.BUG,
                admin
        );
        issue.setPriority(Priority.LOW);

        IssueUpdateRequest request = new IssueUpdateRequest(
                "Titolo modificato",
                "Descrizione modificata",
                IssueType.DOCUMENTATION,
                Priority.HIGH
        );

        // Quando il controller cerca l'ID 1 restituiamo la nostra issue
        when(issueRepository.findById(1L))
                .thenReturn(Optional.of(issue));

        // Quando richiede il salvataggio, restituiamo la stessa issue
        when(issueRepository.save(issue))
                .thenReturn(issue);


        // Procediamo

        var response = controller.updateIssue(
                1L,
                request,
                new UserDetailsImpl(admin)
        );


        // Check che tutto sia corretto

        // L'operazione deve riuscire
        assertEquals(200, response.getStatusCode().value());

        // I dati devono essere stati aggiornati
        assertEquals("Titolo modificato", issue.getTitle());
        assertEquals("Descrizione modificata", issue.getDescription());
        assertEquals(IssueType.DOCUMENTATION, issue.getType());
        assertEquals(Priority.HIGH, issue.getPriority());

        // Il controller deve aver richiesto il salvataggio una volta
        verify(issueRepository).save(issue);
    }

    @Test // Controlla la corretta creazione di una issue
    void utentePuoCreareIssue() {

        // Creazione con mockito per non usare il database
        IssueRepository issueRepository = mock(IssueRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        IssueController controller = new IssueController(
                issueRepository,
                userRepository
        );

        User utente = new User(
                "utente@test.it",
                "password-test",
                Role.USER
        );

        IssueCreateRequest request = new IssueCreateRequest(
                "Errore nel login",
                "Non riesco ad accedere",
                IssueType.BUG,
                Priority.HIGH
        );


        // Esecuzione

        var response = controller.createIssue(
                request,
                new UserDetailsImpl(utente)
        );


        // Controllo dei risultati
        assertEquals(200, response.getStatusCode().value());

        // Recuperiamo la issue che il controller ha passato a save()
        // ArgumentCaptor intercetta la creazione dell'oggetto e ci permette di verificare i dati
        ArgumentCaptor<Issue> captor = ArgumentCaptor.forClass(Issue.class);

        verify(issueRepository).save(captor.capture());

        Issue issueCreata = captor.getValue();

        assertEquals("Errore nel login", issueCreata.getTitle());
        assertEquals("Non riesco ad accedere", issueCreata.getDescription());
        assertEquals(IssueType.BUG, issueCreata.getType());
        assertEquals(Priority.HIGH, issueCreata.getPriority());

        // La nuova issue deve partire dallo stato TODO
        assertEquals(IssueStatus.TODO, issueCreata.getStatus());

        // L'autore deve essere proprio l'utente collegato
        assertSame(utente, issueCreata.getAuthor());
    }

    @Test // Controlla che il filtro funzioni correttamente perciò creiamo due issue con diversi tipi
    void filtroPerTipoRestituisceSoloBug() {

        // Repository con mockito sempre
        IssueRepository issueRepository = mock(IssueRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        IssueController controller = new IssueController(
                issueRepository,
                userRepository
        );

        User utente = new User(
                "utente@test.it",
                "password-test",
                Role.USER
        );

        Issue bug = new Issue(
                "Errore nel login",
                "Non riesco ad accedere",
                IssueType.BUG,
                utente
        );

        Issue feature = new Issue(
                "Aggiungere tema scuro",
                "Vorrei poter scegliere il tema",
                IssueType.FEATURE,
                utente
        );

        // Il repository restituisce due issue di tipo diverso
        when(issueRepository.findAll())
                .thenReturn(List.of(bug, feature));


        // Verifichiamo che tutto sia corretto
        // Filtriamo per BUG
        var response = controller.getIssues(
                IssueType.BUG,
                null,
                null
        );


        // Check
        assertEquals(200, response.getStatusCode().value());

        // La risposta deve contenere soltanto la issue di tipo BUG
        assertEquals(List.of(bug), response.getBody());

        verify(issueRepository).findAll();
    }

@Test
void utenteNonAssegnatarioNonPuoModificareIssue() {

    // 1. PREPARAZIONE

    IssueRepository issueRepository = mock(IssueRepository.class);
    UserRepository userRepository = mock(UserRepository.class);

    IssueController controller = new IssueController(
            issueRepository,
            userRepository
    );

    // Utente che tenta la modifica.
    User utente = mock(User.class);
    when(utente.getId()).thenReturn(2L);
    when(utente.getRole()).thenReturn(Role.USER);

    // Utente diverso, a cui è assegnata la issue.
    User assegnato = mock(User.class);
    when(assegnato.getId()).thenReturn(3L);

    Issue issue = new Issue(
            "Titolo originale",
            "Descrizione originale",
            IssueType.BUG,
            utente
    );

    issue.setPriority(Priority.LOW);
    issue.setAssegnato(assegnato);

    IssueUpdateRequest request = new IssueUpdateRequest(
            "Titolo modificato",
            "Descrizione modificata",
            IssueType.DOCUMENTATION,
            Priority.HIGH
    );

    when(issueRepository.findById(1L))
            .thenReturn(Optional.of(issue));


    // 2. ESECUZIONE

    var response = controller.updateIssue(
            1L,
            request,
            new UserDetailsImpl(utente)
    );


    // 3. VERIFICA

    // La modifica deve essere negata.
    assertEquals(403, response.getStatusCode().value());

    // I dati devono restare invariati.
    assertEquals("Titolo originale", issue.getTitle());
    assertEquals("Descrizione originale", issue.getDescription());
    assertEquals(IssueType.BUG, issue.getType());
    assertEquals(Priority.LOW, issue.getPriority());

    // Non deve essere richiesto alcun salvataggio.
    verify(issueRepository, never()).save(any(Issue.class));
}    



}