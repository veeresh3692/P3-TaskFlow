import React from 'react';
import { useAuth } from '../context/AuthContext';
import { User, Bell } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Navbar({ title = 'Taskflow Agile' }) {
  const { user } = useAuth();

  return (
    <header className="topbar">
      <div className="topbar-title">{title}</div>
      <div className="topbar-actions">
        <Link to="/notifications" style={{ color: 'var(--text-muted)', position: 'relative', display: 'flex', alignItems: 'center' }}>
          <Bell size={20} />
        </Link>
        {user && (
          <div className="user-badge">
            <User size={16} />
            <span>{user.name}</span>
            <span className="role-tag">{user.role}</span>
          </div>
        )}
      </div>
    </header>
  );
}
