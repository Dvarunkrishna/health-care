import api from './api';

const aiService = {
  recommendDoctors: async (params = {}) => {
    const { data } = await api.get('/api/ai/recommend-doctors', { params });
    return data;
  },
  predictNoShow: async (appointmentId) => {
    const { data } = await api.get(`/api/ai/no-show-prediction/${appointmentId}`);
    return data;
  },
};

export default aiService;