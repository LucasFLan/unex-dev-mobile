import { Suspense } from "react";
import { notFound } from "next/navigation";
import { ProfessionalDetails } from "@/components/ProfessionalDetails";
import { getAnuncio } from "@/lib/ads";
import { getAdProposals } from "@/lib/ad-proposals";
import { backHrefFrom, FROM_PARAM } from "@/lib/navigation";

type SearchParams = Promise<Record<string, string | string[] | undefined>>;
import { getCurrentUser } from "@/lib/user";

async function ProfissionalDetalheContent({
  params,
  searchParams,
}: {
  params: Promise<{ id: string }>;
  searchParams: SearchParams;
}) {
  const { id } = await params;
  const backHref = backHrefFrom((await searchParams)[FROM_PARAM], "/home");
  const [ad, user] = await Promise.all([getAnuncio(id), getCurrentUser()]);

  if (!ad) notFound();

  const { isOwner, received, mine } = await getAdProposals(ad, user.id);

  return (
    <ProfessionalDetails
      ad={ad}
      isOwner={isOwner}
      proposals={received}
      myProposal={mine}
      backHref={backHref}
      canPropose={user.validation === "APROVADO"}
    />
  );
}

export default function ProfissionalDetalhePage({
  params,
  searchParams,
}: {
  params: Promise<{ id: string }>;
  searchParams: SearchParams;
}) {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-white" />}>
      <ProfissionalDetalheContent params={params} searchParams={searchParams} />
    </Suspense>
  );
}
