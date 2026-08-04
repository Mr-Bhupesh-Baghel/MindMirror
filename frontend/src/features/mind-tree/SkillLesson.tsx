import { ArrowLeft, Award, CheckCircle2, Lock } from 'lucide-react'
import type { SkillProgress } from '../../types/mindTree'
import { getSkillDefinition } from '../Skills/catalog'

export function SkillLesson({ skill, onBack, onComplete, saving }: { skill: SkillProgress; onBack: () => void; onComplete: () => void; saving: boolean }) {
  const definition = getSkillDefinition(skill.key)
  if (!definition) return null
  return <main className="lesson-page"><button className="back-button" onClick={onBack}><ArrowLeft size={18} /> Learning path</button><section className="lesson-card"><span className="lesson-orb"><Award size={34} /></span><p className="eyebrow">{definition.available ? 'NEXT LESSON' : 'ON THE WAY'}</p><h1>{definition.title}</h1><p>{definition.available ? 'Complete a focused lesson to earn 20 XP, grow your streak, and strengthen your MindTree.' : 'This skill is being prepared for your learning path. Keep mastering the available lessons in the meantime.'}</p>{definition.available && <><div className="lesson-meta"><span>{skill.completedLessons} of {skill.totalLessons} lessons complete</span><span>+20 XP</span></div><button className="lesson-cta" onClick={onComplete} disabled={saving || skill.completed}>{skill.completed ? <><CheckCircle2 size={18} /> Skill complete</> : saving ? 'Saving progress…' : 'Complete a lesson'}</button></>}{!definition.available && <span className="coming-soon"><Lock size={16} /> Coming soon</span>}</section></main>
}
