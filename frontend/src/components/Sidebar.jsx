import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  FolderKanban, 
  Repeat, 
  Kanban, 
  Bell, 
  CheckSquare,
  LogOut
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Sidebar() {
  const { logout, user } = useAuth();

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <CheckSquare size={26} />
        <span>TASKFLOW</span>
      </div>

      <nav className="sidebar-nav">
        <NavLink to="/" end className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
          <LayoutDashboard size={18} />
          <span>Dashboard</span>
        </NavLink>

        <NavLink to="/projects" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
          <FolderKanban size={18} />
          <span>Projects</span>
        </NavLink>

        <NavLink to="/sprints" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
          <Repeat size={18} />
          <span>Sprints</span>
        </NavLink>

        <NavLink to="/board" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
          <Kanban size={18} />
          <span>Task Board</span>
        </NavLink>

        <NavLink to="/notifications" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
          <Bell size={18} />
          <span>Notifications</span>
        </NavLink>
      </nav>

      <div style={{ paddingTop: '1rem', borderTop: '1px solid rgba(255,255,255,0.1)' }}>
        <button 
          onClick={logout} 
          className="nav-link" 
          style={{ width: '100%', background: 'none', border: 'none', cursor: 'pointer', textAlign: 'left' }}
        >
          <LogOut size={18} />
          <span>Logout</span>
        </button>
      </div>
    </aside>
  );
}
