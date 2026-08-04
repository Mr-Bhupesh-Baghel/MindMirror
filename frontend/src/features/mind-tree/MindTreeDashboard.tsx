import { useEffect, useMemo, useState } from 'react'
import { Flame, Sparkles } from 'lucide-react'
import { MindTree } from '../../components/mind-tree/MindTree'
import { ProgressBar } from '../../components/ui/ProgressBar'
import { completeSkillLesson, loadTree, logout, type TreeState, type User } from '../../lib/api'
import type { SkillKey } from '../../types/mindTree'
import { SkillPath } from './SkillPath'
import { SkillLesson } from './SkillLesson'

export function MindTreeDashboard({ token, user, onSignOut }: { token: string; user: User; onSignOut: () => void }) {
  const [tree, setTree] = useState<TreeState | null>(null); const [activeSkill, setActiveSkill] = useState<SkillKey | null>(null); const [error, setError] = useState(''); const [saving, setSaving] = useState(false)
  useEffect(() => { loadTree(token).then(setTree).catch(reason => setError(reason instanceof Error ? reason.message : 'Unable to load your learning path.')) }, [token])
  const completedLessons = useMemo(() => tree?.skills.reduce((sum, skill) => sum + skill.completedLessons, 0) ?? 0, [tree])
  const selected = tree?.skills.find(skill => skill.key === activeSkill)
  async function finishLesson() { if (!activeSkill) return; setSaving(true); setError(''); try { setTree(await completeSkillLesson(token, activeSkill)) } catch (reason) { setError(reason instanceof Error ? reason.message : 'Unable to save this lesson.') } finally { setSaving(false) } }
  async function signOut() { try { await logout(token) } finally { onSignOut() } }
  if (!tree && !error) return <main className="auth-page">Loading your learning path…</main>
  if (selected) return <SkillLesson skill={selected} onBack={() => setActiveSkill(null)} onComplete={finishLesson} saving={saving} />
  const today = new Intl.DateTimeFormat(undefined, { weekday: 'long', day: 'numeric', month: 'short' }).format(new Date())
  return <main className="mind-tree-app"><header className="garden-header"><a className="wordmark" href="#top"><span>✦</span> MindMirror</a><div className="day-chip">{today}</div><button className="avatar" aria-label="Sign out" title="Sign out" onClick={signOut}>{user.displayName.slice(0, 2).toUpperCase()}</button></header><div className="dashboard-shell" id="top"><section className="welcome-row"><div><p className="eyebrow">READY TO LEARN, {user.displayName.toUpperCase()}?</p><h2>One lesson can change your day.</h2></div><div className="level-card"><span>LEVEL</span><b>{tree?.level ?? 1}</b><div><strong>{tree?.xp ?? 0} XP</strong><small>{Math.max(0, (tree?.xpToNextLevel ?? 100) - (tree?.xp ?? 0))} XP to level {(tree?.level ?? 1) + 1}</small><ProgressBar value={(tree?.xp ?? 0) / (tree?.xpToNextLevel ?? 100) * 100} label="Level progress" /></div></div></section>{error && <p className="dashboard-error" role="alert">{error}</p>}<div className="tree-layout"><MindTree stage={tree?.stage ?? 'seedling'} lessonsCompleted={completedLessons} /><aside className="growth-panel"><div className="panel-heading"><div><span className="eyebrow">TODAY'S QUEST</span><h2>Keep your learning streak alive.</h2></div><Sparkles size={19} /></div><p className="quest-copy">Finish a lesson to earn XP and help your MindTree flourish.</p><button className="quest-button" onClick={() => setActiveSkill('dictation')}>Start English Dictation <span>→</span></button><div className="streak-row"><div className="streak-icon"><Flame size={20} /></div><div><span>CURRENT STREAK</span><b>{tree?.currentStreak ?? 0} days of learning</b></div></div></aside></div><SkillPath skills={tree?.skills ?? []} onSelect={setActiveSkill} /></div></main>
}
