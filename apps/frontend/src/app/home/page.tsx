import { Suspense } from "react";
import { HomeContent } from "@/components/Home/HomeContent";
import { getCurrentUser } from "@/lib/user";
import { getAnuncios } from "@/lib/ads";

async function HomeLoader() {
  const [user, anuncios] = await Promise.all([
    getCurrentUser(),
    getAnuncios(),
  ]);

  return <HomeContent 
      userName={user.name} 
      anuncios={anuncios}
    />;
}

export default async function HomePage() {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-white" />}>
      <HomeLoader/>
    </Suspense>
  );
}