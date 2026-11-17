"use client";

import { useState, type FormEvent } from "react";

type FiltersPanelProps = {
  id: string;
  maxPrice: number | null;
  onApply: (maxPrice: number | null) => void;
  onClose: () => void;
};

export function FiltersPanel({
  id,
  maxPrice,
  onApply,
  onClose,
}: FiltersPanelProps) {
  const [draft, setDraft] = useState(maxPrice === null ? "" : String(maxPrice));

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault();

    const value = draft.trim() === "" ? null : Number(draft);
    onApply(value !== null && Number.isFinite(value) && value >= 0 ? value : null);
    onClose();
  };

  const handleClear = () => {
    setDraft("");
    onApply(null);
    onClose();
  };

  return (
    <form
      id={id}
      onSubmit={handleSubmit}
      className="absolute inset-x-0 top-full z-30 mt-2 rounded-xl border border-slate-200 bg-white p-4 shadow-lg"
    >
      <label htmlFor={`${id}-preco`} className="text-xs font-semibold">
        Preço máximo (R$)
      </label>

      <input
        id={`${id}-preco`}
        type="number"
        inputMode="numeric"
        min="0"
        value={draft}
        onChange={(event) => setDraft(event.target.value)}
        placeholder="Sem limite"
        className="mt-2 w-full rounded-lg border border-slate-200 p-2 text-sm outline-none focus:border-blue-500"
      />

      <div className="mt-3 flex gap-2">
        <button
          type="button"
          onClick={handleClear}
          className="h-9 flex-1 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700"
        >
          Limpar
        </button>
        <button
          type="submit"
          className="h-9 flex-1 rounded-lg bg-blue-600 text-xs font-semibold text-white"
        >
          Aplicar filtros
        </button>
      </div>
    </form>
  );
}