import type { AnuncioItem } from "@/lib/ads";
import {
  getPropostasEnviadas,
  getPropostasRecebidas,
  type ProposalItem,
} from "@/lib/proposals";

/**
 * Propostas que o usuário logado precisa ver no detalhe de um anúncio: o autor vê as recebidas;
 * as outras pessoas veem a última que elas mesmas enviaram (se houver).
 */
export async function getAdProposals(ad: AnuncioItem, userId: string) {
  const isOwner = ad.autorId === userId;

  if (isOwner) {
    const received = await getPropostasRecebidas();
    return {
      isOwner,
      received: received.filter((p) => p.anuncio?.id === ad.id),
      mine: null as ProposalItem | null,
    };
  }

  const sent = await getPropostasEnviadas();
  return {
    isOwner,
    received: [] as ProposalItem[],
    // Depois de encerrada, a pessoa pode propor de novo: vale a mais recente.
    mine:
      sent
        .filter((p) => p.anuncio?.id === ad.id)
        .sort((a, b) => b.criadoEm.localeCompare(a.criadoEm))[0] ?? null,
  };
}
