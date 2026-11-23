import { initials } from "@/lib/marketplace";

type AvatarProps = {
  name: string;
  /** Endereço da foto (já pelo proxy); sem foto mostra as iniciais. */
  photo?: string | null;
  /** Tamanho, cor e fonte das iniciais (classes Tailwind). */
  className: string;
};

/** Foto de perfil redonda, ou as iniciais do nome quando não há foto. */
export function Avatar({ name, photo, className }: AvatarProps) {
  if (photo) {
    return (
      // eslint-disable-next-line @next/next/no-img-element -- foto servida pelo proxy da API, sem otimização do Next.
      <img
        src={photo}
        alt=""
        aria-hidden="true"
        className={`${className} shrink-0 rounded-full object-cover`}
      />
    );
  }

  return (
    <span
      className={`${className} flex shrink-0 items-center justify-center rounded-full`}
      aria-hidden="true"
    >
      {initials(name)}
    </span>
  );
}
