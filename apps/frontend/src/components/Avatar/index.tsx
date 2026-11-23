"use client";

import { useState } from "react";
import { avatarUrl } from "@/lib/avatar";
import { initials } from "@/lib/marketplace";

type AvatarProps = {
  /** Dono da foto. Sem id, mostra só as iniciais. */
  userId?: string | null;
  /** Nome para as iniciais (quando não há foto) e para o texto alternativo. */
  name: string;
  /**
   * `fotoAtualizadaEm` do perfil, quando se sabe: `null` = sem foto (nem busca), texto = versão da foto.
   * Omitido (foto de outra pessoa): tenta carregar e cai nas iniciais se não houver.
   */
  version?: string | null;
  /** Tamanho e tipografia, ex.: "h-12 w-12 text-sm". */
  className?: string;
  /** Mostra o nome para leitores de tela (quando não há texto ao lado). */
  labelled?: boolean;
};

/** Foto de perfil redonda; sem foto (ou se ela falhar), as iniciais do nome. */
export function Avatar({ userId, name, version, className = "h-10 w-10 text-xs", labelled = false }: AvatarProps) {
  const src = userId && version !== null ? avatarUrl(userId, version) : null;
  // Guarda qual endereço falhou: trocar de foto (nova versão) tenta de novo sem precisar de efeito.
  const [failedSrc, setFailedSrc] = useState<string | null>(null);
  const showPhoto = src !== null && failedSrc !== src;

  return (
    <span
      className={`relative flex shrink-0 items-center justify-center overflow-hidden rounded-full bg-blue-100 font-bold text-blue-600 ${className}`}
      role={labelled ? "img" : undefined}
      aria-label={labelled ? `Foto de ${name}` : undefined}
      aria-hidden={labelled ? undefined : true}
    >
      {showPhoto ? (
        // eslint-disable-next-line @next/next/no-img-element -- imagem autenticada servida pela rota /api do app
        <img
          src={src}
          alt=""
          loading="lazy"
          decoding="async"
          onError={() => setFailedSrc(src)}
          className="h-full w-full object-cover"
        />
      ) : (
        initials(name)
      )}
    </span>
  );
}
