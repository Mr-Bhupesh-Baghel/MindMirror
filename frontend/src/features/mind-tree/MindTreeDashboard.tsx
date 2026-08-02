import { useEffect, useMemo, useState } from 'react'
import { Flame, Leaf, Sparkles } from 'lucide-react'
import { MindTree } from '../../components/mind-tree/MindTree'
import { ProgressBar } from '../../components/ui/ProgressBar'
import { loadTree, logout, toggleHabit, type TreeState, type User } from '../../lib/api'
import type { Habit, TreeStage } from '../../types/mindTree'
import Dictation from '../Skills/practice/dictation/dictation'

const habits: Habit[] = [
  { id: 'water', key: 'WATER', label: 'Hydrate', category: 'Body', icon: '💧', completed: false },
  { id: 'exercise', key: 'EXERCISE', label: 'Move with intention', category: 'Body', icon: '◌', completed: false },
  { id: 'meditation', key: 'MEDITATION', label: 'Meditate', category: 'Mind', icon: '✦', completed: false },
  { id: 'learning', key: 'LEARNING', label: 'Learn for 20 minutes', category: 'Craft', icon: '⌁', completed: false },
  { id: 'sleep', key: 'SLEEP', label: 'Unplug before bed', category: 'Rest', icon: '☾', completed: false },
  { id: 'mood', key: 'MOOD', label: 'Check in with yourself', category: 'Mind', icon: '☀', completed: false },
]

function getTreeStage(completed: number, total: number): TreeStage {
  const progress = completed / total
  if (progress === 0) return 'seedling'
  if (progress < .4) return 'sapling'
  if (progress < .7) return 'young-tree'
  if (progress < 1) return 'flourishing'
  return 'abundant'
}

export function MindTreeDashboard({ token, user, onSignOut }: { token: string; user: User; onSignOut: () => void }) {
  const [tree, setTree] = useState<TreeState | null>(null)
  const [error, setError] = useState('')
  const [pendingHabit, setPendingHabit] = useState('')
  const [activeSkill, setActiveSkill] = useState<string | null>(null)
  useEffect(() => { loadTree(token).then(setTree).catch(reason => setError(reason.message)) }, [token])
  const progressHabits = habits.map(habit => ({ ...habit, completed: tree?.todayHabits.includes(habit.id) ?? false }))
  const completed = progressHabits.filter(habit => habit.completed).length
  const dailyPercent = useMemo(() => completed / habits.length * 100, [completed])
  const stage = getTreeStage(completed, habits.length)
  const today = new Intl.DateTimeFormat(undefined, { weekday: 'long', day: 'numeric', month: 'short' }).format(new Date())

  async function completeHabit(habit: Habit) {
    setError(''); setPendingHabit(habit.id)
    try { setTree(await toggleHabit(token, habit.key)) }
    catch (reason) { setError(reason instanceof Error ? reason.message : 'Unable to update this ritual.') }
    finally { setPendingHabit('') }
  }
  async function signOut() { try { await logout(token) } finally { onSignOut() } }
  if (!tree && !error) return <main className="auth-page">Growing your garden…</main>
  if (activeSkill === 'dictation') return <Dictation onBack={() => setActiveSkill(null)} />

  return <main className="mind-tree-app">
    <header className="garden-header">
      <a className="wordmark" href="#top" aria-label="MindMirror home"><span>✦</span> MindMirror</a>
      <div className="day-chip">{today}</div>
      <button className="avatar" aria-label="Sign out" title="Sign out" onClick={signOut}>{user.displayName.slice(0, 2).toUpperCase()}</button>
    </header>
    <div className="dashboard-shell" id="top">
      <section className="welcome-row">
        <div><p className="eyebrow">GOOD MORNING, {user.displayName.toUpperCase()}</p><h2>Make today count quietly.</h2></div>
        <div className="level-card"><span>LEVEL</span><b>{tree?.level ?? 1}</b><div><strong>{tree?.xp ?? 0} XP</strong><small>{Math.max(0, (tree?.xpToNextLevel ?? 100) - (tree?.xp ?? 0))} to level {(tree?.level ?? 1) + 1}</small><ProgressBar value={(tree?.xp ?? 0) / (tree?.xpToNextLevel ?? 100) * 100} label="Level progress" /></div></div>
      </section>
      <div className="tree-layout">
        <MindTree stage={stage} completedHabits={completed} />
        <aside className="growth-panel">
          <div className="panel-heading"><div><span className="eyebrow">TODAY'S RHYTHM</span><h2>Small care, visible growth.</h2></div><Sparkles size={19} /></div>
          <div className="daily-progress"><div><b>{completed} of {habits.length}</b><span>rituals tended</span></div><ProgressBar value={dailyPercent} label={`${completed} of ${habits.length} rituals completed`} /></div>
          {error && <p className="dashboard-error" role="alert">{error}</p>}
          <div className="habit-preview">{progressHabits.map(habit => <button type="button" className={habit.completed ? 'ritual complete' : 'ritual'} key={habit.id} onClick={() => completeHabit(habit)} disabled={Boolean(pendingHabit)}><span>{habit.icon}</span><div><b>{habit.label}</b><small>{habit.category}</small></div><i>{pendingHabit === habit.id ? '…' : habit.completed ? '✓' : '○'}</i></button>)}</div>
          <section className="practice-section" aria-labelledby="practice-heading">
            <div className="practice-section-heading"><div><span className="eyebrow">PRACTICE</span><h3 id="practice-heading">Build a skill</h3></div><Sparkles size={17} /></div>
            <button className="skill-card" type="button" onClick={() => setActiveSkill('dictation')}><span className="skill-icon" aria-hidden="true">🎧</span><span><b>English Dictation</b><small>Listen, type, and improve accuracy</small></span><span className="skill-arrow" aria-hidden="true">→</span></button>
          </section>
          <div className="streak-row"><div className="streak-icon"><Flame size={20} /></div><div><span>CURRENT STREAK</span><b>{tree?.currentStreak ?? 0} days of showing up</b></div><Leaf size={18} /></div>
        </aside>
      </div>
      <section className="reflection-card"><span>✦</span><div><p className="eyebrow">A MOMENT FOR YOU</p><h2>“What would make today feel meaningful?”</h2></div><button type="button">Begin a reflection <span>→</span></button></section>
    </div>
  </main>
}
