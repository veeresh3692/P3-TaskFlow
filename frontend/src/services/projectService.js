import api from './api';

export const projectService = {
  async getProjects() {
    const res = await api.get('/projects');
    return res.data;
  },

  async getProjectById(id) {
    const res = await api.get(`/projects/${id}`);
    return res.data;
  },

  async createProject(data) {
    const res = await api.post('/projects', data);
    return res.data;
  },

  async updateProject(id, data) {
    const res = await api.put(`/projects/${id}`, data);
    return res.data;
  },

  async deleteProject(id) {
    await api.delete(`/projects/${id}`);
  },

  async getProjectTasks(projectId) {
    const res = await api.get(`/projects/${projectId}/tasks`);
    return res.data;
  },

  async getProjectSprints(projectId) {
    const res = await api.get(`/projects/${projectId}/sprints`);
    return res.data;
  }
};
