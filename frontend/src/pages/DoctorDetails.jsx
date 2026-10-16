import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Card from '../components/Card';
import LoadingSpinner from '../components/LoadingSpinner';
import doctorService from '../services/doctorService';

const DoctorDetails = () => {
  const { id } = useParams();
  const [doctor, setDoctor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let mounted = true;
    doctorService
      .getById(id)
      .then((res) => mounted && setDoctor(res?.data ?? res))
      .catch((err) => mounted && setError(err?.response?.data?.message || 'Failed to load doctor'))
      .finally(() => mounted && setLoading(false));
    return () => {
      mounted = false;
    };
  }, [id]);

  if (loading) return <LoadingSpinner text="Loading doctor..." />;
  if (error) return <div className="page container"><p className="error">{error}</p></div>;
  if (!doctor) return <div className="page container"><p className="muted">Doctor not found.</p></div>;

  return (
    <div className="page container section">
      <Card
        title={doctor.fullName}
        subtitle={doctor.specialization}
        actions={
          <Link to={`/book/${doctor.id}`} className="btn btn--primary">
            Book Appointment
          </Link>
        }
      >
        <p><strong>Department:</strong> {doctor.departmentName}</p>
        <p><strong>Qualification:</strong> {doctor.qualification}</p>
        <p><strong>Experience:</strong> {doctor.experienceYears} years</p>
        <p><strong>Consultation Fee:</strong> ${doctor.consultationFee}</p>
        <p>{doctor.description}</p>
      </Card>
    </div>
  );
};

export default DoctorDetails;
