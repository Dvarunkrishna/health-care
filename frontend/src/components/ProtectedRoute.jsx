import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

const homeForRole = (user) => {
  const role = (user?.role || '').toUpperCase();
  if (role === 'ADMIN') return '/admin';
  if (role === 'DOCTOR') return '/doctor';
  return '/patient';
};

const ProtectedRoute = ({ roles, children }) => {
  const { isAuthenticated, user, hasRole } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (roles && roles.length > 0) {
    const allowed = roles.some((r) => hasRole(r));
    if (!allowed) {
      return <Navigate to={homeForRole(user)} replace />;
    }
  }

  return children ? children : null;
};

export default ProtectedRoute;
