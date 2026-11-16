"use client";

import { useRouter } from "next/navigation";
import { ArrowLeft } from "lucide-react";

const defaultClassName =
  "flex h-10 w-10 items-center justify-center rounded-xl text-slate-700 focus-visible:outline-2 focus-visible:outline-blue-600";

type BackButtonProps = {
  fallbackHref?: string;
  label?: string;
  className?: string;
  iconSize?: number;
};

export function BackButton({
  fallbackHref = "/home",
  label = "Voltar",
  className = defaultClassName,
  iconSize = 19,
}: BackButtonProps) {
  const router = useRouter();

  const handleClick = () => {
    if (window.history.length > 1) router.back();
    else router.push(fallbackHref);
  };

  return (
    <button
      type="button"
      onClick={handleClick}
      aria-label={label}
      className={className}
    >
      <ArrowLeft size={iconSize} aria-hidden="true" />
    </button>
  );
}