/**
 * Avaliação recebida por um usuário.
 * Ainda não existe endpoint de avaliações no back; o tipo fica pronto para
 * quando existir (usado por ReviewCard).
 */
export type Review = {
  id: number;
  authorName: string;
  rating: number;
  comment: string;
  /** Nome da categoria do serviço avaliado. */
  category: string;
  createdAt: string;
};
