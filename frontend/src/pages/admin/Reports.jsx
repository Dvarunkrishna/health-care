import React, { useEffect, useState } from 'react';
import Card from '../../components/Card';
import LoadingSpinner from '../../components/LoadingSpinner';
import adminService from '../../services/adminService';

const AdminReports = () => {
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminService
      .getReports()
      .catch(() => adminService.getDashboardStats())
      .then((res) => setReport(res?.data ?? res))
      .catch(() => setReport(null))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner text="Loading reports..." />;

  return (
    <div className="page">
      <h1 className="page-title">Reports</h1>
      <Card title="Summary">
        <pre className="report-json">{JSON.stringify(report, null, 2)}</pre>
      </Card>
    </div>
  );
};

export default AdminReports;
