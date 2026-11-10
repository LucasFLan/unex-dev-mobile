"use client";

import {
  Suspense,
  useCallback,
  useMemo,
  useRef,
  useState,
  type ReactNode,
} from "react";
import Link from "next/link";
import { Bell, Search, SlidersHorizontal, X } from "lucide-react";
import { BottomNav } from "@/components/BottomNav";
import { CategoryList } from "@/components/CategoryList";
import { FiltersPanel } from "@/components/FiltersPanel";
import { ProfessionalCard } from "@/components/ProfessionalCard";
import { RequestCard } from "@/components/RequestCard";
import { useDismissable } from "@/hooks/useDismissable";
import { useHomeFilters } from "@/hooks/useHomeFilters";
import {
  SORT_OPTIONS,
  filterProfessionals,
  filterRequests,
} from "@/lib/marketplace";
import { currentUser, mockProfessionals, mockRequests } from "@/lib/mocks";

const carousel =
  "flex gap-2.5 overflow-x-auto px-5 pb-1 scrollbar-none [&::-webkit-scrollbar]:hidden";

function Section({
  title,
  subtitle,
  href,
  children,
}: {
  title: string;
  subtitle: string;
  href: string;
  children: ReactNode;
}) {
  return (
    <section className="mt-4">
      <div className="flex items-center justify-between px-5">
        <div>
          <h2 className="text-xs font-bold">{title}</h2>
          <p className="mt-0.5 text-[10px] text-slate-500">{subtitle}</p>
        </div>

        <Link href={href} className="text-[11px] font-medium text-blue-600">
          Ver todos
        </Link>
      </div>

      <div className="mt-3">{children}</div>
    </section>
  );
}

function EmptyState({
  message,
  onClear,
}: {
  message: string;
  onClear?: () => void;
}) {
  return (
    <div className="mx-5 rounded-xl border border-dashed border-slate-200 px-4 py-6 text-center">
      <p className="text-xs text-slate-500">{message}</p>
      {onClear && (
        <button
          type="button"
          onClick={onClear}
          className="mt-2 text-[11px] font-semibold text-blue-600"
        >
          Limpar filtros
        </button>
      )}
    </div>
  );
}

function HomeContent() {
  const {
    filters,
    hasActiveFilters,
    searchInput,
    setSearchInput,
    setCategory,
    setSort,
    setMaxPrice,
    clearFilters,
  } = useHomeFilters();

  const [showFilters, setShowFilters] = useState(false);
  const filtersRef = useRef<HTMLDivElement>(null);
  const closeFilters = useCallback(() => setShowFilters(false), []);
  useDismissable(filtersRef, showFilters, closeFilters);

  const professionals = useMemo(
    () => filterProfessionals(mockProfessionals, filters),
    [filters]
  );
  const requests = useMemo(
    () => filterRequests(mockRequests, filters),
    [filters]
  );

  const onClear = hasActiveFilters ? clearFilters : undefined;
  const priceFilterActive = filters.maxPrice !== null;

  return (
    <main className="min-h-dvh bg-white pb-28 text-slate-900">
      <div className="mx-auto w-full max-w-lg">
        <header className="px-5 pt-5">
          <div className="flex items-center justify-between">
            <h1 className="text-[15px] font-bold tracking-tight">
              Olá, {currentUser.name}
            </h1>

            <button
              type="button"
              aria-label="Notificações"
              className="relative flex h-10 w-10 items-center justify-center rounded-xl border border-slate-200"
            >
              <Bell size={18} aria-hidden="true" />
            </button>
          </div>

          <div ref={filtersRef} className="relative mt-4">
            <div className="flex gap-2">
              <div className="flex h-10 min-w-0 flex-1 items-center gap-2 rounded-xl border border-slate-200 bg-slate-50 px-3">
                <Search
                  size={16}
                  className="shrink-0 text-slate-400"
                  aria-hidden="true"
                />
                <input
                  type="search"
                  value={searchInput}
                  onChange={(event) => setSearchInput(event.target.value)}
                  placeholder="Buscar serviços ou profissionais"
                  aria-label="Buscar serviços ou profissionais"
                  className="min-w-0 flex-1 bg-transparent text-[11px] outline-none placeholder:text-slate-500 [&::-webkit-search-cancel-button]:hidden"
                />
                {searchInput && (
                  <button
                    type="button"
                    onClick={() => setSearchInput("")}
                    aria-label="Limpar busca"
                    className="-mr-3 flex h-10 w-10 shrink-0 items-center justify-center"
                  >
                    <X size={14} className="text-slate-500" aria-hidden="true" />
                  </button>
                )}
              </div>

              <button
                type="button"
                onClick={() => setShowFilters((open) => !open)}
                aria-label={priceFilterActive ? "Filtros (1 ativo)" : "Filtros"}
                aria-expanded={showFilters}
                aria-controls="painel-filtros"
                className="relative flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-blue-600 text-white"
              >
                <SlidersHorizontal size={19} aria-hidden="true" />
                {priceFilterActive && (
                  <span
                    className="absolute -right-1 -top-1 h-2.5 w-2.5 rounded-full bg-amber-400 ring-2 ring-white"
                    aria-hidden="true"
                  />
                )}
              </button>
            </div>

            {showFilters && (
              <FiltersPanel
                id="painel-filtros"
                maxPrice={filters.maxPrice}
                onApply={setMaxPrice}
                onClose={closeFilters}
              />
            )}
          </div>

          <div className="mt-4 flex gap-1.5 overflow-x-auto pb-1 scrollbar-none [&::-webkit-scrollbar]:hidden">
            {SORT_OPTIONS.map(({ value, label }) => (
              <button
                key={value}
                type="button"
                onClick={() => setSort(value)}
                aria-pressed={filters.sort === value}
                className={`shrink-0 rounded-full border px-3 py-1.5 text-[11px] transition ${
                  filters.sort === value
                    ? "border-blue-600 bg-blue-600 text-white"
                    : "border-slate-200 bg-white text-slate-700"
                }`}
              >
                {label}
              </button>
            ))}
          </div>
        </header>

        <CategoryList active={filters.category} onSelect={setCategory} />

        <Section
          title="Profissionais disponíveis"
          subtitle="Quem oferece serviços perto de você"
          href="/profissionais"
        >
          {professionals.length === 0 ? (
            <EmptyState
              message="Nenhum profissional encontrado."
              onClear={onClear}
            />
          ) : (
            <ul className={carousel}>
              {professionals.map((professional) => (
                <li key={professional.id} className="shrink-0">
                  <ProfessionalCard professional={professional} />
                </li>
              ))}
            </ul>
          )}
        </Section>

        <Section
          title="Pedidos de serviço"
          subtitle="Clientes procurando profissionais agora"
          href="/servicos"
        >
          {requests.length === 0 ? (
            <EmptyState
              message="Nenhum pedido encontrado."
              onClear={onClear}
            />
          ) : (
            <ul className={carousel}>
              {requests.map((request) => (
                <li key={request.id} className="flex shrink-0">
                  <RequestCard request={request} />
                </li>
              ))}
            </ul>
          )}
        </Section>
      </div>

      <BottomNav />
    </main>
  );
}

export default function HomePage() {
  return (
    <Suspense fallback={null}>
      <HomeContent />
    </Suspense>
  );
}