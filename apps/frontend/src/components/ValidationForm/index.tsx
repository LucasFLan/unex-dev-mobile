"use client";

import { useActionState, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Camera, IdCard, Loader2, X } from "lucide-react";
import {
  sendValidationDocuments,
  type ValidationFormState,
} from "@/app/validacao/actions";
import { resizeImageForUpload } from "@/lib/image-resize";

type Slot = {
  name: "documento" | "selfie";
  title: string;
  hint: string;
  icon: typeof IdCard;
};

const SLOTS: Slot[] = [
  {
    name: "documento",
    title: "Foto do documento",
    hint: "RG ou CNH, frente com foto, sem reflexo e com todos os dados legíveis.",
    icon: IdCard,
  },
  {
    name: "selfie",
    title: "Selfie com o documento",
    hint: "Seu rosto e o documento aparecendo juntos, em lugar bem iluminado.",
    icon: Camera,
  },
];

type SlotName = Slot["name"];
/** Foto já reduzida, com a URL local (blob:) da prévia. */
type Picked = { file: File | null; preview: string | null; processing: boolean; error: string | null };
const EMPTY: Picked = { file: null, preview: null, processing: false, error: null };

function ImagePicker({
  slot,
  picked,
  onPick,
}: {
  slot: Slot;
  picked: Picked;
  onPick: (file: File | null) => void;
}) {
  const [inputKey, setInputKey] = useState(0);
  const Icon = slot.icon;
  const { preview } = picked;
  const clear = () => {
    onPick(null);
    setInputKey((key) => key + 1);
  };

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-3 shadow-sm">
      <p className="text-xs font-semibold text-slate-800">{slot.title}</p>
      <p className="mt-0.5 text-[11px] leading-4 text-slate-500">{slot.hint}</p>

      <label className="relative mt-2 flex aspect-[4/3] cursor-pointer items-center justify-center overflow-hidden rounded-lg border border-dashed border-blue-300 bg-blue-50/50 text-blue-600">
        {preview ? (
          // eslint-disable-next-line @next/next/no-img-element -- prévia local (blob:), sem otimização
          <img src={preview} alt={`Prévia: ${slot.title}`} className="h-full w-full object-cover" />
        ) : (
          <span className="flex flex-col items-center gap-1 text-[11px] font-medium">
            {picked.processing ? (
              <>
                <Loader2 size={22} className="animate-spin" aria-hidden="true" />
                Preparando a foto...
              </>
            ) : (
              <>
                <Icon size={22} aria-hidden="true" />
                Tirar foto ou escolher da galeria
              </>
            )}
          </span>
        )}
        {/* Sem "capture": o celular oferece câmera e galeria. A foto é reduzida antes do envio. */}
        <input
          key={inputKey}
          type="file"
          name={slot.name}
          accept="image/*"
          required
          className="absolute inset-0 cursor-pointer opacity-0"
          onChange={(event) => onPick(event.target.files?.[0] ?? null)}
        />
      </label>

      {picked.error && (
        <p role="alert" className="mt-2 text-[11px] text-red-600">
          {picked.error}
        </p>
      )}

      {picked.file && (
        <button
          type="button"
          onClick={clear}
          className="mt-2 flex items-center gap-1 text-[11px] font-medium text-slate-500"
        >
          <X size={12} aria-hidden="true" />
          Trocar imagem
        </button>
      )}
    </div>
  );
}

/** Formulário de envio do documento + selfie. Ao enviar, recarrega a tela com a nova situação. */
export function ValidationForm({ resubmission }: { resubmission: boolean }) {
  const router = useRouter();
  const [state, formAction, pending] = useActionState<ValidationFormState, FormData>(
    sendValidationDocuments,
    null
  );
  const [picked, setPicked] = useState<Record<SlotName, Picked>>({
    documento: EMPTY,
    selfie: EMPTY,
  });
  const processing = picked.documento.processing || picked.selfie.processing;

  useEffect(() => {
    if (state && "success" in state) router.refresh();
  }, [state, router]);

  // Troca a foto de um campo, liberando a URL da prévia anterior.
  function update(name: SlotName, next: Picked) {
    const old = picked[name].preview;
    if (old) URL.revokeObjectURL(old);
    setPicked((current) => ({ ...current, [name]: next }));
  }

  async function pick(name: SlotName, file: File | null) {
    if (!file) {
      update(name, EMPTY);
      return;
    }
    update(name, { ...EMPTY, processing: true });
    try {
      const resized = await resizeImageForUpload(file, name);
      setPicked((current) => ({
        ...current,
        [name]: { file: resized, preview: URL.createObjectURL(resized), processing: false, error: null },
      }));
    } catch {
      setPicked((current) => ({
        ...current,
        [name]: {
          ...EMPTY,
          error: "Não conseguimos abrir essa imagem. Tire outra foto ou escolha um arquivo JPG ou PNG.",
        },
      }));
    }
  }

  // Envia a versão reduzida da foto, não o arquivo original da câmera.
  function submit(formData: FormData) {
    for (const name of ["documento", "selfie"] as const) {
      const file = picked[name].file;
      if (file) formData.set(name, file, file.name);
    }
    formAction(formData);
  }

  return (
    <form action={submit} className="space-y-3">
      <fieldset disabled={pending} className="min-w-0 space-y-3">
        {state && "error" in state && (
          <p role="alert" className="rounded-lg bg-red-50 px-3 py-2 text-[11px] text-red-600">
            {state.error}
          </p>
        )}

        {SLOTS.map((slot) => (
          <ImagePicker
            key={slot.name}
            slot={slot}
            picked={picked[slot.name]}
            onPick={(file) => void pick(slot.name, file)}
          />
        ))}

        <p className="text-[10px] leading-4 text-slate-500">
          Pode tirar a foto na hora ou escolher da galeria. As imagens são vistas só pela equipe do
          ChamaAí para confirmar sua identidade.
        </p>

        <button
          type="submit"
          disabled={processing}
          className="h-11 w-full rounded-lg bg-blue-600 text-xs font-semibold text-white transition hover:bg-blue-700 disabled:opacity-60"
        >
          {pending
            ? "Enviando..."
            : processing
              ? "Preparando as fotos..."
              : resubmission
                ? "Reenviar documentos"
                : "Enviar para análise"}
        </button>
      </fieldset>
    </form>
  );
}
