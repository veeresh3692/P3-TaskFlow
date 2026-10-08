import React, { useState, useEffect } from 'react';
import { sprintService } from '../services/sprintService';
import { projectService } from '../services/projectService';
import SprintModal from '../components/SprintModal';
import { Plus, Repeat, Calendar, Trash2, Edit2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function SprintsPage() {
  const [sprints, setSprints] = useState([]);
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedSprint, setSelectedSprint] = useState(null);
  const { user } = useAuth();

  const loadData = async () => {
    try {
      setLoading(true);
      const [sprintsData, projectsData] = await Promise.all([
        sprintService.getSprints(),
        projectService.getProjects(),
      ]);
      setSprints(sprintsData);
      setProjects(projectsData);
    } catch (err) {
      console.error('Failed to load sprints data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleSaveSprint = async (data) => {
    if (selectedSprint) {
      await sprintService.updateSprint(selectedSprint.id, data);
    } else {
      await sprintService.createSprint(data);
    }
    await loadData();
  };

  const handleDeleteSprint = async (id, name) => {
    if (window.confirm(`Are you sure you want to delete sprint "${name}"?`)) {
      try {
        await sprintService.deleteSprint(id);
        setSprints((prev) => prev.filter((s) => s.id !== id));
      } catch (err) {
        alert(err.response?.data?.message || 'Failed to delete sprint');
      }
    }
  };

  const canManage = user?.role === 'ADMIN' || user?.role === 'MANAGER';

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Sprints Planning</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Timeboxed iterations, milestones, and release schedules
          </p>
        </div>

        {canManage && (
          <button 
            className="btn btn-primary"
            onClick={() => {
              setSelectedSprint(null);
              setIsModalOpen(true);
            }}
          >
            <Plus size={16} />
            <span>Plan Sprint</span>
          </button>
        )}
      </div>

      {loading ? (
        <div style={{ color: 'var(--text-muted)' }}>Loading sprints...</div>
      ) : sprints.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem 1rem' }}>
          <Repeat size={48} style={{ color: '#94a3b8', margin: '0 auto 1rem' }} />
          <h3>No Sprints Configured</h3>
          <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
            Plan a sprint to organize tasks into targeted iterations.
          </p>
          {canManage && (
            <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
              <Plus size={16} /> Plan Sprint
            </button>
          )}
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.25rem' }}>
          {sprints.map((sprint) => {
            const progress = sprint.taskCount > 0
              ? Math.round((sprint.completedTaskCount / sprint.taskCount) * 100)
              : 0;

            return (
              <div key={sprint.id} className="card" style={{ display: 'flex', flexDirection: 'column' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                  <div>
                    <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>{sprint.name}</h3>
                    <div style={{ fontSize: '0.8rem', color: 'var(--primary)', fontWeight: 500 }}>
                      {sprint.projectName}
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span className="role-tag" style={{ textTransform: 'uppercase', fontSize: '0.7rem' }}>
                      {sprint.status}
                    </span>

                    {canManage && (
                      <div style={{ display: 'flex', gap: '0.2rem' }}>
                        <button
                          className="btn-sm"
                          style={{ background: 'none', border: 'none', color: '#64748b', cursor: 'pointer' }}
                          onClick={() => {
                            setSelectedSprint(sprint);
                            setIsModalOpen(true);
                          }}
                          title="Edit sprint"
                        >
                          <Edit2 size={14} />
                        </button>
                        <button
                          className="btn-sm"
                          style={{ background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer' }}
                          onClick={() => handleDeleteSprint(sprint.id, sprint.name)}
                          title="Delete sprint"
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    )}
                  </div>
                </div>

                <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', flexGrow: 1, margin: '0.75rem 0' }}>
                  {sprint.goal || 'No sprint goal specified.'}
                </p>

                {/* Progress bar */}
                <div style={{ marginBottom: '1rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', marginBottom: '0.25rem', color: 'var(--text-muted)' }}>
                    <span>Progress ({sprint.completedTaskCount}/{sprint.taskCount} tasks)</span>
                    <span style={{ fontWeight: 600 }}>{progress}%</span>
                  </div>
                  <div style={{ height: '6px', backgroundColor: '#e2e8f0', borderRadius: '3px', overflow: 'hidden' }}>
                    <div style={{ height: '100%', width: `${progress}%`, backgroundColor: '#10b981', borderRadius: '3px' }} />
                  </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.75rem', color: 'var(--text-muted)', borderTop: '1px solid var(--border)', paddingTop: '0.5rem' }}>
                  <Calendar size={13} />
                  <span>
                    {sprint.startDate ? new Date(sprint.startDate).toLocaleDateString() : 'TBD'} — {sprint.endDate ? new Date(sprint.endDate).toLocaleDateString() : 'TBD'}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      )}

      <SprintModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleSaveSprint}
        initialData={selectedSprint}
        projects={projects}
      />
    </div>
  );
}
