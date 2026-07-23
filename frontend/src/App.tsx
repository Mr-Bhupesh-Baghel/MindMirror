import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import './App.css'

type User = { id: string; email: string; displayName: string }
type AuthResponse = { token: string; user: User }

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8082/api'
const habits = [
  { id: 'water', icon: '💧', label: 'Water', detail: 'Roots & soil', points: 10 },
  { id: 'exercise', icon: '💪', label: 'Exercise', detail: 'Trunk & strength', points: 20 },
  { id: 'learning', icon: '📚', label: 'Learning', detail: 'Branches & canopy', points: 15 },
  { id: 'sleep', icon: '😴', label: 'Sleep', detail: 'Leaves & recovery', points: 20 },
  { id: 'meditation', icon: '🧘', label: 'Meditate', detail: 'Flowers & calm', points: 10 },
  { id: 'mood', icon: '😊', label: 'Positive mood', detail: 'Wildlife & joy', points: 10 },
]

async function api<T>(path: string, options: RequestInit = {}, token?: string): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.headers },
  })
  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.message ?? 'Something went wrong. Please try again.')
  }
  return response.status === 204 ? (undefined as T) : response.json()
}

function App() {
  const [token, setToken] = useState(() => localStorage.getItem('mindmirror_token') ?? '')
  const [user, setUser] = useState<User | null>(null)
  const [mode, setMode] = useState<'login' | 'register'>('register')
  const [loading, setLoading] = useState(Boolean(token))
  const [message, setMessage] = useState('')
  const [completed, setCompleted] = useState<string[]>([])

  useEffect(() => {
    if (!token) return
    api<User>('/auth/me', {}, token).then(setUser).catch(() => { localStorage.removeItem('mindmirror_token'); setToken('') }).finally(() => setLoading(false))
  }, [token])

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault(); setMessage(''); setLoading(true)
    const data = new FormData(event.currentTarget)
    const payload = mode === 'register'
      ? { displayName: data.get('displayName'), email: data.get('email'), password: data.get('password') }
      : { email: data.get('email'), password: data.get('password') }
    try {
      const result = await api<AuthResponse>(`/auth/${mode === 'register' ? 'register' : 'login'}`, { method: 'POST', body: JSON.stringify(payload) })
      localStorage.setItem('mindmirror_token', result.token); setUser(result.user); setToken(result.token)
    } catch (error) { setMessage(error instanceof Error ? error.message : 'Unable to continue.') }
    finally { setLoading(false) }
  }

  async function signOut() {
    try { await api<void>('/auth/logout', { method: 'POST' }, token) } finally { localStorage.removeItem('mindmirror_token'); setToken(''); setUser(null); setCompleted([]) }
  }

  if (loading && token) return <main className="loading">Tending your Mind Tree…</main>
  if (!user) return <AuthScreen mode={mode} setMode={setMode} submit={submit} message={message} loading={loading} />
  const gp = completed.reduce((total, id) => total + (habits.find((habit) => habit.id === id)?.points ?? 0), 0)
  return <Dashboard user={user} completed={completed} setCompleted={setCompleted} gp={gp} signOut={signOut} />
}

function AuthScreen({ mode, setMode, submit, message, loading }: { mode: 'login' | 'register'; setMode: (mode: 'login' | 'register') => void; submit: (event: FormEvent<HTMLFormElement>) => void; message: string; loading: boolean }) {
  const registering = mode === 'register'
  return <main className="auth-page">
    <section className="auth-intro"><div className="brand"><span>❧</span> MindMirror</div><div className="intro-copy"><p className="eyebrow">A gentler way to grow</p><h1>Grow a life<br />you’re proud of.</h1><p>Every small act of care helps your Mind Tree take root, stretch upward, and quietly flourish.</p></div><div className="mini-tree" aria-hidden="true"><span className="sun">☀</span><span className="tree">🌳</span><span className="flower one">✦</span><span className="flower two">✦</span></div><p className="intro-foot">No punishment. No broken streaks. Just a living record of your care.</p></section>
    <section className="auth-panel"><div className="auth-card"><div className="mobile-brand"><span>❧</span> MindMirror</div><p className="eyebrow">WELCOME {registering ? 'HOME' : 'BACK'}</p><h2>{registering ? 'Plant your first seed.' : 'Welcome back.'}</h2><p className="subcopy">{registering ? 'Create your space for steady, meaningful growth.' : 'Your tree has been waiting for you.'}</p><form onSubmit={submit}>
      {registering && <label>What should we call you?<input name="displayName" autoComplete="name" placeholder="Your name" minLength={2} maxLength={80} required /></label>}
      <label>Email<input name="email" type="email" autoComplete="email" placeholder="you@example.com" required /></label>
      <label>Password<input name="password" type="password" autoComplete={registering ? 'new-password' : 'current-password'} placeholder="At least 8 characters" minLength={8} required /></label>
      {message && <p className="form-error" role="alert">{message}</p>}<button className="primary" disabled={loading}>{loading ? 'Please wait…' : registering ? 'Begin growing' : 'Enter my garden'} <span>→</span></button>
    </form><p className="switch">{registering ? 'Already tending a tree?' : 'New to MindMirror?'} <button onClick={() => setMode(registering ? 'login' : 'register')} type="button">{registering ? 'Sign in' : 'Create an account'}</button></p></div></section>
  </main>
}

function Dashboard({ user, completed, setCompleted, gp, signOut }: { user: User; completed: string[]; setCompleted: (ids: string[]) => void; gp: number; signOut: () => void }) {
  return <main className="dashboard"><header><div className="brand"><span>❧</span> MindMirror</div><p>Good to see you, {user.displayName.split(' ')[0]}.</p><button className="text-button" onClick={signOut}>Sign out</button></header><section className="dashboard-grid"><div className="tree-card"><div className="tree-top"><div><p className="eyebrow">YOUR MIND TREE</p><h1>{gp >= 85 ? 'A beautiful day of growth.' : 'Small care, deep roots.'}</h1></div><div className="level">Level 1 <b>Seedling</b></div></div><div className="scene" aria-label="Your growing Mind Tree"><span className="cloud cloud-a">☁</span><span className="cloud cloud-b">☁</span><span className="sparkle s1">✦</span><span className="sparkle s2">✦</span><span className="main-tree">{gp >= 45 ? '🌳' : gp >= 20 ? '🌿' : '🌱'}</span><div className="ground" /></div><div className="growth"><span>Today’s growth</span><b>{gp} <small>/ 85 GP</small></b><div className="progress"><i style={{ width: `${(gp / 85) * 100}%` }} /></div></div></div><aside className="habit-card"><p className="eyebrow">TODAY’S CARE</p><h2>What feels good today?</h2><p className="subcopy">Each completed habit strengthens a different part of your tree.</p><div className="habit-list">{habits.map(habit => { const done = completed.includes(habit.id); return <button className={`habit ${done ? 'done' : ''}`} key={habit.id} onClick={() => setCompleted(done ? completed.filter(id => id !== habit.id) : [...completed, habit.id])}><span className="habit-icon">{habit.icon}</span><span><b>{habit.label}</b><small>{habit.detail}</small></span><em>+{habit.points}</em><i>{done ? '✓' : '+'}</i></button> })}</div><p className="encouragement">{completed.length ? 'Lovely. Your tree felt that. ✦' : 'There’s no rush. Start with one small act.'}</p></aside></section></main>
}

export default App
