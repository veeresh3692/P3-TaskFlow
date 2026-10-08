import api from './api';

export const sprintService = {
  async getSprints() {
    const res = await api.get('/sprints');
    return res.data;
  },

  async getSprintById(id) {
    const res = await api.get(`/sprints/${id}`);
    return res.data;
  },

  async createSprint(data) {
    const res = await api.post('/sprints', data);
    return res.data;
  },

  async updateSprint(id, data) {
    const res = await api.put(`/sprints/${id}`, data);
    return res.data;
  },

  async deleteSprint(id) {
    await api.delete(`/sprints/${id}`);
  },

  async getSprintTasks(sprintId) {
    const res = await api.get(`/sprints/${sprintId}/tasks`);
    return res.data;
  }
};
