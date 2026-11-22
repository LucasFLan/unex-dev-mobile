import { NextResponse, type NextRequest } from "next/server";
import { apiFetchBinary } from "@/lib/api";
import { VALIDATION_ENDPOINTS } from "@/lib/validation";

/**
 * Repassa a imagem do envio (documento ou selfie) da API para o navegador do admin. A API exige o token, que
 * fica num cookie httpOnly: por isso a imagem passa pelo servidor do Next. A API já recusa quem não é ADMIN.
 */
export async function GET(
  _request: NextRequest,
  { params }: { params: Promise<{ id: string; tipo: string }> }
) {
  const { id, tipo } = await params;
  const arquivo = tipo.toUpperCase();
  if (arquivo !== "DOCUMENTO" && arquivo !== "SELFIE") {
    return new NextResponse(null, { status: 404 });
  }

  let response: Response;
  try {
    response = await apiFetchBinary(VALIDATION_ENDPOINTS.file(id, arquivo));
  } catch {
    return new NextResponse(null, { status: 502 });
  }
  if (!response.ok) {
    return new NextResponse(null, { status: response.status });
  }

  return new NextResponse(response.body, {
    headers: {
      "Content-Type": response.headers.get("Content-Type") ?? "application/octet-stream",
      // Documento pessoal: nada de cache no navegador ou em proxies.
      "Cache-Control": "no-store",
      "X-Content-Type-Options": "nosniff",
    },
  });
}
