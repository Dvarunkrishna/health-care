import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import Card from '../components/Card';
import useAuth from '../hooks/useAuth';

const homeForRole = (user) => {
  const role = (user?.role || '').toUpperCase();
  if (role === 'ADMIN') return '/admin';
  if (role === 'DOCTOR') return '/doctor';
  return '/patient';
};

const Register = () => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    role: 'PATIENT',
  });
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) => setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const { user } = await register(form);
      navigate(homeForRole(user), { replace: true });
    } catch (err) {
      setError(err?.response?.data?.message || 'Registration failed');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page auth-page">
      <Card title="Register">
        <form onSubmit={handleSubmit} className="form">
          <div className="form__group">
            <label>Full Name</label>
            <input name="fullName" value={form.fullName} onChange={handleChange} required />
          </div>
          <div className="form__group">
            <label>Email</label>
            <input type="email" name="email" value={form.email} onChange={handleChange} required />
          </div>
          <div className="form__group">
            <label>Phone</label>
            <input name="phone" value={form.phone} onChange={handleChange} required />
          </div>
          <div className="form__group">
            <label>Role</label>
            <select name="role" value={form.role} onChange={handleChange}>
              <option value="PATIENT">Patient</option>
              <option value="DOCTOR">Doctor</option>
            </select>
          </div>
          <div className="form__group">
            <label>Password</label>
            <input type="password" name="password" value={form.password} onChange={handleChange} required />
          </div>
          <div className="form__group">
            <label>Confirm Password</label>
            <input type="password" name="confirmPassword" value={form.confirmPassword} onChange={handleChange} required />
          </div>
          {error && <p className="error">{error}</p>}
          <button type="submit" className="btn btn--primary btn--block" disabled={submitting}>
            {submitting ? 'Creating account...' : 'Register'}
          </button>
        </form>
        <p className="auth-switch">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </Card>
    </div>
  );
};

export default Register;
