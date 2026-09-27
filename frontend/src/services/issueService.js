import api from './api';

export const getIssues = async (filters = {}) => {
    const response = await api.get(`/issues`, { params: filters });
    return response.data;
};

export const createIssue = async (issueData) => {
    const response = await api.post(`/issues`, issueData);
    return response.data;
};

export const updateIssue = async (id, issueData) => {
    const response = await api.put(`/issues/${id}`, issueData);
    return response.data;
};

export const getIssueById = async (id) => {
    const response = await api.get(`/issues/${id}`);
    return response.data;
};

export const assignIssue = async (issueId, utenteId) => {
    const response = await api.put(`/issues/${issueId}/assegnato`, {utenteId});
    return response.data;
};