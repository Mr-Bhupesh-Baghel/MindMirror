import { useMemo, useState } from 'react'
import { Flame, Leaf, Sparkles } from 'lucide-react'
import { MindTree } from '../../components/mind-tree/MindTree'
import { ProgressBar } from '../../components/ui/ProgressBar'
import { mockDailyProgress } from '../../data/mockMindTree'
import type { DailyProgress, TreeStage } from '../../types/mindTree'

function getTreeStage(completed: number, total: number): TreeStage {
  const progress = completed / total
  if (progress === 0) return 'seedling'
  if (progress < .4) return 'sapling'
  if (progress < .7) return 'young-tree'
  if (progress < 1) return 'flourishing'
  return 'abundant'
}

export function MindTreeDashboard() {
  const [progress] = useState<DailyProgress>(mockDailyProgress)
  const completed = progress.habits.filter(habit => habit.completed).length
  const dailyPercent = useMemo(() => completed / progress.habits.length * 100, [completed, progress.habits.length])
  const stage = getTreeStage(completed, progress.habits.length)

  return <main className="mind-tree-app">
    <header className="garden-header">
      <a className="wordmark" href="#top" aria-label="MindMirror home"><span>✦</span> MindMirror</a>
      <div className="day-chip"><span>Wednesday</span><b>30 Jul</b></div>
      <button className="avatar" aria-label="Open profile">AM</button>
    </header>

    <div className="dashboard-shell" id="top">
      <section className="welcome-row">
        <div><p className="eyebrow">GOOD MORNING, ARIA</p><h2>Make today count quietly.</h2></div>
        <div className="level-card"><span>LEVEL</span><b>{progress.level}</b><div><strong>{progress.xp} XP</strong><small>{progress.xpToNextLevel - progress.xp} to level {progress.level + 1}</small><ProgressBar value={progress.xp / progress.xpToNextLevel * 100} label="Level progress" /></div></div>
      </section>

      <div className="tree-layout">
        <MindTree stage={stage} completedHabits={completed} />
        <aside className="growth-panel">
          <div className="panel-heading"><div><span className="eyebrow">TODAY'S RHYTHM</span><h2>Small care, visible growth.</h2></div><Sparkles size={19} /></div>
          <div className="daily-progress"><div><b>{completed} of {progress.habits.length}</b><span>rituals tended</span></div><ProgressBar value={dailyPercent} label={`${completed} of ${progress.habits.length} rituals completed`} /></div>
          <div className="habit-preview">{progress.habits.map(habit => <div className={habit.completed ? 'ritual complete' : 'ritual'} key={habit.id}><span>{habit.icon}</span><div><b>{habit.label}</b><small>{habit.category}</small></div><i>{habit.completed ? '✓' : '○'}</i></div>)}</div>
          <div className="streak-row"><div className="streak-icon"><Flame size={20} /></div><div><span>CURRENT STREAK</span><b>{progress.currentStreak} days of showing up</b></div><Leaf size={18} /></div>
        </aside>
      </div>

      <section className="reflection-card"><span>✦</span><div><p className="eyebrow">A MOMENT FOR YOU</p><h2>“What would make today feel meaningful?”</h2></div><button type="button">Begin a reflection <span>→</span></button></section>
    </div>
  </main>
}
