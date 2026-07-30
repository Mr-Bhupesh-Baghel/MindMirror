export type TreeStage = 'seedling' | 'sapling' | 'young-tree' | 'flourishing' | 'abundant'

export type Habit = {
  id: string
  key: string
  label: string
  category: string
  icon: string
  completed: boolean
}
