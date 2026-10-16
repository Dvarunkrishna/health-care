import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Card from '../components/Card';
import LoadingSpinner from '../components/LoadingSpinner';
import doctorService from '../services/doctorService';

const Doctors = () => {
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const load = (q) => {
    setLoading(true);
    const fn = q ? doctorService.search(q) : doctorService.getAll();
    fn.then((res) => {
      const list = Array.isArray(res) ? res : res?.content || res?.doctors || [];
      setDoctors(list);
    })
      .catch(() => setDoctors([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load('');
  }, []);

  return (
    <div className="page container section">
      <h1 className="section__title">Find a Doctor</h1>
      <div className="search-bar">
        <input
          type="text"
          placeholder="Search by name or specialization..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button className="btn btn--primary" onClick={() => load(search)}>Search</button>
      </div>
      {loading ? (
        <LoadingSpinner text="Loading doctors..." />
      ) : (
        <div className="grid grid--cards">
          {doctors.map((d) => (
            <Card
              key={d.id}
              title={d.fullName}
              subtitle={d.specialization}
              actions={
                <Link to={`/doctors/${d.id}`} className="btn btn--primary btn--sm">
                  View Profile
                </Link>
              }
            >
              <p>{d.departmentName}</p>
              <p className="muted">Fee: ${d.consultationFee ?? '-'}</p>
              <p className="muted">Experience: {d.experienceYears ?? 0} yrs</p>
            </Card>
          ))}
          {doctors.length === 0 && <p className="muted">No doctors found.</p>}
        </div>
      )}
    </div>
  );
};

export default Doctors;
