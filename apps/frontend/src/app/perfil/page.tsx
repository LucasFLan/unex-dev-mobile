import { Suspense } from "react";
import Link from "next/link";
import {
  ArrowLeft,
  BadgeCheck,
  Cake,
  Clock3,
  Mail,
  Pencil,
  Phone,
} from "lucide-react";
import { Block } from "@/components/Block";
import { InfoRow } from "@/components/InfoRow";
import { getMeusAnuncios } from "@/lib/ads";
import { getCategoryMeta } from "@/lib/categories";
import { formatPhone, formatShortDate } from "@/lib/format";
import { Avatar } from "@/components/Avatar";
import { getCurrentUser } from "@/lib/user";

async function PerfilContent() {
  const [user, anuncios] = await Promise.all([
    getCurrentUser(),
    getMeusAnuncios(),
  ]);

  const ativos = anuncios.filter((anuncio) => anuncio.ativo).length;

  const stats = [
    { label: "Anúncios", value: anuncios.length },
    { label: "Ativos", value: ativos },
    { label: "Pausados", value: anuncios.length - ativos },
  ];

  // Categorias (sem repetição) dos anúncios em que o usuário oferece serviço.
  const offeredCategories = [
    ...new Map(
      anuncios
        .filter((anuncio) => anuncio.tipo === "OFERTA")
        .map((anuncio) => [anuncio.categoria.slug, anuncio.categoria])
    ).values(),
  ];

  return (
    <main className="min-h-dvh bg-fundo pb-10 text-slate-800">
      <div className="mx-auto w-full max-w-lg lg:pt-6">
        <header className="sticky top-0 z-20 flex items-center gap-1 bg-fundo px-2 pb-2 pt-3">
          <Link
            href="/menu"
            aria-label="Voltar para o menu"
            className="flex h-10 w-10 items-center justify-center rounded-xl text-slate-700 focus-visible:outline-2 focus-visible:outline-blue-600"
          >
            <ArrowLeft size={19} aria-hidden="true" />
          </Link>
          <h1 className="text-[17px] font-bold tracking-tight">Meu perfil</h1>
        </header>

        <div className="space-y-4 px-3.5">
          {/* Identificação */}
          <section className="flex flex-col items-center rounded-xl border border-slate-200 bg-white px-4 py-5 text-center shadow-sm">
            <Avatar
              userId={user.id}
              name={user.name}
              version={user.photoVersion}
              className="h-20 w-20 text-2xl"
            />

            <div className="mt-3 flex items-center gap-1">
              <h2 className="text-base font-bold">{user.name}</h2>
              {user.validation === "APROVADO" && (
                <BadgeCheck
                  size={16}
                  className="text-blue-600"
                  aria-label="Perfil verificado"
                />
              )}
            </div>

            {user.validation !== "APROVADO" && (
              <Link
                href="/validacao?de=%2Fperfil"
                className="mt-1 flex items-center gap-1 rounded-full bg-amber-50 px-2.5 py-1 text-[10px] font-medium text-amber-700"
              >
                <Clock3 size={11} aria-hidden="true" />
                {user.validation === "REPROVADO"
                  ? "Documentos não aprovados · reenviar"
                  : "Conta não validada · validar agora"}
              </Link>
            )}

            <Link
              href="/perfil/editar"
              className="mt-3 flex h-9 items-center gap-1.5 rounded-lg border border-slate-200 px-4 text-xs font-semibold text-blue-600 active:bg-slate-50 focus-visible:outline-2 focus-visible:outline-blue-600"
            >
              <Pencil size={13} aria-hidden="true" />
              Editar perfil
            </Link>
          </section>

          <dl className="grid grid-cols-3 divide-x divide-slate-100 rounded-xl border border-slate-200 bg-white py-3 text-center shadow-sm">
            {stats.map(({ label, value }) => (
              <div key={label} className="px-2">
                <dd className="text-sm font-bold text-slate-900">{value}</dd>
                <dt className="mt-0.5 text-[10px] text-slate-500">{label}</dt>
              </div>
            ))}
          </dl>

          <Block title="Dados de contato">
            <ul className="divide-y divide-slate-100 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
              <InfoRow icon={Mail} label="E-mail" value={user.email} />
              <InfoRow
                icon={Phone}
                label="Telefone"
                value={user.phone ? formatPhone(user.phone) : "Não informado"}
              />
              {user.birthDate && (
                <InfoRow
                  icon={Cake}
                  label="Data de nascimento"
                  value={formatShortDate(user.birthDate)}
                />
              )}
            </ul>
          </Block>

          {offeredCategories.length > 0 && (
            <Block title="Serviços que ofereço">
              <ul className="flex flex-wrap gap-2">
                {offeredCategories.map((category) => {
                  const { icon: Icon, bg, text } = getCategoryMeta(
                    category.slug
                  );

                  return (
                    <li
                      key={category.id}
                      className="flex items-center gap-2 rounded-full border border-slate-200 bg-white py-1 pl-1 pr-3 text-[11px] font-medium text-slate-700 shadow-sm"
                    >
                      <span
                        className={`flex h-6 w-6 items-center justify-center rounded-full ${bg} ${text}`}
                      >
                        <Icon size={13} strokeWidth={1.8} aria-hidden="true" />
                      </span>
                      {category.nome}
                    </li>
                  );
                })}
              </ul>
            </Block>
          )}

          <Link
            href="/historico"
            className="flex h-10 items-center justify-center rounded-xl border border-slate-200 bg-white text-xs font-semibold text-blue-600 shadow-sm"
          >
            Ver meus anúncios
          </Link>
        </div>
      </div>
    </main>
  );
}

export default function PerfilPage() {
  return (
    <Suspense fallback={<main className="min-h-dvh bg-fundo" />}>
      <PerfilContent />
    </Suspense>
  );
}