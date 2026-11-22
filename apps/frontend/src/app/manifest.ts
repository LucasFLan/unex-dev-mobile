import type { MetadataRoute } from "next";

/** Manifesto do PWA: servido em /manifest.webmanifest e ligado automaticamente no <head>. */
export default function manifest(): MetadataRoute.Manifest {
  return {
    id: "/",
    name: "ChamaAí",
    short_name: "ChamaAí",
    description: "Encontre profissionais e serviços perto de você, ou anuncie os seus.",
    lang: "pt-BR",
    start_url: "/home",
    scope: "/",
    display: "standalone",
    orientation: "portrait",
    background_color: "#F8FAFC",
    theme_color: "#2563EB",
    categories: ["business", "lifestyle", "productivity"],
    icons: [
      { src: "/icons/icon-192.png", sizes: "192x192", type: "image/png", purpose: "any" },
      { src: "/icons/icon-512.png", sizes: "512x512", type: "image/png", purpose: "any" },
      { src: "/icons/icon-maskable-512.png", sizes: "512x512", type: "image/png", purpose: "maskable" },
    ],
  };
}
