import api from './api';

const doctorService = {
  getAll: async (params = {}) => {
    const { data } = await api.get('/api/doctors', { params });
    return Array.isArray(data) ? data : [];
  },
  search: async (keyword) => {
    const all = await doctorService.getAll();
    if (!keyword) return all;
    const q = keyword.toLowerCase();
    return all.filter((d) =>
      [d.fullName, d.specialization, d.departmentName]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
        .includes(q)
    );
  },
  getById: async (id) => {
    const { data } = await api.get(`/api/doctors/${id}`);
    return data;
  },
  create: async (payload) => {
    const { data } = await api.post('/api/doctors', payload);
    return data;
  },
  update: async (id, payload) => {
    const { data } = await api.put(`/api/doctors/${id}`, payload);
    return data;
  },
  remove: async (id) => {
    await api.delete(`/api/doctors/${id}`);
    return true;
  },
};

export default doctorService;