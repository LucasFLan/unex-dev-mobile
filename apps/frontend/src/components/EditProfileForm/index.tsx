"use client";

import { useActionState, useState, type ChangeEvent } from "react";
import Link from "next/link";
import { CalendarDays, ContactRound, Mail, Phone, UserRound } from "lucide-react";
import { updateProfile, type ProfileFormState } from "@/app/perfil/editar/actions";
import { BackButton } from "@/components/BackButton";
import Button from "@/components/Button";
import { InfoRow } from "@/components/InfoRow";
import Input from "@/components/Input";
import { maskDate, maskPhone } from "@/lib/masks";
import { initials } from "@/lib/marketplace";

export type EditProfileInitial = {
  nome: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  cpf: string;
};

export function EditProfileForm({ initial }: { initial: EditProfileInitial }) {
  const [state, formAction, pending] = useActionState<
    ProfileFormState,
    FormData
  >(updateProfile, null);

  const [form, setForm] = useState({
    nome: initial.nome,
    dataNascimento: initial.dataNascimento,
    telefone: initial.telefone,
  });

  const update =
    (key: keyof typeof form, mask?: (value: string) => string) =>
    (event: ChangeEvent<HTMLInputElement>) => {
      const value = mask ? mask(event.target.value) : event.target.value;
      setForm((current) => ({ ...current, [key]: value }));
    };

  return (
    <main className="min-h-screen w-full bg-white px-6 pb-8 pt-6 text-slate-900">
      <div className="mx-auto flex w-full max-w-md flex-col">
        <BackButton
          fallbackHref="/perfil"
          iconSize={19}
          className="mb-4 flex h-9 w-9 items-center justify-center rounded-xl border border-slate-200 text-slate-800 transition hover:bg-slate-50"
        />

        <header className="mb-5 flex items-center gap-3">
          <span
            className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full bg-blue-100 text-lg font-bold text-blue-600"
            aria-hidden="true"
          >
            {initials(form.nome || initial.nome)}
          </span>

          <div>
            <h1 className="text-2xl font-bold tracking-tight">
              Editar perfil
            </h1>
            <p className="mt-0.5 text-sm leading-5 text-slate-500">
              Atualize seus dados pessoais.
            </p>
          </div>
        </header>

        <form action={formAction}>
          <input type="hidden" name="email" value={initial.email} />
          <input type="hidden" name="cpf" value={initial.cpf} />

          <fieldset disabled={pending} className="flex min-w-0 flex-col gap-3">
            {state?.error && (
              <p
                role="alert"
                className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600"
              >
                {state.error}
              </p>
            )}

            <Input
              labelText="Nome completo"
              placeholder="Digite seu nome completo"
              type="text"
              icon={<UserRound size={17} />}
              name="nome"
              autoComplete="name"
              value={form.nome}
              onChange={update("nome")}
            />

            <Input
              labelText="Data de nascimento"
              placeholder="DD/MM/AAAA"
              type="text"
              icon={<CalendarDays size={17} />}
              name="dataNascimento"
              inputMode="numeric"
              autoComplete="bday"
              maxLength={10}
              value={form.dataNascimento}
              onChange={update("dataNascimento", maskDate)}
            />

            <Input
              labelText="Telefone"
              placeholder="(00) 90000-0000"
              type="tel"
              icon={<Phone size={17} />}
              name="telefone"
              autoComplete="tel"
              maxLength={15}
              value={form.telefone}
              onChange={update("telefone", maskPhone)}
            />

            <div className="mt-1">
              <p className="mb-1.5 text-sm font-medium text-gray-700">
                Dados que não podem ser alterados
              </p>

              <ul className="divide-y divide-slate-100 overflow-hidden rounded-[10px] border border-slate-200 bg-slate-50">
                <InfoRow icon={Mail} label="E-mail" value={initial.email} />
                <InfoRow icon={ContactRound} label="CPF" value={initial.cpf} />
              </ul>
            </div>

            <div className="mt-2 flex flex-col gap-2">
              <Button
                text={pending ? "Salvando..." : "Salvar alterações"}
                type="submit"
              />

              <Link
                href="/perfil"
                className="flex h-9.75 items-center justify-center rounded-[10px] text-sm font-medium text-slate-600 hover:bg-slate-50"
              >
                Cancelar
              </Link>
            </div>
          </fieldset>
        </form>
      </div>
    </main>
  );
}