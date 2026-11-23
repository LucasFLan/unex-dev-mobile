"use client";

import { useRef, useState, useTransition, type ChangeEvent } from "react";
import { Camera, ImageIcon, Loader2, Trash2 } from "lucide-react";
import { removeProfilePhoto, updateProfilePhoto, type PhotoResult } from "@/app/perfil/editar/photo-actions";
import { Avatar } from "@/components/Avatar";
import { CameraCapture, cameraSupported } from "@/components/CameraCapture";
import { resizeImageForUpload } from "@/lib/image-resize";

/** Lado da foto de perfil enviada: nítida no tamanho em que aparece, leve para o celular. */
const PROFILE_SIDE = 640;

/**
 * Foto de perfil com as ações de trocar (câmera ou galeria) e remover. A foto é enviada na hora, sem
 * depender do "Salvar alterações" do formulário.
 */
export function ProfilePhotoPicker({
  userId,
  name,
  initialVersion,
}: {
  userId: string;
  name: string;
  initialVersion: string | null;
}) {
  const [version, setVersion] = useState(initialVersion);
  const [menuOpen, setMenuOpen] = useState(false);
  const [cameraOpen, setCameraOpen] = useState(false);
  const [message, setMessage] = useState<{ kind: "ok" | "error"; text: string } | null>(null);
  const [pending, startTransition] = useTransition();
  const galleryRef = useRef<HTMLInputElement>(null);

  function apply(result: PhotoResult, okText: string) {
    if ("error" in result) {
      setMessage({ kind: "error", text: result.error });
    } else {
      setVersion(result.version);
      setMessage({ kind: "ok", text: okText });
    }
  }

  function upload(original: File) {
    setMenuOpen(false);
    setMessage(null);
    startTransition(async () => {
      let file: File;
      try {
        file = await resizeImageForUpload(original, "perfil", { maxSide: PROFILE_SIDE, square: true });
      } catch {
        setMessage({ kind: "error", text: "Não foi possível abrir esta imagem. Tente outra foto." });
        return;
      }
      const form = new FormData();
      form.set("foto", file);
      apply(await updateProfilePhoto(form), "Foto atualizada.");
    });
  }

  function onGallery(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    event.target.value = ""; // Escolher a mesma foto de novo também dispara o change.
    if (file) upload(file);
  }

  function remove() {
    setMenuOpen(false);
    setMessage(null);
    startTransition(async () => apply(await removeProfilePhoto(), "Foto removida."));
  }

  return (
    <div className="relative">
      <button
        type="button"
        onClick={() => setMenuOpen((open) => !open)}
        disabled={pending}
        aria-haspopup="menu"
        aria-expanded={menuOpen}
        aria-label={version ? "Trocar foto de perfil" : "Adicionar foto de perfil"}
        className="relative block rounded-full focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
      >
        <Avatar userId={userId} name={name} version={version} className="h-16 w-16 text-lg" />
        <span className="absolute -bottom-0.5 -right-0.5 flex h-6 w-6 items-center justify-center rounded-full bg-blue-600 text-white ring-2 ring-white">
          {pending ? (
            <Loader2 size={12} className="animate-spin" aria-hidden="true" />
          ) : (
            <Camera size={12} aria-hidden="true" />
          )}
        </span>
      </button>

      {menuOpen && (
        <div
          role="menu"
          className="absolute left-0 top-full z-20 mt-2 w-52 overflow-hidden rounded-xl border border-slate-200 bg-white py-1 shadow-lg"
        >
          {cameraSupported() && (
            <button
              type="button"
              role="menuitem"
              onClick={() => {
                setMenuOpen(false);
                setCameraOpen(true);
              }}
              className="flex w-full items-center gap-2.5 px-3 py-2.5 text-left text-sm text-slate-700 hover:bg-slate-50"
            >
              <Camera size={16} aria-hidden="true" />
              Tirar foto
            </button>
          )}
          <button
            type="button"
            role="menuitem"
            onClick={() => galleryRef.current?.click()}
            className="flex w-full items-center gap-2.5 px-3 py-2.5 text-left text-sm text-slate-700 hover:bg-slate-50"
          >
            <ImageIcon size={16} aria-hidden="true" />
            Escolher da galeria
          </button>
          {version && (
            <button
              type="button"
              role="menuitem"
              onClick={remove}
              className="flex w-full items-center gap-2.5 px-3 py-2.5 text-left text-sm text-red-600 hover:bg-red-50"
            >
              <Trash2 size={16} aria-hidden="true" />
              Remover foto
            </button>
          )}
        </div>
      )}

      <input
        ref={galleryRef}
        type="file"
        accept="image/*"
        onChange={onGallery}
        className="hidden"
        aria-hidden="true"
        tabIndex={-1}
      />

      {message && (
        <p
          role={message.kind === "error" ? "alert" : "status"}
          className={`absolute left-0 top-full mt-2 w-60 text-xs ${
            message.kind === "error" ? "text-red-600" : "text-emerald-600"
          } ${menuOpen ? "hidden" : ""}`}
        >
          {message.text}
        </p>
      )}

      {cameraOpen && (
        <CameraCapture
          kind="perfil"
          title="Foto de perfil"
          onCapture={(file) => {
            setCameraOpen(false);
            upload(file);
          }}
          onClose={(error) => {
            setCameraOpen(false);
            if (error) setMessage({ kind: "error", text: error });
          }}
        />
      )}
    </div>
  );
}
