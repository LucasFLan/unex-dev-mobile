export const SERVICE_TYPES = ["Procuro", "Ofereço", "Contratei"] as const;
export const SERVICE_STATUSES = [
  "Ativo",
  "Pausado",
  "Concluído",
  "Cancelado",
] as const;

export type ServiceType = (typeof SERVICE_TYPES)[number];
export type ServiceStatus = (typeof SERVICE_STATUSES)[number];
export type ServiceCategory = "wrench" | "paint" | "zap" | "drop" | "sparkles";

export type Service = {
  id: number;
  date: string;
  type: ServiceType;
  status: ServiceStatus;
  title: string;
  description: string;
  price?: string;
  payment?: string;
  category: ServiceCategory;
  proposals?: number;
  action?: string;
  rating?: boolean;
};
