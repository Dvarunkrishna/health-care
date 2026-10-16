import React, { useEffect, useState } from 'react';
import Card from '../../components/Card';
import LoadingSpinner from '../../components/LoadingSpinner';
import adminService from '../../services/adminService';

const AdminAppointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminService
      .getAppointments()
      .then((res) => {
        const list = Array.isArray(res) ? res : res?.content || res?.appointments || [];
        setAppointments(list);
      })
      .catch(() => setAppointments([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner text="Loading appointments..." />;

  return (
    <div className="page">
      <h1 className="page-title">Appointments</h1>
      <div className="table-wrapper">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Patient</th>
              <th>Doctor</th>
              <th>Date</th>
              <th>Time</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {appointments.map((a) => (
              <tr key={a.id}>
                <td>{a.id}</td>
                <td>{a.patientName}</td>
                <td>{a.doctorName}</td>
                <td>{a.appointmentDate}</td>
                <td>{a.appointmentTime}</td>
                <td>{a.status}</td>
              </tr>
            ))}
            {appointments.length === 0 && (
              <tr><td colSpan="6" className="muted">No appointments.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminAppointments;
