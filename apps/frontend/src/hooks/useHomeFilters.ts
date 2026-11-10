"use client";

import { useCallback, useEffect, useMemo, useState } from "react";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { isCategoryName, type CategoryName } from "@/lib/categories";
import {
  DEFAULT_SORT,
  isSortOption,
  type HomeFilters,
  type SortOption,
} from "@/lib/marketplace";

type ParamKey = "q" | "categoria" | "ordem" | "preco";

const SEARCH_DEBOUNCE_MS = 300;

export function useHomeFilters() {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const query = searchParams.get("q") ?? "";

  const categoryParam = searchParams.get("categoria");
  const category: CategoryName | null = isCategoryName(categoryParam)
    ? categoryParam
    : null;

  const sortParam = searchParams.get("ordem");
  const sort: SortOption = isSortOption(sortParam) ? sortParam : DEFAULT_SORT;

  const priceParam = searchParams.get("preco");
  const parsedPrice = priceParam ? Number(priceParam) : NaN;
  const maxPrice =
    Number.isFinite(parsedPrice) && parsedPrice >= 0 ? parsedPrice : null;

  const filters: HomeFilters = useMemo(
    () => ({ query, category, sort, maxPrice }),
    [query, category, sort, maxPrice]
  );

  const updateParams = useCallback(
    (changes: Partial<Record<ParamKey, string | null>>) => {
      const params = new URLSearchParams(window.location.search);
      for (const [key, value] of Object.entries(changes)) {
        if (value) params.set(key, value);
        else params.delete(key);
      }
      const queryString = params.toString();
      router.replace(queryString ? `${pathname}?${queryString}` : pathname, {
        scroll: false,
      });
    },
    [router, pathname]
  );

  const [searchInput, setSearchInput] = useState(query);

  useEffect(() => {
    const timer = setTimeout(() => {
      const current = new URLSearchParams(window.location.search).get("q") ?? "";
      const next = searchInput.trim();
      if (next !== current) updateParams({ q: next || null });
    }, SEARCH_DEBOUNCE_MS);

    return () => clearTimeout(timer);
  }, [searchInput, updateParams]);

  const setCategory = useCallback(
    (value: CategoryName | null) => updateParams({ categoria: value }),
    [updateParams]
  );

  const setSort = useCallback(
    (value: SortOption) =>
      updateParams({ ordem: value === DEFAULT_SORT ? null : value }),
    [updateParams]
  );

  const setMaxPrice = useCallback(
    (value: number | null) =>
      updateParams({ preco: value === null ? null : String(value) }),
    [updateParams]
  );

  const clearFilters = useCallback(() => {
    setSearchInput("");
    updateParams({ q: null, categoria: null, preco: null });
  }, [updateParams]);

  return {
    filters,
    hasActiveFilters: query !== "" || category !== null || maxPrice !== null,
    searchInput,
    setSearchInput,
    setCategory,
    setSort,
    setMaxPrice,
    clearFilters,
  };
}