import React from 'react';
import { Calendar, User, ChevronRight, ChevronLeft, Trash2 } from 'lucide-react';

const STATUS_FLOW = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];

export default function TaskCard({ task, onStatusChange, onClick, onDelete }) {
  const currentIndex = STATUS_FLOW.indexOf(task.status);
  const prevStatus = currentIndex > 0 ? STATUS_FLOW[currentIndex - 1] : null;
  const nextStatus = currentIndex < STATUS_FLOW.length - 1 ? STATUS_FLOW[currentIndex + 1] : null;

  return (
    <div className="task-card" onClick={onClick}>
      <div className="task-card-header">
        <span className={`priority-badge priority-${task.priority}`}>
          {task.priority}
        </span>
        <button 
          className="btn-sm" 
          style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', padding: 0 }}
          onClick={(e) => {
            e.stopPropagation();
            if (window.confirm(`Delete task "${task.title}"?`)) {
              onDelete(task.id);
            }
          }}
          title="Delete task"
        >
          <Trash2 size={14} />
        </button>
      </div>

      <h4 className="task-card-title">{task.title}</h4>
      {task.description && (
        <p className="task-card-desc">{task.description}</p>
      )}

      <div className="task-card-footer">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
          <User size={13} />
          <span>{task.assigneeName || 'Unassigned'}</span>
        </div>

        {task.dueDate && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
            <Calendar size={13} />
            <span>{task.dueDate}</span>
          </div>
        )}
      </div>

      {/* Quick Move Buttons */}
      <div 
        style={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          marginTop: '0.5rem', 
          paddingTop: '0.4rem', 
          borderTop: '1px dashed #e2e8f0' 
        }}
        onClick={(e) => e.stopPropagation()}
      >
        {prevStatus ? (
          <button 
            className="btn btn-secondary btn-sm" 
            style={{ padding: '0.15rem 0.4rem', fontSize: '0.7rem' }}
            onClick={() => onStatusChange(task.id, prevStatus)}
            title={`Move back to ${prevStatus}`}
          >
            <ChevronLeft size={12} /> {prevStatus.replace('_', ' ')}
          </button>
        ) : <div />}

        {nextStatus && (
          <button 
            className="btn btn-primary btn-sm" 
            style={{ padding: '0.15rem 0.4rem', fontSize: '0.7rem' }}
            onClick={() => onStatusChange(task.id, nextStatus)}
            title={`Move to ${nextStatus}`}
          >
            {nextStatus.replace('_', ' ')} <ChevronRight size={12} />
          </button>
        )}
      </div>
    </div>
  );
}
