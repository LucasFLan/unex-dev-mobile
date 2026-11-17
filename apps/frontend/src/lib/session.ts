import { cookies } from "next/headers";
import { SESSION_COOKIE } from "@/lib/api-contract";

const DEFAULT_MAX_AGE_SECONDS = 60 * 60 * 24 * 7;

function maxAgeFromJwt(token: string): number {
  try {
    const payload = JSON.parse(
      Buffer.from(token.split(".")[1] ?? "", "base64url").toString("utf8")
    );

    if (typeof payload.exp === "number") {
      return Math.max(0, payload.exp - Math.floor(Date.now() / 1000));
    }
  } catch {
  }

  return DEFAULT_MAX_AGE_SECONDS;
}

export async function setSessionToken(token: string) {
  (await cookies()).set(SESSION_COOKIE, token, {
    httpOnly: true, 
    sameSite: "lax",
    secure: process.env.NODE_ENV === "production",
    path: "/",
    maxAge: maxAgeFromJwt(token),
  });
}

export async function clearSessionToken() {
  (await cookies()).delete(SESSION_COOKIE);
}