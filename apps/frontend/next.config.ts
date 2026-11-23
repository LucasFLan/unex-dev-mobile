import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Imagem Docker enxuta: o build gera um servidor Node autocontido em .next/standalone.
  output: "standalone",
  cacheComponents: true,
  partialPrefetching: true,
  turbopack: {
    rules: {
      "*.css": {
        loaders: ["@tailwindcss/turbopack"],
        as: "*.css",
      },
    },
  },
  // Abrir o dev server pelo celular na rede local (ex.: http://192.168.0.161:3000).
  allowedDevOrigins: ["192.168.*.*"],
  // O service worker precisa ser revalidado sempre, senão o navegador demora a pegar a versão nova.
  async headers() {
    return [
      {
        source: "/sw.js",
        headers: [
          { key: "Cache-Control", value: "no-cache, no-store, must-revalidate" },
          { key: "Content-Type", value: "application/javascript; charset=utf-8" },
        ],
      },
    ];
  },
  experimental: {
    // Validação de conta: documento + selfie (até 5 MB cada) passam por uma server action.
    serverActions: {
      bodySizeLimit: "30mb",
      // Endereços públicos do front (o Next confere a origem dos formulários). Inclui o caso de proxy
      // reverso que não repassa o Host original.
      allowedOrigins: ["192.168.254.210:23000", "chamai.grupopapa.com"],
    },
  },
};

export default nextConfig;
