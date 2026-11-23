"use client";

import { useActionState } from "react";
import Link from "next/link";
import {
  changePassword,
  type PasswordFormState,
} from "@/app/seguranca/senha/actions";
import { BackButton } from "@/components/BackButton";
import Button from "@/components/Button";
import { PasswordField } from "@/components/seguranca/PasswordField";

export function ChangePasswordForm() {
  const [state, formAction, pending] = useActionState<
    PasswordFormState,
    FormData
  >(changePassword, null);

  return (
    <main className="min-h-screen w-full bg-white px-6 pb-8 pt-6 text-slate-900 md:bg-fundo md:py-12">
      <div className="mx-auto flex w-full max-w-md flex-col md:rounded-2xl md:border md:border-slate-200 md:bg-white md:p-8 md:shadow-sm">
        <BackButton
          href="/seguranca"
          iconSize={19}
          className="mb-4 flex h-9 w-9 items-center justify-center rounded-xl border border-slate-200 text-slate-800 transition hover:bg-slate-50"
        />

        <header className="mb-5">
          <h1 className="text-2xl font-bold tracking-tight">Alterar senha</h1>

          <p className="mt-1 text-sm leading-5 text-slate-500">
            Para sua segurança, informe a senha atual e escolha uma nova.
          </p>
        </header>

        <form action={formAction}>
          {/* fieldset desabilitado trava os campos e o botão enquanto envia */}
          <fieldset disabled={pending} className="flex min-w-0 flex-col gap-3">
            {state && "error" in state && (
              <p
                role="alert"
                className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600"
              >
                {state.error}
              </p>
            )}

            {state && "success" in state && (
              <p
                role="status"
                className="rounded-lg bg-emerald-50 px-3 py-2 text-sm text-emerald-700"
              >
                Senha alterada com sucesso.
              </p>
            )}

            <PasswordField
              labelText="Senha atual"
              name="senhaAtual"
              placeholder="Digite sua senha atual"
              autoComplete="current-password"
            />

            <div>
              <PasswordField
                labelText="Nova senha"
                name="novaSenha"
                placeholder="Crie uma nova senha"
                autoComplete="new-password"
              />

              <p className="mt-2 text-xs text-slate-500">
                Mínimo de 8 caracteres, com letras e números.
              </p>
            </div>

            <PasswordField
              labelText="Confirmar nova senha"
              name="confirmar"
              placeholder="Repita a nova senha"
              autoComplete="new-password"
            />

            <div className="mt-2 flex flex-col gap-2">
              <Button
                text={pending ? "Salvando..." : "Salvar nova senha"}
                type="submit"
              />

              <Link
                href="/seguranca"
                className="flex h-9.75 items-center justify-center rounded-[10px] text-sm font-medium text-slate-600 hover:bg-slate-50"
              >
                Voltar
              </Link>
            </div>
          </fieldset>
        </form>
      </div>
    </main>
  );
}