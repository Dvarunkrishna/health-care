import React from 'react';
import { NavLink } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

const menus = {
  patient: [
    { to: '/patient', label: 'Dashboard', icon: '📊' },
    { to: '/patient/profile', label: 'Profile', icon: '👤' },
    { to: '/patient/appointments', label: 'Appointments', icon: '📅' },
  ],
  doctor: [
    { to: '/doctor', label: 'Dashboard', icon: '📊' },
    { to: '/doctor/availability', label: 'Availability', icon: '🕐' },
    { to: '/doctor/appointments', label: 'Appointments', icon: '📅' },
  ],
  admin: [
    { to: '/admin', label: 'Dashboard', icon: '📊' },
    { to: '/admin/users', label: 'Users', icon: '👥' },
    { to: '/admin/doctors', label: 'Doctors', icon: '🩺' },
    { to: '/admin/departments', label: 'Departments', icon: '🏥' },
    { to: '/admin/appointments', label: 'Appointments', icon: '📅' },
    { to: '/admin/reports', label: 'Reports', icon: '📈' },
  ],
};

const Sidebar = ({ links }) => {
  const { isAdmin, isDoctor } = useAuth();
  let role = 'patient';
  if (isAdmin) role = 'admin';
  else if (isDoctor) role = 'doctor';
  const items = links || menus[role];

  return (
    <aside className="sidebar">
      <ul className="sidebar__list">
        {items.map((item) => (
          <li key={item.to}>
            <NavLink
              to={item.to}
              end={item.to === '/patient' || item.to === '/doctor' || item.to === '/admin'}
              className={({ isActive }) => `sidebar__link${isActive ? ' sidebar__link--active' : ''}`}
            >
              <span className="sidebar__icon">{item.icon}</span>
              {item.label}
            </NavLink>
          </li>
        ))}
      </ul>
    </aside>
  );
};

export default Sidebar;
