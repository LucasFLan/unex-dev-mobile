"use client";

import { useCallback, useMemo, useRef, useState } from "react";
import { usePathname, useSearchParams } from "next/navigation";
import { Search, SlidersHorizontal, X } from "lucide-react";
import { BackButton } from "@/components/BackButton";
import { BottomNav } from "@/components/BottomNav";
import { CategoryList } from "@/components/Home/CategoryList";
import { FiltersPanel } from "@/components/Home/FiltersPanel";
import { ProfessionalCard } from "@/components/Home/ProfessionalCard";
import { RequestCard } from "@/components/Home/RequestCard";
import { useDismissable } from "@/hooks/useDismissable";
import { useHomeFilters } from "@/hooks/useHomeFilters";
import {
  SORT_OPTIONS,
  filterProfessionals,
  filterRequests,
  toProfessional,
  toRequest,
  withDistance,
  type SortOption,
} from "@/lib/marketplace";
import { useMyLocation } from "@/hooks/useMyLocation";
import type { AnuncioItem } from "@/lib/ads";
import type { Category } from "@/lib/categories-api";

const COPY = {
  profissionais: {
    title: "Profissionais disponíveis",
    search: "Buscar profissionais ou serviços",
    empty: "Nenhum profissional encontrado.",
    count: (n: number) => (n === 1 ? "1 profissional" : `${n} profissionais`),
  },
  servicos: {
    title: "Pedidos de serviço",
    search: "Buscar pedidos de serviço",
    empty: "Nenhum pedido encontrado.",
    count: (n: number) => (n === 1 ? "1 pedido" : `${n} pedidos`),
  },
} as const;

/**
 * Lista completa de um tipo de anúncio ("Ver todos" da Home), com a mesma busca e os mesmos filtros.
 * Os filtros ficam na URL, então a Home e esta tela compartilham a seleção nos dois sentidos.
 */
export function ListingContent({
  kind,
  anuncios,
  categories,
}: {
  kind: "profissionais" | "servicos";
  anuncios: AnuncioItem[];
  categories: Category[];
}) {
  const copy = COPY[kind];
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

  // "Mais perto": a distância é calculada aqui, com a localização aproximada do aparelho (não vai ao servidor).
  const location = useMyLocation();
  const chooseSort = (value: SortOption) => {
    if (value === "perto" && !location.point) location.request();
    setSort(value);
  };
  const locationMessage =
    filters.sort !== "perto"
      ? null
      : location.status === "loading"
        ? "Descobrindo sua localização..."
        : location.status === "error"
          ? location.message
          : !location.point
            ? "Toque em \"Mais perto\" e permita a localização."
            : null;

  const professionals = useMemo(
    () =>
      kind === "profissionais"
        ? filterProfessionals(
            withDistance(anuncios.filter((anuncio) => anuncio.tipo === "OFERTA").map(toProfessional), location.point),
            filters
          )
        : [],
    [kind, anuncios, filters, location.point]
  );

  const requests = useMemo(
    () =>
      kind === "servicos"
        ? filterRequests(
            withDistance(anuncios.filter((anuncio) => anuncio.tipo === "PEDIDO").map(toRequest), location.point),
            filters
          )
        : [],
    [kind, anuncios, filters, location.point]
  );

  const total = kind === "profissionais" ? professionals.length : requests.length;
  const priceFilterActive = filters.maxPrice !== null;

  // Voltar e abrir um anúncio preservam os filtros escolhidos.
  const pathname = usePathname();
  const queryString = useSearchParams().toString();
  const withQuery = (path: string) => (queryString ? `${path}?${queryString}` : path);
  const here = withQuery(pathname);

  return (
    <main className="min-h-dvh bg-white pb-28 text-slate-900 lg:pb-12">
      <div className="mx-auto w-full max-w-lg lg:max-w-6xl lg:px-4">
        <header className="px-5 pt-4 lg:pt-8">
          <div className="-ml-2 flex items-center gap-1">
            <BackButton href={withQuery("/home")} />
            <div className="min-w-0">
              <h1 className="truncate text-[15px] font-bold tracking-tight lg:text-2xl">{copy.title}</h1>
              <p className="text-[10px] text-slate-500 lg:text-sm" aria-live="polite">
                {copy.count(total)}
              </p>
            </div>
          </div>

          <div ref={filtersRef} className="relative mt-4 lg:max-w-2xl">
            <div className="flex gap-2">
              <div className="flex h-10 min-w-0 flex-1 items-center gap-2 rounded-xl border border-slate-200 bg-slate-50 px-3 focus-within:border-blue-500 lg:h-12 lg:px-4">
                <Search size={16} className="shrink-0 text-slate-400" aria-hidden="true" />
                <input
                  type="search"
                  value={searchInput}
                  onChange={(event) => setSearchInput(event.target.value)}
                  placeholder={copy.search}
                  aria-label={copy.search}
                  className="min-w-0 flex-1 bg-transparent text-[11px] outline-none lg:text-sm placeholder:text-slate-500 [&::-webkit-search-cancel-button]:hidden"
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
                className="relative flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-blue-600 text-white transition hover:bg-blue-700 lg:h-12 lg:w-12"
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
                onClick={() => chooseSort(value)}
                aria-pressed={filters.sort === value}
                className={`shrink-0 rounded-full border px-3 py-1.5 text-[11px] transition lg:px-4 lg:py-2 lg:text-sm ${
                  filters.sort === value
                    ? "border-blue-600 bg-blue-600 text-white"
                    : "border-slate-200 bg-white text-slate-700"
                }`}
              >
                {label}
              </button>
            ))}
          </div>

          {locationMessage && (
            <p className="mt-1.5 text-[10px] text-slate-500" role="status">
              {locationMessage}{" "}
              {location.status === "error" && (
                <button type="button" onClick={location.request} className="font-semibold text-blue-600">
                  Tentar de novo
                </button>
              )}
            </p>
          )}
        </header>

        <CategoryList categories={categories} active={filters.category} onSelect={setCategory} />

        <section className="mt-4 px-5 lg:mt-8" aria-label={copy.title}>
          {total === 0 ? (
            <div className="rounded-xl border border-dashed border-slate-200 px-4 py-6 text-center">
              <p className="text-xs text-slate-500">{copy.empty}</p>
              {hasActiveFilters && (
                <button
                  type="button"
                  onClick={clearFilters}
                  className="mt-2 text-[11px] font-semibold text-blue-600"
                >
                  Limpar filtros
                </button>
              )}
            </div>
          ) : kind === "profissionais" ? (
            <ul className="grid grid-cols-2 gap-2.5 sm:grid-cols-3 lg:gap-4 xl:grid-cols-4">
              {professionals.map((professional) => (
                <li key={professional.id} className="min-w-0">
                  <ProfessionalCard professional={professional} from={here} className="w-full" />
                </li>
              ))}
            </ul>
          ) : (
            <ul className="flex flex-col gap-2.5 lg:grid lg:grid-cols-2 lg:gap-4 xl:grid-cols-3">
              {requests.map((request) => (
                <li key={request.id} className="flex">
                  <RequestCard request={request} from={here} className="w-full" />
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>

      <BottomNav />
    </main>
  );
}
