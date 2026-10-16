import React, { useEffect, useState } from 'react';
import Card from '../../components/Card';
import LoadingSpinner from '../../components/LoadingSpinner';
import appointmentService from '../../services/appointmentService';

const DoctorDashboard = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    appointmentService
      .getDoctorAppointments()
      .then((res) => {
        const list = Array.isArray(res) ? res : res?.content || res?.appointments || [];
        setAppointments(list);
      })
      .catch(() => setAppointments([]))
      .finally(() => setLoading(false));
  }, []);

  const pending = appointments.filter((a) => (a.status || '').toUpperCase() === 'PENDING' || (a.status || '').toUpperCase() === 'BOOKED');

  if (loading) return <LoadingSpinner text="Loading..." />;

  return (
    <div className="page">
      <h1 className="page-title">Doctor Dashboard</h1>
      <div className="grid grid--stats">
        <Card title="Total Appointments">
          <p className="stat">{appointments.length}</p>
        </Card>
        <Card title="Pending">
          <p className="stat">{pending.length}</p>
        </Card>
      </div>
    </div>
  );
};

export default DoctorDashboard;
