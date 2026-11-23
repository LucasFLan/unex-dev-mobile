"use client";

import { useEffect, useMemo, useRef, useState, useTransition } from "react";
import Link from "next/link";
import { ChevronDown, Plus, Search, SlidersHorizontal, X } from "lucide-react";
import { deleteAd, setAdActive } from "@/app/anunciar/actions";
import { BottomNav } from "@/components/BottomNav";
import { SentProposals } from "@/components/SentProposals";
import { ServiceCard } from "@/components/ServiceCard";
import type { AnuncioItem } from "@/lib/ads";
import type { ProposalItem } from "@/lib/proposals";
import {
  SERVICE_STATUSES,
  SERVICE_TYPES,
  toService,
  type Service,
  type ServiceStatus,
  type ServiceType,
} from "@/lib/services";
import { formatMonth, normalize } from "@/lib/format";

type Filters = {
  type: ServiceType | null;
  status: ServiceStatus | null;
};

const NO_FILTERS: Filters = { type: null, status: null };

const chipBase =
  "flex h-8 shrink-0 items-center gap-1 rounded-full border px-2.5 text-[11px]";
const chipOn = "border-blue-500 bg-blue-50 text-blue-600";
const chipOff = "border-slate-200 bg-white text-slate-700";

type Tab = "anuncios" | "propostas";

type HistoricoContentProps = {
  anuncios: AnuncioItem[];
  /** Propostas que o usuário enviou para anúncios de outras pessoas. */
  sentProposals: ProposalItem[];
  initialTab?: Tab;
};

export function HistoricoContent({
  anuncios,
  sentProposals,
  initialTab = "anuncios",
}: HistoricoContentProps) {
  const [tab, setTab] = useState<Tab>(initialTab);
  const [services, setServices] = useState<Service[]>(() =>
    anuncios.map(toService)
  );
  const [search, setSearch] = useState("");
  const [filters, setFilters] = useState<Filters>(NO_FILTERS);
  const [showFilters, setShowFilters] = useState(false);
  const [oldestFirst, setOldestFirst] = useState(false);
  const [toast, setToast] = useState("");
  const [busyId, setBusyId] = useState<string | null>(null);
  const [, startTransition] = useTransition();

  const toastTimer = useRef<ReturnType<typeof setTimeout> | undefined>(
    undefined
  );
  const filtersRef = useRef<HTMLDivElement>(null);

  // Atualiza a lista quando o servidor manda dados novos (ex.: após revalidar).
  const [sourceAnuncios, setSourceAnuncios] = useState(anuncios);
  if (sourceAnuncios !== anuncios) {
    setSourceAnuncios(anuncios);
    setServices(anuncios.map(toService));
  }

  const activeFilterCount =
    Number(filters.type !== null) + Number(filters.status !== null);
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
    setFilters((f) => ({ ...f, type: f.type === type ? null : type }));

  const toggleStatus = (status: ServiceStatus) =>
    setFilters((f) => ({ ...f, status: f.status === status ? null : status }));

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
        return true;
      })
      .sort(
        (a, b) =>
          direction * a.date.localeCompare(b.date) || a.id.localeCompare(b.id)
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

  const togglePause = (id: string) => {
    const target = services.find((service) => service.id === id);
    if (!target || busyId) return;

    const pausing = target.status === "Ativo";
    setBusyId(id);

    startTransition(async () => {
      const result = await setAdActive(id, !pausing);
      setBusyId(null);

      if (!result.ok) {
        notify(result.error);
        return;
      }

      setServices((current) =>
        current.map((service) =>
          service.id === id
            ? result.ad
              ? toService(result.ad)
              : { ...service, status: pausing ? "Pausado" : "Ativo" }
            : service
        )
      );
      notify(pausing ? "Anúncio pausado." : "Anúncio retomado.");
    });
  };

  const remove = (id: string) => {
    const target = services.find((service) => service.id === id);
    if (!target || busyId) return;

    if (
      !window.confirm(
        `Excluir "${target.title}"? Esta ação não pode ser desfeita.`
      )
    ) {
      return;
    }

    setBusyId(id);

    startTransition(async () => {
      const result = await deleteAd(id);
      setBusyId(null);

      if (!result.ok) {
        notify(result.error);
        return;
      }

      setServices((current) => current.filter((service) => service.id !== id));
      notify("Anúncio excluído.");
    });
  };

  const count = filteredServices.length;

  return (
    <main className="min-h-dvh bg-fundo pb-24 text-slate-800 lg:pb-12">
      <div className="mx-auto w-full max-w-120 lg:max-w-5xl lg:px-6">
        <header className="sticky top-0 z-20 bg-fundo px-3.5 pb-2 pt-4 lg:pb-4 lg:pt-8">
          <div className="mb-2 flex items-center justify-between">
            <h1 className="text-[17px] font-bold tracking-tight lg:text-2xl">
              Histórico
            </h1>

            <Link
              href="/anunciar"
              className="flex h-8 items-center gap-1 rounded-lg bg-blue-600 px-2.5 text-[11px] font-semibold text-white transition hover:bg-blue-700 lg:h-10 lg:px-4 lg:text-sm"
            >
              <Plus size={13} aria-hidden="true" />
              Novo
            </Link>
          </div>

          <div
            role="tablist"
            aria-label="O que mostrar"
            className="mb-2 grid grid-cols-2 rounded-lg bg-slate-100 p-0.5 text-[11px] font-medium lg:mb-3 lg:max-w-xl lg:text-sm"
          >
            {(
              [
                ["anuncios", `Meus anúncios (${services.length})`],
                ["propostas", `Propostas enviadas (${sentProposals.length})`],
              ] as const
            ).map(([value, label]) => (
              <button
                key={value}
                type="button"
                role="tab"
                aria-selected={tab === value}
                onClick={() => setTab(value)}
                className={`h-8 rounded-md transition ${
                  tab === value
                    ? "bg-white text-blue-600 shadow-sm"
                    : "text-slate-600"
                }`}
              >
                {label}
              </button>
            ))}
          </div>

          {tab === "anuncios" && (
          <>

          <div className="flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-white px-2.5 focus-within:border-blue-500 lg:h-11 lg:max-w-xl lg:px-3.5">
            <Search size={14} className="text-slate-400" aria-hidden="true" />
            <input
              type="search"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Buscar nos meus anúncios"
              aria-label="Buscar nos meus anúncios"
              className="h-full w-full bg-transparent text-[11px] outline-none lg:text-sm placeholder:text-slate-500 [&::-webkit-search-cancel-button]:hidden"
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

          <div ref={filtersRef} className="relative mt-2 lg:mt-3 lg:max-w-xl">
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

              {SERVICE_TYPES.map((type) => (
                <button
                  key={type}
                  type="button"
                  onClick={() => toggleType(type)}
                  aria-pressed={filters.type === type}
                  className={`${chipBase} ${
                    filters.type === type ? chipOn : `${chipOff} text-blue-600`
                  }`}
                >
                  {type}
                </button>
              ))}
            </div>

            {showFilters && (
              <div
                id="painel-filtros"
                className="absolute inset-x-0 top-full z-30 mt-2 rounded-xl border border-slate-200 bg-white p-3 shadow-lg"
              >
                <div className="mb-2 flex items-center justify-between">
                  <span className="text-xs font-semibold">
                    Filtrar anúncios
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
          </>
          )}
        </header>

        <section className="space-y-3 px-3 pb-4 lg:space-y-6 lg:px-3.5">
          {tab === "propostas" ? (
            <SentProposals proposals={sentProposals} />
          ) : services.length === 0 ? (
            <div className="rounded-xl border border-slate-200 bg-white px-4 py-10 text-center">
              <p className="text-xs font-semibold text-slate-600">
                Você ainda não publicou anúncios
              </p>
              <p className="mt-1 text-[11px] text-slate-500">
                Publique um pedido ou ofereça seus serviços.
              </p>
              <Link
                href="/anunciar"
                className="mt-3 inline-block text-[11px] font-semibold text-blue-600"
              >
                Criar anúncio
              </Link>
            </div>
          ) : groups.length > 0 ? (
            groups.map(([monthKey, items]) => (
              <div key={monthKey}>
                <h2 className="mb-2 mt-1 text-[10px] font-medium uppercase tracking-wide text-slate-500 lg:mb-3 lg:text-xs">
                  {formatMonth(items[0].date)}
                </h2>

                <div className="space-y-2 lg:grid lg:grid-cols-2 lg:items-start lg:gap-3 lg:space-y-0">
                  {items.map((service) => (
                    <ServiceCard
                      key={service.id}
                      service={service}
                      busy={busyId === service.id}
                      onTogglePause={togglePause}
                      onDelete={remove}
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
                Nenhum anúncio encontrado
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
          className="fixed bottom-20 left-1/2 z-50 -translate-x-1/2 lg:bottom-8 lg:left-[calc(50%+7.5rem)] rounded-lg bg-slate-800 px-4 py-2.5 text-xs text-white shadow-lg"
        >
          {toast}
        </div>
      )}

      <BottomNav />
    </main>
  );
}
