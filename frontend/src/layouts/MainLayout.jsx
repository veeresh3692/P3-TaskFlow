import React from 'react';
import { Outlet, Navigate, useLocation } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import Navbar from '../components/Navbar';
import { useAuth } from '../context/AuthContext';

export default function MainLayout() {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div style={{ display: 'flex', height: '100vh', alignItems: 'center', justifyContent: 'center' }}>
        <div style={{ color: 'var(--primary)', fontWeight: 600 }}>Loading Taskflow...</div>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  const getPageTitle = (pathname) => {
    if (pathname === '/') return 'Dashboard Overview';
    if (pathname.startsWith('/projects')) return 'Projects';
    if (pathname.startsWith('/sprints')) return 'Sprints Planning';
    if (pathname.startsWith('/board')) return 'Agile Kanban Board';
    if (pathname.startsWith('/notifications')) return 'Activity Notifications';
    return 'Taskflow';
  };

  return (
    <div className="app-container">
      <Sidebar />
      <div className="main-content">
        <Navbar title={getPageTitle(location.pathname)} />
        <main className="content-body">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
