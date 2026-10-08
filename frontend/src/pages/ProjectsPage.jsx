import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { projectService } from '../services/projectService';
import ProjectModal from '../components/ProjectModal';
import { FolderKanban, Plus, Calendar, ListTodo, Repeat, Trash2, Edit2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function ProjectsPage() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null);
  const { user } = useAuth();

  const loadProjects = async () => {
    try {
      setLoading(true);
      const data = await projectService.getProjects();
      setProjects(data);
    } catch (err) {
      console.error('Failed to load projects:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProjects();
  }, []);

  const handleSaveProject = async (data) => {
    if (selectedProject) {
      await projectService.updateProject(selectedProject.id, data);
    } else {
      await projectService.createProject(data);
    }
    await loadProjects();
  };

  const handleDeleteProject = async (id, name) => {
    if (window.confirm(`Are you sure you want to delete project "${name}"? All associated tasks will be removed.`)) {
      try {
        await projectService.deleteProject(id);
        setProjects((prev) => prev.filter((p) => p.id !== id));
      } catch (err) {
        alert(err.response?.data?.message || 'Failed to delete project');
      }
    }
  };

  const canManage = user?.role === 'ADMIN' || user?.role === 'MANAGER';
  const canDelete = user?.role === 'ADMIN';

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Projects</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Manage workspaces, repositories, and deliverables
          </p>
        </div>

        {canManage && (
          <button 
            className="btn btn-primary"
            onClick={() => {
              setSelectedProject(null);
              setIsModalOpen(true);
            }}
          >
            <Plus size={16} />
            <span>Create Project</span>
          </button>
        )}
      </div>

      {loading ? (
        <div style={{ color: 'var(--text-muted)' }}>Loading projects...</div>
      ) : projects.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem 1rem' }}>
          <FolderKanban size={48} style={{ color: '#94a3b8', margin: '0 auto 1rem' }} />
          <h3>No Projects Created Yet</h3>
          <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
            Create your first project to start organizing tasks and sprints.
          </p>
          {canManage && (
            <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
              <Plus size={16} /> Create Project
            </button>
          )}
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1.25rem' }}>
          {projects.map((project) => (
            <div key={project.id} className="card" style={{ display: 'flex', flexDirection: 'column' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
                <Link 
                  to={`/projects/${project.id}`}
                  style={{ fontSize: '1.1rem', fontWeight: 600, color: 'var(--primary)', textDecoration: 'none' }}
                >
                  {project.name}
                </Link>

                <div style={{ display: 'flex', gap: '0.35rem' }}>
                  {canManage && (
                    <button
                      className="btn-sm"
                      style={{ background: 'none', border: 'none', color: '#64748b', cursor: 'pointer' }}
                      onClick={() => {
                        setSelectedProject(project);
                        setIsModalOpen(true);
                      }}
                      title="Edit project"
                    >
                      <Edit2 size={15} />
                    </button>
                  )}
                  {canDelete && (
                    <button
                      className="btn-sm"
                      style={{ background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer' }}
                      onClick={() => handleDeleteProject(project.id, project.name)}
                      title="Delete project"
                    >
                      <Trash2 size={15} />
                    </button>
                  )}
                </div>
              </div>

              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', flexGrow: 1, marginBottom: '1.25rem' }}>
                {project.description || 'No description provided.'}
              </p>

              <div style={{ display: 'flex', gap: '1rem', borderTop: '1px solid var(--border)', paddingTop: '0.75rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                  <ListTodo size={14} />
                  <span>{project.taskCount} Tasks</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                  <Repeat size={14} />
                  <span>{project.sprintCount} Sprints</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', marginLeft: 'auto' }}>
                  <Calendar size={14} />
                  <span>{new Date(project.createdAt).toLocaleDateString()}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <ProjectModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleSaveProject}
        initialData={selectedProject}
      />
    </div>
  );
}
