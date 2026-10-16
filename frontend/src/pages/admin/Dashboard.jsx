import React, { useEffect, useState } from 'react';
import Card from '../../components/Card';
import LoadingSpinner from '../../components/LoadingSpinner';
import adminService from '../../services/adminService';

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminService
      .getDashboardStats()
      .then((res) => setStats(res?.data ?? res))
      .catch(() => setStats(null))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner text="Loading dashboard..." />;

  const s = stats || {};
  return (
    <div className="page">
      <h1 className="page-title">Admin Dashboard</h1>
      <div className="grid grid--stats">
        <Card title="Patients"><p className="stat">{s.totalPatients ?? '-'}</p></Card>
        <Card title="Doctors"><p className="stat">{s.totalDoctors ?? '-'}</p></Card>
        <Card title="Appointments"><p className="stat">{s.totalAppointments ?? '-'}</p></Card>
        <Card title="Departments"><p className="stat">{s.totalDepartments ?? '-'}</p></Card>
        <Card title="Today"><p className="stat">{s.appointmentsToday ?? '-'}</p></Card>
        <Card title="Pending"><p className="stat">{s.pendingAppointments ?? '-'}</p></Card>
      </div>
    </div>
  );
};

export default AdminDashboard;
