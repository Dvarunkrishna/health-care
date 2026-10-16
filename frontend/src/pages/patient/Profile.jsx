import React from 'react';
import Card from '../../components/Card';
import useAuth from '../../hooks/useAuth';

const PatientProfile = () => {
  const { user } = useAuth();
  return (
    <div className="page">
      <h1 className="page-title">My Profile</h1>
      <Card title="Profile Details">
        <p><strong>Name:</strong> {user?.fullName}</p>
        <p><strong>Email:</strong> {user?.email}</p>
        <p><strong>Phone:</strong> {user?.phone || '-'}</p>
        <p><strong>Role:</strong> {(user?.role || '').replace(/^ROLE_/, '')}</p>
      </Card>
    </div>
  );
};

export default PatientProfile;
