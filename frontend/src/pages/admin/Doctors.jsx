import React, { useEffect, useState } from 'react';
import Card from '../../components/Card';
import LoadingSpinner from '../../components/LoadingSpinner';
import adminService from '../../services/adminService';

const AdminDoctors = () => {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    adminService
      .getDoctors()
      .then((res) => {
        const list = Array.isArray(res) ? res : res?.content || res?.doctors || [];
        setDoctors(list);
      })
      .catch(() => setDoctors([]))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Delete doctor?')) return;
    try {
      await adminService.deleteDoctor(id);
      load();
    } catch {
      alert('Failed to delete doctor');
    }
  };

  if (loading) return <LoadingSpinner text="Loading doctors..." />;

  return (
    <div className="page">
      <h1 className="page-title">Doctors</h1>
      <div className="table-wrapper">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Specialization</th>
              <th>Department</th>
              <th>Fee</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {doctors.map((d) => (
              <tr key={d.id}>
                <td>{d.id}</td>
                <td>{d.fullName}</td>
                <td>{d.specialization}</td>
                <td>{d.departmentName}</td>
                <td>${d.consultationFee}</td>
                <td>
                  <button className="btn btn--danger btn--sm" onClick={() => handleDelete(d.id)}>Delete</button>
                </td>
              </tr>
            ))}
            {doctors.length === 0 && (
              <tr><td colSpan="6" className="muted">No doctors.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminDoctors;
