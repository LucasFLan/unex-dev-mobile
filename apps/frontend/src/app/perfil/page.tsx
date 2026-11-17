import { Suspense } from "react";
import Link from "next/link";
import {
  ArrowLeft,
  BadgeCheck,
  Mail,
  MapPin,
  Pencil,
  Phone,
  Star,
} from "lucide-react";
import { Block } from "@/components/Block";
import { InfoRow } from "@/components/InfoRow";
import { ReviewCard } from "@/components/ReviewCard";
import { CATEGORY_META } from "@/lib/categories";
import { formatPhone } from "@/lib/format";
import { initials } from "@/lib/marketplace";
import { formatMonth, formatRating } from "@/lib/format";
import { mockProfile, mockReviews } from "@/lib/mocks";
import { getCurrentUser } from "@/lib/user";

async function PerfilContent() {
  const user = await getCurrentUser();

  const profile = {
    ...mockProfile,
    name: user.name,
    email: user.email,
    phone: user.phone,
    cpf: user.cpf,
    birthDate: user.birthDate,
  };
  const reviews = mockReviews;

  const stats = [
    {
      label: "Avaliação",
      value: (
        <span className="flex items-center justify-center gap-1">
          <Star
            size={13}
            className="fill-amber-400 text-amber-400"
            aria-hidden="true"
          />
          {formatRating(profile.rating)}
        </span>
      ),
    },
    { label: "Avaliações", value: profile.reviewsCount },
    { label: "Serviços concluídos", value: profile.completedServices },
  ];

  return (
    <main className="min-h-dvh bg-fundo pb-10 text-slate-800">
      <div className="mx-auto w-full max-w-lg">
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
            <span
              className="flex h-20 w-20 items-center justify-center rounded-full bg-blue-100 text-2xl font-bold text-blue-600"
              aria-hidden="true"
            >
              {initials(profile.name)}
            </span>

            <div className="mt-3 flex items-center gap-1">
              <h2 className="text-base font-bold">{profile.name}</h2>
              {profile.verified && (
                <BadgeCheck
                  size={16}
                  className="text-blue-600"
                  aria-label="Perfil verificado"
                />
              )}
            </div>

            <p className="mt-0.5 text-[11px] text-slate-500">
              Membro desde {formatMonth(profile.memberSince)}
            </p>

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

          {profile.bio && (
            <Block title="Sobre">
              <p className="rounded-xl border border-slate-200 bg-white p-3 text-[11px] leading-4 text-slate-600 shadow-sm">
                {profile.bio}
              </p>
            </Block>
          )}

          <Block title="Dados de contato">
            <ul className="divide-y divide-slate-100 overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
              <InfoRow icon={Mail} label="E-mail" value={profile.email} />
              <InfoRow
                icon={Phone}
                label="Telefone"
                value={formatPhone(profile.phone)}
              />
              <InfoRow
                icon={MapPin}
                label="Bairro"
                value={profile.neighborhood}
              />
            </ul>
          </Block>

          {profile.categories.length > 0 && (
            <Block title="Serviços que ofereço">
              <ul className="flex flex-wrap gap-2">
                {profile.categories.map((category) => {
                  const { icon: Icon, bg, text } = CATEGORY_META[category];

                  return (
                    <li
                      key={category}
                      className="flex items-center gap-2 rounded-full border border-slate-200 bg-white py-1 pl-1 pr-3 text-[11px] font-medium text-slate-700 shadow-sm"
                    >
                      <span
                        className={`flex h-6 w-6 items-center justify-center rounded-full ${bg} ${text}`}
                      >
                        <Icon size={13} strokeWidth={1.8} aria-hidden="true" />
                      </span>
                      {category}
                    </li>
                  );
                })}
              </ul>
            </Block>
          )}

          <Block title="Avaliações recebidas">
            {reviews.length > 0 ? (
              <ul className="space-y-2">
                {reviews.map((review) => (
                  <li key={review.id}>
                    <ReviewCard review={review} />
                  </li>
                ))}
              </ul>
            ) : (
              <p className="rounded-xl border border-dashed border-slate-200 px-4 py-6 text-center text-xs text-slate-500">
                Você ainda não recebeu avaliações.
              </p>
            )}
          </Block>
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