"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import {
  ArrowLeft,
  UserRound,
  ContactRound,
  CalendarDays,
  Mail,
  LockKeyhole,
  Eye,
  EyeOff,
} from "lucide-react";

import Button from "@/components/Button";
import Input from "@/components/Input";

const Register = () => {
  const router = useRouter();
  const [showPassword, setShowPassword] = useState(false);
  const [acceptedTerms, setAcceptedTerms] = useState(false);

  return (
    <main className="min-h-screen w-full bg-white px-6 pb-8 pt-6 text-slate-900">
      <div className="mx-auto flex w-full max-w-md flex-col">
        <button
          type="button"
          onClick={() => router.back()}
          aria-label="Voltar"
          className="mb-4 flex h-9 w-9 items-center justify-center rounded-xl border border-slate-200 text-slate-800 transition hover:bg-slate-50"
        >
          <ArrowLeft size={19} strokeWidth={1.8} />
        </button>

        <header className="mb-5">
          <h1 className="text-2xl font-bold tracking-tight">
            Criar conta
          </h1>

          <p className="mt-1 text-sm leading-5 text-slate-500">
            Preencha seus dados para começar a usar o ChamaAí.
          </p>
        </header>

        <form
          className="flex flex-col gap-3"
          onSubmit={(event) => {
            event.preventDefault();

            if (!acceptedTerms) {
              return;
            }

          }}
        >
          <Input
            labelText="Nome completo"
            placeholder="Digite seu nome completo"
            type="text"
            icon={<UserRound size={17} />}
          />

          <Input
            labelText="CPF"
            placeholder="000.000.000-00"
            type="text"
            icon={<ContactRound size={17} />}
          />

          <Input
            labelText="Data de nascimento"
            placeholder="DD/MM/AAAA"
            type="text"
            icon={<CalendarDays size={17} />}
          />

          <Input
            labelText="E-mail"
            placeholder="seu@email.com"
            type="email"
            icon={<Mail size={17} />}
          />

          <div>
            <div className="relative">
              <Input
                labelText="Senha"
                placeholder="Crie uma senha"
                type={showPassword ? "text" : "password"}
                icon={<LockKeyhole size={17} />}
              />

              <button
                type="button"
                aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"}
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-9.5 text-slate-400 hover:text-slate-600"
              >
                {showPassword ? (
                  <EyeOff size={18} />
                ) : (
                  <Eye size={18} />
                )}
              </button>
            </div>

            <p className="mt-2 text-xs text-slate-500">
              Mínimo de 8 caracteres, com letras e números.
            </p>
          </div>

          <label className="mt-1 flex cursor-pointer items-start gap-2">
            <input
              type="checkbox"
              checked={acceptedTerms}
              onChange={(event) => setAcceptedTerms(event.target.checked)}
              className="mt-0.5 h-4 w-4 shrink-0 cursor-pointer accent-blue-600"
            />

            <span className="text-xs leading-4.5 text-slate-500">
              Li e concordo com os{" "}
              <a
                href="/termos"
                className="font-medium text-blue-600 hover:underline"
                onClick={(event) => event.stopPropagation()}
              >
                Termos de Uso
              </a>{" "}
              e a{" "}
              <a
                href="/privacidade"
                className="font-medium text-blue-600 hover:underline"
                onClick={(event) => event.stopPropagation()}
              >
                Política de Privacidade
              </a>.
            </span>
          </label>

          <div className="mt-1">
            <Button text="Criar conta" type="submit" />
          </div>

          <p className="mt-1 text-center text-xs text-slate-500">
            Já possui uma conta?{" "}
            <a
              href="/login"
              className="font-semibold text-blue-600 hover:underline"
            >
              Entrar
            </a>
          </p>
        </form>
      </div>
    </main>
  );
};

export default Register;
