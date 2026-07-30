import type { TreeStage } from '../../types/mindTree'

type Props = { stage: TreeStage; completedHabits: number }

const copy: Record<TreeStage, { title: string; subtitle: string }> = {
  seedling: { title: 'A new beginning', subtitle: 'One gentle action will help it take root.' },
  sapling: { title: 'Your roots are settling in', subtitle: 'Consistency is making room for growth.' },
  'young-tree': { title: 'Your tree is reaching upward', subtitle: 'Each completed ritual strengthens its branches.' },
  flourishing: { title: 'A flourishing rhythm', subtitle: 'Your care is beginning to bloom.' },
  abundant: { title: 'An abundant inner world', subtitle: 'What you nurture is bearing fruit.' },
}

export function MindTree({ stage, completedHabits }: Props) {
  const details = copy[stage]
  const showBranches = stage !== 'seedling'
  const showFlowers = stage === 'flourishing' || stage === 'abundant'
  const showFruit = stage === 'abundant'

  return (
    <section className={`mind-tree stage-${stage}`} aria-label={`Mind Tree: ${details.title}`}>
      <div className="tree-aura" />
      <div className="tree-copy">
        <span className="eyebrow">YOUR MIND TREE</span>
        <h1>{details.title}</h1>
        <p>{details.subtitle}</p>
      </div>
      <svg className="tree-illustration" viewBox="0 0 520 505" role="img" aria-hidden="true">
        <defs>
          <linearGradient id="trunk" x1="0" x2="1"><stop stopColor="#563b2d"/><stop offset=".52" stopColor="#9b6845"/><stop offset="1" stopColor="#422d26"/></linearGradient>
          <radialGradient id="leaf" cx="40%" cy="25%"><stop stopColor="#b6dc82"/><stop offset=".72" stopColor="#5c9b64"/><stop offset="1" stopColor="#347050"/></radialGradient>
          <filter id="soft"><feGaussianBlur stdDeviation="8"/></filter>
        </defs>
        <ellipse className="tree-shadow" cx="259" cy="448" rx="167" ry="27" />
        <g className="tree-roots"><path d="M254 425c-38 9-75 24-124 13M258 426c-2 9 13 17 39 19M253 428c-26 6-50 17-70 29M270 427c38 7 73 5 104 20" /></g>
        <path className="tree-trunk" d="M241 429c15-44 9-86 1-119-12-52 5-87 23-109 4 45 14 68 17 99 2 37-3 79 17 129z" />
        {showBranches && <g className="tree-branches"><path d="M255 263c-38-20-67-43-91-78M263 258c41-30 69-63 88-96M255 315c-44-3-78-18-113-47M271 310c43-4 76-24 108-54" /></g>}
        <g className="tree-canopy">
          <circle cx="180" cy="183" r="73" /><circle cx="275" cy="126" r="85" /><circle cx="359" cy="195" r="76" />
          {showBranches && <><circle cx="128" cy="254" r="54"/><circle cx="406" cy="262" r="56"/><circle cx="230" cy="240" r="78"/><circle cx="323" cy="246" r="81"/></>}
        </g>
        {showFlowers && <g className="tree-flowers"><circle cx="180" cy="171" r="8"/><circle cx="316" cy="104" r="7"/><circle cx="383" cy="216" r="8"/><circle cx="243" cy="245" r="7"/><circle cx="129" cy="250" r="6" /></g>}
        {showFruit && <g className="tree-fruit"><circle cx="276" cy="172" r="8"/><circle cx="336" cy="252" r="8"/><circle cx="198" cy="232" r="7" /></g>}
        <g className="tree-sparkles"><circle cx="115" cy="146" r="3"/><circle cx="430" cy="153" r="3"/><circle cx="390" cy="106" r="2"/></g>
      </svg>
      <div className="tree-grounding"><span>{completedHabits}</span> care moments today</div>
    </section>
  )
}
