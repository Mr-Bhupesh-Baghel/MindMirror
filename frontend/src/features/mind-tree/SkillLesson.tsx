import { ArrowLeft, Award, CheckCircle2, Lock } from 'lucide-react'
import type { SkillKey, SkillProgress } from '../../types/mindTree'

const titles: Record<SkillKey, string> = { dictation: 'English Dictation', vocabulary: 'Vocabulary', reading: 'Reading', writing: 'Writing', grammar: 'Grammar', speaking: 'Speaking', quiz: 'Quiz' }
export function SkillLesson({ skill, onBack, onComplete, saving }: { skill: SkillProgress; onBack: () => void; onComplete: () => void; saving: boolean }) {
  const isAvailable = skill.key !== 'speaking' && skill.key !== 'quiz'
  return <main className="lesson-page"><button className="back-button" onClick={onBack}><ArrowLeft size={18} /> Learning path</button><section className="lesson-card"><span className="lesson-orb"><Award size={34} /></span><p className="eyebrow">{isAvailable ? 'NEXT LESSON' : 'ON THE WAY'}</p><h1>{titles[skill.key]}</h1><p>{isAvailable ? 'Complete a focused lesson to earn 20 XP, grow your streak, and strengthen your MindTree.' : 'This skill is being prepared for your learning path. Keep mastering the available lessons in the meantime.'}</p>{isAvailable && <><div className="lesson-meta"><span>{skill.completedLessons} of {skill.totalLessons} lessons complete</span><span>+20 XP</span></div><button className="lesson-cta" onClick={onComplete} disabled={saving || skill.completed}>{skill.completed ? <><CheckCircle2 size={18} /> Skill complete</> : saving ? 'Saving progress…' : 'Complete a lesson'}</button></>}{!isAvailable && <span className="coming-soon"><Lock size={16} /> Coming soon</span>}</section></main>
}
