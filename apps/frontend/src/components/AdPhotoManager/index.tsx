"use client";

import { useRef, useState, useTransition } from "react";
import { useRouter } from "next/navigation";
import { ImagePlus, Trash2 } from "lucide-react";
import { addAdPhotos, removeAdPhoto } from "@/app/anunciar/actions";
import { adPhotoUrl, MAX_AD_PHOTOS } from "@/lib/ad-photos";
import type { AnuncioItem } from "@/lib/ads";
import { resizeImageForUpload } from "@/lib/image-resize";

/**
 * Fotos do anúncio no detalhe, para o autor: adiciona e remove na hora, sem abrir a edição. A primeira foto é
 * a capa do card na Home.
 */
export function AdPhotoManager({ ad }: { ad: AnuncioItem }) {
  const router = useRouter();
  const input = useRef<HTMLInputElement>(null);
  const [pending, startTransition] = useTransition();
  const [busyId, setBusyId] = useState<string | null>(null);
  const [error, setError] = useState("");
  const room = MAX_AD_PHOTOS - ad.fotos.length;

  function add(files: FileList | null) {
    if (!files || files.length === 0) return;
    const selected = Array.from(files).slice(0, Math.max(0, room));

    startTransition(async () => {
      setError(files.length > room ? `O anúncio pode ter até ${MAX_AD_PHOTOS} fotos: algumas ficaram de fora.` : "");
      const form = new FormData();
      for (const [index, file] of selected.entries()) {
        try {
          form.append("fotos", await resizeImageForUpload(file, `foto-${Date.now()}-${index}`));
        } catch {
          setError(`Não foi possível abrir "${file.name}". Escolha fotos em JPG ou PNG.`);
          return;
        }
      }
      const result = await addAdPhotos(ad.id, form);
      if (!result.ok) setError(result.error);
      else router.refresh();
    });
  }

  function remove(photoId: string, index: number) {
    if (!window.confirm(`Remover a foto ${index + 1} do anúncio?`)) return;
    setBusyId(photoId);
    startTransition(async () => {
      setError("");
      const result = await removeAdPhoto(ad.id, photoId);
      if (!result.ok) setError(result.error);
      else router.refresh();
      setBusyId(null);
    });
  }

  return (
    <section className="mt-4 lg:mt-8" aria-labelledby="titulo-fotos-anuncio">
      <div className="flex items-center justify-between">
        <h2 id="titulo-fotos-anuncio" className="text-[11px] font-bold md:text-sm lg:text-base">
          Fotos do anúncio
        </h2>
        <span className="text-[9px] text-slate-400 md:text-xs">
          {ad.fotos.length}/{MAX_AD_PHOTOS}
        </span>
      </div>

      <div className="mt-2 grid grid-cols-4 gap-1.5 md:gap-2 lg:mt-3 lg:grid-cols-6">
        {ad.fotos.map((photoId, index) => (
          <div
            key={photoId}
            className={`relative aspect-square overflow-hidden rounded-lg border border-slate-200 bg-slate-100 ${
              busyId === photoId ? "opacity-40" : ""
            }`}
          >
            {/* eslint-disable-next-line @next/next/no-img-element -- imagem servida pela rota /api do app */}
            <img
              src={adPhotoUrl(ad.id, photoId)}
              alt={`Foto ${index + 1}`}
              loading="lazy"
              className="h-full w-full object-cover"
            />
            {index === 0 && (
              <span className="absolute bottom-1 left-1 rounded bg-slate-900/70 px-1 py-0.5 text-[8px] font-medium text-white">
                Capa
              </span>
            )}
            <button
              type="button"
              onClick={() => remove(photoId, index)}
              disabled={pending}
              aria-label={`Remover foto ${index + 1}`}
              className="absolute right-1 top-1 flex h-6 w-6 items-center justify-center rounded-full bg-white/95 text-red-600 shadow transition hover:bg-white disabled:opacity-50"
            >
              <Trash2 size={12} />
            </button>
          </div>
        ))}

        {room > 0 && (
          <button
            type="button"
            onClick={() => input.current?.click()}
            disabled={pending}
            className="flex aspect-square flex-col items-center justify-center gap-1 rounded-lg border border-dashed border-blue-300 bg-blue-50/50 text-blue-600 transition hover:bg-blue-50 disabled:opacity-60"
          >
            <ImagePlus size={16} />
            <span className="text-[9px] font-medium md:text-xs">
              {pending && !busyId ? "Enviando..." : "Adicionar"}
            </span>
          </button>
        )}
      </div>

      {ad.fotos.length === 0 && (
        <p className="mt-1.5 text-[9px] text-slate-500 md:text-xs">
          Anúncios com fotos chamam mais atenção. A primeira foto vira a capa do anúncio.
        </p>
      )}

      {error && (
        <p role="alert" className="mt-1.5 text-[9px] text-red-600 md:text-xs">
          {error}
        </p>
      )}

      <input
        ref={input}
        type="file"
        accept="image/*"
        multiple
        className="hidden"
        onChange={(event) => {
          add(event.target.files);
          event.target.value = "";
        }}
      />
    </section>
  );
}
