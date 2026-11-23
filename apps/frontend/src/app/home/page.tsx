import { Suspense } from "react";
import { HomeContent } from "@/components/Home/HomeContent";
import { getCurrentUser } from "@/lib/user";
import { getAnuncios } from "@/lib/ads";
import { getCategories } from "@/lib/categories-api";
import { getNaoLidas } from "@/lib/notifications";

async function HomeLoader() {
  const [user, anuncios, categories, unreadNotifications] = await Promise.all([
    getCurrentUser(),
    getAnuncios(),
    getCategories(),
    getNaoLidas(),
  ]);

  return (
    <HomeContent
      userName={user.name}
      anuncios={anuncios}
      categories={categories}
      unreadNotifications={unreadNotifications}
      validation={user.validation}
    />
  );
}

export default async function HomePage() {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-white" />}>
      <HomeLoader/>
    </Suspense>
  );
}