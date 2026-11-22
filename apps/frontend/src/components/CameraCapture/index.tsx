"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { Check, Loader2, RefreshCw, RotateCcw, X } from "lucide-react";

type Facing = "user" | "environment";

/** Câmera ao vivo no navegador (precisa de HTTPS ou localhost). */
export function cameraSupported() {
  return typeof navigator !== "undefined" && !!navigator.mediaDevices?.getUserMedia;
}

function cameraError(error: unknown): string {
  const name = error instanceof DOMException ? error.name : "";
  if (name === "NotAllowedError" || name === "SecurityError") {
    return "Permita o acesso à câmera nas configurações do navegador para tirar a foto por aqui. Ou escolha uma foto da galeria.";
  }
  if (name === "NotFoundError" || name === "OverconstrainedError") {
    return "Não encontramos uma câmera neste aparelho. Escolha uma foto da galeria.";
  }
  if (name === "NotReadableError") {
    return "A câmera está sendo usada por outro app. Feche-o e tente de novo.";
  }
  return "Não foi possível abrir a câmera. Escolha uma foto da galeria.";
}

/**
 * Tela cheia com a câmera do aparelho e uma moldura-guia. Entrega a foto como JPEG pelo onCapture.
 * Documento abre a câmera traseira; selfie, a frontal (a prévia fica espelhada, a foto não).
 */
export function CameraCapture({
  kind,
  title,
  onCapture,
  onClose,
}: {
  kind: "documento" | "selfie";
  title: string;
  onCapture: (file: File) => void;
  onClose: (error?: string) => void;
}) {
  const videoRef = useRef<HTMLVideoElement>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const [facing, setFacing] = useState<Facing>(kind === "selfie" ? "user" : "environment");
  const [ready, setReady] = useState(false);
  const [canFlip, setCanFlip] = useState(false);
  const [shot, setShot] = useState<{ file: File; url: string } | null>(null);
  // Guarda o onClose mais recente sem reabrir a câmera a cada renderização do formulário.
  const onCloseRef = useRef(onClose);
  useEffect(() => {
    onCloseRef.current = onClose;
  }, [onClose]);

  const stop = useCallback(() => {
    streamRef.current?.getTracks().forEach((track) => track.stop());
    streamRef.current = null;
  }, []);

  // Abre (ou troca) a câmera; desliga ao sair da tela.
  useEffect(() => {
    let cancelled = false;
    navigator.mediaDevices
      .getUserMedia({
        video: { facingMode: { ideal: facing }, width: { ideal: 1920 }, height: { ideal: 1080 } },
        audio: false,
      })
      .then(async (stream) => {
        if (cancelled) {
          stream.getTracks().forEach((track) => track.stop());
          return;
        }
        stop();
        streamRef.current = stream;
        const video = videoRef.current;
        if (video) {
          video.srcObject = stream;
          await video.play().catch(() => {});
        }
        const devices = await navigator.mediaDevices.enumerateDevices().catch(() => []);
        if (!cancelled) {
          setCanFlip(devices.filter((device) => device.kind === "videoinput").length > 1);
        }
      })
      .catch((error) => {
        if (!cancelled) onCloseRef.current(cameraError(error));
      });
    return () => {
      cancelled = true;
      stop();
    };
  }, [facing, stop]);

  // Libera a prévia da foto tirada quando ela muda ou a tela fecha.
  useEffect(() => {
    return () => {
      if (shot) URL.revokeObjectURL(shot.url);
    };
  }, [shot]);

  // Trava a rolagem da página por trás enquanto a câmera está aberta.
  useEffect(() => {
    const previous = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = previous;
    };
  }, []);

  function capture() {
    const video = videoRef.current;
    if (!video || !video.videoWidth) return;
    const canvas = document.createElement("canvas");
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    canvas.getContext("2d")?.drawImage(video, 0, 0);
    canvas.toBlob(
      (blob) => {
        if (!blob) return;
        const file = new File([blob], `${kind}.jpg`, { type: "image/jpeg" });
        setShot({ file, url: URL.createObjectURL(blob) });
      },
      "image/jpeg",
      0.92
    );
  }

  const mirrored = facing === "user";

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-label={title}
      className="fixed inset-0 z-50 flex flex-col bg-black text-white"
    >
      <header className="flex items-center justify-between px-3 pb-2 pt-[max(12px,env(safe-area-inset-top))]">
        <button
          type="button"
          onClick={() => onClose()}
          aria-label="Fechar câmera"
          className="flex h-10 w-10 items-center justify-center rounded-full bg-white/10"
        >
          <X size={20} aria-hidden="true" />
        </button>
        <p className="text-sm font-semibold">{title}</p>
        <span className="h-10 w-10" />
      </header>

      <div className="relative flex-1 overflow-hidden">
        <video
          ref={videoRef}
          playsInline
          muted
          onLoadedData={() => setReady(true)}
          className={`absolute inset-0 h-full w-full object-cover ${mirrored ? "-scale-x-100" : ""} ${shot ? "invisible" : ""}`}
        />
        {shot && (
          // eslint-disable-next-line @next/next/no-img-element -- foto local (blob:), sem otimização
          <img src={shot.url} alt="Foto tirada" className="absolute inset-0 h-full w-full object-contain" />
        )}

        {!shot && (
          <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center gap-4 px-6">
            {kind === "documento" ? (
              <div className="aspect-[1.58] w-full max-w-sm rounded-xl border-2 border-white/90 shadow-[0_0_0_9999px_rgba(0,0,0,0.45)]" />
            ) : (
              <div className="flex flex-col items-center gap-3">
                <div className="h-56 w-44 rounded-[50%] border-2 border-white/90" />
                <div className="aspect-[1.58] w-40 rounded-lg border-2 border-dashed border-white/80" />
              </div>
            )}
            <p className="rounded-full bg-black/50 px-3 py-1.5 text-center text-xs">
              {kind === "documento"
                ? "Encaixe o documento na moldura, sem reflexo"
                : "Rosto no oval e o documento logo abaixo"}
            </p>
          </div>
        )}

        {!ready && !shot && (
          <div className="absolute inset-0 flex items-center justify-center">
            <Loader2 size={28} className="animate-spin" aria-label="Abrindo a câmera" />
          </div>
        )}
      </div>

      <footer className="flex items-center justify-around px-6 pb-[max(20px,env(safe-area-inset-bottom))] pt-4">
        {shot ? (
          <>
            <button
              type="button"
              onClick={() => setShot(null)}
              className="flex h-12 items-center gap-2 rounded-full bg-white/10 px-5 text-sm font-semibold"
            >
              <RotateCcw size={18} aria-hidden="true" />
              Tirar outra
            </button>
            <button
              type="button"
              onClick={() => {
                stop();
                onCapture(shot.file);
              }}
              className="flex h-12 items-center gap-2 rounded-full bg-blue-600 px-5 text-sm font-semibold"
            >
              <Check size={18} aria-hidden="true" />
              Usar foto
            </button>
          </>
        ) : (
          <>
            <span className="h-12 w-12" />
            <button
              type="button"
              onClick={capture}
              disabled={!ready}
              aria-label="Tirar foto"
              className="h-18 w-18 rounded-full border-4 border-white bg-white/25 transition active:scale-95 disabled:opacity-40"
            />
            {canFlip ? (
              <button
                type="button"
                onClick={() => {
                  setReady(false);
                  setFacing((current) => (current === "user" ? "environment" : "user"));
                }}
                aria-label="Trocar de câmera"
                className="flex h-12 w-12 items-center justify-center rounded-full bg-white/10"
              >
                <RefreshCw size={20} aria-hidden="true" />
              </button>
            ) : (
              <span className="h-12 w-12" />
            )}
          </>
        )}
      </footer>
    </div>
  );
}
