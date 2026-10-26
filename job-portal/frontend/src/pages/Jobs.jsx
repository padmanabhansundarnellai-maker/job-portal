import { useEffect, useState } from 'react';
import { api, getAuth } from '../api';

function JobCard({ job, canApply }) {
  const [file, setFile] = useState(null);
  const [msg, setMsg] = useState('');

  const apply = async () => {
    if (!file) return setMsg('Choose a PDF or Word resume first.');
    const form = new FormData();
    form.append('resume', file);
    try {
      await api(`/api/applications/${job.id}`, { method: 'POST', form });
      setMsg('Application sent.');
    } catch (e) { setMsg(e.message); }
  };

  return (
    <div className="card">
      <h3>{job.title}</h3>
      <p className="meta">{job.company} · {job.location}</p>
      <p>{job.description}</p>
      {canApply && (
        <div className="row">
          <input type="file" accept=".pdf,.doc,.docx" onChange={(e) => setFile(e.target.files[0])} />
          <button className="btn small" onClick={apply}>Apply</button>
        </div>
      )}
      {msg && <p className="hint">{msg}</p>}
    </div>
  );
}

export default function Jobs() {
  const [q, setQ] = useState('');
  const [location, setLocation] = useState('');
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const auth = getAuth();

  const load = async (page = 0) => {
    try {
      setError('');
      setData(await api(`/api/jobs?q=${encodeURIComponent(q)}&location=${encodeURIComponent(location)}&page=${page}&size=5`));
    } catch (e) { setError(e.message); }
  };
  useEffect(() => { load(0); }, []); // eslint-disable-line

  return (
    <div>
      <form className="row search" onSubmit={(e) => { e.preventDefault(); load(0); }}>
        <input placeholder="Job title or company" value={q} onChange={(e) => setQ(e.target.value)} />
        <input placeholder="Location" value={location} onChange={(e) => setLocation(e.target.value)} />
        <button className="btn" type="submit">Search</button>
      </form>
      {error && <p className="error">{error}</p>}
      {data && data.content.length === 0 && <p>No jobs match your search.</p>}
      {data && data.content.map((j) => <JobCard key={j.id} job={j} canApply={auth?.role === 'CANDIDATE'} />)}
      {data && data.totalPages > 1 && (
        <div className="row pager">
          <button className="btn small" disabled={data.number === 0} onClick={() => load(data.number - 1)}>Previous</button>
          <span>Page {data.number + 1} of {data.totalPages}</span>
          <button className="btn small" disabled={data.number + 1 >= data.totalPages} onClick={() => load(data.number + 1)}>Next</button>
        </div>
      )}
    </div>
  );
}
