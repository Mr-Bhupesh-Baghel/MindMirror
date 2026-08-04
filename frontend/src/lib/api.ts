export type User = { id: string; email: string; displayName: string }
import type { SkillProgress, TreeStage } from '../types/mindTree'
export type TreeState = { currentStreak: number; longestStreak: number; unlockedAchievements: string[]; season: string; xp: number; xpToNextLevel: number; level: number; stage: TreeStage; skills: SkillProgress[] }

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8082'

async function request<T>(path: string, options: RequestInit = {}, token?: string): Promise<T> {
  const response = await fetch(`${apiBaseUrl}${path}`, { ...options, headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.headers } })
  if (!response.ok) {
    const body = await response.json().catch(() => null) as { message?: string } | null
    throw new Error(body?.message ?? 'Something went wrong. Please try again.')
  }
  return response.status === 204 ? undefined as T : response.json() as Promise<T>
}

export function authenticate(mode: 'login' | 'register', values: { email: string; password: string; displayName?: string }) {
  return request<{ token: string; user: User }>(`/api/auth/${mode}`, { method: 'POST', body: JSON.stringify(mode === 'register' ? values : { email: values.email, password: values.password }) })
}
export function currentUser(token: string) { return request<User>('/api/auth/me', {}, token) }
export function loadTree(token: string) { return request<TreeState>('/api/tree', {}, token) }
export function completeSkillLesson(token: string, skill: string) { return request<TreeState>(`/api/skills/${skill}/lessons/complete`, { method: 'POST' }, token) }
export function logout(token: string) { return request<void>('/api/auth/logout', { method: 'POST' }, token) }
