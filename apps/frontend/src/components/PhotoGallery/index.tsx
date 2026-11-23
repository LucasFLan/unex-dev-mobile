"use client";

import { useRef, useState } from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";
import type { AdPhoto } from "@/lib/photos";

/**
 * Fotos do anúncio no topo do detalhe: desliza com o dedo no celular, setas no desktop. Ocupa a área toda do
 * elemento pai (que define a altura).
 */
export function PhotoGallery({ photos, title }: { photos: AdPhoto[]; title: string }) {
  const track = useRef<HTMLDivElement>(null);
  const [current, setCurrent] = useState(0);

  function goTo(index: number) {
    const element = track.current;
    if (!element) return;
    const next = Math.max(0, Math.min(photos.length - 1, index));
    element.scrollTo({ left: next * element.clientWidth, behavior: "smooth" });
  }

  return (
    <div className="absolute inset-0">
      <div
        ref={track}
        onScroll={(event) => {
          const element = event.currentTarget;
          setCurrent(Math.round(element.scrollLeft / Math.max(1, element.clientWidth)));
        }}
        className="flex h-full snap-x snap-mandatory overflow-x-auto [scrollbar-width:none] [&::-webkit-scrollbar]:hidden"
      >
        {photos.map((photo, index) => (
          // eslint-disable-next-line @next/next/no-img-element -- foto servida pelo proxy da API.
          <img
            key={photo.id}
            src={photo.src}
            alt={`${title}: foto ${index + 1} de ${photos.length}`}
            loading={index === 0 ? "eager" : "lazy"}
            className="h-full w-full shrink-0 snap-center object-cover"
          />
        ))}
      </div>

      {photos.length > 1 && (
        <>
          <button
            type="button"
            onClick={() => goTo(current - 1)}
            disabled={current === 0}
            aria-label="Foto anterior"
            className="absolute left-3 top-1/2 hidden h-8 w-8 -translate-y-1/2 items-center justify-center rounded-full bg-white/90 text-slate-700 shadow-sm transition hover:bg-white disabled:opacity-0 md:flex"
          >
            <ChevronLeft size={18} />
          </button>
          <button
            type="button"
            onClick={() => goTo(current + 1)}
            disabled={current === photos.length - 1}
            aria-label="Próxima foto"
            className="absolute right-3 top-1/2 hidden h-8 w-8 -translate-y-1/2 items-center justify-center rounded-full bg-white/90 text-slate-700 shadow-sm transition hover:bg-white disabled:opacity-0 md:flex"
          >
            <ChevronRight size={18} />
          </button>
          <span className="absolute bottom-5 right-3 rounded-full bg-slate-900/70 px-2 py-0.5 text-[10px] font-medium text-white">
            {current + 1}/{photos.length}
          </span>
        </>
      )}
    </div>
  );
}
