# BugBoard26

Progetto di Ingegneria del Software, a.a. 2025/2026, Università degli Studi di Napoli Federico II.

BugBoard26 è un'applicazione web per gestire le segnalazioni (issue) di un progetto software. Gli utenti possono creare issue, vederle in una lista, filtrarle e commentarle. L'amministratore, in più, può creare nuovi utenti e assegnare le issue. Una issue può essere modificata solo da chi ne è assegnatario oppure da un amministratore.

Il progetto è diviso in due parti: il backend in Java con Spring Boot e il frontend in React. I dati vengono salvati in un database PostgreSQL.

## Cosa serve

- Java 17 o superiore
- Node.js e npm
- PostgreSQL

## 1. Database

Basta creare un database vuoto chiamato `bugboard26`. Le tabelle non vanno create a mano: ci pensa il backend al primo avvio.

Se il vostro PostgreSQL usa un utente o una password diversi, cambiateli nel file `backend/src/main/resources/application.properties` (`spring.datasource.username` e `spring.datasource.password`).

## 2. Avviare il backend

cd backend  
.\mvnw.cmd spring-boot:run


Su Linux o Mac il comando è `./mvnw spring-boot:run`. Non serve installare Maven, perché lo scarica il wrapper. La prima volta ci mette un po', dato che deve scaricare anche le dipendenze. Il backend parte su http://localhost:8080.

## 3. Avviare il frontend

In un altro terminale:

cd frontend
npm install
npm start


`npm install` serve solo la prima volta. Il frontend si apre su http://localhost:3000.

## Primo accesso

Al primo avvio il backend crea da solo un amministratore:

- email: admin@bugboard26.com
- password: admin123

Da lì si possono creare gli altri utenti, scegliendo se renderli utenti normali o amministratori.

## Test

I test di unità (JUnit e Mockito) si lanciano dalla cartella del backend:

cd backend
.\mvnw.cmd test

## Struttura del progetto

- `backend/`: API REST in Spring Boot, divise in controller, model, repository, dto, security e config
- `frontend/`: interfaccia in React, divisa in pages (le schermate) e services (le chiamate al backend)