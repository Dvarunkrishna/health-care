import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Card from '../components/Card';
import LoadingSpinner from '../components/LoadingSpinner';
import doctorService from '../services/doctorService';
import appointmentService from '../services/appointmentService';

const BookAppointment = () => {
  const { doctorId } = useParams();
  const navigate = useNavigate();
  const [doctor, setDoctor] = useState(null);
  const [doctors, setDoctors] = useState([]);
  const [selectedDoctor, setSelectedDoctor] = useState(doctorId || '');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [form, setForm] = useState({
    appointmentDate: '',
    appointmentTime: '',
    reason: '',
  });

  useEffect(() => {
    let mounted = true;
    const tasks = [];
    if (doctorId) tasks.push(doctorService.getById(doctorId).then((r) => mounted && setDoctor(r?.data ?? r)));
    tasks.push(doctorService.getAll().then((r) => {
      const list = Array.isArray(r) ? r : r?.content || r?.doctors || [];
      if (mounted) setDoctors(list);
    }));
    Promise.allSettled(tasks).finally(() => mounted && setLoading(false));
    return () => {
      mounted = false;
    };
  }, [doctorId]);

  const handleChange = (e) => setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await appointmentService.book({
        doctorId: Number(selectedDoctor || doctorId),
        appointmentDate: form.appointmentDate,
        appointmentTime: form.appointmentTime,
        reason: form.reason,
      });
      navigate('/patient/appointments');
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to book appointment');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner text="Loading..." />;

  return (
    <div className="page container section">
      <Card title="Book Appointment">
        {doctor && <p className="muted">Doctor: {doctor.fullName} ({doctor.specialization})</p>}
        <form onSubmit={handleSubmit} className="form">
          {!doctorId && (
            <div className="form__group">
              <label>Doctor</label>
              <select value={selectedDoctor} onChange={(e) => setSelectedDoctor(e.target.value)} required>
                <option value="">Select doctor</option>
                {doctors.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.fullName} - {d.specialization}
                  </option>
                ))}
              </select>
            </div>
          )}
          <div className="form__group">
            <label>Date</label>
            <input type="date" name="appointmentDate" value={form.appointmentDate} onChange={handleChange} required />
          </div>
          <div className="form__group">
            <label>Time</label>
            <input type="time" name="appointmentTime" value={form.appointmentTime} onChange={handleChange} required />
          </div>
          <div className="form__group">
            <label>Reason</label>
            <textarea name="reason" value={form.reason} onChange={handleChange} required rows={3} />
          </div>
          {error && <p className="error">{error}</p>}
          <button type="submit" className="btn btn--primary" disabled={submitting}>
            {submitting ? 'Booking...' : 'Book Appointment'}
          </button>
        </form>
      </Card>
    </div>
  );
};

export default BookAppointment;
