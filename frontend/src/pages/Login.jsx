import React, { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import Card from '../components/Card';
import useAuth from '../hooks/useAuth';

const homeForRole = (user) => {
  const role = (user?.role || '').toUpperCase();
  if (role === 'ADMIN') return '/admin';
  if (role === 'DOCTOR') return '/doctor';
  return '/patient';
};

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) => setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const { user } = await login(form);
      const from = location.state?.from?.pathname || homeForRole(user);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err?.response?.data?.message || 'Invalid credentials');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page auth-page">
      <Card title="Login">
        <form onSubmit={handleSubmit} className="form">
          <div className="form__group">
            <label>Email</label>
            <input type="email" name="email" value={form.email} onChange={handleChange} required />
          </div>
          <div className="form__group">
            <label>Password</label>
            <input type="password" name="password" value={form.password} onChange={handleChange} required />
          </div>
          {error && <p className="error">{error}</p>}
          <button type="submit" className="btn btn--primary btn--block" disabled={submitting}>
            {submitting ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
        <p className="auth-switch">
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </Card>
    </div>
  );
};

export default Login;
