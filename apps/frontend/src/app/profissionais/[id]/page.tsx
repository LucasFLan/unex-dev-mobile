import { notFound } from "next/navigation";
import { ProfessionalDetails } from "@/components/ProfessionalDetails";
import { getMockProfessional } from "@/lib/mocks";

export default async function ProfissionalDetalhePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;

  const professional = getMockProfessional(Number(id));
  if (!professional) notFound();

  return <ProfessionalDetails professional={professional} />;
}