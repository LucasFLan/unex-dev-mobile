import { Suspense } from "react";
import { EditProfileForm } from "@/components/EditProfileForm";
import { isoToBr, maskCpf, maskPhone } from "@/lib/masks";
import { getCurrentUser } from "@/lib/user";

async function EditarPerfilContent() {
  const user = await getCurrentUser();

  return (
    <EditProfileForm
      initial={{
        nome: user.name,
        dataNascimento: isoToBr(user.birthDate),
        telefone: maskPhone(user.phone),
        email: user.email,
        cpf: maskCpf(user.cpf),
      }}
    />
  );
}

export default function EditarPerfilPage() {
  return (
    <Suspense fallback={<main className="min-h-screen bg-white" />}>
      <EditarPerfilContent />
    </Suspense>
  );
}