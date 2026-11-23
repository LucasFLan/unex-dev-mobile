"use client";

import { useActionState } from "react";
import Link from "next/link";
import { CheckCircle2, KeyRound, Mail } from "lucide-react";
import { recoverPassword, type RecoveryState } from "@/app/recuperar-senha/actions";
import { BackButton } from "@/components/BackButton";
import Button from "@/components/Button";
import Input from "@/components/Input";
import { PasswordField } from "@/components/seguranca/PasswordField";

const INITIAL: RecoveryState = { step: "email", email: "" };

const TITLES: Record<RecoveryState["step"], { title: string; subtitle: string }> = {
  email: {
    title: "Recuperar senha",
    subtitle: "Informe o e-mail da sua conta. Vamos enviar um código para você criar uma nova senha.",
  },
  codigo: {
    title: "Digite o código",
    subtitle: "Enviamos um código de 6 números para o seu e-mail. Ele vale por 15 minutos.",
  },
  senha: {
    title: "Crie uma nova senha",
    subtitle: "Use pelo menos 8 caracteres, com letra maiúscula, número e caractere especial.",
  },
  concluido: {
    title: "Senha redefinida!",
    subtitle: "Pronto. Agora é só entrar com a sua nova senha.",
  },
};

export function RecoverPasswordForm() {
  const [state, formAction, pending] = useActionState(recoverPassword, INITIAL);
  const { title, subtitle } = TITLES[state.step];

  return (
    <main className="min-h-screen w-full bg-white px-6 pb-8 pt-6 text-slate-900 md:bg-fundo md:py-12">
      <div className="mx-auto flex w-full max-w-md flex-col md:rounded-2xl md:border md:border-slate-200 md:bg-white md:p-8 md:shadow-sm">
        <BackButton
          href="/login"
          label="Voltar para o login"
          iconSize={19}
          className="mb-4 flex h-9 w-9 items-center justify-center rounded-xl border border-slate-200 text-slate-800 transition hover:bg-slate-50"
        />

        <header className="mb-5">
          <h1 className="text-2xl font-bold tracking-tight">{title}</h1>
          <p className="mt-1 text-sm leading-5 text-slate-500">{subtitle}</p>
          {state.step === "codigo" && (
            <p className="mt-1 text-sm font-medium text-slate-700">{state.email}</p>
          )}
        </header>

        {state.step === "concluido" ? (
          <div className="flex flex-col items-center gap-4 py-4 text-center">
            <CheckCircle2 size={48} className="text-emerald-500" aria-hidden="true" />
            <Link
              href="/login"
              className="w-full rounded-lg bg-principal px-4 py-3 text-center font-semibold text-white transition hover:opacity-90"
            >
              Ir para o login
            </Link>
          </div>
        ) : (
          // key: cada etapa monta campos novos (o código não aparece no campo da senha, etc.)
          <form key={state.step} action={formAction}>
            <fieldset disabled={pending} className="flex min-w-0 flex-col gap-3">
              {state.error && (
                <p role="alert" className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600">
                  {state.error}
                </p>
              )}
              {state.info && !state.error && (
                <p role="status" className="rounded-lg bg-blue-50 px-3 py-2 text-sm text-blue-700">
                  {state.info}
                </p>
              )}

              {state.step === "email" && (
                <>
                  <Input
                    name="email"
                    type="email"
                    labelText="E-mail"
                    placeholder="seu@email.com"
                    autoComplete="email"
                    icon={<Mail size={17} />}
                    defaultValue={state.email}
                    required
                  />
                  <input type="hidden" name="acao" value="solicitar" />
                  <div className="mt-2">
                    <Button text={pending ? "Enviando..." : "Enviar código"} type="submit" />
                  </div>
                </>
              )}

              {state.step === "codigo" && (
                <>
                  <Input
                    name="codigo"
                    type="text"
                    inputMode="numeric"
                    autoComplete="one-time-code"
                    maxLength={7}
                    labelText="Código"
                    placeholder="000000"
                    icon={<KeyRound size={17} />}
                    required
                  />
                  {/* Primeiro botão de envio: é o usado ao apertar Enter no campo do código. */}
                  <button
                    type="submit"
                    name="acao"
                    value="validar"
                    className="mt-2 w-full rounded-lg bg-principal px-4 py-3 font-semibold text-white transition hover:opacity-90"
                  >
                    {pending ? "Conferindo..." : "Continuar"}
                  </button>
                  <div className="mt-1 flex items-center justify-between text-sm">
                    <button
                      type="submit"
                      name="acao"
                      value="voltar"
                      formNoValidate
                      className="text-slate-500 hover:underline"
                    >
                      Trocar e-mail
                    </button>
                    <button
                      type="submit"
                      name="acao"
                      value="reenviar"
                      formNoValidate
                      className="font-semibold text-principal hover:underline"
                    >
                      Reenviar código
                    </button>
                  </div>
                  <p className="text-xs text-slate-400">
                    Não chegou? Confira a caixa de spam.
                  </p>
                </>
              )}

              {state.step === "senha" && (
                <>
                  <PasswordField
                    labelText="Nova senha"
                    name="novaSenha"
                    placeholder="Digite a nova senha"
                    autoComplete="new-password"
                  />
                  <PasswordField
                    labelText="Confirmar nova senha"
                    name="confirmar"
                    placeholder="Digite a nova senha de novo"
                    autoComplete="new-password"
                  />
                  <input type="hidden" name="acao" value="redefinir" />
                  <div className="mt-2">
                    <Button
                      text={pending ? "Salvando..." : "Redefinir senha"}
                      type="submit"
                    />
                  </div>
                </>
              )}
            </fieldset>
          </form>
        )}
      </div>
    </main>
  );
}
