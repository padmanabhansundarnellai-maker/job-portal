import { useEffect, useState } from 'react';
import { api, downloadResume } from '../api';

export default function Candidate() {
  const [apps, setApps] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    api('/api/applications/mine').then(setApps).catch((e) => setError(e.message));
  }, []);

  return (
    <div>
      <h2>My applications</h2>
      {error && <p className="error">{error}</p>}
      {apps.length === 0 && !error && <p>You have not applied to any jobs yet.</p>}
      {apps.map((a) => (
        <div className="card" key={a.id}>
          <h3>{a.job.title}</h3>
          <p className="meta">{a.job.company} · {a.job.location}</p>
          <p>Status: <span className={`badge ${a.status}`}>{a.status}</span></p>
          <button className="btn small" onClick={() => downloadResume(a.id)}>View my resume</button>
        </div>
      ))}
    </div>
  );
}
