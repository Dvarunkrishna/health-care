import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

const Navbar = () => {
  const { user, isAuthenticated, logout, isPatient, isDoctor, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="navbar">
      <div className="navbar__inner container">
        <Link to="/" className="navbar__brand">
          <span className="navbar__logo">MediCare</span>
          <span className="navbar__tag">Healthcare</span>
        </Link>
        <nav className="navbar__links">
          <Link to="/doctors">Doctors</Link>
          {isAuthenticated ? (
            <>
              {isPatient && <Link to="/patient">Dashboard</Link>}
              {isDoctor && <Link to="/doctor">Dashboard</Link>}
              {isAdmin && <Link to="/admin">Dashboard</Link>}
              <span className="navbar__user">{user?.fullName}</span>
              <button className="btn btn--ghost btn--sm" onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn--outline btn--sm">Login</Link>
              <Link to="/register" className="btn btn--primary btn--sm">Register</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Navbar;
