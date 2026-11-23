import { NextResponse, type NextRequest } from "next/server";
import { apiFetchBinary } from "@/lib/api";

const UUID = "[0-9a-fA-F-]{36}";

/** Só estes caminhos da API passam pelo proxy: foto de perfil e foto de anúncio. */
const ALLOWED = [
  new RegExp(`^usuarios/${UUID}/foto$`),
  new RegExp(`^anuncios/${UUID}/fotos/${UUID}$`),
];

/**
 * Repassa as fotos da API para o navegador. A API fica na rede interna do Docker; o token (cookie httpOnly) vai
 * junto para o autor conseguir ver as fotos do próprio anúncio pausado.
 */
export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ path: string[] }> }
) {
  const path = (await params).path.join("/");
  if (!ALLOWED.some((pattern) => pattern.test(path))) {
    return new NextResponse(null, { status: 404 });
  }

  let response: Response;
  try {
    response = await apiFetchBinary(`/${path}${request.nextUrl.search}`);
  } catch {
    return new NextResponse(null, { status: 502 });
  }
  if (!response.ok) {
    return new NextResponse(null, { status: response.status === 404 ? 404 : 502 });
  }

  return new NextResponse(response.body, {
    headers: {
      "Content-Type": response.headers.get("Content-Type") ?? "application/octet-stream",
      // A URL muda quando a foto muda ("?v=" no perfil; id novo no anúncio): pode ficar em cache no navegador.
      "Cache-Control": response.headers.get("Cache-Control") ?? "private, max-age=3600",
      "X-Content-Type-Options": "nosniff",
    },
  });
}
