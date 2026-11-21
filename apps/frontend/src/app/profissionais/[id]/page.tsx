import { Suspense } from "react";
import { notFound } from "next/navigation";
import { ProfessionalDetails } from "@/components/ProfessionalDetails";
import { getAnuncio } from "@/lib/ads";
import { getAdProposals } from "@/lib/ad-proposals";
import { getCurrentUser } from "@/lib/user";

async function ProfissionalDetalheContent({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const [ad, user] = await Promise.all([getAnuncio(id), getCurrentUser()]);

  if (!ad) notFound();

  const { isOwner, received, mine } = await getAdProposals(ad, user.id);

  return (
    <ProfessionalDetails
      ad={ad}
      isOwner={isOwner}
      proposals={received}
      myProposal={mine}
    />
  );
}

export default function ProfissionalDetalhePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-white" />}>
      <ProfissionalDetalheContent params={params} />
    </Suspense>
  );
}
