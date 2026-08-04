export type TreeStage = 'seedling' | 'sapling' | 'young-tree' | 'flourishing' | 'abundant'
export type SkillKey = 'dictation' | 'vocabulary' | 'reading' | 'writing' | 'grammar' | 'speaking' | 'quiz'
export type SkillProgress = { key: SkillKey; completedLessons: number; totalLessons: number; unlocked: boolean; completed: boolean }
