import React, { useState, useEffect } from 'react';
import Modal from './Modal';

export default function TaskModal({
  isOpen,
  onClose,
  onSubmit,
  initialData = null,
  projects = [],
  sprints = [],
  users = [],
}) {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    status: 'TODO',
    priority: 'MEDIUM',
    dueDate: '',
    projectId: '',
    sprintId: '',
    assigneeId: '',
  });

  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (initialData) {
      setFormData({
        title: initialData.title || '',
        description: initialData.description || '',
        status: initialData.status || 'TODO',
        priority: initialData.priority || 'MEDIUM',
        dueDate: initialData.dueDate || '',
        projectId: initialData.projectId || '',
        sprintId: initialData.sprintId || '',
        assigneeId: initialData.assigneeId || '',
      });
    } else {
      setFormData({
        title: '',
        description: '',
        status: 'TODO',
        priority: 'MEDIUM',
        dueDate: '',
        projectId: projects.length > 0 ? projects[0].id : '',
        sprintId: '',
        assigneeId: '',
      });
    }
    setErrors({});
  }, [initialData, isOpen, projects]);

  const filteredSprints = sprints.filter(
    (s) => !formData.projectId || s.projectId === Number(formData.projectId)
  );

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});

    // Client validation
    if (!formData.title || formData.title.trim().length < 3) {
      setErrors({ title: 'Title must be at least 3 characters' });
      return;
    }
    if (!formData.projectId) {
      setErrors({ projectId: 'Project is required' });
      return;
    }

    try {
      setSubmitting(true);
      const payload = {
        title: formData.title.trim(),
        description: formData.description.trim(),
        status: formData.status,
        priority: formData.priority,
        dueDate: formData.dueDate || null,
        projectId: Number(formData.projectId),
        sprintId: formData.sprintId ? Number(formData.sprintId) : null,
        assigneeId: formData.assigneeId ? Number(formData.assigneeId) : null,
      };

      await onSubmit(payload);
      onClose();
    } catch (err) {
      if (err.response && err.response.data && err.response.data.validationErrors) {
        setErrors(err.response.data.validationErrors);
      } else {
        setErrors({ general: err.response?.data?.message || 'Failed to save task' });
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={initialData ? 'Edit Task' : 'Create New Task'}
    >
      <form onSubmit={handleSubmit}>
        {errors.general && (
          <div style={{ color: '#ef4444', marginBottom: '1rem', fontSize: '0.85rem' }}>
            {errors.general}
          </div>
        )}

        <div className="form-group">
          <label>Title *</label>
          <input
            type="text"
            className="input"
            placeholder="e.g. Implement Kafka event consumer"
            value={formData.title}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            required
          />
          {errors.title && <div className="form-error">{errors.title}</div>}
        </div>

        <div className="form-group">
          <label>Description</label>
          <textarea
            className="textarea"
            rows="3"
            placeholder="Provide context, acceptance criteria..."
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label>Project *</label>
            <select
              className="select"
              value={formData.projectId}
              onChange={(e) => setFormData({ ...formData, projectId: e.target.value, sprintId: '' })}
              required
            >
              <option value="">Select Project</option>
              {projects.map((p) => (
                <option key={p.id} value={p.id}>{p.name}</option>
              ))}
            </select>
            {errors.projectId && <div className="form-error">{errors.projectId}</div>}
          </div>

          <div className="form-group">
            <label>Sprint</label>
            <select
              className="select"
              value={formData.sprintId}
              onChange={(e) => setFormData({ ...formData, sprintId: e.target.value })}
            >
              <option value="">Backlog (No Sprint)</option>
              {filteredSprints.map((s) => (
                <option key={s.id} value={s.id}>{s.name} ({s.status})</option>
              ))}
            </select>
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label>Status</label>
            <select
              className="select"
              value={formData.status}
              onChange={(e) => setFormData({ ...formData, status: e.target.value })}
            >
              <option value="TODO">To Do</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="IN_REVIEW">In Review</option>
              <option value="DONE">Done</option>
            </select>
          </div>

          <div className="form-group">
            <label>Priority</label>
            <select
              className="select"
              value={formData.priority}
              onChange={(e) => setFormData({ ...formData, priority: e.target.value })}
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label>Assignee</label>
            <select
              className="select"
              value={formData.assigneeId}
              onChange={(e) => setFormData({ ...formData, assigneeId: e.target.value })}
            >
              <option value="">Unassigned</option>
              {users.map((u) => (
                <option key={u.id} value={u.id}>{u.name} ({u.role})</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Due Date</label>
            <input
              type="date"
              className="input"
              value={formData.dueDate}
              onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
            />
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
          <button type="button" className="btn btn-secondary" onClick={onClose} disabled={submitting}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {submitting ? 'Saving...' : initialData ? 'Update Task' : 'Create Task'}
          </button>
        </div>
      </form>
    </Modal>
  );
}
