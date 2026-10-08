import React, { useState, useEffect, useCallback } from 'react';
import { taskService } from '../services/taskService';
import { projectService } from '../services/projectService';
import { sprintService } from '../services/sprintService';
import { userService } from '../services/userService';
import TaskCard from '../components/TaskCard';
import FilterBar from '../components/FilterBar';
import TaskModal from '../components/TaskModal';
import { Plus } from 'lucide-react';

const COLUMNS = [
  { id: 'TODO', title: 'To Do', color: '#64748b' },
  { id: 'IN_PROGRESS', title: 'In Progress', color: '#2563eb' },
  { id: 'IN_REVIEW', title: 'In Review', color: '#8b5cf6' },
  { id: 'DONE', title: 'Done', color: '#10b981' },
];

export default function TaskBoardPage() {
  const [tasks, setTasks] = useState([]);
  const [projects, setProjects] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  // Jira-style filters
  const [filters, setFilters] = useState({
    projectId: '',
    sprintId: '',
    status: '',
    priority: '',
    assigneeId: '',
    search: '',
    sort: 'createdAt,desc',
  });

  // Modal states
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);

  const loadDependencies = async () => {
    try {
      const [projList, sprintList, userList] = await Promise.all([
        projectService.getProjects(),
        sprintService.getSprints(),
        userService.getUsers(),
      ]);
      setProjects(projList);
      setSprints(sprintList);
      setUsers(userList);
    } catch (err) {
      console.error('Failed to load board dependencies:', err);
    }
  };

  const fetchTasks = useCallback(async () => {
    try {
      setLoading(true);
      const params = {};
      if (filters.projectId) params.projectId = filters.projectId;
      if (filters.sprintId) params.sprintId = filters.sprintId;
      if (filters.status) params.status = filters.status;
      if (filters.priority) params.priority = filters.priority;
      if (filters.assigneeId) params.assigneeId = filters.assigneeId;
      if (filters.search) params.search = filters.search;
      if (filters.sort) params.sort = filters.sort;

      params.size = 100; // fetch all active board cards

      const data = await taskService.getTasks(params);
      setTasks(data.content || []);
    } catch (err) {
      console.error('Failed to fetch tasks:', err);
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    loadDependencies();
  }, []);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  const handleStatusChange = async (taskId, newStatus) => {
    try {
      // Optimistic update for fluid UI
      setTasks((prev) =>
        prev.map((t) => (t.id === taskId ? { ...t, status: newStatus } : t))
      );
      await taskService.updateTaskStatus(taskId, newStatus);
    } catch (err) {
      console.error('Failed to update task status:', err);
      fetchTasks(); // rollback on failure
    }
  };

  const handleSaveTask = async (taskPayload) => {
    if (selectedTask) {
      await taskService.updateTask(selectedTask.id, taskPayload);
    } else {
      await taskService.createTask(taskPayload);
    }
    await fetchTasks();
  };

  const handleDeleteTask = async (taskId) => {
    try {
      await taskService.deleteTask(taskId);
      setTasks((prev) => prev.filter((t) => t.id !== taskId));
    } catch (err) {
      console.error('Failed to delete task:', err);
      alert('Failed to delete task');
    }
  };

  const handleClearFilters = () => {
    setFilters({
      projectId: '',
      sprintId: '',
      status: '',
      priority: '',
      assigneeId: '',
      search: '',
      sort: 'createdAt,desc',
    });
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Agile Task Board</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Interactive Kanban board with workflow transitions
          </p>
        </div>

        <button 
          className="btn btn-primary"
          onClick={() => {
            setSelectedTask(null);
            setIsModalOpen(true);
          }}
        >
          <Plus size={16} />
          <span>New Task</span>
        </button>
      </div>

      {/* Jira-style Filter Bar */}
      <FilterBar
        filters={filters}
        onChange={setFilters}
        onClear={handleClearFilters}
        projects={projects}
        sprints={sprints}
        users={users}
      />

      {loading ? (
        <div style={{ color: 'var(--text-muted)', padding: '2rem 0' }}>Loading tasks...</div>
      ) : (
        <div className="kanban-board">
          {COLUMNS.map((column) => {
            const columnTasks = tasks.filter((t) => t.status === column.id);
            return (
              <div key={column.id} className="kanban-column">
                <div className="column-header">
                  <div className="column-title">
                    <span 
                      style={{ 
                        width: '10px', 
                        height: '10px', 
                        borderRadius: '50%', 
                        backgroundColor: column.color 
                      }} 
                    />
                    <span>{column.title}</span>
                  </div>
                  <span className="badge-count">{columnTasks.length}</span>
                </div>

                <div className="column-tasks">
                  {columnTasks.length === 0 ? (
                    <div style={{ textAlign: 'center', color: '#94a3b8', fontSize: '0.8rem', padding: '2rem 1rem' }}>
                      No tasks in this column
                    </div>
                  ) : (
                    columnTasks.map((task) => (
                      <TaskCard
                        key={task.id}
                        task={task}
                        onStatusChange={handleStatusChange}
                        onClick={() => {
                          setSelectedTask(task);
                          setIsModalOpen(true);
                        }}
                        onDelete={handleDeleteTask}
                      />
                    ))
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      <TaskModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleSaveTask}
        initialData={selectedTask}
        projects={projects}
        sprints={sprints}
        users={users}
      />
    </div>
  );
}
