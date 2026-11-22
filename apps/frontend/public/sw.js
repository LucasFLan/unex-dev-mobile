/*
 * Service worker do ChamaAí.
 *
 * - Telas: sempre da rede (anúncios, propostas e sessão mudam o tempo todo). Sem internet, mostra
 *   /offline.html em vez do erro do navegador.
 * - Arquivos estáticos do build (/_next/static, com hash no nome) e ícones: cache, que acelera a abertura.
 * - Nada de API, imagens privadas ou respostas com dados do usuário vai para o cache.
 *
 * Ao mudar o que vai no cache, troque a VERSAO: os caches antigos são apagados na ativação.
 */
const VERSAO = "v1";
const CACHE_ESTATICO = `chamaai-estatico-${VERSAO}`;
const PAGINA_OFFLINE = "/offline.html";
const PRE_CACHE = [PAGINA_OFFLINE, "/icons/icon-192.png"];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_ESTATICO).then((cache) => cache.addAll(PRE_CACHE)).then(() => self.skipWaiting())
  );
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches
      .keys()
      .then((nomes) =>
        Promise.all(
          nomes.filter((nome) => nome.startsWith("chamaai-") && nome !== CACHE_ESTATICO).map((nome) => caches.delete(nome))
        )
      )
      .then(() => self.clients.claim())
  );
});

function ehEstatico(url) {
  return url.pathname.startsWith("/_next/static/") || url.pathname.startsWith("/icons/");
}

self.addEventListener("fetch", (event) => {
  const { request } = event;
  if (request.method !== "GET") return;

  const url = new URL(request.url);
  if (url.origin !== self.location.origin) return;

  // Navegação entre telas: rede; sem conexão, a página offline.
  if (request.mode === "navigate") {
    event.respondWith(fetch(request).catch(() => caches.match(PAGINA_OFFLINE)));
    return;
  }

  // Estáticos com hash: cache primeiro (o conteúdo de uma URL nunca muda).
  if (ehEstatico(url)) {
    event.respondWith(
      caches.match(request).then(
        (emCache) =>
          emCache ||
          fetch(request).then((resposta) => {
            if (resposta.ok) {
              const copia = resposta.clone();
              caches.open(CACHE_ESTATICO).then((cache) => cache.put(request, copia));
            }
            return resposta;
          })
      )
    );
  }
  // O resto (API, /api/*, dados das telas) segue direto para a rede.
});
