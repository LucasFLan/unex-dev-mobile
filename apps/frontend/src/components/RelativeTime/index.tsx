"use client";

import { useEffect, useState } from "react";
import { formatRelativeTime } from "@/lib/format";

export function RelativeTime({ isoDate }: { isoDate: string }) {
  const [now, setNow] = useState<number | null>(null);

  useEffect(() => {
    const tick = () => setNow(Date.now());

    const first = setTimeout(tick, 0);
    const interval = setInterval(tick, 60_000);

    return () => {
      clearTimeout(first);
      clearInterval(interval);
    };
  }, []);

  const label =
    now === null
      ? new Date(isoDate).toLocaleDateString("pt-BR", { timeZone: "UTC" })
      : formatRelativeTime(isoDate, now);

  return (
    <time dateTime={isoDate} suppressHydrationWarning>
      {label}
    </time>
  );
}