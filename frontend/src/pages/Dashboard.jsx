import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { dashboardService } from '../services/dashboardService';
import { projectService } from '../services/projectService';
import { sprintService } from '../services/sprintService';
import { userService } from '../services/userService';
import { taskService } from '../services/taskService';
import TaskModal from '../components/TaskModal';
import { 
  FolderKanban, 
  Repeat, 
  CheckCircle2, 
  ListTodo, 
  AlertTriangle,
  Plus,
  ArrowRight
} from 'lucide-react';

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [projects, setProjects] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [users, setUsers] = useState([]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [statsData, projectsData, sprintsData, usersData] = await Promise.all([
        dashboardService.getStats(),
        projectService.getProjects(),
        sprintService.getSprints(),
        userService.getUsers(),
      ]);
      setStats(statsData);
      setProjects(projectsData);
      setSprints(sprintsData);
      setUsers(usersData);
    } catch (err) {
      console.error('Failed to load dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreateTask = async (taskData) => {
    await taskService.createTask(taskData);
    await loadData();
  };

  if (loading) {
    return <div style={{ color: 'var(--text-muted)' }}>Loading dashboard metrics...</div>;
  }

  const completionPercentage = stats && stats.totalTasks > 0
    ? Math.round((stats.completedTasks / stats.totalTasks) * 100)
    : 0;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Agile Project Overview</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Real-time team velocity and delivery metrics</p>
        </div>

        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <button className="btn btn-primary" onClick={() => setIsTaskModalOpen(true)}>
            <Plus size={16} />
            <span>Create Task</span>
          </button>
          <Link to="/board" className="btn btn-secondary">
            <span>View Board</span>
            <ArrowRight size={16} />
          </Link>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}>
            <FolderKanban size={24} />
          </div>
          <div>
            <div className="stat-val">{stats?.totalProjects || 0}</div>
            <div className="stat-label">Total Projects</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#faf5ff', color: '#8b5cf6' }}>
            <Repeat size={24} />
          </div>
          <div>
            <div className="stat-val">{stats?.activeSprints || 0}</div>
            <div className="stat-label">Active Sprints</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#f0fdf4', color: '#16a34a' }}>
            <ListTodo size={24} />
          </div>
          <div>
            <div className="stat-val">{stats?.totalTasks || 0}</div>
            <div className="stat-label">Total Tasks</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#059669' }}>
            <CheckCircle2 size={24} />
          </div>
          <div>
            <div className="stat-val">{stats?.completedTasks || 0} ({completionPercentage}%)</div>
            <div className="stat-label">Completed Tasks</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fef2f2', color: '#dc2626' }}>
            <AlertTriangle size={24} />
          </div>
          <div>
            <div className="stat-val">{stats?.highPriorityTasks || 0}</div>
            <div className="stat-label">High / Critical Priority</div>
          </div>
        </div>
      </div>

      {/* Charts & Breakdowns */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem', marginBottom: '2rem' }}>
        {/* Status Distribution */}
        <div className="card">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '1rem' }}>Tasks by Status</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {[
              { label: 'To Do', key: 'TODO', color: '#64748b' },
              { label: 'In Progress', key: 'IN_PROGRESS', color: '#2563eb' },
              { label: 'In Review', key: 'IN_REVIEW', color: '#8b5cf6' },
              { label: 'Done', key: 'DONE', color: '#10b981' },
            ].map((col) => {
              const count = stats?.tasksByStatus?.[col.key] || 0;
              const pct = stats?.totalTasks > 0 ? (count / stats.totalTasks) * 100 : 0;
              return (
                <div key={col.key}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                    <span>{col.label}</span>
                    <span style={{ fontWeight: 600 }}>{count} ({Math.round(pct)}%)</span>
                  </div>
                  <div style={{ height: '8px', backgroundColor: '#e2e8f0', borderRadius: '4px', overflow: 'hidden' }}>
                    <div style={{ height: '100%', width: `${pct}%`, backgroundColor: col.color, borderRadius: '4px' }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Priority Distribution */}
        <div className="card">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '1rem' }}>Tasks by Priority</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {[
              { label: 'Critical', key: 'CRITICAL', color: '#ef4444' },
              { label: 'High', key: 'HIGH', color: '#f59e0b' },
              { label: 'Medium', key: 'MEDIUM', color: '#3b82f6' },
              { label: 'Low', key: 'LOW', color: '#10b981' },
            ].map((p) => {
              const count = stats?.tasksByPriority?.[p.key] || 0;
              const pct = stats?.totalTasks > 0 ? (count / stats.totalTasks) * 100 : 0;
              return (
                <div key={p.key}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                    <span>{p.label}</span>
                    <span style={{ fontWeight: 600 }}>{count} ({Math.round(pct)}%)</span>
                  </div>
                  <div style={{ height: '8px', backgroundColor: '#e2e8f0', borderRadius: '4px', overflow: 'hidden' }}>
                    <div style={{ height: '100%', width: `${pct}%`, backgroundColor: p.color, borderRadius: '4px' }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      <TaskModal
        isOpen={isTaskModalOpen}
        onClose={() => setIsTaskModalOpen(false)}
        onSubmit={handleCreateTask}
        projects={projects}
        sprints={sprints}
        users={users}
      />
    </div>
  );
}
