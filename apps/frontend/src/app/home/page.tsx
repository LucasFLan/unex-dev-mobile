import { Suspense } from "react";
import { HomeContent } from "@/components/Home/HomeContent";
import { getCurrentUser } from "@/lib/user";

async function HomeLoader() {
  const user = await getCurrentUser();

  return <HomeContent userName={user.name} />;
}

export default function HomePage() {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-white" />}>
      <HomeLoader />
    </Suspense>
  );
}