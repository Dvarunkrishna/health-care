import React, { useEffect, useState } from 'react';
import Card from '../../components/Card';
import LoadingSpinner from '../../components/LoadingSpinner';
import appointmentService from '../../services/appointmentService';

const PatientDashboard = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    appointmentService
      .getPatientAppointments()
      .then((res) => {
        const list = Array.isArray(res) ? res : res?.content || res?.appointments || [];
        setAppointments(list);
      })
      .catch(() => setAppointments([]))
      .finally(() => setLoading(false));
  }, []);

  const upcoming = appointments.filter((a) => (a.status || '').toUpperCase() !== 'CANCELLED');

  return (
    <div className="page">
      <h1 className="page-title">Patient Dashboard</h1>
      <div className="grid grid--stats">
        <Card title="Total Appointments">
          <p className="stat">{appointments.length}</p>
        </Card>
        <Card title="Upcoming">
          <p className="stat">{upcoming.length}</p>
        </Card>
      </div>
      <Card title="Quick Actions">
        <div className="actions">
          <a href="/doctors" className="btn btn--primary">Find Doctors</a>
          <a href="/book" className="btn btn--outline">Book Appointment</a>
        </div>
      </Card>
    </div>
  );
};

export default PatientDashboard;
