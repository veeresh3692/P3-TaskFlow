import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { CheckSquare } from 'lucide-react';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || '/';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(email, password);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickLogin = (demoEmail) => {
    setEmail(demoEmail);
    setPassword('password123');
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div className="auth-header">
          <div style={{ display: 'flex', justifyContent: 'center', color: 'var(--primary)' }}>
            <CheckSquare size={40} />
          </div>
          <h1>Welcome to Taskflow</h1>
          <p>Agile Task & Project Management System</p>
        </div>

        {error && <div className="form-error" style={{ marginBottom: '1rem', textAlign: 'center' }}>{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email Address</label>
            <input
              type="email"
              className="input"
              placeholder="e.g. dev@taskflow.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              className="input"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button 
            type="submit" 
            className="btn btn-primary" 
            style={{ width: '100%', padding: '0.75rem', marginTop: '0.5rem' }}
            disabled={loading}
          >
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <div className="demo-creds">
          <div style={{ fontWeight: 600, marginBottom: '0.4rem' }}>Quick Demo Accounts:</div>
          <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
            <button 
              type="button" 
              className="btn btn-secondary btn-sm"
              onClick={() => handleQuickLogin('admin@taskflow.com')}
            >
              Admin
            </button>
            <button 
              type="button" 
              className="btn btn-secondary btn-sm"
              onClick={() => handleQuickLogin('manager@taskflow.com')}
            >
              Manager
            </button>
            <button 
              type="button" 
              className="btn btn-secondary btn-sm"
              onClick={() => handleQuickLogin('member@taskflow.com')}
            >
              Member
            </button>
          </div>
          <div style={{ marginTop: '0.4rem', fontSize: '0.75rem' }}>Default Password: <b>password123</b></div>
        </div>

        <div className="auth-links">
          Don't have an account? <Link to="/register">Create Account</Link>
        </div>
      </div>
    </div>
  );
}
