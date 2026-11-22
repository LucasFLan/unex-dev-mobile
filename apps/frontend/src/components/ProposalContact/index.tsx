import { MessageCircle, Phone } from "lucide-react";
import { formatPhone } from "@/lib/format";
import type { ProposalPerson } from "@/lib/proposals";

/** Contato da outra pessoa, liberado depois que a proposta é aceita. */
export function ProposalContact({ person }: { person: ProposalPerson }) {
  if (!person.telefone) {
    return (
      <p className="text-[10px] text-slate-500">
        {person.nome} não informou telefone.
      </p>
    );
  }

  const digits = person.telefone.replace(/\D/g, "");

  return (
    <div className="flex flex-wrap items-center gap-1.5">
      <span className="text-[10px] font-medium text-slate-700">
        {person.nome}: {formatPhone(person.telefone)}
      </span>
      <a
        href={`tel:${digits}`}
        className="flex h-7 items-center gap-1 rounded-md border border-slate-200 px-2 text-[10px] font-medium text-slate-700"
      >
        <Phone size={11} aria-hidden="true" />
        Ligar
      </a>
      <a
        href={`https://wa.me/55${digits}`}
        target="_blank"
        rel="noopener noreferrer"
        className="flex h-7 items-center gap-1 rounded-md bg-emerald-600 px-2 text-[10px] font-medium text-white"
      >
        <MessageCircle size={11} aria-hidden="true" />
        WhatsApp
      </a>
    </div>
  );
}
