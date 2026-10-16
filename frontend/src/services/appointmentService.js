import api from './api';

const appointmentService = {
  getAll: async () => {
    const { data } = await api.get('/api/admin/appointments');
    return Array.isArray(data) ? data : [];
  },
  getById: async (id) => {
    const { data } = await api.get(`/api/appointments/${id}`);
    return data;
  },
  getMyAppointments: async () => {
    const { data } = await api.get('/api/appointments/my');
    return Array.isArray(data) ? data : [];
  },
  getPatientAppointments: async () => appointmentService.getMyAppointments(),
  getDoctorAppointments: async () => {
    const { data } = await api.get('/api/doctor/appointments');
    return Array.isArray(data) ? data : [];
  },
  book: async (payload) => {
    const { data } = await api.post('/api/appointments', payload);
    return data;
  },
  cancel: async (id) => {
    const { data } = await api.put(`/api/appointments/${id}/cancel`);
    return data;
  },
  reschedule: async (id, payload) => {
    const { data } = await api.put(`/api/appointments/${id}/reschedule`, payload);
    return data;
  },
  updateStatus: async (id, status) => {
    const { data } = await api.put(`/api/doctor/appointments/${id}/status`, { status });
    return data;
  },
};

export default appointmentService;