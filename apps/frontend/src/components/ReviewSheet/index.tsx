"use client";

import { useState, useTransition } from "react";
import { Star, X } from "lucide-react";
import { rateProposal } from "@/app/propostas/actions";

const MAX_COMMENT = 500;
const LABELS = ["", "Ruim", "Regular", "Bom", "Muito bom", "Excelente"];

/** Modal de avaliação do serviço concluído: estrelas (obrigatório) e comentário opcional. */
export function ReviewSheet({
  proposalId,
  professionalName,
  onDone,
  onClose,
}: {
  proposalId: string;
  professionalName: string;
  onDone: () => void;
  onClose: () => void;
}) {
  const [rating, setRating] = useState(0);
  const [hover, setHover] = useState(0);
  const [comment, setComment] = useState("");
  const [error, setError] = useState("");
  const [pending, startTransition] = useTransition();
  const shown = hover || rating;

  function submit() {
    if (rating === 0) {
      setError("Escolha de 1 a 5 estrelas.");
      return;
    }
    setError("");
    startTransition(async () => {
      const result = await rateProposal(proposalId, rating, comment);
      if (result.ok) onDone();
      else setError(result.error);
    });
  }

  return (
    <div
      className="fixed inset-0 z-40 flex items-end justify-center bg-black/40"
      role="dialog"
      aria-modal="true"
      aria-labelledby="titulo-avaliacao"
    >
      <div className="w-full max-w-md rounded-t-2xl bg-white p-5 pb-[max(20px,env(safe-area-inset-bottom))]">
        <div className="mb-1 flex items-center justify-between">
          <h3 id="titulo-avaliacao" className="text-sm font-bold">
            Avaliar serviço
          </h3>
          <button
            type="button"
            onClick={onClose}
            aria-label="Fechar"
            className="-mr-2 flex h-9 w-9 items-center justify-center text-slate-500"
          >
            <X size={16} aria-hidden="true" />
          </button>
        </div>
        <p className="text-xs text-slate-500">Como foi o serviço de {professionalName}?</p>

        <fieldset disabled={pending} className="min-w-0">
          <legend className="sr-only">Nota de 1 a 5 estrelas</legend>
          <div className="mt-4 flex items-center justify-center gap-1" onMouseLeave={() => setHover(0)}>
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                type="button"
                onClick={() => setRating(star)}
                onMouseEnter={() => setHover(star)}
                aria-label={`${star} ${star === 1 ? "estrela" : "estrelas"}: ${LABELS[star]}`}
                aria-pressed={rating === star}
                className="flex h-11 w-11 items-center justify-center rounded-full focus-visible:outline-2 focus-visible:outline-blue-600"
              >
                <Star
                  size={30}
                  aria-hidden="true"
                  className={star <= shown ? "fill-amber-400 text-amber-400" : "fill-slate-100 text-slate-300"}
                />
              </button>
            ))}
          </div>
          <p className="mt-1 h-4 text-center text-xs font-medium text-amber-700" aria-live="polite">
            {LABELS[shown]}
          </p>

          <div className="mt-3 flex items-center justify-between">
            <label htmlFor="comentario-avaliacao" className="text-xs font-medium">
              Comentário <span className="font-normal text-slate-400">(opcional)</span>
            </label>
            <span className="text-[10px] text-slate-400">
              {comment.length}/{MAX_COMMENT}
            </span>
          </div>
          <textarea
            id="comentario-avaliacao"
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            maxLength={MAX_COMMENT}
            rows={3}
            placeholder="Conte como foi: pontualidade, capricho, preço..."
            className="mt-2 w-full resize-none rounded-lg border border-slate-200 p-3 text-xs outline-none focus:border-blue-500"
          />

          {error && (
            <p role="alert" className="mt-2 rounded-lg bg-red-50 px-3 py-2 text-[11px] text-red-600">
              {error}
            </p>
          )}

          <button
            type="button"
            onClick={submit}
            className="mt-3 h-11 w-full rounded-lg bg-blue-600 text-xs font-semibold text-white transition hover:bg-blue-700 disabled:opacity-60"
          >
            {pending ? "Enviando..." : "Enviar avaliação"}
          </button>
        </fieldset>
      </div>
    </div>
  );
}
