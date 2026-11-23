"use client";

import { useActionState, useEffect, useMemo, useRef, useState, type ChangeEvent } from "react";
import { Camera, CheckCircle2, ImagePlus, Loader2, X } from "lucide-react";
import { sendProposal, type ProposalFormState } from "@/app/propostas/actions";
import { CameraCapture, cameraSupported } from "@/components/CameraCapture";
import { resizeImageForUpload } from "@/lib/image-resize";
import { MAX_PROPOSAL_PHOTOS } from "@/lib/proposal-limits";

const MAX_MESSAGE = 1000;

type ProposalSheetProps = {
  adId: string;
  /** Título do modal, ex.: "Enviar proposta" ou "Solicitar orçamento". */
  title: string;
  /** Texto de apoio abaixo do título. */
  description: string;
  valueLabel: string;
  messagePlaceholder: string;
  onClose: () => void;
};

type Attachment = { id: string; file: File; preview: string };

/** Modal que envia POST /anuncios/{id}/propostas, com até 5 fotos anexadas. */
export function ProposalSheet({
  adId,
  title,
  description,
  valueLabel,
  messagePlaceholder,
  onClose,
}: ProposalSheetProps) {
  const [photos, setPhotos] = useState<Attachment[]>([]);
  const [processing, setProcessing] = useState(0);
  const [photoError, setPhotoError] = useState<string | null>(null);
  const [cameraOpen, setCameraOpen] = useState(false);
  const galleryRef = useRef<HTMLInputElement>(null);

  // As fotos vão já reduzidas no aparelho; o input escondido não entra no envio.
  const action = useMemo(() => {
    const send = sendProposal.bind(null, adId);
    return (previous: ProposalFormState, formData: FormData) => {
      formData.delete("fotos");
      photos.forEach((photo) => formData.append("fotos", photo.file));
      return send(previous, formData);
    };
  }, [adId, photos]);
  const [state, formAction, pending] = useActionState<ProposalFormState, FormData>(action, null);
  const [message, setMessage] = useState("");

  const sent = state !== null && "success" in state;
  const remaining = MAX_PROPOSAL_PHOTOS - photos.length - processing;

  // Libera as prévias quando o modal fecha.
  const photosRef = useRef(photos);
  useEffect(() => {
    photosRef.current = photos;
  }, [photos]);
  useEffect(() => () => photosRef.current.forEach((photo) => URL.revokeObjectURL(photo.preview)), []);

  async function addFiles(files: File[]) {
    setPhotoError(null);
    const accepted = files.slice(0, Math.max(0, remaining));
    if (files.length > accepted.length) {
      setPhotoError(`Você pode anexar até ${MAX_PROPOSAL_PHOTOS} fotos.`);
    }
    if (accepted.length === 0) return;

    setProcessing((count) => count + accepted.length);
    for (const original of accepted) {
      try {
        const file = await resizeImageForUpload(original, `foto-${Date.now()}`);
        setPhotos((current) => [
          ...current,
          { id: crypto.randomUUID(), file, preview: URL.createObjectURL(file) },
        ]);
      } catch {
        setPhotoError("Não foi possível abrir uma das imagens. Tente outra foto.");
      } finally {
        setProcessing((count) => count - 1);
      }
    }
  }

  function onGallery(event: ChangeEvent<HTMLInputElement>) {
    const files = Array.from(event.target.files ?? []);
    event.target.value = "";
    void addFiles(files);
  }

  function removePhoto(id: string) {
    setPhotos((current) => {
      const photo = current.find((item) => item.id === id);
      if (photo) URL.revokeObjectURL(photo.preview);
      return current.filter((item) => item.id !== id);
    });
    setPhotoError(null);
  }

  return (
    <div
      className="fixed inset-0 z-30 flex items-end justify-center bg-black/40"
      role="dialog"
      aria-modal="true"
      aria-labelledby="titulo-proposta"
    >
      <div className="max-h-[92dvh] w-full max-w-md overflow-y-auto rounded-t-2xl bg-white p-5 pb-[max(20px,env(safe-area-inset-bottom))]">
        <div className="mb-4 flex items-center justify-between">
          <h3 id="titulo-proposta" className="text-sm font-bold">
            {title}
          </h3>

          <button
            type="button"
            onClick={onClose}
            className="-mr-2 flex h-9 w-9 items-center justify-center text-slate-500"
            aria-label="Fechar"
          >
            <X size={16} aria-hidden="true" />
          </button>
        </div>

        {sent ? (
          <div className="py-4 text-center" role="status">
            <CheckCircle2 size={36} className="mx-auto text-emerald-500" aria-hidden="true" />
            <p className="mt-2 text-sm font-semibold">Proposta enviada!</p>
            <p className="mt-1 text-xs text-slate-500">
              O anunciante foi notificado e vai entrar em contato.
            </p>
            <button
              type="button"
              onClick={onClose}
              className="mt-4 h-10 w-full rounded-lg bg-blue-600 text-xs font-semibold text-white"
            >
              Fechar
            </button>
          </div>
        ) : (
          <form action={formAction}>
            <fieldset disabled={pending} className="min-w-0">
              <p className="text-xs text-slate-500">{description}</p>

              {state && "error" in state && (
                <p role="alert" className="mt-3 rounded-lg bg-red-50 px-3 py-2 text-[11px] text-red-600">
                  {state.error}
                </p>
              )}

              <label htmlFor="valor-proposta" className="mt-4 block text-xs font-medium">
                {valueLabel} <span className="font-normal text-slate-400">(opcional)</span>
              </label>

              <div className="mt-2 flex h-11 items-center gap-2 rounded-lg border border-slate-200 px-3 focus-within:border-blue-500">
                <span className="text-sm font-semibold text-slate-600">R$</span>
                <input
                  id="valor-proposta"
                  name="valor"
                  type="number"
                  inputMode="decimal"
                  min="0.01"
                  step="0.01"
                  placeholder="0,00 (deixe vazio para combinar)"
                  className="min-w-0 flex-1 text-sm outline-none"
                />
              </div>

              <div className="mt-4 flex items-center justify-between">
                <label htmlFor="mensagem-proposta" className="text-xs font-medium">
                  Mensagem para o anunciante
                </label>
                <span className="text-[10px] text-slate-400">
                  {message.length}/{MAX_MESSAGE}
                </span>
              </div>

              <textarea
                id="mensagem-proposta"
                name="mensagem"
                required
                maxLength={MAX_MESSAGE}
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder={messagePlaceholder}
                rows={4}
                className="mt-2 w-full resize-none rounded-lg border border-slate-200 p-3 text-xs outline-none focus:border-blue-500"
              />

              <div className="mt-3 flex items-center justify-between">
                <p className="text-xs font-medium">
                  Fotos <span className="font-normal text-slate-400">(opcional, até {MAX_PROPOSAL_PHOTOS})</span>
                </p>
                <span className="text-[10px] text-slate-400">
                  {photos.length}/{MAX_PROPOSAL_PHOTOS}
                </span>
              </div>

              <ul className="mt-2 flex flex-wrap gap-2" aria-label="Fotos anexadas">
                {photos.map((photo, index) => (
                  <li key={photo.id} className="relative h-16 w-16">
                    {/* eslint-disable-next-line @next/next/no-img-element -- prévia local (blob:) */}
                    <img
                      src={photo.preview}
                      alt={`Foto ${index + 1}`}
                      className="h-full w-full rounded-lg border border-slate-200 object-cover"
                    />
                    <button
                      type="button"
                      onClick={() => removePhoto(photo.id)}
                      aria-label={`Remover foto ${index + 1}`}
                      className="absolute -right-1.5 -top-1.5 flex h-6 w-6 items-center justify-center rounded-full bg-slate-800 text-white ring-2 ring-white"
                    >
                      <X size={12} aria-hidden="true" />
                    </button>
                  </li>
                ))}

                {Array.from({ length: processing }, (_, index) => (
                  <li
                    key={`processando-${index}`}
                    className="flex h-16 w-16 items-center justify-center rounded-lg border border-dashed border-slate-300"
                  >
                    <Loader2 size={18} className="animate-spin text-slate-400" aria-label="Preparando foto" />
                  </li>
                ))}

                {remaining > 0 && (
                  <>
                    {cameraSupported() && (
                      <li>
                        <button
                          type="button"
                          onClick={() => setCameraOpen(true)}
                          className="flex h-16 w-16 flex-col items-center justify-center gap-1 rounded-lg border border-dashed border-slate-300 text-[10px] text-slate-500 hover:border-blue-400 hover:text-blue-600"
                        >
                          <Camera size={18} aria-hidden="true" />
                          Câmera
                        </button>
                      </li>
                    )}
                    <li>
                      <button
                        type="button"
                        onClick={() => galleryRef.current?.click()}
                        className="flex h-16 w-16 flex-col items-center justify-center gap-1 rounded-lg border border-dashed border-slate-300 text-[10px] text-slate-500 hover:border-blue-400 hover:text-blue-600"
                      >
                        <ImagePlus size={18} aria-hidden="true" />
                        Galeria
                      </button>
                    </li>
                  </>
                )}
              </ul>

              <input
                ref={galleryRef}
                type="file"
                accept="image/*"
                multiple
                onChange={onGallery}
                className="hidden"
                aria-hidden="true"
                tabIndex={-1}
              />

              {photoError && (
                <p role="alert" className="mt-2 text-[11px] text-red-600">
                  {photoError}
                </p>
              )}

              <button
                type="submit"
                disabled={processing > 0}
                className="mt-4 h-11 w-full rounded-lg bg-blue-600 text-xs font-semibold text-white transition hover:bg-blue-700 disabled:opacity-60"
              >
                {pending ? "Enviando..." : processing > 0 ? "Preparando fotos..." : "Enviar proposta"}
              </button>
            </fieldset>
          </form>
        )}
      </div>

      {cameraOpen && (
        <CameraCapture
          kind="livre"
          title="Foto para a proposta"
          onCapture={(file) => {
            setCameraOpen(false);
            void addFiles([file]);
          }}
          onClose={(error) => {
            setCameraOpen(false);
            if (error) setPhotoError(error);
          }}
        />
      )}
    </div>
  );
}
