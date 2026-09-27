import api from './api';

// Recupera gli utenti da mostrare nel menu
export const getUsers = async () => {
    const response = await api.get('/users');
    return response.data;
};

