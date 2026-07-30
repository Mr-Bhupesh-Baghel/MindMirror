import type { DailyProgress } from '../types/mindTree'

export const mockDailyProgress: DailyProgress = {
  xp: 340,
  xpToNextLevel: 500,
  level: 8,
  currentStreak: 12,
  longestStreak: 21,
  habits: [
    { id: 'hydrate', label: 'Hydrate', category: 'Body', icon: '💧', completed: true },
    { id: 'move', label: 'Move with intention', category: 'Body', icon: '◌', completed: true },
    { id: 'reflect', label: 'Journal a thought', category: 'Mind', icon: '✦', completed: true },
    { id: 'learn', label: 'Learn for 20 minutes', category: 'Craft', icon: '⌁', completed: false },
    { id: 'unwind', label: 'Unplug before bed', category: 'Rest', icon: '☾', completed: false },
  ],
}
