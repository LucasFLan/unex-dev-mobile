"use client";

import { useActionState, useState, type ChangeEvent } from "react";
import Link from "next/link";
import {
  UserRound,
  ContactRound,
  CalendarDays,
  Mail,
  LockKeyhole,
  Eye,
  EyeOff,
  Phone,
} from "lucide-react";

import { register, type AuthState } from "@/app/actions";
import { BackButton } from "@/components/BackButton";
import Button from "@/components/Button";
import Input from "@/components/Input";
import { maskCpf, maskDate, maskPhone } from "@/lib/masks";

const Register = () => {
  const [state, formAction, pending] = useActionState<AuthState, FormData>(
    register,
    null
  );
  const [showPassword, setShowPassword] = useState(false);
  const [form, setForm] = useState({
    nome: "",
    cpf: "",
    dataNascimento: "",
    telefone: "",
    email: "",
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
          fallbackHref="/login"
          iconSize={19}
          className="mb-4 flex h-9 w-9 items-center justify-center rounded-xl border border-slate-200 text-slate-800 transition hover:bg-slate-50"
        />

        <header className="mb-5">
          <h1 className="text-2xl font-bold tracking-tight">Criar conta</h1>

          <p className="mt-1 text-sm leading-5 text-slate-500">
            Preencha seus dados para começar a usar o ChamaAí.
          </p>
        </header>

        <form action={formAction}>
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
              required
              value={form.nome}
              onChange={update("nome")}
            />

            <Input
              labelText="CPF"
              placeholder="000.000.000-00"
              type="text"
              icon={<ContactRound size={17} />}
              name="cpf"
              inputMode="numeric"
              maxLength={14}
              required
              value={form.cpf}
              onChange={update("cpf", maskCpf)}
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
              required
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
              required
              value={form.telefone}
              onChange={update("telefone", maskPhone)}
            />

            <Input
              labelText="E-mail"
              placeholder="seu@email.com"
              type="email"
              icon={<Mail size={17} />}
              name="email"
              autoComplete="email"
              required
              value={form.email}
              onChange={update("email")}
            />

            <div>
              <div className="relative">
                <Input
                  labelText="Senha"
                  placeholder="Crie uma senha"
                  type={showPassword ? "text" : "password"}
                  icon={<LockKeyhole size={17} />}
                  name="senha"
                  autoComplete="new-password"
                  minLength={8}
                  required
                />

                <button
                  type="button"
                  aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"}
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-9.5 text-slate-400 hover:text-slate-600"
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>

              <p className="mt-2 text-xs text-slate-500">
                Mínimo de 8 caracteres, com letras e números.
              </p>
            </div>

            <label className="mt-1 flex cursor-pointer items-start gap-2">
              <input
                type="checkbox"
                name="termos"
                required
                className="mt-0.5 h-4 w-4 shrink-0 cursor-pointer accent-blue-600"
              />

              <span className="text-xs leading-4.5 text-slate-500">
                Li e concordo com os{" "}
                <Link
                  href="/termos"
                  className="font-medium text-blue-600 hover:underline"
                >
                  Termos de Uso
                </Link>{" "}
                e a{" "}
                <Link
                  href="/privacidade"
                  className="font-medium text-blue-600 hover:underline"
                >
                  Política de Privacidade
                </Link>
                .
              </span>
            </label>

            <div className="mt-1">
              <Button
                text={pending ? "Criando conta..." : "Criar conta"}
                type="submit"
              />
            </div>

            <p className="mt-1 text-center text-xs text-slate-500">
              Já possui uma conta?{" "}
              <Link
                href="/login"
                className="font-semibold text-blue-600 hover:underline"
              >
                Entrar
              </Link>
            </p>
          </fieldset>
        </form>
      </div>
    </main>
  );
};

export default Register;