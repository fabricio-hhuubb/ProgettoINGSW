import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import IssueListPage from './pages/IssueListPage';
import IssueFormPage from './pages/IssueFormPage';
import IssueDetailPage from './pages/IssueDetailPage';
import CreateUserPage from './pages/CreateUserPage';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/issues/new" element={<IssueFormPage />} />
                <Route path="/issues/:id" element={<IssueDetailPage />} />
                <Route path="/issues" element={<IssueListPage />} />
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/users/new" element={<CreateUserPage />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;