import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

import MainLayout from './layouts/MainLayout.jsx';
import DashboardLayout from './layouts/DashboardLayout.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';

import Home from './pages/Home.jsx';
import Login from './pages/Login.jsx';
import Register from './pages/Register.jsx';
import Doctors from './pages/Doctors.jsx';
import DoctorDetails from './pages/DoctorDetails.jsx';
import BookAppointment from './pages/BookAppointment.jsx';

import PatientDashboard from './pages/patient/Dashboard.jsx';
import PatientProfile from './pages/patient/Profile.jsx';
import PatientAppointments from './pages/patient/Appointments.jsx';

import DoctorDashboard from './pages/doctor/Dashboard.jsx';
import DoctorAvailability from './pages/doctor/Availability.jsx';
import DoctorAppointments from './pages/doctor/Appointments.jsx';

import AdminDashboard from './pages/admin/Dashboard.jsx';
import AdminUsers from './pages/admin/Users.jsx';
import AdminDoctors from './pages/admin/Doctors.jsx';
import AdminDepartments from './pages/admin/Departments.jsx';
import AdminAppointments from './pages/admin/Appointments.jsx';
import AdminReports from './pages/admin/Reports.jsx';

const App = () => (
  <Routes>
    <Route element={<MainLayout />}>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/doctors" element={<Doctors />} />
      <Route path="/doctors/:id" element={<DoctorDetails />} />
      <Route path="/book/:doctorId?" element={<BookAppointment />} />
    </Route>

    <Route
      element={
        <ProtectedRoute roles={['PATIENT', 'USER']}>
          <DashboardLayout />
        </ProtectedRoute>
      }
    >
      <Route path="/patient" element={<PatientDashboard />} />
      <Route path="/patient/profile" element={<PatientProfile />} />
      <Route path="/patient/appointments" element={<PatientAppointments />} />
    </Route>

    <Route
      element={
        <ProtectedRoute roles={['DOCTOR']}>
          <DashboardLayout />
        </ProtectedRoute>
      }
    >
      <Route path="/doctor" element={<DoctorDashboard />} />
      <Route path="/doctor/availability" element={<DoctorAvailability />} />
      <Route path="/doctor/appointments" element={<DoctorAppointments />} />
    </Route>

    <Route
      element={
        <ProtectedRoute roles={['ADMIN']}>
          <DashboardLayout />
        </ProtectedRoute>
      }
    >
      <Route path="/admin" element={<AdminDashboard />} />
      <Route path="/admin/users" element={<AdminUsers />} />
      <Route path="/admin/doctors" element={<AdminDoctors />} />
      <Route path="/admin/departments" element={<AdminDepartments />} />
      <Route path="/admin/appointments" element={<AdminAppointments />} />
      <Route path="/admin/reports" element={<AdminReports />} />
    </Route>

    <Route path="*" element={<Navigate to="/" replace />} />
  </Routes>
);

export default App;
