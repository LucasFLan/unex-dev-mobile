"use client";

import { useActionState, useState } from "react";
import Link from "next/link";
import { Mail } from "lucide-react";
import { login, type AuthState } from "@/app/actions";
import Button from "@/components/Button";
import Input from "@/components/Input";
import { PasswordField } from "@/components/seguranca/PasswordField";

export function LoginForm() {
  const [state, formAction, pending] = useActionState<AuthState, FormData>(
    login,
    null
  );
  const [email, setEmail] = useState("");

  return (
    <form action={formAction} className="w-full">
      {/* fieldset desabilitado trava os campos e o botão enquanto envia */}
      <fieldset disabled={pending} className="flex min-w-0 flex-col gap-5">
        {state?.error && (
          <p
            role="alert"
            className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-600"
          >
            {state.error}
          </p>
        )}

        <div className="flex flex-col gap-2">
          <Input
            name="email"
            type="email"
            placeholder="seu@email.com"
            labelText="E-mail"
            icon={<Mail size={17} />}
            autoComplete="email"
            required
            value={email}
            onChange={(event) => setEmail(event.target.value)}
          />
        </div>

        <PasswordField
          name="senha"
          placeholder="Digite sua senha"
          labelText="Senha"
          autoComplete="current-password"
          className="flex flex-col gap-2"
        />

        <div className="flex justify-end">
          <Link
            href="/recuperar-senha"
            className="text-sm text-principal hover:underline"
          >
            Esqueci minha senha
          </Link>
        </div>

        <Button text={pending ? "Entrando..." : "Entrar"} type="submit" />

        <p className="text-center text-sm text-gray-600">
          Ainda não tem uma conta?{" "}
          <Link
            href="/register"
            className="font-semibold text-principal hover:underline"
          >
            Criar conta
          </Link>
        </p>
      </fieldset>
    </form>
  );
}