import { NextResponse, type NextRequest } from "next/server";
import { apiFetchBinary } from "@/lib/api";
import { ENDPOINTS } from "@/lib/api-contract";
import { isUuid } from "@/lib/avatar";

/**
 * Repassa uma foto anexada a uma proposta. A API só entrega para quem enviou ou recebeu a proposta; o token
 * fica num cookie httpOnly, por isso a imagem passa pelo servidor do Next.
 */
export async function GET(
  _request: NextRequest,
  { params }: { params: Promise<{ id: string; fotoId: string }> }
) {
  const { id, fotoId } = await params;
  if (!isUuid(id) || !isUuid(fotoId)) return new NextResponse(null, { status: 404 });

  let response: Response;
  try {
    response = await apiFetchBinary(ENDPOINTS.proposalPhoto(id, fotoId));
  } catch {
    return new NextResponse(null, { status: 502 });
  }
  if (!response.ok) return new NextResponse(null, { status: response.status });

  return new NextResponse(response.body, {
    headers: {
      "Content-Type": response.headers.get("Content-Type") ?? "application/octet-stream",
      // A foto de um id nunca muda; "private" porque só os envolvidos podem vê-la.
      "Cache-Control": "private, max-age=604800, immutable",
      "X-Content-Type-Options": "nosniff",
    },
  });
}
