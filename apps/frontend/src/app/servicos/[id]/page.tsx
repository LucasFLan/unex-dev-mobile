import { notFound } from "next/navigation";
import { ServiceDetails } from "@/components/ServiceDetails";
import { getMockRequest } from "@/lib/mocks";

export default async function ServicoDetalhePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;

  const request = getMockRequest(Number(id));
  if (!request) notFound();

  return <ServiceDetails request={request} />;
}