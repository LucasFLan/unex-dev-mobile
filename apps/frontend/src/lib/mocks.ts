import type {
  Professional,
  ProfessionalDetail,
  ServiceRequest,
  ServiceRequestDetail,
} from "@/lib/marketplace";
import type { Review, UserProfile } from "@/lib/perfil";
import { Service } from "./services";

const minutesAgo = (minutes: number) =>
  new Date(Date.now() - minutes * 60_000).toISOString();

const daysAgo = (days: number) => minutesAgo(days * 24 * 60);

export const mockProfile: UserProfile = {
  id: 1,
  name: "Lucas",
  email: "lucas@email.com",
  phone: "75999990000",
  cpf: "12345678909",
  birthDate: "1996-04-18",
  neighborhood: "Centro",
  bio: "Eletricista e montador de móveis há 8 anos. Atendo residências e pequenos comércios, com orçamento sem compromisso e garantia do serviço.",
  verified: true,
  memberSince: "2026-03-12",
  rating: 4.9,
  reviewsCount: 12,
  completedServices: 27,
  categories: ["Eletricista", "Montador"],
};

export const mockReviews: Review[] = [
  {
    id: 1,
    authorName: "Marina S.",
    rating: 5,
    comment:
      "Pontual e muito caprichoso. Instalou as tomadas da cozinha e deixou tudo limpo.",
    category: "Eletricista",
    createdAt: "2026-10-20T14:30:00Z",
  },
  {
    id: 2,
    authorName: "Roberto C.",
    rating: 4,
    comment: "Montou o guarda-roupa rápido, só atrasou um pouco na chegada.",
    category: "Montador",
    createdAt: "2026-10-05T09:10:00Z",
  },
];


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
      "Preciso de um eletricista para instalar 3 tomadas novas na cozinha (bancada da pia) e trocar o chuveiro do banheiro, que queimou. Já comprei o chuveiro novo (220V). A fiação está aparente em um dos pontos. Disponível para receber o profissional hoje à tarde ou amanhã pela manhã.",
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

const requestExtras: Record<
  number,
  Omit<ServiceRequestDetail, keyof ServiceRequest>
> = {
  1: {
    client: {
      name: "Mariana Lopes",
      rating: 4.8,
      reviewsCount: 14,
      verified: true,
      memberSince: "2024-03-10",
      requestsCount: 5,
    },
    proposalsCount: 2,
    photos: ["/chuveiro.jpg", "/chuveiro.jpg", "/chuveiro.jpg"],
    preferredDate: "2026-11-12",
    requirements: [
      "Três tomadas na cozinha",
      "Troca do chuveiro no banheiro social",
      "O chuveiro novo já está comprado",
    ],
  },
  2: {
    client: {
      name: "Paulo H.",
      rating: null,
      reviewsCount: 0,
      verified: false,
      memberSince: "2026-11-02",
      requestsCount: 1,
    },
    proposalsCount: 0,
    photos: [],
    preferredDate: null,
    requirements: [
      "Quarto de 12 m², paredes e teto",
      "Tinta fornecida pelo cliente",
      "Móveis serão retirados antes",
    ],
  },
};

export function getMockRequest(id: number): ServiceRequestDetail | undefined {
  const base = mockRequests.find((request) => request.id === id);
  const extra = requestExtras[id];

  return base && extra ? { ...base, ...extra } : undefined;
}

const professionalExtras: Record<
  number,
  Omit<ProfessionalDetail, keyof Professional>
> = {
  1: {
    description:
      "Instalação, troca e reparo de tomadas, interruptores, chuveiros e luminárias em casas e apartamentos. O atendimento inclui avaliação no local, orçamento sem compromisso e garantia de 90 dias. O material pode ser incluso ou fornecido por você.",
    priceUnit: "visita",
    serviceRadiusKm: 10,
    verified: true,
    memberSince: "2025-08-04",
    completedServices: 64,
    availability: "Seg a sáb, 8h às 18h",
    services: [
      { name: "Visita técnica e orçamento", price: 80, unit: "visita" },
      { name: "Instalação de tomada ou interruptor", price: 35, unit: "unidade" },
      { name: "Instalação de chuveiro elétrico", price: 90, unit: null },
      { name: "Troca de disjuntor", price: 60, unit: "unidade" },
    ],
    reviews: [
      {
        id: 1,
        authorName: "Marina S.",
        rating: 5,
        comment:
          "Pontual e muito caprichoso. Instalou as tomadas da cozinha e deixou tudo limpo.",
        category: "Eletricista",
        createdAt: "2026-10-20T14:30:00Z",
      },
      {
        id: 2,
        authorName: "Roberto C.",
        rating: 5,
        comment: "Resolveu o problema do quadro de luz no mesmo dia.",
        category: "Eletricista",
        createdAt: "2026-10-11T09:10:00Z",
      },
      {
        id: 3,
        authorName: "Helena T.",
        rating: 4,
        comment: "Bom serviço e preço justo. Só demorou um pouco para chegar.",
        category: "Eletricista",
        createdAt: "2026-09-28T16:45:00Z",
      },
    ],
  },
  2: {
    description:
      "Limpeza completa de casas e apartamentos, com rotina semanal, quinzenal ou faxina avulsa. Levo meus próprios produtos de limpeza, se você preferir.",
    priceUnit: null,
    serviceRadiusKm: 8,
    verified: true,
    memberSince: "2025-11-18",
    completedServices: 41,
    availability: "Seg a sex, 7h às 16h",
    services: [
      { name: "Limpeza completa (até 60 m²)", price: 120, unit: null },
      { name: "Limpeza pós-obra", price: 250, unit: null },
      { name: "Passar roupa", price: 20, unit: "hora" },
    ],
    reviews: [
      {
        id: 1,
        authorName: "Camila R.",
        rating: 5,
        comment: "Deixou o apartamento impecável. Super recomendo!",
        category: "Diarista",
        createdAt: "2026-10-26T11:00:00Z",
      },
      {
        id: 2,
        authorName: "Diego P.",
        rating: 5,
        comment: "Muito atenciosa e organizada.",
        category: "Diarista",
        createdAt: "2026-10-09T15:20:00Z",
      },
    ],
  },
  3: {
    description:
      "Reparos hidráulicos e instalações: vazamentos, entupimentos, troca de torneiras e registros. Atendimento rápido, com diagnóstico no local e orçamento antes de começar.",
    priceUnit: "visita",
    serviceRadiusKm: 15,
    verified: false,
    memberSince: "2026-02-09",
    completedServices: 30,
    availability: "Todos os dias, inclusive plantão",
    services: [
      { name: "Visita e diagnóstico", price: 90, unit: "visita" },
      { name: "Desentupimento de pia ou ralo", price: 110, unit: null },
      { name: "Conserto de vazamento", price: 130, unit: null },
    ],
    reviews: [],
  },
};

export function getMockProfessional(
  id: number
): ProfessionalDetail | undefined {
  const base = mockProfessionals.find((professional) => professional.id === id);
  const extra = professionalExtras[id];

  return base && extra ? { ...base, ...extra } : undefined;
}

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