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
  experimental: {
    // Validação de conta: documento + selfie (até 5 MB cada) passam por uma server action.
    serverActions: {
      bodySizeLimit: "12mb",
      // Endereços públicos do front (o Next confere a origem dos formulários). Inclui o caso de proxy
      // reverso que não repassa o Host original.
      allowedOrigins: ["192.168.254.210:23000", "chamai.grupopapa.com"],
    },
  },
};

export default nextConfig;
