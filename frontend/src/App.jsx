import React from 'react';
import { AuthProvider } from './context/AuthContext';
import AppRouter from './router/AppRouter';

export default function App() {
  return (
    <AuthProvider>
      <AppRouter />
    </AuthProvider>
  );
}
