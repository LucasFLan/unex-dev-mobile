import Image from "next/image";
import Logo from "../../public/logo.svg";
import Input from "@/components/Input";

export default function Home() {
  return (
    <main className="flex min-h-screen w-full flex-col items-center justify-center bg-white p-6">
      <div className="flex w-full max-w-md flex-col gap-8">

        <div className="flex flex-col items-center justify-center">
          <Image
            src={Logo}
            alt="Logo ChamaAí"
            width={100}
            height={100}
            priority
          />

          <h1 className="text-2xl font-bold">
            Chama<span className="text-principal">Aí</span>
          </h1>
        </div>

        <div className="flex flex-col items-center text-center">
          <h2 className="text-3xl font-bold">
            Bem-vindo ao ChamaAí
          </h2>

          <p className="mt-2 text-sm text-gray-600">
            Encontre ou ofereça serviços de forma simples e segura.
          </p>
        </div>

        <form className="flex w-full flex-col gap-5">

          <div className="flex flex-col gap-2">
            <label
              htmlFor="email"
              className="text-sm font-medium text-gray-700"
            >
              E-mail
            </label>

            <Input type="email" placeholder="seu@email.com"/>
          </div>

          <div className="flex flex-col gap-2">
            <label
              htmlFor="senha"
              className="text-sm font-medium text-gray-700"
            >
              Senha
            </label>

            <Input type="password" placeholder="Digite sua senha"/>
          </div>

          <div className="flex justify-end">
            <a
              href="/recuperar-senha"
              className="text-sm text-principal hover:underline"
            >
              Esqueci minha senha
            </a>
          </div>

          <button
            type="submit"
            className="w-full rounded-lg bg-principal px-4 py-3 font-semibold text-white transition hover:opacity-90"
          >
            Entrar
          </button>

          <p className="text-center text-sm text-gray-600">
            Ainda não tem uma conta?{" "}
            <a
              href="/cadastro"
              className="font-semibold text-principal hover:underline"
            >
              Criar conta
            </a>
          </p>
        </form>

      </div>
    </main>
  );
}
