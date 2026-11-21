import { Suspense } from "react";
import { notFound } from "next/navigation";
import { ServiceDetails } from "@/components/ServiceDetails";
import { getAnuncio } from "@/lib/ads";
import { getAdProposals } from "@/lib/ad-proposals";
import { getCurrentUser } from "@/lib/user";

async function ServicoDetalheContent({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const [ad, user] = await Promise.all([getAnuncio(id), getCurrentUser()]);

  if (!ad) notFound();

  const { isOwner, received, mine } = await getAdProposals(ad, user.id);

  return (
    <ServiceDetails
      ad={ad}
      isOwner={isOwner}
      proposals={received}
      myProposal={mine}
    />
  );
}

export default function ServicoDetalhePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-white" />}>
      <ServicoDetalheContent params={params} />
    </Suspense>
  );
}
