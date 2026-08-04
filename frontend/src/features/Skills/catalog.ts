import { BookOpenText, Check, Headphones, Languages, Mic, PenLine, Trophy, type LucideIcon } from 'lucide-react'
import type { SkillKey } from '../../types/mindTree'

export type SkillDefinition = {
  key: SkillKey
  title: string
  description: string
  lessons: number
  icon: LucideIcon
  available: boolean
}

// Add a skill here to place it on the dashboard. Keep its key and lesson count
// aligned with the backend skill catalog so progress and unlocks are persisted.
export const skillCatalog: SkillDefinition[] = [
  { key: 'dictation', title: 'English Dictation', description: 'Listen closely and write with confidence.', lessons: 8, icon: Headphones, available: true },
  { key: 'vocabulary', title: 'Vocabulary', description: 'Grow a useful everyday word bank.', lessons: 6, icon: Languages, available: true },
  { key: 'reading', title: 'Reading', description: 'Build comprehension one story at a time.', lessons: 6, icon: BookOpenText, available: true },
  { key: 'writing', title: 'Writing', description: 'Turn your ideas into clear sentences.', lessons: 5, icon: PenLine, available: true },
  { key: 'grammar', title: 'Grammar', description: 'Make every sentence feel natural.', lessons: 8, icon: Check, available: true },
  { key: 'speaking', title: 'Speaking', description: 'Practice saying it out loud.', lessons: 5, icon: Mic, available: false },
  { key: 'quiz', title: 'Quiz', description: 'Put your new skills to the test.', lessons: 5, icon: Trophy, available: false },
]

export function getSkillDefinition(key: SkillKey) { return skillCatalog.find(skill => skill.key === key) }
