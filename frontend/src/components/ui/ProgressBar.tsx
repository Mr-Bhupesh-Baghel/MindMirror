type Props = { value: number; label: string }

export function ProgressBar({ value, label }: Props) {
  return (
    <div className="progress" aria-label={label}>
      <span style={{ width: `${Math.min(100, Math.max(0, value))}%` }} />
    </div>
  )
}
