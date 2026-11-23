import { NextResponse, type NextRequest } from "next/server";
import { apiFetchBinary } from "@/lib/api";
import { ENDPOINTS } from "@/lib/api-contract";
import { isUuid } from "@/lib/avatar";

/**
 * Repassa a foto de perfil da API para o navegador. A API exige login, e o token fica num cookie httpOnly:
 * por isso a imagem passa pelo servidor do Next.
 */
export async function GET(request: NextRequest, { params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  if (!isUuid(id)) return new NextResponse(null, { status: 404 });

  let response: Response;
  try {
    response = await apiFetchBinary(ENDPOINTS.userPhoto(id));
  } catch {
    return new NextResponse(null, { status: 502 });
  }

  if (!response.ok) {
    // "Sem foto" é a resposta mais comum (cards de anúncio): guarda por um minuto para não repetir a busca.
    return new NextResponse(null, {
      status: response.status,
      headers: response.status === 404 ? { "Cache-Control": "private, max-age=60" } : {},
    });
  }

  // Com ?v= (versão da foto), a URL muda a cada troca: pode ficar em cache por muito tempo.
  const versioned = request.nextUrl.searchParams.has("v");
  return new NextResponse(response.body, {
    headers: {
      "Content-Type": response.headers.get("Content-Type") ?? "application/octet-stream",
      "Cache-Control": versioned ? "private, max-age=31536000, immutable" : "private, max-age=300",
      "X-Content-Type-Options": "nosniff",
    },
  });
}
