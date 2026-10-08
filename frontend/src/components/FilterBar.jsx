import React from 'react';
import { Search, X, Filter } from 'lucide-react';

export default function FilterBar({
  filters,
  onChange,
  onClear,
  projects = [],
  sprints = [],
  users = [],
}) {
  const handleChange = (key, value) => {
    onChange({ ...filters, [key]: value });
  };

  const hasActiveFilters = Object.values(filters).some((val) => val !== '' && val !== null && val !== undefined);

  return (
    <div className="filter-bar">
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 600 }}>
        <Filter size={16} />
        <span>Filters:</span>
      </div>

      <div className="filter-input" style={{ position: 'relative' }}>
        <input
          type="text"
          className="input"
          placeholder="Search summary, description..."
          value={filters.search || ''}
          onChange={(e) => handleChange('search', e.target.value)}
          style={{ paddingLeft: '2rem' }}
        />
        <Search size={16} style={{ position: 'absolute', left: '0.6rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
      </div>

      <select
        className="select"
        style={{ width: 'auto' }}
        value={filters.projectId || ''}
        onChange={(e) => handleChange('projectId', e.target.value)}
      >
        <option value="">All Projects</option>
        {projects.map((p) => (
          <option key={p.id} value={p.id}>{p.name}</option>
        ))}
      </select>

      <select
        className="select"
        style={{ width: 'auto' }}
        value={filters.sprintId || ''}
        onChange={(e) => handleChange('sprintId', e.target.value)}
      >
        <option value="">All Sprints</option>
        {sprints.map((s) => (
          <option key={s.id} value={s.id}>{s.name}</option>
        ))}
      </select>

      <select
        className="select"
        style={{ width: 'auto' }}
        value={filters.status || ''}
        onChange={(e) => handleChange('status', e.target.value)}
      >
        <option value="">All Statuses</option>
        <option value="TODO">To Do</option>
        <option value="IN_PROGRESS">In Progress</option>
        <option value="IN_REVIEW">In Review</option>
        <option value="DONE">Done</option>
      </select>

      <select
        className="select"
        style={{ width: 'auto' }}
        value={filters.priority || ''}
        onChange={(e) => handleChange('priority', e.target.value)}
      >
        <option value="">All Priorities</option>
        <option value="LOW">Low</option>
        <option value="MEDIUM">Medium</option>
        <option value="HIGH">High</option>
        <option value="CRITICAL">Critical</option>
      </select>

      <select
        className="select"
        style={{ width: 'auto' }}
        value={filters.assigneeId || ''}
        onChange={(e) => handleChange('assigneeId', e.target.value)}
      >
        <option value="">All Assignees</option>
        {users.map((u) => (
          <option key={u.id} value={u.id}>{u.name}</option>
        ))}
      </select>

      <select
        className="select"
        style={{ width: 'auto' }}
        value={filters.sort || 'createdAt,desc'}
        onChange={(e) => handleChange('sort', e.target.value)}
      >
        <option value="createdAt,desc">Newest First</option>
        <option value="createdAt,asc">Oldest First</option>
        <option value="dueDate,asc">Due Date (Earliest)</option>
        <option value="priority,desc">Priority</option>
      </select>

      {hasActiveFilters && (
        <button className="btn btn-secondary btn-sm" onClick={onClear} title="Clear all filters">
          <X size={14} />
          <span>Clear</span>
        </button>
      )}
    </div>
  );
}
