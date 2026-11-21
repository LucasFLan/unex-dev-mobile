"use client";

import { useActionState, useMemo, useState } from "react";
import { CheckCircle2, X } from "lucide-react";
import { sendProposal, type ProposalFormState } from "@/app/propostas/actions";

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

/** Modal que envia POST /anuncios/{id}/propostas. */
export function ProposalSheet({
  adId,
  title,
  description,
  valueLabel,
  messagePlaceholder,
  onClose,
}: ProposalSheetProps) {
  const action = useMemo(() => sendProposal.bind(null, adId), [adId]);
  const [state, formAction, pending] = useActionState<
    ProposalFormState,
    FormData
  >(action, null);
  const [message, setMessage] = useState("");

  const sent = state !== null && "success" in state;

  return (
    <div
      className="fixed inset-0 z-30 flex items-end justify-center bg-black/40"
      role="dialog"
      aria-modal="true"
      aria-labelledby="titulo-proposta"
    >
      <div className="w-full max-w-md rounded-t-2xl bg-white p-5 pb-[max(20px,env(safe-area-inset-bottom))]">
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
            <CheckCircle2
              size={36}
              className="mx-auto text-emerald-500"
              aria-hidden="true"
            />
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
                <p
                  role="alert"
                  className="mt-3 rounded-lg bg-red-50 px-3 py-2 text-[11px] text-red-600"
                >
                  {state.error}
                </p>
              )}

              <label htmlFor="valor-proposta" className="mt-4 block text-xs font-medium">
                {valueLabel}{" "}
                <span className="font-normal text-slate-400">(opcional)</span>
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

              <button
                type="submit"
                className="mt-3 h-11 w-full rounded-lg bg-blue-600 text-xs font-semibold text-white transition hover:bg-blue-700 disabled:opacity-60"
              >
                {pending ? "Enviando..." : "Enviar proposta"}
              </button>
            </fieldset>
          </form>
        )}
      </div>
    </div>
  );
}
