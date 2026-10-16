import React from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import useAuth from '../hooks/useAuth';

const DashboardLayout = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="dashboard-shell">
      <Sidebar />
      <div className="dashboard-main">
        <header className="dashboard-header">
          <div>
            <h2 className="dashboard-header__title">Welcome, {user?.fullName || 'User'}</h2>
            <p className="dashboard-header__subtitle">{(user?.role || '').replace(/^ROLE_/, '')}</p>
          </div>
          <button
            className="btn btn--outline btn--sm"
            onClick={() => {
              logout();
              navigate('/');
            }}
          >
            Logout
          </button>
        </header>
        <div className="dashboard-content">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default DashboardLayout;
