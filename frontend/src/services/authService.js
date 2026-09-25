import api from './api';

export const login = async (email, password) => {
    const response = await api.post('/auth/login', { email, password });
    return response.data;
};

export const logout = async () => {
    const response = await api.post('/auth/logout');
    return response.data;
};

// Recupera l'utente collegato attualmente
export const getCurrentUser = async () => {
    const response = await api.get('/auth/me');
    return response.data;
};

// Chiede al backend la creazione di un utente
export const createUser = async (email, password, role) => {
    const response = await api.post('/auth/register', {email, password, role});
    return response.data;
}