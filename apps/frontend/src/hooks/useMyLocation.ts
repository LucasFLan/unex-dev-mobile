"use client";

import { useCallback, useMemo, useState, useSyncExternalStore } from "react";
import type { GeoPoint } from "@/lib/ads";

const STORAGE_KEY = "chamaai:minha-regiao";

/** Avisa as telas abertas quando a região muda (o evento "storage" só chega às outras abas). */
const listeners = new Set<() => void>();

function subscribe(listener: () => void) {
  listeners.add(listener);
  window.addEventListener("storage", listener);
  return () => {
    listeners.delete(listener);
    window.removeEventListener("storage", listener);
  };
}

function readRaw(): string | null {
  try {
    return window.localStorage.getItem(STORAGE_KEY);
  } catch {
    return null;
  }
}

function parse(raw: string | null): GeoPoint | null {
  if (!raw) return null;
  try {
    const p = JSON.parse(raw) as GeoPoint;
    return Number.isFinite(p.latitude) && Number.isFinite(p.longitude) ? p : null;
  } catch {
    return null;
  }
}

/** Arredonda para ~1 km: o app não precisa (nem guarda) o ponto exato de quem usa. */
const roughly = (value: number) => Math.round(value * 100) / 100;

type Status = { status: "idle" | "loading" | "ready" } | { status: "error"; message: string };

/**
 * Localização aproximada do aparelho, para ordenar por distância. Fica só no navegador (nunca vai para o
 * servidor) e a última região é lembrada. No servidor e na hidratação vale `null`, para o HTML bater.
 */
export function useMyLocation() {
  const raw = useSyncExternalStore(subscribe, readRaw, () => null);
  const stored = useMemo(() => parse(raw), [raw]);
  // Sem localStorage (aba anônima), a região fica só nesta tela.
  const [session, setSession] = useState<GeoPoint | null>(null);
  const [state, setState] = useState<Status>({ status: "idle" });
  const point = stored ?? session;

  const request = useCallback(() => {
    if (typeof navigator === "undefined" || !navigator.geolocation) {
      setState({ status: "error", message: "Este navegador não informa a localização." });
      return;
    }
    setState({ status: "loading" });
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const found = { latitude: roughly(position.coords.latitude), longitude: roughly(position.coords.longitude) };
        setSession(found);
        try {
          window.localStorage.setItem(STORAGE_KEY, JSON.stringify(found));
        } catch {
          // Sem armazenamento: vale só nesta visita.
        }
        listeners.forEach((listener) => listener());
        setState({ status: "ready" });
      },
      (error) =>
        setState({
          status: "error",
          message:
            error.code === error.PERMISSION_DENIED
              ? "Permita a localização no navegador para ver o que está mais perto."
              : "Não foi possível descobrir sua localização agora.",
        }),
      { enableHighAccuracy: false, timeout: 10000, maximumAge: 10 * 60 * 1000 }
    );
  }, []);

  return {
    point,
    status: point && state.status === "idle" ? ("ready" as const) : state.status,
    message: state.status === "error" ? state.message : "",
    request,
  };
}
