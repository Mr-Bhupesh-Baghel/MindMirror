export type TreeStage = 'seedling' | 'sapling' | 'young-tree' | 'flourishing' | 'abundant'

export type Habit = {
  id: string
  label: string
  category: string
  icon: string
  completed: boolean
}

export type DailyProgress = {
  xp: number
  xpToNextLevel: number
  level: number
  currentStreak: number
  longestStreak: number
  habits: Habit[]
}
