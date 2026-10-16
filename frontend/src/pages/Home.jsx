import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from '../components/Card';
import LoadingSpinner from '../components/LoadingSpinner';
import doctorService from '../services/doctorService';

const Home = () => {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;
    doctorService
      .getAll()
      .then((res) => {
        const list = Array.isArray(res) ? res : res?.content || res?.doctors || [];
        if (mounted) setDoctors(list.slice(0, 6));
      })
      .catch(() => {
        if (mounted) setDoctors([]);
      })
      .finally(() => mounted && setLoading(false));
    return () => {
      mounted = false;
    };
  }, []);

  return (
    <div className="page home-page">
      <section className="hero">
        <div className="container hero__inner">
          <div>
            <h1>Your Health, Our Priority</h1>
            <p>Book appointments with top specialists in just a few clicks. Smart scheduling powered by AI insights.</p>
            <div className="hero__actions">
              <Link to="/register" className="btn btn--primary btn--lg">Get Started</Link>
              <Link to="/doctors" className="btn btn--outline btn--lg">Find Doctors</Link>
            </div>
          </div>
          <div className="hero__art">🩺</div>
        </div>
      </section>

      <section className="container section">
        <h2 className="section__title">Featured Doctors</h2>
        {loading ? (
          <LoadingSpinner text="Loading doctors..." />
        ) : doctors.length === 0 ? (
          <p className="muted">No doctors available yet.</p>
        ) : (
          <div className="grid grid--cards">
            {doctors.map((d) => (
              <Card
                key={d.id}
                title={d.fullName}
                subtitle={d.specialization}
                actions={
                  <Link to={`/doctors/${d.id}`} className="btn btn--primary btn--sm">
                    View
                  </Link>
                }
              >
                <p>{d.departmentName}</p>
                <p className="muted">Fee: ${d.consultationFee ?? '-'}</p>
              </Card>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default Home;
