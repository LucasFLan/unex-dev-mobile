"use client";

import { useState } from "react";
import {
  MapPin,
  Search,
  SlidersHorizontal,
  Bell,
  Zap,
  Droplet,
  Sparkles,
  Hammer,
  Wrench,
  Star,
  BadgeCheck,
  Clock3,
  Plus,
  History,
  Menu,
  House,
  X,
  Paintbrush,
} from "lucide-react";
import formatPrice from "@/utils/format";

const categories = [
  { name: "Eletricista", icon: Zap, color: "bg-amber-100 text-amber-500" },
  { name: "Encanador", icon: Droplet, color: "bg-blue-100 text-blue-500" },
  { name: "Diarista", icon: Sparkles, color: "bg-violet-100 text-violet-500" },
  { name: "Pedreiro", icon: Hammer, color: "bg-orange-100 text-orange-500" },
  { name: "Montador", icon: Wrench, color: "bg-teal-100 text-teal-500" },
  { name: "Pintor", icon: Paintbrush, color: "bg-pink-100 text-pink-500" },
];

const professionals = [
  {
    id: 1,
    category: "Eletricista",
    name: "Carlos M.",
    service: "Eletricista residencial e instalações",
    rating: "4.9",
    reviews: 32,
    location: "Centro",
    distance: "2 km",
    price: 80,
    icon: Zap,
    color: "bg-amber-100",
    iconColor: "text-amber-500",
  },
  {
    id: 2,
    category: "Diarista",
    name: "Ana Paula",
    service: "Limpeza completa de casas e apartamentos",
    rating: "5.0",
    reviews: 18,
    location: "Jardim",
    distance: "3 km",
    price: 120,
    icon: Sparkles,
    color: "bg-violet-100",
    iconColor: "text-violet-500",
  },
  {
    id: 3,
    category: "Encanador",
    name: "Marcos R.",
    service: "Reparos hidráulicos e instalações",
    rating: "4.8",
    reviews: 24,
    location: "Centro",
    distance: "4 km",
    price: 90,
    icon: Droplet,
    color: "bg-blue-100",
    iconColor: "text-blue-500",
  },
];

const serviceRequests = [
  {
    id: 1,
    category: "Eletricista",
    urgent: true,
    time: "há 15 min",
    title: "Instalar 3 tomadas e um chuveiro",
    description:
      "Preciso de alguém para instalar tomadas na cozinha e trocar o chuveiro do banheiro.",
    location: "Boa Vista",
    distance: "3 km",
    price: 150,
  },
  {
    id: 2,
    category: "Pintor",
    urgent: false,
    time: "há 30 min",
    title: "Pintura de quarto",
    description:
      "Quarto com aproximadamente 12 m². Tinta será fornecida.",
    location: "Centro",
    distance: "2 km",
    price: 250,
  },
];

export default function Home() {
  const [search, setSearch] = useState(""); 
  const [sort, setSort] = useState("Mais recentes");
  const [activeCategory, setActiveCategory] = useState("Todas");
  const [activeTab, setActiveTab] = useState("Início");
  const [showFilters, setShowFilters] = useState(false);
  const [maxPrice, setMaxPrice] = useState("500");

  const filteredProfessionals = professionals
    .filter((professional) => {
      const matchesSearch =
        `${professional.name} ${professional.category} ${professional.service}`
          .toLowerCase()
          .includes(search.toLowerCase());

      const matchesCategory =
        activeCategory === "Todas" ||
        professional.category === activeCategory;

      return matchesSearch && matchesCategory;
    })
    .sort((a, b) => {
      if (sort === "Menor preço") return a.price - b.price;
      if (sort === "Melhor avaliados")
        return Number(b.rating) - Number(a.rating);
      return a.id - b.id;
    });

  const filteredRequests = serviceRequests.filter((request) =>
    `${request.title} ${request.category} ${request.description}`
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  return (
    <main className="min-h-screen bg-white text-slate-900">
      <div className="mx-auto min-h-screen w-full max-w-lg pb-28">

        <header className="px-5 pt-5">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-[15px] font-bold tracking-tight">
                Olá, Lucas
              </h1>
            </div>

            <button
              type="button"
              aria-label="Notificações"
              className="relative flex h-10 w-10 items-center justify-center rounded-xl border border-slate-200 cursor-pointer"
            >
              <Bell size={18} />
            </button>
          </div>

          <div className="mt-4 flex gap-2">
            <div className="flex h-10 min-w-0 flex-1 items-center gap-2 rounded-xl border border-slate-200 bg-slate-50 px-3">
              <Search size={16} className="shrink-0 text-slate-400" />
              <input
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                placeholder="Buscar serviços ou profissionais"
                aria-label="Buscar serviços ou profissionais"
                className="min-w-0 flex-1 bg-transparent text-[10px] outline-none placeholder:text-slate-400"
              />
              {search && (
                <button
                  type="button"
                  onClick={() => setSearch("")}
                  aria-label="Limpar busca"
                >
                  <X size={14} className="text-slate-400" />
                </button>
              )}
            </div>

            <button
              type="button"
              onClick={() => setShowFilters(!showFilters)}
              aria-label="Abrir filtros"
              className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-blue-600 text-white"
            >
              <SlidersHorizontal size={19} />
            </button>
          </div>

          <div className="mt-4 flex gap-1.5 overflow-x-auto pb-1 scrollbar-none [&::-webkit-scrollbar]:hidden">
            {["Mais recentes", "Menor preço", "Melhor avaliados"].map(
              (item) => (
                <button
                  key={item}
                  type="button"
                  onClick={() => setSort(item)}
                  className={`shrink-0 rounded-full border px-3 py-1.5 text-[10px] transition cursor-pointer ${
                    sort === item
                      ? "border-blue-600 bg-blue-600 text-white"
                      : "border-slate-200 bg-white text-slate-700"
                  }`}
                >
                  {item}
                </button>
              )
            )}
          </div>

          {showFilters && (
            <div className="mt-3 rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
              <label
                htmlFor="maxPrice"
                className="text-xs font-semibold"
              >
                Preço máximo (R$)
              </label>

              <input
                id="maxPrice"
                type="number"
                min="0"
                value={maxPrice}
                onChange={(event) => setMaxPrice(event.target.value)}
                className="mt-2 w-full rounded-lg border border-slate-200 p-2 text-sm outline-none focus:border-blue-500"
              />

              <button
                type="button"
                onClick={() => setShowFilters(false)}
                className="mt-3 w-full rounded-lg bg-blue-600 py-2 text-xs font-semibold text-white"
              >
                Aplicar filtros
              </button>
            </div>
          )}
        </header>

        <section className="mt-4">
          <div className="mb-3 flex items-center justify-between px-5">
            <h2 className="text-xs font-bold">Categorias</h2>
            <button
              type="button"
              onClick={() => setActiveCategory("Todas")}
              className="text-[10px] font-medium text-blue-600 cursor-pointer"
            >
              Ver todas
            </button>
          </div>

          <div className="flex gap-3 overflow-x-auto px-5 pb-1 scrollbar-none [&::-webkit-scrollbar]:hidden">
            {categories.map(({ name, icon: Icon, color }) => (
              <button
                key={name}
                type="button"
                onClick={() =>
                  setActiveCategory(
                    activeCategory === name ? "Todas" : name
                  )
                }
                className="flex w-10.5 shrink-0 flex-col items-center gap-1.5 cursor-pointer"
              >
                <span
                  className={`flex h-10 w-10 items-center justify-center rounded-xl ${color} ${
                    activeCategory === name
                      ? "ring-2 ring-blue-600 ring-offset-2"
                      : ""
                  }`}
                >
                  <Icon size={19} strokeWidth={1.8} />
                </span>

                <span className="w-full truncate text-center text-[9px] text-slate-700">
                  {name}
                </span>
              </button>
            ))}
          </div>
        </section>

        <section className="mt-4">
          <div className="flex items-center justify-between px-5">
            <div>
              <h2 className="text-xs font-bold">
                Profissionais disponíveis
              </h2>
              <p className="mt-0.5 text-[9px] text-slate-500">
                Quem oferece serviços perto de você
              </p>
            </div>

            <a
              href="/profissionais"
              className="text-[10px] font-medium text-blue-600 cursor-pointer"
            >
              Ver todos
            </a>
          </div>

          <div className="mt-3 flex gap-2.5 overflow-x-auto px-5 pb-1 scrollbar-none [&::-webkit-scrollbar]:hidden">
            {filteredProfessionals.length === 0 && (
              <p className="py-5 text-xs text-slate-500">
                Nenhum profissional encontrado.
              </p>
            )}

            {filteredProfessionals.map((professional) => {
              const Icon = professional.icon;

              return (
                <article
                  key={professional.id}
                  className="w-47.5 shrink-0 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm"
                >
                  <div
                    className={`relative flex h-21.5 items-center justify-center ${professional.color}`}
                  >
                    <span className="absolute left-2 top-2 rounded-md bg-white/90 px-2 py-1 text-[8px] font-medium text-amber-800">
                      {professional.category}
                    </span>

                    <Icon
                      size={29}
                      strokeWidth={1.7}
                      className={professional.iconColor}
                    />
                  </div>

                  <div className="p-2">
                    <h3 className="min-h-8 text-[10px] font-semibold leading-3.5">
                      {professional.service}
                    </h3>

                    <div className="mt-1.5 flex items-center gap-1 text-[9px] text-slate-600">
                      <span className="flex h-4 w-4 shrink-0 items-center justify-center rounded-full bg-slate-100 text-[7px]">
                        {professional.name.slice(0, 2).toUpperCase()}
                      </span>

                      <span className="truncate">{professional.name}</span>
                      <BadgeCheck
                        size={11}
                        className="shrink-0 text-blue-600"
                      />
                    </div>

                    <div className="mt-1.5 flex items-center gap-1 text-[9px] text-slate-500">
                      <Star
                        size={11}
                        className="fill-amber-400 text-amber-400"
                      />
                      <span className="font-semibold text-slate-800">
                        {professional.rating}
                      </span>
                      <span>({professional.reviews}) ·</span>
                      <span className="truncate">
                        {professional.location} · {professional.distance}
                      </span>
                    </div>

                    <p className="mt-1.5 text-[9px] text-slate-500">
                      A partir de{" "}
                      <strong className="text-xs text-slate-900">
                        {formatPrice(professional.price)}
                      </strong>
                    </p>
                  </div>
                </article>
              );
            })}
          </div>
        </section>

        <section className="mt-4">
          <div className="flex items-center justify-between px-5">
            <div>
              <h2 className="text-xs font-bold">Pedidos de serviço</h2>
              <p className="mt-0.5 text-[9px] text-slate-500">
                Clientes procurando profissionais agora
              </p>
            </div>

            <a
              href="/servicos"
              className="text-[10px] font-medium text-blue-600 cursor-pointer"
            >
              Ver todos
            </a>
          </div>

          <div className="mt-3 flex gap-2.5 overflow-x-auto px-5 pb-1 scrollbar-none [&::-webkit-scrollbar]:hidden">
            {filteredRequests
              .filter((request) => request.price <= Number(maxPrice || 0))
              .map((request) => (
                <article
                  key={request.id}
                  className="flex w-60 shrink-0 flex-col rounded-xl border border-slate-200 bg-white p-2.5 shadow-sm"
                >
                  <div className="flex items-center justify-between gap-1">
                    <div className="flex items-center gap-1">
                      <span className="rounded bg-amber-50 px-1.5 py-1 text-[8px] font-medium text-amber-800">
                        {request.category}
                      </span>

                      {request.urgent && (
                        <span className="rounded bg-red-50 px-1.5 py-1 text-[8px] font-medium text-red-600">
                          Urgente
                        </span>
                      )}
                    </div>

                    <span className="flex shrink-0 items-center gap-1 text-[8px] text-slate-500">
                      <Clock3 size={10} />
                      {request.time}
                    </span>
                  </div>

                  <h3 className="mt-2 text-[10px] font-semibold leading-4">
                    {request.title}
                  </h3>

                  <p className="mt-1 min-h-10.5 text-[9px] leading-3.5 text-slate-500">
                    {request.description}
                  </p>

                  <div className="mt-2 flex items-center justify-between gap-2 border-t border-slate-100 pt-2">
                    <span className="flex min-w-0 items-center gap-1 text-[9px] text-slate-500">
                      <MapPin size={11} className="shrink-0" />
                      <span className="truncate">
                        {request.location} · {request.distance}
                      </span>
                    </span>

                    <strong className="shrink-0 text-[10px]">
                      {formatPrice(request.price)}
                    </strong>
                  </div>

                  <button
                    type="button"
                    onClick={() =>
                      alert(`Click ok`)
                    }
                    className="mt-2 rounded-lg bg-blue-50 py-2 text-[10px] font-semibold text-blue-600 transition hover:bg-blue-100"
                  >
                    Enviar proposta
                  </button>
                </article>
              ))}
          </div>
        </section>

      </div>

      <nav className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-200 bg-white pb-[max(8px,env(safe-area-inset-bottom))]">
        <div className="mx-auto grid h-15 w-full max-w-lg grid-cols-4">
          {[
            { label: "Início", icon: House },
            { label: "Anunciar", icon: Plus },
            { label: "Histórico", icon: History },
            { label: "Menu", icon: Menu },
          ].map(({ label, icon: Icon }) => {
            const active = activeTab === label;

            return (
              <button
                key={label}
                type="button"
                onClick={() => setActiveTab(label)}
                className={`flex flex-col items-center justify-center gap-1 ${
                  active ? "text-blue-600" : "text-slate-500"
                }`}
              >
                <span
                  className={`flex h-7 w-7 items-center justify-center rounded-full ${
                    label === "Anunciar" ? "bg-blue-600 text-white" : ""
                  }`}
                >
                  <Icon
                    size={label === "Anunciar" ? 19 : 18}
                    strokeWidth={1.8}
                  />
                </span>

                <span className="text-[9px] font-medium">{label}</span>
              </button>
            );
          })}
        </div>
      </nav>
    </main>
  );
}
