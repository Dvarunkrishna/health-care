import React, { useEffect, useState } from 'react';
import Card from '../../components/Card';
import LoadingSpinner from '../../components/LoadingSpinner';
import adminService from '../../services/adminService';

const AdminDepartments = () => {
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');

  const load = () => {
    setLoading(true);
    adminService
      .getDepartments()
      .then((res) => {
        const list = Array.isArray(res) ? res : res?.content || res?.departments || [];
        setDepartments(list);
      })
      .catch(() => setDepartments([]))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await adminService.createDepartment({ departmentName: name, description });
      setName('');
      setDescription('');
      load();
    } catch {
      alert('Failed to create department');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete department?')) return;
    try {
      await adminService.deleteDepartment(id);
      load();
    } catch {
      alert('Failed to delete department');
    }
  };

  if (loading) return <LoadingSpinner text="Loading departments..." />;

  return (
    <div className="page">
      <h1 className="page-title">Departments</h1>
      <Card title="Add Department">
        <form onSubmit={handleCreate} className="form form--inline">
          <div className="form__group">
            <label>Name</label>
            <input value={name} onChange={(e) => setName(e.target.value)} required />
          </div>
          <div className="form__group">
            <label>Description</label>
            <input value={description} onChange={(e) => setDescription(e.target.value)} />
          </div>
          <button type="submit" className="btn btn--primary">Add</button>
        </form>
      </Card>

      <div className="table-wrapper" style={{ marginTop: '1rem' }}>
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Description</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {departments.map((d) => (
              <tr key={d.id}>
                <td>{d.id}</td>
                <td>{d.departmentName}</td>
                <td>{d.description}</td>
                <td>
                  <button className="btn btn--danger btn--sm" onClick={() => handleDelete(d.id)}>Delete</button>
                </td>
              </tr>
            ))}
            {departments.length === 0 && (
              <tr><td colSpan="4" className="muted">No departments.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminDepartments;
