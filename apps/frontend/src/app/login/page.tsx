import Image from "next/image";
import Logo from "@/../public/logo.svg";
import { LoginForm } from "@/components/LoginForm";

export default function Login() {
  return (
    <main className="flex min-h-screen w-full flex-col items-center justify-center p-6">
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
          <h2 className="text-3xl font-bold">Bem-vindo ao ChamaAí</h2>

          <p className="mt-2 text-sm text-slate-500">
            Encontre ou ofereça serviços de forma simples e segura.
          </p>
        </div>

        <LoginForm />
      </div>
    </main>
  );
}