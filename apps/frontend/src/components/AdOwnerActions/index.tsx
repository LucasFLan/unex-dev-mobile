"use client";

import { useState, useTransition } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { Pause, Pencil, Play, Trash2 } from "lucide-react";
import { deleteAd, setAdActive } from "@/app/anunciar/actions";
import type { AnuncioItem } from "@/lib/ads";
import { withFrom } from "@/lib/navigation";

const buttonBase =
  "flex h-9.75 items-center justify-center gap-1.5 rounded-lg border text-[10px] font-semibold transition disabled:opacity-50";

/** Rodapé com as ações do autor no detalhe do anúncio. */
export function AdOwnerActions({
  ad,
  backHref,
}: {
  ad: AnuncioItem;
  /** Origem do detalhe, para onde ir depois de excluir. */
  backHref: string;
}) {
  const router = useRouter();
  const pathname = usePathname();
  // A edição volta para este detalhe, que por sua vez lembra a própria origem.
  const detailHref = backHref === "/home" ? pathname : withFrom(pathname, backHref);
  const [pending, startTransition] = useTransition();
  const [error, setError] = useState("");

  const toggleActive = () =>
    startTransition(async () => {
      setError("");
      const result = await setAdActive(ad.id, !ad.ativo);
      if (!result.ok) setError(result.error);
      else router.refresh();
    });

  const remove = () => {
    if (!window.confirm(`Excluir "${ad.titulo}"? Esta ação não pode ser desfeita.`)) {
      return;
    }

    startTransition(async () => {
      setError("");
      const result = await deleteAd(ad.id);
      if (!result.ok) setError(result.error);
      else router.replace(backHref);
    });
  };

  return (
    <footer className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-4 pb-[max(20px,env(safe-area-inset-bottom))] pt-2">
      <div className="mx-auto max-w-md">
        {!ad.ativo && (
          <p className="mb-2 rounded-lg bg-slate-100 px-3 py-2 text-[10px] text-slate-600">
            Este anúncio está pausado e não aparece para outras pessoas.
          </p>
        )}

        {error && (
          <p
            role="alert"
            className="mb-2 rounded-lg bg-red-50 px-3 py-2 text-[10px] text-red-600"
          >
            {error}
          </p>
        )}

        <div className="grid grid-cols-3 gap-2">
          <Link
            href={withFrom(`/anunciar/${ad.id}/editar`, detailHref)}
            className={`${buttonBase} border-blue-600 bg-blue-600 text-white`}
          >
            <Pencil size={13} aria-hidden="true" />
            Editar
          </Link>

          <button
            type="button"
            onClick={toggleActive}
            disabled={pending}
            className={`${buttonBase} border-slate-200 text-slate-700`}
          >
            {ad.ativo ? (
              <Pause size={13} aria-hidden="true" />
            ) : (
              <Play size={13} aria-hidden="true" />
            )}
            {ad.ativo ? "Pausar" : "Retomar"}
          </button>

          <button
            type="button"
            onClick={remove}
            disabled={pending}
            className={`${buttonBase} border-red-200 text-red-600`}
          >
            <Trash2 size={13} aria-hidden="true" />
            Excluir
          </button>
        </div>
      </div>
    </footer>
  );
}
