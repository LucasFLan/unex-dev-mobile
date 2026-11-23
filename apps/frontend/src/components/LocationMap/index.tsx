"use client";

import { useEffect, useRef } from "react";
import "leaflet/dist/leaflet.css";
import type { GeoPoint } from "@/lib/ads";

/** Raio do círculo quando não há raio de atendimento: cobre a imprecisão do ponto (~1 km). */
const AREA_RADIUS_M = 900;

/**
 * Mapa (OpenStreetMap) com um círculo sobre a região do anúncio. Nunca marca um ponto exato: o centro já vem
 * arredondado da API. `radiusKm` (oferta) desenha a área de atendimento do profissional.
 */
export function LocationMap({
  center,
  radiusKm,
  label,
  className = "h-36",
}: {
  center: GeoPoint;
  radiusKm?: number | null;
  label: string;
  className?: string;
}) {
  const containerRef = useRef<HTMLDivElement>(null);
  const { latitude, longitude } = center;

  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;
    let disposed = false;
    let map: import("leaflet").Map | null = null;

    // Leaflet usa window: carrega só no navegador.
    import("leaflet").then((L) => {
      if (disposed || !container) return;
      map = L.map(container, {
        zoomControl: false,
        attributionControl: true,
        scrollWheelZoom: false,
        // Arrastar o mapa com um dedo no celular atrapalha a rolagem da página.
        dragging: !L.Browser.mobile,
        touchZoom: true,
      }).setView([latitude, longitude], 14); // o círculo só calcula os limites com o mapa já posicionado
      L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 18,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }).addTo(map);
      const circle = L.circle([latitude, longitude], {
        radius: radiusKm ? radiusKm * 1000 : AREA_RADIUS_M,
        color: "#2563EB",
        weight: 1.5,
        fillColor: "#3B82F6",
        fillOpacity: 0.18,
      }).addTo(map);
      map.fitBounds(circle.getBounds(), { padding: [12, 12] });
    });

    return () => {
      disposed = true;
      map?.remove();
    };
  }, [latitude, longitude, radiusKm]);

  return (
    <div
      ref={containerRef}
      role="img"
      aria-label={label}
      className={`relative z-0 w-full overflow-hidden rounded-xl bg-slate-100 ${className}`}
    />
  );
}
