import { useState, type FormEvent } from 'react'
import { authenticate, type User } from '../../lib/api'

export function AuthScreen({ onAuthenticated }: { onAuthenticated: (session: { token: string; user: User }) => void }) {
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault(); const values = new FormData(event.currentTarget); setError(''); setIsSubmitting(true)
    try { onAuthenticated(await authenticate(mode, { email: String(values.get('email') ?? ''), password: String(values.get('password') ?? ''), displayName: String(values.get('displayName') ?? '') })) }
    catch (reason) { setError(reason instanceof Error ? reason.message : 'Unable to continue.') }
    finally { setIsSubmitting(false) }
  }
  return <main className="auth-page"><form className="auth-card" onSubmit={submit}>
    <p className="auth-wordmark">✦ MindMirror</p><h1>{mode === 'login' ? 'Welcome back' : 'Plant your first seed'}</h1><p>{mode === 'login' ? 'Sign in to tend your Mind Tree.' : 'Create an account to begin your mindful journey.'}</p>
    {mode === 'register' && <label>Display name<input name="displayName" minLength={2} maxLength={80} required autoComplete="name" /></label>}
    <label>Email<input name="email" type="email" required autoComplete="email" /></label><label>Password<input name="password" type="password" minLength={8} required autoComplete={mode === 'login' ? 'current-password' : 'new-password'} /></label>
    {error && <p className="auth-error" role="alert">{error}</p>}<button type="submit" disabled={isSubmitting}>{isSubmitting ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account'}</button>
    <button className="auth-switch" type="button" onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError('') }}>{mode === 'login' ? 'New here? Create an account' : 'Already have an account? Sign in'}</button>
  </form></main>
}
