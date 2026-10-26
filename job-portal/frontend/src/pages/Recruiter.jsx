import { useEffect, useState } from 'react';
import { api, downloadResume } from '../api';

const STATUSES = ['APPLIED', 'SHORTLISTED', 'REJECTED'];
const empty = { title: '', company: '', location: '', description: '' };

export default function Recruiter() {
  const [form, setForm] = useState(empty);
  const [jobs, setJobs] = useState([]);
  const [apps, setApps] = useState([]);
  const [error, setError] = useState('');

  const refresh = async () => {
    try {
      setJobs(await api('/api/recruiter/jobs'));
      setApps(await api('/api/applications/recruiter'));
    } catch (e) { setError(e.message); }
  };
  useEffect(() => { refresh(); }, []);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const post = async (e) => {
    e.preventDefault();
    setError('');
    try { await api('/api/jobs', { method: 'POST', body: form }); setForm(empty); refresh(); }
    catch (err) { setError(err.message); }
  };

  const remove = async (id) => {
    if (!window.confirm('Delete this job and its applications?')) return;
    try { await api(`/api/jobs/${id}`, { method: 'DELETE' }); refresh(); }
    catch (err) { setError(err.message); }
  };

  const setStatus = async (id, status) => {
    try { await api(`/api/applications/${id}/status?status=${status}`, { method: 'PUT' }); refresh(); }
    catch (err) { setError(err.message); }
  };

  return (
    <div>
      <h2>Post a job</h2>
      <form className="panel" onSubmit={post}>
        <label>Title<input value={form.title} onChange={set('title')} required /></label>
        <label>Company<input value={form.company} onChange={set('company')} required /></label>
        <label>Location<input value={form.location} onChange={set('location')} required /></label>
        <label>Description<textarea rows="3" value={form.description} onChange={set('description')} required /></label>
        <button className="btn" type="submit">Post job</button>
      </form>
      {error && <p className="error">{error}</p>}

      <h2>My jobs</h2>
      {jobs.length === 0 && <p>You have not posted any jobs yet.</p>}
      {jobs.map((j) => (
        <div className="card row between" key={j.id}>
          <div><b>{j.title}</b><p className="meta">{j.company} · {j.location}</p></div>
          <button className="btn small danger" onClick={() => remove(j.id)}>Delete</button>
        </div>
      ))}

      <h2>Applications received</h2>
      {apps.length === 0 && <p>No applications yet.</p>}
      {apps.length > 0 && (
        <table>
          <thead><tr><th>Candidate</th><th>Job</th><th>Status</th><th>Resume</th></tr></thead>
          <tbody>
            {apps.map((a) => (
              <tr key={a.id}>
                <td>{a.candidate.name}<br /><span className="meta">{a.candidate.email}</span></td>
                <td>{a.job.title}</td>
                <td>
                  <select value={a.status} onChange={(e) => setStatus(a.id, e.target.value)}>
                    {STATUSES.map((s) => <option key={s}>{s}</option>)}
                  </select>
                </td>
                <td><button className="btn small" onClick={() => downloadResume(a.id)}>Download</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
