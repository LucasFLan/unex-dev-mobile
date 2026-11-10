import type { Professional, ServiceRequest } from "@/lib/marketplace";
import type {Service} from "@/lib/services"

const minutesAgo = (minutes: number) =>
  new Date(Date.now() - minutes * 60_000).toISOString();

const daysAgo = (days: number) => minutesAgo(days * 24 * 60);

export const currentUser = { name: "Lucas" };

export const mockProfessionals: Professional[] = [
  {
    id: 1,
    category: "Eletricista",
    name: "Carlos M.",
    service: "Eletricista residencial e instalações",
    rating: 4.9,
    reviewsCount: 32,
    neighborhood: "Centro",
    distanceKm: 2,
    priceFrom: 80,
    createdAt: daysAgo(2),
  },
  {
    id: 2,
    category: "Diarista",
    name: "Ana Paula",
    service: "Limpeza completa de casas e apartamentos",
    rating: 5,
    reviewsCount: 18,
    neighborhood: "Jardim",
    distanceKm: 3,
    priceFrom: 120,
    createdAt: daysAgo(3),
  },
  {
    id: 3,
    category: "Encanador",
    name: "Marcos R.",
    service: "Reparos hidráulicos e instalações",
    rating: 4.8,
    reviewsCount: 24,
    neighborhood: "Centro",
    distanceKm: 4,
    priceFrom: 90,
    createdAt: daysAgo(5),
  },
];

export const mockRequests: ServiceRequest[] = [
  {
    id: 1,
    category: "Eletricista",
    urgent: true,
    createdAt: minutesAgo(15),
    title: "Instalar 3 tomadas e um chuveiro",
    description:
      "Preciso de alguém para instalar tomadas na cozinha e trocar o chuveiro do banheiro.",
    neighborhood: "Boa Vista",
    distanceKm: 3,
    price: 150,
  },
  {
    id: 2,
    category: "Pintor",
    urgent: false,
    createdAt: minutesAgo(30),
    title: "Pintura de quarto",
    description: "Quarto com aproximadamente 12 m². Tinta será fornecida.",
    neighborhood: "Centro",
    distanceKm: 2,
    price: 250,
  },
];

export const mockServices: Service[] = [
  {
    id: 1,
    date: "2026-10-14",
    type: "Procuro",
    status: "Ativo",
    title: "Montagem de guarda-roupa 6 portas",
    description: "Publicado hoje · Montador",
    price: "R$ 200",
    category: "wrench",
    action: "Sem propostas",
  },
  {
    id: 2,
    date: "2026-10-13",
    type: "Procuro",
    status: "Ativo",
    title: "Pintura de quarto 3x4 m",
    description: "Publicado ontem · Pintor",
    payment: "A combinar",
    category: "paint",
    proposals: 3,
    action: "Propostas",
  },
  {
    id: 3,
    date: "2026-10-10",
    type: "Ofereço",
    status: "Ativo",
    title: "Eletricista residencial e instalações",
    description: "Publicado em 10 out · Eletricista",
    price: "R$ 80",
    payment: "/ visita",
    category: "zap",
    proposals: 2,
    action: "Solicitações",
  },
  {
    id: 4,
    date: "2026-10-02",
    type: "Contratei",
    status: "Concluído",
    title: "Conserto de vazamento na pia",
    description: "com João R. · 02 out · Encanador",
    price: "R$ 70",
    category: "drop",
    rating: true,
  },
  {
    id: 5,
    date: "2026-09-15",
    type: "Contratei",
    status: "Concluído",
    title: "Limpeza completa de apartamento",
    description: "com Ana Paula · 15 set · Diarista",
    price: "R$ 120",
    category: "sparkles",
    rating: true,
  },
  {
    id: 6,
    date: "2026-09-08",
    type: "Contratei",
    status: "Cancelado",
    title: "Pintura de sala",
    description: "com Pedro A. · 08 set · Pintor",
    price: "R$ 350",
    category: "paint",
  },
];
