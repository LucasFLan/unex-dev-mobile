"use client";

import { useEffect, useState } from "react";
import { ChevronLeft, ChevronRight, X } from "lucide-react";

const photoUrl = (proposalId: string, photoId: string) =>
  `/api/propostas/${encodeURIComponent(proposalId)}/fotos/${encodeURIComponent(photoId)}`;

/** Miniaturas das fotos anexadas a uma proposta; tocar abre a foto em tela cheia. */
export function ProposalPhotos({ proposalId, photos }: { proposalId: string; photos: string[] }) {
  const [open, setOpen] = useState<number | null>(null);

  if (photos.length === 0) return null;

  return (
    <>
      <ul className="mt-2 flex gap-1.5 overflow-x-auto pb-0.5" aria-label="Fotos anexadas">
        {photos.map((photoId, index) => (
          <li key={photoId} className="shrink-0">
            <button
              type="button"
              onClick={() => setOpen(index)}
              aria-label={`Ver foto ${index + 1} de ${photos.length}`}
              className="block h-14 w-14 overflow-hidden rounded-lg border border-slate-200 bg-slate-100 focus-visible:outline-2 focus-visible:outline-blue-600"
            >
              {/* eslint-disable-next-line @next/next/no-img-element -- imagem autenticada servida pela rota /api do app */}
              <img
                src={photoUrl(proposalId, photoId)}
                alt=""
                loading="lazy"
                decoding="async"
                className="h-full w-full object-cover"
              />
            </button>
          </li>
        ))}
      </ul>

      {open !== null && (
        <PhotoViewer
          urls={photos.map((photoId) => photoUrl(proposalId, photoId))}
          index={open}
          onChange={setOpen}
          onClose={() => setOpen(null)}
        />
      )}
    </>
  );
}

function PhotoViewer({
  urls,
  index,
  onChange,
  onClose,
}: {
  urls: string[];
  index: number;
  onChange: (index: number) => void;
  onClose: () => void;
}) {
  const many = urls.length > 1;
  const previous = () => onChange((index - 1 + urls.length) % urls.length);
  const next = () => onChange((index + 1) % urls.length);

  // Esc fecha; setas trocam de foto. A página por trás não rola.
  useEffect(() => {
    function onKey(event: KeyboardEvent) {
      if (event.key === "Escape") onClose();
      if (many && event.key === "ArrowLeft") onChange((index - 1 + urls.length) % urls.length);
      if (many && event.key === "ArrowRight") onChange((index + 1) % urls.length);
    }
    const overflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    window.addEventListener("keydown", onKey);
    return () => {
      document.body.style.overflow = overflow;
      window.removeEventListener("keydown", onKey);
    };
  }, [index, many, urls.length, onChange, onClose]);

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-label={`Foto ${index + 1} de ${urls.length}`}
      className="fixed inset-0 z-50 flex flex-col bg-black/95 text-white"
    >
      <header className="flex items-center justify-between px-3 pb-2 pt-[max(12px,env(safe-area-inset-top))]">
        <p className="text-sm">{many ? `${index + 1} de ${urls.length}` : ""}</p>
        <button
          type="button"
          onClick={onClose}
          aria-label="Fechar foto"
          className="flex h-10 w-10 items-center justify-center rounded-full bg-white/10"
        >
          <X size={20} aria-hidden="true" />
        </button>
      </header>

      <div className="relative flex flex-1 items-center justify-center overflow-hidden px-2 pb-[max(16px,env(safe-area-inset-bottom))]">
        {/* eslint-disable-next-line @next/next/no-img-element -- imagem autenticada servida pela rota /api do app */}
        <img src={urls[index]} alt={`Foto ${index + 1} anexada à proposta`} className="max-h-full max-w-full object-contain" />

        {many && (
          <>
            <button
              type="button"
              onClick={previous}
              aria-label="Foto anterior"
              className="absolute left-2 flex h-10 w-10 items-center justify-center rounded-full bg-white/15"
            >
              <ChevronLeft size={22} aria-hidden="true" />
            </button>
            <button
              type="button"
              onClick={next}
              aria-label="Próxima foto"
              className="absolute right-2 flex h-10 w-10 items-center justify-center rounded-full bg-white/15"
            >
              <ChevronRight size={22} aria-hidden="true" />
            </button>
          </>
        )}
      </div>
    </div>
  );
}
