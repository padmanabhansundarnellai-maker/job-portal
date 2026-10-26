import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api, saveAuth } from '../api';

export default function Auth({ mode, onLogin }) {
  const isRegister = mode === 'register';
  const [form, setForm] = useState({ name: '', email: '', password: '', role: 'CANDIDATE' });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const body = isRegister ? form : { email: form.email, password: form.password };
      const data = await api(isRegister ? '/api/auth/register' : '/api/auth/login', { method: 'POST', body });
      saveAuth(data);
      onLogin(data);
      navigate(data.role === 'RECRUITER' ? '/recruiter' : data.role === 'ADMIN' ? '/admin' : '/');
    } catch (err) { setError(err.message); }
  };

  return (
    <form className="panel narrow" onSubmit={submit}>
      <h2>{isRegister ? 'Create your account' : 'Log in'}</h2>
      {isRegister && (
        <label>Name<input value={form.name} onChange={set('name')} required /></label>
      )}
      <label>Email<input type="email" value={form.email} onChange={set('email')} required /></label>
      <label>Password<input type="password" value={form.password} onChange={set('password')} required minLength={6} /></label>
      {isRegister && (
        <label>I am a
          <select value={form.role} onChange={set('role')}>
            <option value="CANDIDATE">Candidate (looking for a job)</option>
            <option value="RECRUITER">Recruiter (hiring)</option>
          </select>
        </label>
      )}
      {error && <p className="error">{error}</p>}
      <button className="btn" type="submit">{isRegister ? 'Register' : 'Log in'}</button>
      {!isRegister && <p className="hint">Admin demo login: admin@jobportal.com / Admin@123</p>}
    </form>
  );
}
