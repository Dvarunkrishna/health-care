import api from './api';
import doctorService from './doctorService';

const adminService = {
  getDashboardStats: async () => (await api.get('/api/admin/dashboard')).data,
  getReports: async () => (await api.get('/api/admin/reports')).data,
  getUsers: async () => (await api.get('/api/admin/users')).data,
  deleteUser: async (id) => { await api.delete(`/api/users/${id}`); },
  getDoctors: async () => doctorService.getAll(),
  createDoctor: async (payload) => doctorService.create(payload),
  updateDoctor: async (id, payload) => doctorService.update(id, payload),
  deleteDoctor: async (id) => doctorService.remove(id),
  getDepartments: async () => (await api.get('/api/departments')).data,
  createDepartment: async (payload) => (await api.post('/api/departments', payload)).data,
  updateDepartment: async (id, payload) => (await api.put(`/api/departments/${id}`, payload)).data,
  deleteDepartment: async (id) => { await api.delete(`/api/departments/${id}`); },
  getAppointments: async () => (await api.get('/api/admin/appointments')).data,
};

export default adminService;