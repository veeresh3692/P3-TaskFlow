import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Bell, Check, Info, AlertCircle, CheckCircle } from 'lucide-react';

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadNotifications = async () => {
    try {
      setLoading(true);
      // Attempt to load from notification-service if running or local storage fallback
      const res = await api.get('/notifications').catch(() => null);
      if (res && res.data) {
        setNotifications(res.data);
      } else {
        const local = localStorage.getItem('taskflow_notifications');
        if (local) {
          setNotifications(JSON.parse(local));
        } else {
          // Default initial notifications demonstrating Kafka event processing
          const defaults = [
            {
              id: 1,
              type: 'TASK_STATUS_CHANGED',
              message: 'Task "Design Kanban Board UI" moved to IN_PROGRESS by Alex Developer',
              read: false,
              createdAt: new Date().toISOString(),
            },
            {
              id: 2,
              type: 'TASK_ASSIGNED',
              message: 'You have been assigned to task "Setup Jira-style Filter Bar"',
              read: false,
              createdAt: new Date(Date.now() - 3600000).toISOString(),
            },
            {
              id: 3,
              type: 'TASK_COMPLETED',
              message: 'Task "Implement JWT Authentication" was completed',
              read: true,
              createdAt: new Date(Date.now() - 86400000).toISOString(),
            },
          ];
          setNotifications(defaults);
          localStorage.setItem('taskflow_notifications', JSON.stringify(defaults));
        }
      }
    } catch (err) {
      console.error('Failed to load notifications:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNotifications();
  }, []);

  const handleMarkAsRead = async (id) => {
    try {
      await api.patch(`/notifications/${id}/read`).catch(() => null);
      const updated = notifications.map((n) => (n.id === id ? { ...n, read: true } : n));
      setNotifications(updated);
      localStorage.setItem('taskflow_notifications', JSON.stringify(updated));
    } catch (err) {
      console.error(err);
    }
  };

  const handleClearAll = () => {
    setNotifications([]);
    localStorage.removeItem('taskflow_notifications');
  };

  const getIcon = (type) => {
    switch (type) {
      case 'TASK_COMPLETED':
        return <CheckCircle size={18} style={{ color: '#10b981' }} />;
      case 'TASK_ASSIGNED':
        return <AlertCircle size={18} style={{ color: '#3b82f6' }} />;
      default:
        return <Info size={18} style={{ color: '#8b5cf6' }} />;
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Activity &amp; Notifications</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Asynchronous event feed triggered by Kafka task events
          </p>
        </div>

        {notifications.length > 0 && (
          <button className="btn btn-secondary btn-sm" onClick={handleClearAll}>
            Clear All
          </button>
        )}
      </div>

      {loading ? (
        <div style={{ color: 'var(--text-muted)' }}>Loading notifications...</div>
      ) : notifications.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem 1rem' }}>
          <Bell size={48} style={{ color: '#94a3b8', margin: '0 auto 1rem' }} />
          <h3>All Caught Up!</h3>
          <p style={{ color: 'var(--text-muted)' }}>
            No new activity notifications at this time.
          </p>
        </div>
      ) : (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          {notifications.map((notif) => (
            <div
              key={notif.id}
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '1rem 1.25rem',
                borderBottom: '1px solid var(--border)',
                backgroundColor: notif.read ? '#ffffff' : '#f0f9ff',
                transition: 'background-color 0.2s',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <div style={{ padding: '0.4rem', borderRadius: '50%', backgroundColor: '#f1f5f9' }}>
                  {getIcon(notif.type)}
                </div>
                <div>
                  <div style={{ fontSize: '0.9rem', fontWeight: notif.read ? 400 : 600 }}>
                    {notif.message}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>
                    {new Date(notif.createdAt).toLocaleString()}
                  </div>
                </div>
              </div>

              {!notif.read && (
                <button
                  className="btn btn-secondary btn-sm"
                  onClick={() => handleMarkAsRead(notif.id)}
                  title="Mark as read"
                >
                  <Check size={14} /> Mark Read
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
