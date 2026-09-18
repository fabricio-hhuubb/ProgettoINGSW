import api from './api';

export const getComments = async (issueId) => {
    const response = await api.get(`/issues/${issueId}/comments`);
    return response.data;
};

export const addComment = async (issueId, text) => {
    const response = await api.post(`/issues/${issueId}/comments`, { text });
    return response.data;
};