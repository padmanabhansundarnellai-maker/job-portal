import { useState } from 'react';
import { Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom';
import { getAuth, clearAuth } from './api';
import Auth from './pages/Auth';
import Jobs from './pages/Jobs';
import Candidate from './pages/Candidate';
import Recruiter from './pages/Recruiter';
import Admin from './pages/Admin';

function Protected({ role, children }) {
  const a = getAuth();
  if (!a) return <Navigate to="/login" replace />;
  if (role && a.role !== role) return <Navigate to="/" replace />;
  return children;
}

export default function App() {
  const [auth, setAuth] = useState(getAuth());
  const navigate = useNavigate();

  const logout = () => { clearAuth(); setAuth(null); navigate('/login'); };

  return (
    <>
      <header className="nav">
        <Link to="/" className="brand">Job Portal</Link>
        <nav>
          <Link to="/">Jobs</Link>
          {auth?.role === 'CANDIDATE' && <Link to="/my-applications">My applications</Link>}
          {auth?.role === 'RECRUITER' && <Link to="/recruiter">Recruiter dashboard</Link>}
          {auth?.role === 'ADMIN' && <Link to="/admin">Admin</Link>}
          {auth ? (
            <>
              <span className="who">{auth.name} ({auth.role.toLowerCase()})</span>
              <button className="link" onClick={logout}>Log out</button>
            </>
          ) : (
            <>
              <Link to="/login">Log in</Link>
              <Link to="/register">Register</Link>
            </>
          )}
        </nav>
      </header>
      <main>
        <Routes>
          <Route path="/" element={<Jobs />} />
          <Route path="/login" element={<Auth mode="login" onLogin={setAuth} />} />
          <Route path="/register" element={<Auth mode="register" onLogin={setAuth} />} />
          <Route path="/my-applications" element={<Protected role="CANDIDATE"><Candidate /></Protected>} />
          <Route path="/recruiter" element={<Protected role="RECRUITER"><Recruiter /></Protected>} />
          <Route path="/admin" element={<Protected role="ADMIN"><Admin /></Protected>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </>
  );
}
