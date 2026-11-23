import Link from "next/link";
import { LockKeyhole, MapPin } from "lucide-react";
import { Avatar } from "@/components/Avatar";
import { LocationMap } from "@/components/LocationMap";
import { RatingBadge } from "@/components/Stars";
import type { AnuncioItem } from "@/lib/ads";
import { withFrom } from "@/lib/navigation";

const regionName = (ad: AnuncioItem) =>
  [ad.bairro, `${ad.cidade}, ${ad.uf}`].filter(Boolean).join(" · ");

/**
 * Região do anúncio no mapa. Num pedido, a área aproximada do serviço (o endereço só depois do aceite); numa
 * oferta, até onde o profissional atende. Anúncios antigos, sem CEP, mostram só bairro/cidade.
 */
export function AdLocation({ ad, isOwner }: { ad: AnuncioItem; isOwner: boolean }) {
  const offering = ad.tipo === "OFERTA";
  const region = regionName(ad);
  const radius = offering ? ad.raioAtendimentoKm : null;

  return (
    <section className="mt-4">
      <h2 className="text-[11px] font-bold lg:text-base">{offering ? "Região de atendimento" : "Localização aproximada"}</h2>

      {ad.regiao ? (
        <LocationMap
          center={ad.regiao}
          radiusKm={radius}
          label={`Mapa da região: ${region}`}
          className="mt-2 h-36 lg:h-72"
        />
      ) : (
        <div className="mt-2 flex h-14 items-center justify-center gap-1.5 rounded-xl bg-slate-50 text-[10px] text-slate-500 lg:h-20 lg:text-sm">
          <MapPin size={13} aria-hidden="true" />
          {region}
        </div>
      )}

      <div className="mt-2 flex items-start gap-1 text-[9px] leading-3 text-slate-500 lg:text-sm lg:leading-5">
        {offering ? (
          <MapPin size={11} className="mt-0.5 shrink-0" aria-hidden="true" />
        ) : (
          <LockKeyhole size={11} className="mt-0.5 shrink-0" aria-hidden="true" />
        )}
        <p>
          {offering
            ? `${region}${radius ? ` · atende até ${radius} km` : " · atende a cidade toda"}`
            : isOwner && ad.endereco
              ? `Seu endereço (${[ad.endereco.logradouro, ad.endereco.numero].filter(Boolean).join(", ") || ad.endereco.cep}) só aparece para quem você aceitar.`
              : `Região de ${region}. O endereço exato é liberado quando a proposta é aceita.`}
        </p>
      </div>
    </section>
  );
}

/** "Anunciado por" com foto, nome e média; leva ao perfil com as avaliações. */
export function AdAuthor({ ad, isOwner, from }: { ad: AnuncioItem; isOwner: boolean; from: string }) {
  const author = ad.autor;
  const name = author?.nome || "Anunciante";

  const content = (
    <>
      <Avatar userId={ad.autorId} name={name} version={author ? author.fotoAtualizadaEm : undefined} className="h-7 w-7 text-[9px] lg:h-11 lg:w-11 lg:text-sm" />
      <span className="min-w-0">
        <span className="block text-[8px] text-slate-500 lg:text-xs">Anunciado por</span>
        <span className="flex items-center gap-1.5">
          <strong className="truncate text-[11px] text-slate-800 lg:text-base">{isOwner ? "você" : name}</strong>
          {author && <RatingBadge media={author.media} total={author.total} className="text-[9px] lg:text-xs" />}
        </span>
      </span>
    </>
  );

  return isOwner ? (
    <div className="mt-2 flex items-center gap-2 lg:mt-4 lg:gap-3">{content}</div>
  ) : (
    <Link
      href={withFrom(`/usuarios/${ad.autorId}`, from)}
      className="mt-2 flex items-center gap-2 rounded-lg lg:mt-4 lg:gap-3 focus-visible:outline-2 focus-visible:outline-blue-600"
    >
      {content}
      <span className="ml-auto text-[9px] font-medium text-blue-600 lg:text-xs">Ver avaliações</span>
    </Link>
  );
}
