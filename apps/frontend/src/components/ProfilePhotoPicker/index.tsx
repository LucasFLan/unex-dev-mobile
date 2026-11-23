"use client";

import { useRef, useState, useTransition } from "react";
import { Camera, Trash2 } from "lucide-react";
import {
  removeProfilePhoto,
  uploadProfilePhoto,
  type ProfilePhotoResult,
} from "@/app/perfil/editar/actions";
import { Avatar } from "@/components/Avatar";
import { resizeImageForUpload } from "@/lib/image-resize";

/** Foto de perfil com os botões de trocar e remover. A foto é reduzida no aparelho antes do envio. */
export function ProfilePhotoPicker({ name, photo }: { name: string; photo: string | null }) {
  const input = useRef<HTMLInputElement>(null);
  const [current, setCurrent] = useState(photo);
  const [error, setError] = useState<string | null>(null);
  const [pending, startTransition] = useTransition();

  function apply(result: ProfilePhotoResult) {
    if (result.ok) {
      setCurrent(result.photo);
      setError(null);
    } else {
      setError(result.error);
    }
  }

  function choose(file: File | undefined) {
    if (!file) return;
    startTransition(async () => {
      let resized: File;
      try {
        resized = await resizeImageForUpload(file, "foto-perfil");
      } catch {
        setError("Não foi possível abrir essa imagem. Escolha uma foto em JPG ou PNG.");
        return;
      }
      const form = new FormData();
      form.append("foto", resized);
      apply(await uploadProfilePhoto(form));
    });
  }

  return (
    <div className="flex flex-col items-center gap-3 sm:flex-row sm:items-center sm:gap-4">
      <div className="relative">
        <Avatar
          name={name}
          photo={current}
          className={`h-20 w-20 bg-blue-100 text-2xl font-bold text-blue-600 ${pending ? "opacity-50" : ""}`}
        />
        <button
          type="button"
          onClick={() => input.current?.click()}
          disabled={pending}
          aria-label={current ? "Trocar foto de perfil" : "Adicionar foto de perfil"}
          className="absolute -bottom-1 -right-1 flex h-8 w-8 items-center justify-center rounded-full border-2 border-white bg-blue-600 text-white shadow transition hover:bg-blue-700"
        >
          <Camera size={15} aria-hidden="true" />
        </button>
      </div>

      <div className="flex flex-col items-center gap-1 sm:items-start">
        <div className="flex gap-2">
          <button
            type="button"
            onClick={() => input.current?.click()}
            disabled={pending}
            className="rounded-lg border border-slate-200 px-3 py-1.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
          >
            {pending ? "Enviando..." : current ? "Trocar foto" : "Adicionar foto"}
          </button>

          {current && (
            <button
              type="button"
              onClick={() => startTransition(async () => apply(await removeProfilePhoto()))}
              disabled={pending}
              className="flex items-center gap-1 rounded-lg px-3 py-1.5 text-sm font-semibold text-red-600 transition hover:bg-red-50"
            >
              <Trash2 size={14} aria-hidden="true" />
              Remover
            </button>
          )}
        </div>

        <p className="text-xs text-slate-500">JPG, PNG ou WEBP.</p>

        {error && (
          <p role="alert" className="text-xs text-red-600">
            {error}
          </p>
        )}
      </div>

      <input
        ref={input}
        type="file"
        accept="image/*"
        className="hidden"
        onChange={(event) => {
          choose(event.target.files?.[0]);
          event.target.value = "";
        }}
      />
    </div>
  );
}
