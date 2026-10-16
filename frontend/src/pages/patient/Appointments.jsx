import React, { useEffect, useState } from 'react';
import Card from '../../components/Card';
import LoadingSpinner from '../../components/LoadingSpinner';
import appointmentService from '../../services/appointmentService';

const PatientAppointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    appointmentService
      .getPatientAppointments()
      .then((res) => {
        const list = Array.isArray(res) ? res : res?.content || res?.appointments || [];
        setAppointments(list);
      })
      .catch(() => setAppointments([]))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const handleCancel = async (id) => {
    if (!window.confirm('Cancel this appointment?')) return;
    try {
      await appointmentService.cancel(id);
      load();
    } catch (e) {
      alert('Failed to cancel');
    }
  };

  if (loading) return <LoadingSpinner text="Loading appointments..." />;

  return (
    <div className="page">
      <h1 className="page-title">My Appointments</h1>
      {appointments.length === 0 ? (
        <p className="muted">No appointments found.</p>
      ) : (
        <div className="grid grid--cards">
          {appointments.map((a) => (
            <Card
              key={a.id}
              title={`${a.doctorName || 'Doctor'} - ${a.specialization || ''}`}
              subtitle={`${a.appointmentDate} ${a.appointmentTime}`}
            >
              <p><strong>Status:</strong> {a.status}</p>
              <p><strong>Reason:</strong> {a.reason}</p>
              {String(a.status).toUpperCase() !== 'CANCELLED' && (
                <button className="btn btn--danger btn--sm" onClick={() => handleCancel(a.id)}>
                  Cancel
                </button>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default PatientAppointments;
