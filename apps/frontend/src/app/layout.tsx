import type { Metadata, Viewport } from "next";
import { Suspense } from "react";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { AppSidebar } from "@/components/AppSidebar";
import { ServiceWorkerRegister } from "@/components/ServiceWorkerRegister";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: { default: "ChamaAí", template: "%s · ChamaAí" },
  description: "Encontre profissionais e serviços perto de você, ou anuncie os seus.",
  applicationName: "ChamaAí",
  // iPhone: abre em tela cheia quando adicionado à Tela de Início.
  appleWebApp: { capable: true, title: "ChamaAí", statusBarStyle: "default" },
  formatDetection: { telephone: false },
};

export const viewport: Viewport = {
  themeColor: "#2563EB",
  width: "device-width",
  initialScale: 1,
  viewportFit: "cover",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="pt-BR"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full flex flex-col bg-white">
        {/* usePathname() só existe em runtime: o Suspense evita bloquear o pré-render. */}
        <Suspense fallback={null}>
          <AppSidebar />
        </Suspense>
        {children}
        <ServiceWorkerRegister />
      </body>
    </html>
  );
}
