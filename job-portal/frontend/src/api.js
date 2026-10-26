// Empty = same origin; the Vite dev server proxies /api to the backend (see vite.config.js).
export const API = import.meta.env.VITE_API || '';

export function getAuth() {
  try { return JSON.parse(localStorage.getItem('auth')); } catch { return null; }
}
export function saveAuth(a) { localStorage.setItem('auth', JSON.stringify(a)); }
export function clearAuth() { localStorage.removeItem('auth'); }

export async function api(path, { method = 'GET', body, form } = {}) {
  const headers = {};
  const auth = getAuth();
  if (auth) headers.Authorization = `Bearer ${auth.token}`;
  let payload;
  if (form) payload = form;
  else if (body) { headers['Content-Type'] = 'application/json'; payload = JSON.stringify(body); }

  const res = await fetch(API + path, { method, headers, body: payload });
  if (!res.ok) {
    let msg = res.statusText || 'Request failed';
    try { const d = await res.json(); msg = d.message || d.error || msg; } catch { /* no body */ }
    throw new Error(msg);
  }
  const type = res.headers.get('content-type') || '';
  return type.includes('application/json') ? res.json() : null;
}

export async function downloadResume(applicationId) {
  const auth = getAuth();
  const res = await fetch(`${API}/api/applications/${applicationId}/resume`, {
    headers: { Authorization: `Bearer ${auth.token}` },
  });
  if (!res.ok) throw new Error('Could not download resume');
  const raw = await res.blob();
  const blob = new Blob([raw], { type: 'application/pdf' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `resume-${applicationId}.pdf`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}
