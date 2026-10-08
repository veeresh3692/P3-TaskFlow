import React, { useState, useEffect } from 'react';
import Modal from './Modal';

export default function SprintModal({
  isOpen,
  onClose,
  onSubmit,
  initialData = null,
  projects = [],
}) {
  const [formData, setFormData] = useState({
    name: '',
    goal: '',
    startDate: '',
    endDate: '',
    status: 'PLANNED',
    projectId: '',
  });

  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name || '',
        goal: initialData.goal || '',
        startDate: initialData.startDate || '',
        endDate: initialData.endDate || '',
        status: initialData.status || 'PLANNED',
        projectId: initialData.projectId || '',
      });
    } else {
      setFormData({
        name: '',
        goal: '',
        startDate: '',
        endDate: '',
        status: 'PLANNED',
        projectId: projects.length > 0 ? projects[0].id : '',
      });
    }
    setError('');
  }, [initialData, isOpen, projects]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!formData.name.trim()) {
      setError('Sprint name is required');
      return;
    }
    if (!formData.projectId) {
      setError('Project is required');
      return;
    }
    if (formData.startDate && formData.endDate && formData.endDate < formData.startDate) {
      setError('End date cannot be earlier than start date');
      return;
    }

    try {
      setSubmitting(true);
      await onSubmit({
        name: formData.name.trim(),
        goal: formData.goal.trim(),
        startDate: formData.startDate || null,
        endDate: formData.endDate || null,
        status: formData.status,
        projectId: Number(formData.projectId),
      });
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save sprint');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={initialData ? 'Edit Sprint' : 'Create New Sprint'}
    >
      <form onSubmit={handleSubmit}>
        {error && <div className="form-error" style={{ marginBottom: '1rem' }}>{error}</div>}

        <div className="form-group">
          <label>Sprint Name *</label>
          <input
            type="text"
            className="input"
            placeholder="e.g. Sprint 2 - User Onboarding"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            required
          />
        </div>

        <div className="form-group">
          <label>Project *</label>
          <select
            className="select"
            value={formData.projectId}
            onChange={(e) => setFormData({ ...formData, projectId: e.target.value })}
            required
          >
            <option value="">Select Project</option>
            {projects.map((p) => (
              <option key={p.id} value={p.id}>{p.name}</option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Sprint Goal</label>
          <textarea
            className="textarea"
            rows="2"
            placeholder="Target outcome for this iteration..."
            value={formData.goal}
            onChange={(e) => setFormData({ ...formData, goal: e.target.value })}
          />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label>Start Date</label>
            <input
              type="date"
              className="input"
              value={formData.startDate}
              onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
            />
          </div>

          <div className="form-group">
            <label>End Date</label>
            <input
              type="date"
              className="input"
              value={formData.endDate}
              onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
            />
          </div>
        </div>

        <div className="form-group">
          <label>Status</label>
          <select
            className="select"
            value={formData.status}
            onChange={(e) => setFormData({ ...formData, status: e.target.value })}
          >
            <option value="PLANNED">Planned</option>
            <option value="ACTIVE">Active</option>
            <option value="COMPLETED">Completed</option>
          </select>
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
          <button type="button" className="btn btn-secondary" onClick={onClose} disabled={submitting}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {submitting ? 'Saving...' : initialData ? 'Update Sprint' : 'Create Sprint'}
          </button>
        </div>
      </form>
    </Modal>
  );
}
