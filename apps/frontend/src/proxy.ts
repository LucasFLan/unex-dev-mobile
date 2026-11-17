import { NextResponse, type NextRequest } from "next/server";
import { SESSION_COOKIE } from "@/lib/api-contract";

const AUTH_PATHS = ["/login", "/register"];

const OPEN_PATHS = ["/termos", "/privacidade", "/recuperar-senha"];

const matches = (pathname: string, paths: string[]) =>
  paths.some((path) => pathname === path || pathname.startsWith(`${path}/`));

export function proxy(request: NextRequest) {
  const { pathname } = request.nextUrl;

  const hasSession = request.cookies.has(SESSION_COOKIE);
  const isAuthPage = matches(pathname, AUTH_PATHS);
  const isOpenPage = matches(pathname, OPEN_PATHS);

  if (!hasSession && !isAuthPage && !isOpenPage) {
    const url = request.nextUrl.clone();
    url.pathname = "/login";
    return NextResponse.redirect(url);
  }

  if (hasSession && isAuthPage) {
    const url = request.nextUrl.clone();
    url.pathname = "/home";
    return NextResponse.redirect(url);
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)"],
};