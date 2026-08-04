import { Check, ChevronRight, Lock } from 'lucide-react'
import type { SkillKey, SkillProgress } from '../../types/mindTree'
import { getSkillDefinition } from '../Skills/catalog'

export function SkillPath({ skills, onSelect }: { skills: SkillProgress[]; onSelect: (key: SkillKey) => void }) {
  return <section className="skill-path" aria-labelledby="learning-path-heading">
    <div className="section-heading"><div><p className="eyebrow">YOUR LEARNING PATH</p><h2 id="learning-path-heading">Choose your next lesson</h2></div><span>{skills.filter(skill => skill.completed).length}/{skills.length} skills mastered</span></div>
    <div className="skill-nodes">{skills.map(skill => {
      const info = getSkillDefinition(skill.key)
      if (!info) return null
      const Icon = info.icon; const unavailable = !skill.unlocked || !info.available
      return <button type="button" key={skill.key} className={`skill-node ${skill.completed ? 'is-complete' : ''}`} onClick={() => onSelect(skill.key)} disabled={unavailable}>
        <span className="skill-badge"><Icon size={23} /></span><span className="skill-copy"><b>{info.title}</b><small>{unavailable ? (!skill.unlocked ? 'Complete the previous skill to unlock' : 'Coming soon') : info.description}</small><span className="skill-progress"><i style={{ width: `${skill.completedLessons / skill.totalLessons * 100}%` }} />{skill.completedLessons}/{skill.totalLessons} lessons</span></span>
        <span className="skill-action">{unavailable ? <Lock size={17} /> : skill.completed ? <Check size={18} /> : <ChevronRight size={19} />}</span>
      </button>
    })}</div>
  </section>
}
