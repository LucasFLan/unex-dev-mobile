import type { LucideIcon } from "lucide-react";

export function InfoRow({
  icon: Icon,
  label,
  value,
}: {
  icon: LucideIcon;
  label: string;
  value: string;
}) {
  return (
    <li className="flex items-center gap-3 px-3 py-2.5">
      <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-slate-100 text-slate-500">
        <Icon size={16} strokeWidth={1.8} aria-hidden="true" />
      </span>
      <div className="min-w-0">
        <p className="text-[10px] text-slate-500">{label}</p>
        <p className="text-xs font-medium text-slate-800">{value}</p>
      </div>
    </li>
  );
}