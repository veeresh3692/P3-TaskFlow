import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { projectService } from '../services/projectService';
import { sprintService } from '../services/sprintService';
import { taskService } from '../services/taskService';
import { userService } from '../services/userService';
import TaskModal from '../components/TaskModal';
import SprintModal from '../components/SprintModal';
import { Plus, ArrowLeft, Calendar, Repeat, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function ProjectDetailsPage() {
  const { id } = useParams();
  const [project, setProject] = useState(null);
  const [sprints, setSprints] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [isSprintModalOpen, setIsSprintModalOpen] = useState(false);
  const { user } = useAuth();

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      const [projData, sprintsData, tasksData, usersData] = await Promise.all([
        projectService.getProjectById(id),
        projectService.getProjectSprints(id),
        projectService.getProjectTasks(id),
        userService.getUsers(),
      ]);
      setProject(projData);
      setSprints(sprintsData);
      setTasks(tasksData);
      setUsers(usersData);
    } catch (err) {
      console.error('Failed to load project details:', err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleCreateTask = async (data) => {
    await taskService.createTask({ ...data, projectId: Number(id) });
    await loadData();
  };

  const handleCreateSprint = async (data) => {
    await sprintService.createSprint({ ...data, projectId: Number(id) });
    await loadData();
  };

  const canManage = user?.role === 'ADMIN' || user?.role === 'MANAGER';

  if (loading) {
    return <div style={{ color: 'var(--text-muted)' }}>Loading project details...</div>;
  }

  if (!project) {
    return <div>Project not found</div>;
  }

  return (
    <div>
      <Link to="/projects" style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: 'var(--text-muted)', textDecoration: 'none', marginBottom: '1rem', fontSize: '0.9rem' }}>
        <ArrowLeft size={16} /> Back to Projects
      </Link>

      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 700, marginBottom: '0.5rem' }}>{project.name}</h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', marginBottom: '1rem' }}>
              {project.description || 'No description provided.'}
            </p>
          </div>

          <div style={{ display: 'flex', gap: '0.5rem' }}>
            {canManage && (
              <button className="btn btn-secondary btn-sm" onClick={() => setIsSprintModalOpen(true)}>
                <Plus size={14} /> New Sprint
              </button>
            )}
            <button className="btn btn-primary btn-sm" onClick={() => setIsTaskModalOpen(true)}>
              <Plus size={14} /> New Task
            </button>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '1.5rem', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          <div>Tasks: <b>{tasks.length}</b></div>
          <div>Sprints: <b>{sprints.length}</b></div>
          <div>Created: <b>{new Date(project.createdAt).toLocaleDateString()}</b></div>
        </div>
      </div>

      {/* Sprints Section */}
      <div style={{ marginBottom: '2rem' }}>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 600, marginBottom: '1rem' }}>Project Sprints</h3>
        {sprints.length === 0 ? (
          <div className="card" style={{ color: 'var(--text-muted)', fontSize: '0.9rem', textAlign: 'center', padding: '1.5rem' }}>
            No sprints planned for this project yet.
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1rem' }}>
            {sprints.map((sprint) => (
              <div key={sprint.id} className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                  <h4 style={{ fontWeight: 600 }}>{sprint.name}</h4>
                  <span className={`role-tag`} style={{ textTransform: 'uppercase', fontSize: '0.7rem' }}>
                    {sprint.status}
                  </span>
                </div>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginBottom: '0.75rem' }}>
                  {sprint.goal || 'No goal set'}
                </p>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                  Tasks: {sprint.taskCount} ({sprint.completedTaskCount} done)
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Tasks Section */}
      <div>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 600, marginBottom: '1rem' }}>Project Tasks</h3>
        {tasks.length === 0 ? (
          <div className="card" style={{ color: 'var(--text-muted)', fontSize: '0.9rem', textAlign: 'center', padding: '1.5rem' }}>
            No tasks in this project yet.
          </div>
        ) : (
          <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
              <thead style={{ backgroundColor: '#f8fafc', borderBottom: '1px solid var(--border)' }}>
                <tr>
                  <th style={{ padding: '0.75rem 1rem' }}>Title</th>
                  <th style={{ padding: '0.75rem 1rem' }}>Status</th>
                  <th style={{ padding: '0.75rem 1rem' }}>Priority</th>
                  <th style={{ padding: '0.75rem 1rem' }}>Assignee</th>
                  <th style={{ padding: '0.75rem 1rem' }}>Due Date</th>
                </tr>
              </thead>
              <tbody>
                {tasks.map((task) => (
                  <tr key={task.id} style={{ borderBottom: '1px solid #f1f5f9' }}>
                    <td style={{ padding: '0.75rem 1rem', fontWeight: 500 }}>{task.title}</td>
                    <td style={{ padding: '0.75rem 1rem' }}>
                      <span className="role-tag">{task.status}</span>
                    </td>
                    <td style={{ padding: '0.75rem 1rem' }}>
                      <span className={`priority-badge priority-${task.priority}`}>{task.priority}</span>
                    </td>
                    <td style={{ padding: '0.75rem 1rem', color: 'var(--text-muted)' }}>
                      {task.assigneeName || 'Unassigned'}
                    </td>
                    <td style={{ padding: '0.75rem 1rem', color: 'var(--text-muted)' }}>
                      {task.dueDate || '-'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <TaskModal
        isOpen={isTaskModalOpen}
        onClose={() => setIsTaskModalOpen(false)}
        onSubmit={handleCreateTask}
        projects={[project]}
        sprints={sprints}
        users={users}
      />

      <SprintModal
        isOpen={isSprintModalOpen}
        onClose={() => setIsSprintModalOpen(false)}
        onSubmit={handleCreateSprint}
        projects={[project]}
      />
    </div>
  );
}
