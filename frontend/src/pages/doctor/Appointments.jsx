import React, { useEffect, useState } from 'react';
import Card from '../../components/Card';
import LoadingSpinner from '../../components/LoadingSpinner';
import appointmentService from '../../services/appointmentService';

const DoctorAppointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    appointmentService
      .getDoctorAppointments()
      .then((res) => {
        const list = Array.isArray(res) ? res : res?.content || res?.appointments || [];
        setAppointments(list);
      })
      .catch(() => setAppointments([]))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const updateStatus = async (id, status) => {
    try {
      await appointmentService.updateStatus(id, status);
      load();
    } catch {
      alert('Failed to update status');
    }
  };

  if (loading) return <LoadingSpinner text="Loading appointments..." />;

  return (
    <div className="page">
      <h1 className="page-title">Appointments</h1>
      {appointments.length === 0 ? (
        <p className="muted">No appointments.</p>
      ) : (
        <div className="grid grid--cards">
          {appointments.map((a) => (
            <Card key={a.id} title={`${a.patientName || 'Patient'}`} subtitle={`${a.appointmentDate} ${a.appointmentTime}`}>
              <p><strong>Status:</strong> {a.status}</p>
              <p><strong>Reason:</strong> {a.reason}</p>
              <div className="actions">
                <button className="btn btn--primary btn--sm" onClick={() => updateStatus(a.id, 'CONFIRMED')}>Confirm</button>
                <button className="btn btn--danger btn--sm" onClick={() => updateStatus(a.id, 'CANCELLED')}>Cancel</button>
                <button className="btn btn--outline btn--sm" onClick={() => updateStatus(a.id, 'COMPLETED')}>Complete</button>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default DoctorAppointments;
