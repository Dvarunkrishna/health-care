import React, { useEffect, useState } from 'react';
import Card from '../../components/Card';
import LoadingSpinner from '../../components/LoadingSpinner';
import api from '../../services/api';
import doctorService from '../../services/doctorService';
import useAuth from '../../hooks/useAuth';

const DoctorAvailability = () => {
  const { user } = useAuth();
  const [doctorId, setDoctorId] = useState(null);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ availableDate: '', startTime: '', endTime: '', isAvailable: true });
  const [saving, setSaving] = useState(false);

  const loadDoctor = async () => {
    const doctors = await doctorService.getAll();
    const doc = doctors.find((d) => d.email === user?.email);
    if (doc) setDoctorId(doc.id);
    return doc?.id;
  };

  const load = async () => {
    setLoading(true);
    try {
      let id = doctorId;
      if (!id) id = await loadDoctor();
      if (!id) throw new Error('Doctor profile not found');
      const { data } = await api.get(`/api/doctors/${id}/availability`);
      setItems(Array.isArray(data) ? data : data?.content || []);
    } catch {
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line
  }, [user]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((f) => ({ ...f, [name]: type === 'checkbox' ? checked : value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!doctorId) return;
    setSaving(true);
    try {
      await api.post(`/api/doctors/${doctorId}/availability`, form);
      setForm({ availableDate: '', startTime: '', endTime: '', isAvailable: true });
      load();
    } catch (err) {
      alert(err?.response?.data?.message || 'Failed to save availability');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner text="Loading..." />;

  return (
    <div className="page">
      <h1 className="page-title">Manage Availability</h1>
      <Card title="Add Availability">
        <form onSubmit={handleSubmit} className="form form--inline">
          <div className="form__group">
            <label>Date</label>
            <input type="date" name="availableDate" value={form.availableDate} onChange={handleChange} required />
          </div>
          <div className="form__group">
            <label>Start</label>
            <input type="time" name="startTime" value={form.startTime} onChange={handleChange} required />
          </div>
          <div className="form__group">
            <label>End</label>
            <input type="time" name="endTime" value={form.endTime} onChange={handleChange} required />
          </div>
          <div className="form__group form__group--check">
            <label>
              <input type="checkbox" name="isAvailable" checked={form.isAvailable} onChange={handleChange} /> Available
            </label>
          </div>
          <button type="submit" className="btn btn--primary" disabled={saving}>
            {saving ? 'Saving...' : 'Add'}
          </button>
        </form>
      </Card>

      {loading ? (
        <LoadingSpinner text="Loading..." />
      ) : (
        <div className="grid grid--cards" style={{ marginTop: '1rem' }}>
          {items.map((a) => (
            <Card key={a.id} title={`${a.availableDate}`}>
              <p>{a.startTime} - {a.endTime}</p>
              <p>{a.isAvailable ? 'Available' : 'Unavailable'}</p>
            </Card>
          ))}
          {items.length === 0 && <p className="muted">No availability slots.</p>}
        </div>
      )}
    </div>
  );
};

export default DoctorAvailability;