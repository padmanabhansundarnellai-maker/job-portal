import { useEffect, useState } from 'react';
import { api } from '../api';

export default function Admin() {
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      setStats(await api('/api/admin/stats'));
      setUsers(await api('/api/admin/users'));
    } catch (e) { setError(e.message); }
  };
  useEffect(() => { load(); }, []);

  const remove = async (id) => {
    if (!window.confirm('Delete this user?')) return;
    try { setError(''); await api(`/api/admin/users/${id}`, { method: 'DELETE' }); load(); }
    catch (e) { setError(e.message); }
  };

  return (
    <div>
      <h2>Admin dashboard</h2>
      {stats && (
        <div className="row stats">
          <div className="card"><b>{stats.users}</b><p className="meta">Users</p></div>
          <div className="card"><b>{stats.jobs}</b><p className="meta">Jobs</p></div>
          <div className="card"><b>{stats.applications}</b><p className="meta">Applications</p></div>
        </div>
      )}
      {error && <p className="error">{error}</p>}
      <table>
        <thead><tr><th>Name</th><th>Email</th><th>Role</th><th></th></tr></thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.name}</td><td>{u.email}</td><td>{u.role}</td>
              <td>{u.role !== 'ADMIN' && <button className="btn small danger" onClick={() => remove(u.id)}>Delete</button>}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
