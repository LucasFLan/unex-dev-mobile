import type { ReactNode } from "react";

export function Block({
  title,
  children,
}: {
  title: string;
  children: ReactNode;
}) {
  return (
    <section aria-label={title}>
      <h2 className="mb-2 px-1 text-[10px] font-medium uppercase tracking-wide text-slate-500">
        {title}
      </h2>
      {children}
    </section>
  );
}