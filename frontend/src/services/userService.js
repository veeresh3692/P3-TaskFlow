import api from './api';

export const userService = {
  async getUsers() {
    const res = await api.get('/users');
    return res.data;
  },

  async getUserById(id) {
    const res = await api.get(`/users/${id}`);
    return res.data;
  }
};
