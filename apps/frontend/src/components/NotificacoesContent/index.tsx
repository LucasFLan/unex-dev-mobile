"use client";

import { useEffect, useRef } from "react";
import Link from "next/link";
import {
  Bell,
  CheckCircle2,
  ChevronRight,
  HandCoins,
  MinusCircle,
  XCircle,
  type LucideIcon,
} from "lucide-react";
import { markAllNotificationsRead } from "@/app/notificacoes/actions";
import { BackButton } from "@/components/BackButton";
import { BottomNav } from "@/components/BottomNav";
import { RelativeTime } from "@/components/RelativeTime";
import type { NotificationItem } from "@/lib/notifications";

const TYPE_STYLE: Record<string, { icon: LucideIcon; tone: string }> = {
  PROPOSTA_RECEBIDA: { icon: HandCoins, tone: "bg-blue-100 text-blue-600" },
  PROPOSTA_ACEITA: { icon: CheckCircle2, tone: "bg-emerald-100 text-emerald-600" },
  PROPOSTA_RECUSADA: { icon: XCircle, tone: "bg-red-100 text-red-500" },
  PROPOSTA_NAO_SELECIONADA: { icon: MinusCircle, tone: "bg-slate-100 text-slate-500" },
};

/**
 * Quem recebeu a proposta vai ao anúncio (lá aceita ou recusa). Quem enviou vai às suas propostas:
 * o pedido aceito fica pausado e não abre mais para outras pessoas.
 */
function hrefFor(notification: NotificationItem) {
  if (notification.tipo === "PROPOSTA_RECEBIDA") {
    return notification.anuncioId ? `/anuncios/${notification.anuncioId}` : null;
  }
  return "/historico?aba=propostas";
}

function NotificationCard({ notification }: { notification: NotificationItem }) {
  const { icon: Icon, tone } =
    TYPE_STYLE[notification.tipo] ?? TYPE_STYLE.PROPOSTA_RECEBIDA;
  const href = hrefFor(notification);

  const content = (
    <>
      <span
        className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-full ${tone}`}
        aria-hidden="true"
      >
        <Icon size={17} />
      </span>

      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-1.5">
          <p className="truncate text-xs font-semibold">{notification.titulo}</p>
          {!notification.lida && (
            <span className="rounded-full bg-blue-600 px-1.5 py-0.5 text-[9px] font-semibold text-white">
              Nova
            </span>
          )}
        </div>
        <p className="mt-0.5 text-[11px] leading-4 text-slate-600">
          {notification.mensagem}
        </p>
        <p className="mt-1 text-[10px] text-slate-400">
          <RelativeTime isoDate={notification.criadoEm} />
        </p>
      </div>

      {href && (
        <ChevronRight
          size={15}
          className="mt-2 shrink-0 text-slate-400"
          aria-hidden="true"
        />
      )}
    </>
  );

  const className = `flex gap-2.5 rounded-xl border p-3 shadow-sm ${
    notification.lida
      ? "border-slate-200 bg-white"
      : "border-blue-200 bg-blue-50/60"
  }`;

  return href ? (
    <Link href={href} className={className}>
      {content}
    </Link>
  ) : (
    <div className={className}>{content}</div>
  );
}

export function NotificacoesContent({
  notifications,
}: {
  notifications: NotificationItem[];
}) {
  const hasUnread = notifications.some((notification) => !notification.lida);
  const marked = useRef(false);

  // Entrar na tela conta como "ver" as notificações: marca todas como lidas.
  // O destaque de "Nova" continua até a próxima visita.
  useEffect(() => {
    if (!hasUnread || marked.current) return;
    marked.current = true;
    void markAllNotificationsRead();
  }, [hasUnread]);

  return (
    <main className="min-h-dvh bg-fundo pb-24 text-slate-800">
      <div className="mx-auto w-full max-w-lg">
        <header className="sticky top-0 z-20 flex items-center gap-1 bg-fundo px-2 pb-2 pt-3">
          <BackButton />
          <h1 className="text-[17px] font-bold tracking-tight">Notificações</h1>
        </header>

        <section className="space-y-2 px-3.5">
          {notifications.length === 0 ? (
            <div className="rounded-xl border border-slate-200 bg-white px-4 py-10 text-center">
              <Bell
                size={25}
                className="mx-auto mb-2 text-slate-300"
                aria-hidden="true"
              />
              <p className="text-xs font-semibold text-slate-600">
                Nenhuma notificação por aqui
              </p>
              <p className="mt-1 text-[11px] text-slate-500">
                Quando alguém enviar uma proposta para seus anúncios, ela
                aparece aqui.
              </p>
            </div>
          ) : (
            <ul className="space-y-2">
              {notifications.map((notification) => (
                <li key={notification.id}>
                  <NotificationCard notification={notification} />
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>

      <BottomNav />
    </main>
  );
}
