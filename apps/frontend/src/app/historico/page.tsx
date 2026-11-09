"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { Check, ChevronDown, Search, SlidersHorizontal, X } from "lucide-react";
import { BottomNav } from "@/components/BottomNav";
import { ServiceCard } from "@/components/ServiceCard";
import {
  SERVICE_STATUSES,
  SERVICE_TYPES,
  type Service,
  type ServiceStatus,
  type ServiceType,
} from "@/lib/services";
import {formatMonth, normalize} from "@/lib/format";
import {mockServices} from "@/lib/mocks"

type Filters = {
  type: ServiceType | null;
  status: ServiceStatus | null;
  mine: boolean;
};

const NO_FILTERS: Filters = { type: null, status: null, mine: false };

const chipBase =
  "flex h-8 shrink-0 items-center gap-1 rounded-full border px-2.5 text-[11px]";
const chipOn = "border-blue-500 bg-blue-50 text-blue-600";
const chipOff = "border-slate-200 bg-white text-slate-700";

export default function HistoricoPage() {
  const [services, setServices] = useState<Service[]>(mockServices);
  const [search, setSearch] = useState("");
  const [filters, setFilters] = useState<Filters>(NO_FILTERS);
  const [showFilters, setShowFilters] = useState(false);
  const [oldestFirst, setOldestFirst] = useState(false);
  const [toast, setToast] = useState("");

  const toastTimer = useRef<ReturnType<typeof setTimeout> | undefined>(
    undefined
  );
  const filtersRef = useRef<HTMLDivElement>(null);

  const activeFilterCount =
    Number(filters.type !== null) +
    Number(filters.status !== null) +
    Number(filters.mine);
  const hasActiveFilters = activeFilterCount > 0 || search.trim() !== "";


  const notify = (message: string) => {
    setToast(message);
    clearTimeout(toastTimer.current);
    toastTimer.current = setTimeout(() => setToast(""), 2500);
  };

  useEffect(() => () => clearTimeout(toastTimer.current), []);


  useEffect(() => {
    if (!showFilters) return;

    const onPointerDown = (event: PointerEvent) => {
      if (!filtersRef.current?.contains(event.target as Node)) {
        setShowFilters(false);
      }
    };
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") setShowFilters(false);
    };

    document.addEventListener("pointerdown", onPointerDown);
    document.addEventListener("keydown", onKeyDown);
    return () => {
      document.removeEventListener("pointerdown", onPointerDown);
      document.removeEventListener("keydown", onKeyDown);
    };
  }, [showFilters]);


  const toggleType = (type: ServiceType) =>
    setFilters((f) => ({
      ...f,
      type: f.type === type ? null : type,
      // "Meus anúncios" e "Contratei" são excludentes
      mine: type === "Contratei" ? false : f.mine,
    }));

  const toggleStatus = (status: ServiceStatus) =>
    setFilters((f) => ({ ...f, status: f.status === status ? null : status }));

  const toggleMine = () =>
    setFilters((f) => ({
      ...f,
      mine: !f.mine,
      type: !f.mine && f.type === "Contratei" ? null : f.type,
    }));

  const clearFilters = () => {
    setSearch("");
    setFilters(NO_FILTERS);
  };


  const filteredServices = useMemo(() => {
    const query = normalize(search.trim());
    const direction = oldestFirst ? 1 : -1;

    return services
      .filter((service) => {
        if (
          query &&
          !normalize(`${service.title} ${service.description}`).includes(query)
        ) {
          return false;
        }
        if (filters.type && service.type !== filters.type) return false;
        if (filters.status && service.status !== filters.status) return false;
        if (filters.mine && service.type === "Contratei") return false;
        return true;
      })
      .sort(
        (a, b) => direction * a.date.localeCompare(b.date) || a.id - b.id
      );
  }, [services, search, filters, oldestFirst]);

  const groups = useMemo(() => {
    const byMonth = new Map<string, Service[]>();
    for (const service of filteredServices) {
      const key = service.date.slice(0, 7);
      byMonth.set(key, [...(byMonth.get(key) ?? []), service]);
    }
    return [...byMonth.entries()];
  }, [filteredServices]);


  const togglePause = (id: number) => {
    const target = services.find((service) => service.id === id);
    if (!target) return;

    const pausing = target.status === "Ativo";

    setServices((current) =>
      current.map((service) =>
        service.id === id
          ? { ...service, status: pausing ? "Pausado" : "Ativo" }
          : service
      )
    );
    notify(pausing ? "Anúncio pausado." : "Anúncio retomado.");
  };

  const count = filteredServices.length;

  return (
    <main className="min-h-dvh bg-fundo pb-24 text-slate-800">
      <div className="mx-auto w-full max-w-120">
        <header className="sticky top-0 z-20 bg-fundo px-3.5 pb-2 pt-4">
          <h1 className="mb-2 text-[17px] font-bold tracking-tight">
            Histórico
          </h1>

          <div className="flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-white px-2.5">
            <Search
              size={14}
              className="text-slate-400"
              aria-hidden="true"
            />
            <input
              type="search"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Buscar no histórico"
              aria-label="Buscar no histórico"
              className="h-full w-full bg-transparent text-[11px] outline-none placeholder:text-slate-500 [&::-webkit-search-cancel-button]:hidden"
            />
            {search && (
              <button
                type="button"
                onClick={() => setSearch("")}
                aria-label="Limpar busca"
                className="-mr-2 flex h-9 w-9 shrink-0 items-center justify-center"
              >
                <X size={13} className="text-slate-500" aria-hidden="true" />
              </button>
            )}
          </div>

          <div ref={filtersRef} className="relative mt-2">
            <div className="flex gap-1.5">
              <button
                type="button"
                onClick={() => setShowFilters((open) => !open)}
                aria-expanded={showFilters}
                aria-controls="painel-filtros"
                className={`${chipBase} ${
                  showFilters || activeFilterCount > 0 ? chipOn : chipOff
                }`}
              >
                <SlidersHorizontal size={12} aria-hidden="true" />
                Filtros
                <span className="flex h-3.5 min-w-3.5 items-center justify-center rounded-full bg-blue-600 px-1 text-[9px] text-white">
                  {activeFilterCount}
                </span>
              </button>

              <button
                type="button"
                onClick={toggleMine}
                aria-pressed={filters.mine}
                className={`${chipBase} ${filters.mine ? chipOn : `${chipOff} text-blue-600`}`}
              >
                <Check size={11} aria-hidden="true" />
                Meus anúncios
              </button>

              <button
                type="button"
                onClick={() => toggleType("Contratei")}
                aria-pressed={filters.type === "Contratei"}
                className={`${chipBase} ${
                  filters.type === "Contratei"
                    ? chipOn
                    : `${chipOff} text-blue-600`
                }`}
              >
                <Check size={11} aria-hidden="true" />
                Contratei
              </button>
            </div>

            {showFilters && (
              <div
                id="painel-filtros"
                className="absolute inset-x-0 top-full z-30 mt-2 rounded-xl border border-slate-200 bg-white p-3 shadow-lg"
              >
                <div className="mb-2 flex items-center justify-between">
                  <span className="text-xs font-semibold">
                    Filtrar histórico
                  </span>
                  <button
                    type="button"
                    onClick={() => setShowFilters(false)}
                    aria-label="Fechar filtros"
                    className="-mr-2 flex h-9 w-9 items-center justify-center"
                  >
                    <X size={15} aria-hidden="true" />
                  </button>
                </div>

                <fieldset className="mb-2">
                  <legend className="mb-1.5 text-[11px] font-medium text-slate-500">
                    Tipo
                  </legend>
                  <div className="flex flex-wrap gap-1.5">
                    {SERVICE_TYPES.map((type) => (
                      <button
                        key={type}
                        type="button"
                        onClick={() => toggleType(type)}
                        aria-pressed={filters.type === type}
                        className={`rounded-full border px-3 py-1.5 text-[11px] ${
                          filters.type === type
                            ? "border-blue-600 bg-blue-50 text-blue-600"
                            : "border-slate-200 text-slate-600"
                        }`}
                      >
                        {type}
                      </button>
                    ))}
                  </div>
                </fieldset>

                <fieldset className="mb-3">
                  <legend className="mb-1.5 text-[11px] font-medium text-slate-500">
                    Status
                  </legend>
                  <div className="flex flex-wrap gap-1.5">
                    {SERVICE_STATUSES.map((status) => (
                      <button
                        key={status}
                        type="button"
                        onClick={() => toggleStatus(status)}
                        aria-pressed={filters.status === status}
                        className={`rounded-full border px-3 py-1.5 text-[11px] ${
                          filters.status === status
                            ? "border-blue-600 bg-blue-50 text-blue-600"
                            : "border-slate-200 text-slate-600"
                        }`}
                      >
                        {status}
                      </button>
                    ))}
                  </div>
                </fieldset>

                <button
                  type="button"
                  onClick={() => setShowFilters(false)}
                  className="h-9 w-full rounded-lg bg-blue-600 text-[11px] font-semibold text-white"
                >
                  Ver {count} {count === 1 ? "resultado" : "resultados"}
                </button>
              </div>
            )}
          </div>

          <div className="mt-2 flex items-center justify-between text-[10px]">
            <div className="flex items-center gap-2">
              <span className="text-slate-500" role="status">
                {count} {count === 1 ? "resultado" : "resultados"}
              </span>
              {hasActiveFilters && (
                <button
                  type="button"
                  onClick={clearFilters}
                  className="font-medium text-blue-600"
                >
                  Limpar filtros
                </button>
              )}
            </div>

            <button
              type="button"
              onClick={() => setOldestFirst((value) => !value)}
              className="flex items-center gap-1 text-slate-600"
            >
              {oldestFirst ? "Mais antigos" : "Mais recentes"}
              <ChevronDown size={12} aria-hidden="true" />
            </button>
          </div>
        </header>

        <section className="space-y-3 px-3 pb-4">
          {groups.length > 0 ? (
            groups.map(([monthKey, items]) => (
              <div key={monthKey}>
                <h2 className="mb-2 mt-1 text-[10px] font-medium uppercase tracking-wide text-slate-500">
                  {formatMonth(items[0].date)}
                </h2>

                <div className="space-y-2">
                  {items.map((service) => (
                    <ServiceCard
                      key={service.id}
                      service={service}
                      onTogglePause={togglePause}
                      onNotify={notify}
                    />
                  ))}
                </div>
              </div>
            ))
          ) : (
            <div className="rounded-xl border border-slate-200 bg-white px-4 py-10 text-center">
              <Search
                size={25}
                className="mx-auto mb-2 text-slate-300"
                aria-hidden="true"
              />
              <p className="text-xs font-semibold text-slate-600">
                Nenhum serviço encontrado
              </p>
              <p className="mt-1 text-[11px] text-slate-500">
                Tente alterar sua pesquisa ou seus filtros.
              </p>
              <button
                type="button"
                onClick={clearFilters}
                className="mt-3 text-[11px] font-semibold text-blue-600"
              >
                Limpar filtros
              </button>
            </div>
          )}
        </section>
      </div>

      {toast && (
        <div
          role="status"
          className="fixed bottom-20 left-1/2 z-50 -translate-x-1/2 rounded-lg bg-slate-800 px-4 py-2.5 text-xs text-white shadow-lg"
        >
          {toast}
        </div>
      )}

      <BottomNav />
    </main>
  );
}