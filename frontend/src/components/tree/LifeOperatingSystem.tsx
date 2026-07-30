import { useCallback, useMemo, useState } from 'react'
import { Background, Controls, MiniMap, ReactFlow, ReactFlowProvider, type Edge, type Node, type NodeMouseHandler, useReactFlow } from '@xyflow/react'
import '@xyflow/react/dist/style.css'
import { Bell, Bot, CalendarDays, Check, ChevronLeft, ChevronRight, CirclePlus, FilePenLine, FolderKanban, Goal, Leaf, Maximize, Moon, Search, Sparkles, Sun, Target, TreePine, X } from 'lucide-react'
import { BranchEdge } from './BranchEdge'
import { TreeNode, type TreeNodeData } from './TreeNode'

type Branch = { id: string; parentId: string | null; name: string; icon: string; color: string; position: number }
type Tree = { currentStreak: number; longestStreak: number; todayHabits: string[]; unlockedAchievements: string[]; season: string }
type Props = { tree: Tree; branches: Branch[]; busy: boolean; onHabit: (id: string) => void; onOpen: (id: string) => void; onAddBranch: () => void }

const habits = [
 { id: 'water', icon: '💧', label: 'Water' }, { id: 'exercise', icon: '💪', label: 'Move your body' },
 { id: 'learning', icon: '📚', label: 'Learn something' }, { id: 'sleep', icon: '😴', label: 'Wind down well' },
]
const palette: Record<string, string> = { Health: '#4d9b65', Learning: '#5783d9', Career: '#8c62cf', Personal: '#e39555', Finance: '#2e9b7c', Relationships: '#dc7195' }

function CanvasActions({ onAdd }: { onAdd: () => void }) {
 const flow = useReactFlow()
 return <div className="canvas-actions"><button onClick={() => flow.zoomIn()} aria-label="Zoom in">+</button><button onClick={() => flow.zoomOut()} aria-label="Zoom out">−</button><button onClick={() => flow.fitView({ duration: 500, padding: .25 })} aria-label="Center tree"><Target size={16} /></button><button onClick={() => document.documentElement.requestFullscreen?.()} aria-label="Fullscreen"><Maximize size={16} /></button><button className="canvas-add" onClick={onAdd}><CirclePlus size={16} /> Add branch</button></div>
}

export function LifeOperatingSystem({ tree, branches, busy, onHabit, onOpen, onAddBranch }: Props) {
 const [sidebarOpen, setSidebarOpen] = useState(true)
 const [rightOpen, setRightOpen] = useState(true)
 const [query, setQuery] = useState('')
 const [dark, setDark] = useState(false)
 const [assistantOpen, setAssistantOpen] = useState(false)
 const [notice, setNotice] = useState('')
 const [activeArea, setActiveArea] = useState('My Mind Tree')
 const visibleBranches = useMemo(() => branches.filter(branch => branch.name.toLowerCase().includes(query.toLowerCase())), [branches, query])
 const nodes = useMemo<Node<TreeNodeData>[]>(() => {
  const root: Node<TreeNodeData> = { id: 'life', type: 'tree', position: { x: 0, y: 0 }, data: { title: 'My Life', icon: '🌱', color: '#315e45', kind: 'Life tree', progress: Math.min(100, tree.currentStreak * 3), childCount: branches.length } }
  const rootBranches = visibleBranches.filter(item => !item.parentId)
  return [root, ...visibleBranches.map((branch, index) => {
   const parent = visibleBranches.find(candidate => candidate.id === branch.parentId)
   const isRoot = !parent
   const rootIndex = rootBranches.findIndex(item => item.id === branch.id)
   const angle = isRoot ? (rootIndex / Math.max(rootBranches.length, 1)) * Math.PI * 2 - Math.PI / 2 : ((index % 6) / 6) * Math.PI * 2
   const parentRootIndex = rootBranches.findIndex(item => item.id === parent?.id)
   const parentAngle = parentRootIndex >= 0 ? (parentRootIndex / Math.max(rootBranches.length, 1)) * Math.PI * 2 - Math.PI / 2 : 0
   const baseX = isRoot ? 0 : Math.cos(parentAngle) * 460
   const baseY = isRoot ? 0 : Math.sin(parentAngle) * 330
   return { id: branch.id, type: 'tree', position: { x: baseX + Math.cos(angle) * (isRoot ? 460 : 235), y: baseY + Math.sin(angle) * (isRoot ? 330 : 180) }, data: { title: branch.name, icon: branch.icon, color: palette[branch.name] ?? branch.color, kind: isRoot ? 'Life area' : 'Sub-branch', progress: Math.min(100, 25 + branches.filter(item => item.parentId === branch.id).length * 18), childCount: branches.filter(item => item.parentId === branch.id).length } }
  })]
 }, [branches, tree.currentStreak, visibleBranches])
 const edges = useMemo<Edge[]>(() => visibleBranches.map(branch => ({ id: `edge-${branch.id}`, source: branch.parentId && visibleBranches.some(candidate => candidate.id === branch.parentId) ? branch.parentId : 'life', target: branch.id, type: 'branch', animated: true })), [visibleBranches])
 const focusBranch = useCallback((id: string, name?: string) => { setActiveArea(name ?? 'Life area'); onOpen(id) }, [onOpen])
 const nodeTypes = useMemo(() => ({ tree: TreeNode }), [])
 const edgeTypes = useMemo(() => ({ branch: BranchEdge }), [])
 const onNodeClick: NodeMouseHandler = (_, node) => { if (node.id !== 'life') focusBranch(node.id, (node.data as TreeNodeData).title) }
 const quickActions = [{ label: 'Branch', icon: TreePine, action: onAddBranch }, { label: 'Skill', icon: Sparkles }, { label: 'Habit', icon: Leaf }, { label: 'Goal', icon: Goal }, { label: 'Note', icon: FilePenLine }, { label: 'Project', icon: FolderKanban }, { label: 'Calendar', icon: CalendarDays }]
 const completeToday = tree.todayHabits.length
 return <ReactFlowProvider><div className={`life-os ${dark ? 'night' : ''}`}>
  <header className="os-navbar"><button className="os-brand" onClick={() => setActiveArea('My Mind Tree')}><span>✦</span> MindMirror</button><label className="os-search"><Search size={16} /><input value={query} onChange={event => setQuery(event.target.value)} placeholder="Search your life tree" /><kbd>⌘ K</kbd></label><div className="nav-actions"><button onClick={() => setNotice('You’re all caught up — no new notifications.')} aria-label="Notifications"><Bell size={18} /></button><button className="ai-button" onClick={() => setAssistantOpen(value => !value)}><Bot size={17} /> AI Assistant</button><button onClick={() => setDark(value => !value)} aria-label="Toggle theme">{dark ? <Sun size={18} /> : <Moon size={18} />}</button><button className="profile" onClick={() => setNotice('Your profile settings will be available here soon.')}>MM</button></div></header>
  <aside className={`os-sidebar ${sidebarOpen ? '' : 'collapsed'}`}><button className="panel-toggle" onClick={() => setSidebarOpen(value => !value)} aria-label="Toggle navigation">{sidebarOpen ? <ChevronLeft size={16} /> : <ChevronRight size={16} />}</button>{sidebarOpen && <><p className="sidebar-label">YOUR WORLD</p><button className={`side-item ${activeArea === 'My Mind Tree' ? 'active' : ''}`} onClick={() => setActiveArea('My Mind Tree')}><span>🌱</span> My Mind Tree</button>{branches.filter(branch => !branch.parentId).map(branch => <button className={`side-item ${activeArea === branch.name ? 'active' : ''}`} key={branch.id} onClick={() => focusBranch(branch.id, branch.name)}><span>{branch.icon}</span>{branch.name}</button>)}<button className="side-add" onClick={onAddBranch}><CirclePlus size={16} /> Add Branch</button></>}</aside>
  <main className="os-canvas"><div className="canvas-heading"><p>YOUR LIFE MAP</p><h1>{query ? `Results for “${query}”` : activeArea}</h1><span>{visibleBranches.length} connected {visibleBranches.length === 1 ? 'area' : 'areas'} · {tree.season} season</span></div><ReactFlow nodes={nodes} edges={edges} nodeTypes={nodeTypes} edgeTypes={edgeTypes} onNodeClick={onNodeClick} fitView fitViewOptions={{ padding: .24 }} minZoom={.25} maxZoom={1.8} proOptions={{ hideAttribution: true }}><Background gap={26} size={1} color="#dfe9dc" /><Controls showInteractive={false} /><MiniMap nodeColor={node => (node.data as TreeNodeData).color} maskColor="rgba(248, 251, 246, .7)" /></ReactFlow><CanvasActions onAdd={onAddBranch} /><div className="canvas-caption"><TreePine size={15} /> Drag, scroll, and explore your life</div>{assistantOpen && <section className="assistant-popover"><button className="popover-close" onClick={() => setAssistantOpen(false)} aria-label="Close"><X size={15} /></button><Sparkles size={18} /><b>What would you like to reflect on?</b><p>I can help turn a thought into a goal, habit, or a clearer next step.</p><button onClick={() => { setAssistantOpen(false); setNotice('Reflection mode is ready. Start by adding a note to a life area.') }}>Start a reflection →</button></section>}</main>
  <aside className={`os-right-panel ${rightOpen ? '' : 'collapsed'}`}><button className="panel-toggle right-toggle" onClick={() => setRightOpen(value => !value)} aria-label="Toggle life pulse">{rightOpen ? <ChevronRight size={16} /> : <ChevronLeft size={16} />}</button>{rightOpen && <div className="right-content"><p className="sidebar-label">LIFE PULSE</p><div className="tree-level"><span>🌳</span><div><b>Your Mind Tree</b><small>{tree.currentStreak ? `${tree.currentStreak}-day growth rhythm` : 'Ready for today'}</small></div></div><div className="stat-grid"><Stat label="Today’s care" value={`${completeToday}/4`} /><Stat label="Habit streak" value={`${tree.currentStreak} days`} /><Stat label="Life areas" value={String(branches.length)} /><Stat label="Achievements" value={String(tree.unlockedAchievements.length)} /></div><section className="today-card"><div><p className="sidebar-label">TODAY’S CARE</p><b>{completeToday === habits.length ? 'Beautifully done today.' : 'Small actions, living results.'}</b><span className="today-progress"><i style={{ width: `${completeToday / habits.length * 100}%` }} /></span></div>{habits.map(habit => <button disabled={busy} className={tree.todayHabits.includes(habit.id) ? 'task done' : 'task'} key={habit.id} onClick={() => onHabit(habit.id)}><span>{habit.icon}</span>{habit.label}<i>{tree.todayHabits.includes(habit.id) ? <Check size={12} /> : '+'}</i></button>)}</section><section className="focus-card"><p className="sidebar-label">NEXT GENTLE STEP</p><b>Choose one small thing that helps future-you.</b><button onClick={onAddBranch}>Shape a life area <ChevronRight size={14} /></button></section></div>}</aside>
  <nav className="bottom-toolbar">{quickActions.map(action => <button key={action.label} onClick={() => action.action ? action.action() : setNotice(`${action.label} capture is ready to connect to a life area.`)}><action.icon size={17} /><span>{action.label}</span></button>)}</nav>
  {notice && <div className="os-toast" role="status">{notice}<button onClick={() => setNotice('')}><X size={15} /></button></div>}
 </div></ReactFlowProvider>
}
function Stat({ label, value }: { label: string; value: string }) { return <div className="os-stat"><small>{label}</small><b>{value}</b></div> }
