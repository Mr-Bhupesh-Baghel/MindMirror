export type TreeStage = 'seedling' | 'sapling' | 'young-tree' | 'flourishing' | 'abundant'
export type SkillKey = string
export type SkillProgress = { key: SkillKey; completedLessons: number; totalLessons: number; unlocked: boolean; completed: boolean }
