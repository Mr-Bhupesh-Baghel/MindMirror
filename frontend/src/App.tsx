import { useEffect, useState } from 'react'
import { AuthScreen } from './features/auth/AuthScreen'
import { currentUser, type User } from './lib/api'
import { MindTreeDashboard } from './features/mind-tree/MindTreeDashboard'
import './App.css'

export default function App() {
  const [session, setSession] = useState<{ token: string; user: User } | null>(() => {
    const token = sessionStorage.getItem('mindmirror-token')
    return token ? { token, user: { id: '', email: '', displayName: '' } } : null
  })
  const [checkingSession, setCheckingSession] = useState(Boolean(session))
  useEffect(() => {
    if (!session || session.user.id) return
    currentUser(session.token).then(user => setSession({ token: session.token, user })).catch(() => { sessionStorage.removeItem('mindmirror-token'); setSession(null) }).finally(() => setCheckingSession(false))
  }, [session])
  function signedIn(next: { token: string; user: User }) { sessionStorage.setItem('mindmirror-token', next.token); setSession(next) }
  function signedOut() { sessionStorage.removeItem('mindmirror-token'); setSession(null) }
  if (checkingSession) return <main className="auth-page">Restoring your garden…</main>
  return session ? <MindTreeDashboard token={session.token} user={session.user} onSignOut={signedOut} /> : <AuthScreen onAuthenticated={signedIn} />
}
