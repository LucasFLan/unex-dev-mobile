"use client";

import { useRouter } from "next/navigation";
import { ArrowLeft } from "lucide-react";

const defaultClassName =
  "flex h-10 w-10 items-center justify-center rounded-xl text-slate-700 focus-visible:outline-2 focus-visible:outline-blue-600";

type BackButtonProps = {
  /** Tela-pai: para onde o botão leva (ver lib/navigation). */
  href: string;
  label?: string;
  className?: string;
  iconSize?: number;
};

/**
 * Volta para a tela-pai, e não para a última página visitada: usar o histórico do navegador fazia
 * Menu → Segurança → Senha → voltar → voltar cair de novo em Senha.
 */
export function BackButton({
  href,
  label = "Voltar",
  className = defaultClassName,
  iconSize = 19,
}: BackButtonProps) {
  const router = useRouter();

  return (
    <button
      type="button"
      onClick={() => router.push(href)}
      aria-label={label}
      className={className}
    >
      <ArrowLeft size={iconSize} aria-hidden="true" />
    </button>
  );
}
