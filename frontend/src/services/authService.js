import api from './api';

export const authService = {
  async login(email, password) {
    const response = await api.post('/auth/login', { email, password });
    if (response.data && response.data.token) {
      localStorage.setItem('taskflow_token', response.data.token);
      localStorage.setItem('taskflow_user', JSON.stringify({
        id: response.data.id,
        name: response.data.name,
        email: response.data.email,
        role: response.data.role,
      }));
    }
    return response.data;
  },

  async register(name, email, password, role = 'MEMBER') {
    const response = await api.post('/auth/register', { name, email, password, role });
    if (response.data && response.data.token) {
      localStorage.setItem('taskflow_token', response.data.token);
      localStorage.setItem('taskflow_user', JSON.stringify({
        id: response.data.id,
        name: response.data.name,
        email: response.data.email,
        role: response.data.role,
      }));
    }
    return response.data;
  },

  async getCurrentUser() {
    const response = await api.get('/auth/me');
    return response.data;
  },

  logout() {
    localStorage.removeItem('taskflow_token');
    localStorage.removeItem('taskflow_user');
    window.location.href = '/login';
  },

  getUser() {
    const userStr = localStorage.getItem('taskflow_user');
    try {
      return userStr ? JSON.parse(userStr) : null;
    } catch (e) {
      return null;
    }
  },

  getToken() {
    return localStorage.getItem('taskflow_token');
  },

  isAuthenticated() {
    return !!localStorage.getItem('taskflow_token');
  }
};
