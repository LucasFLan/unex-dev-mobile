"use client";

import { useActionState, useMemo, useState } from "react";
import { Check, X } from "lucide-react";
import {
  approveValidation,
  rejectValidation,
  type ReviewState,
} from "@/app/admin/validacoes/actions";

/** Botões de decisão do admin: aprovar direto ou reprovar com motivo. */
export function ValidationReview({ envioId }: { envioId: string }) {
  const approve = useMemo(() => approveValidation.bind(null, envioId), [envioId]);
  const reject = useMemo(() => rejectValidation.bind(null, envioId), [envioId]);
  const [approveState, approveAction, approving] = useActionState<ReviewState, FormData>(
    async () => approve(),
    null
  );
  const [rejectState, rejectAction, rejecting] = useActionState<ReviewState, FormData>(reject, null);
  const [rejectOpen, setRejectOpen] = useState(false);
  const busy = approving || rejecting;
  const error = approveState?.error ?? rejectState?.error;

  return (
    <div className="space-y-2">
      {error && (
        <p role="alert" className="rounded-lg bg-red-50 px-3 py-2 text-[11px] text-red-600">
          {error}
        </p>
      )}

      {rejectOpen ? (
        <form action={rejectAction} className="space-y-2 rounded-xl border border-red-200 bg-white p-3">
          <label htmlFor="motivo" className="text-xs font-semibold text-slate-800">
            Motivo da reprovação
          </label>
          <textarea
            id="motivo"
            name="motivo"
            required
            minLength={5}
            maxLength={500}
            rows={3}
            placeholder="Ex.: A foto do documento está desfocada. Envie de novo com boa iluminação."
            className="w-full resize-none rounded-lg border border-slate-200 p-2.5 text-xs outline-none focus:border-red-400"
          />
          <p className="text-[10px] text-slate-500">O usuário vai ver essa mensagem e poderá reenviar.</p>
          <div className="grid grid-cols-2 gap-2">
            <button
              type="button"
              onClick={() => setRejectOpen(false)}
              disabled={busy}
              className="h-10 rounded-lg border border-slate-200 text-xs font-medium text-slate-700"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={busy}
              className="h-10 rounded-lg bg-red-600 text-xs font-semibold text-white disabled:opacity-60"
            >
              {rejecting ? "Reprovando..." : "Confirmar reprovação"}
            </button>
          </div>
        </form>
      ) : (
        <div className="grid grid-cols-2 gap-2">
          <button
            type="button"
            onClick={() => setRejectOpen(true)}
            disabled={busy}
            className="flex h-11 items-center justify-center gap-1.5 rounded-lg border border-red-200 text-xs font-semibold text-red-600 disabled:opacity-60"
          >
            <X size={14} aria-hidden="true" />
            Reprovar
          </button>
          <form action={approveAction}>
            <button
              type="submit"
              disabled={busy}
              className="flex h-11 w-full items-center justify-center gap-1.5 rounded-lg bg-emerald-600 text-xs font-semibold text-white disabled:opacity-60"
            >
              <Check size={14} aria-hidden="true" />
              {approving ? "Aprovando..." : "Aprovar"}
            </button>
          </form>
        </div>
      )}
    </div>
  );
}
