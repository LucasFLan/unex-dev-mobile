"use client";

import { useActionState, useMemo, useRef, useState } from "react";
import {
  Search,
  BriefcaseBusiness,
  LayoutGrid,
  Pencil,
  ChevronDown,
  Clock3,
  CalendarDays,
  Zap,
  MapPin,
  ImagePlus,
  Image as ImageIcon,
  X,
} from "lucide-react";
import {
  createAd,
  updateAd,
  type AdFormState,
} from "@/app/anunciar/actions";
import { BackButton } from "@/components/BackButton";
import Button from "@/components/Button";
import type { AnuncioItem } from "@/lib/ads";
import type { Category } from "@/lib/categories-api";
import { ESTADOS } from "@/lib/estados";

type AdType = "procurar" | "oferecer";

const MAX_PHOTOS = 6;
const MAX_TITLE = 120;
const MAX_DESCRIPTION = 5000;

function PhotoPreview({
  file,
  index,
  onRemove,
}: {
  file: File;
  index: number;
  onRemove: (index: number) => void;
}) {
  const url = useMemo(() => URL.createObjectURL(file), [file]);

  return (
    <div className="relative aspect-square overflow-hidden rounded-lg border border-slate-200">
      <img
        src={url}
        alt={`Foto ${index + 1}`}
        className="h-full w-full object-cover"
      />

      <button
        type="button"
        onClick={() => onRemove(index)}
        aria-label="Remover foto"
        className="absolute right-1 top-1 flex h-5 w-5 items-center justify-center rounded-full bg-white text-slate-700 shadow"
      >
        <X size={12} />
      </button>
    </div>
  );
}

type AdFormProps = {
  categories: Category[];
  /** Quando informado, o formulário edita este anúncio (PUT /anuncios/{id}). */
  ad?: AnuncioItem;
  /** Tela-pai: Início ao criar; de onde veio a edição (Histórico ou detalhe). */
  backHref: string;
};

export function AdForm({ categories, ad, backHref }: AdFormProps) {
  const isEditing = ad !== undefined;
  const submit = useMemo(
    () => (ad ? updateAd.bind(null, ad.id) : createAd),
    [ad]
  );
  const [state, formAction, pending] = useActionState<AdFormState, FormData>(
    submit,
    null
  );
  const formRef = useRef<HTMLFormElement>(null);

  const [adType, setAdType] = useState<AdType>(
    ad?.tipo === "OFERTA" ? "oferecer" : "procurar"
  );
  const [category, setCategory] = useState(ad?.categoria.id ?? "");
  const [title, setTitle] = useState(ad?.titulo ?? "");
  const [description, setDescription] = useState(ad?.descricao ?? "");
  const [price, setPrice] = useState(
    ad?.preco != null ? String(ad.preco) : ""
  );
  const [negotiable, setNegotiable] = useState(
    isEditing && ad.preco === null
  );
  const [paymentType, setPaymentType] = useState("por visita");
  const [availability, setAvailability] = useState("Esta semana");
  const [radius, setRadius] = useState("10 km");
  const [address, setAddress] = useState("");
  const [city, setCity] = useState(ad?.cidade ?? "");
  const [uf, setUf] = useState(ad?.uf ?? "");
  const [photos, setPhotos] = useState<File[]>([]);

  const isOffering = adType === "oferecer";

  function handlePhotos(files: FileList | null) {
    if (!files) return;

    const selected = Array.from(files).slice(
      0,
      Math.max(0, MAX_PHOTOS - photos.length)
    );

    setPhotos((current) => [...current, ...selected]);
  }

  function removePhoto(index: number) {
    setPhotos((current) => current.filter((_, i) => i !== index));
  }

  const availabilityOptions = isOffering
    ? [
        { label: "Hoje", icon: Zap },
        { label: "Agendar", icon: CalendarDays },
        { label: "Disponível", icon: Clock3 },
      ]
    : [
        { label: "Sem pressa", icon: Clock3 },
        { label: "Esta semana", icon: CalendarDays },
        { label: "Urgente", icon: Zap },
      ];

  return (
    <main className="min-h-screen bg-white text-slate-900">
      <div className="mx-auto min-h-screen w-full max-w-md pb-24">
        <header className="flex h-14.5 items-center justify-between px-4">
          <BackButton href={backHref} />

          <h1 className="text-[11px] font-semibold">
            {isEditing ? "Editar anúncio" : "Criar anúncio"}
          </h1>

          <div className="w-8" />
        </header>

        <form ref={formRef} action={formAction} className="px-4">
          {isEditing && <input type="hidden" name="voltar" value={backHref} />}
          <input
            type="hidden"
            name="tipo"
            value={isOffering ? "OFERTA" : "PEDIDO"}
          />
          <input
            type="hidden"
            name="preco"
            value={negotiable ? "" : price}
          />

          <fieldset disabled={pending} className="min-w-0">
            {state?.error && (
              <p
                role="alert"
                className="mb-3 rounded-lg bg-red-50 px-3 py-2 text-[11px] text-red-600"
              >
                {state.error}
              </p>
            )}

            <section>
              <h2 className="mb-2 text-[9px] font-medium">
                O que você deseja fazer?
              </h2>

              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  onClick={() => {
                    setAdType("procurar");
                    setAvailability("Esta semana");
                  }}
                  className={`relative flex min-h-22.75 flex-col rounded-xl border p-2 text-left transition ${
                    !isOffering
                      ? "border-blue-500 bg-blue-50/50 ring-1 ring-blue-500"
                      : "border-slate-200 bg-white"
                  }`}
                >
                  <span className="flex items-center justify-between">
                    <span
                      className={`flex h-7 w-7 items-center justify-center rounded-lg ${
                        !isOffering
                          ? "bg-blue-600 text-white"
                          : "bg-slate-100 text-slate-500"
                      }`}
                    >
                      <Search size={16} />
                    </span>

                    <span
                      className={`flex h-3 w-3 items-center justify-center rounded-full border ${
                        !isOffering ? "border-blue-600" : "border-slate-300"
                      }`}
                    >
                      {!isOffering && (
                        <span className="h-1.5 w-1.5 rounded-full bg-blue-600" />
                      )}
                    </span>
                  </span>

                  <span className="mt-2 text-[9px] font-semibold">
                    Procuro um profissional
                  </span>

                  <span className="mt-1 text-[8px] leading-3 text-slate-500">
                    Preciso contratar alguém para um serviço
                  </span>
                </button>

                <button
                  type="button"
                  onClick={() => {
                    setAdType("oferecer");
                    setAvailability("Hoje");
                  }}
                  className={`relative flex min-h-22.75 flex-col rounded-xl border p-2 text-left transition ${
                    isOffering
                      ? "border-blue-500 bg-blue-50/50 ring-1 ring-blue-500"
                      : "border-slate-200 bg-white"
                  }`}
                >
                  <span className="flex items-center justify-between">
                    <span
                      className={`flex h-7 w-7 items-center justify-center rounded-lg ${
                        isOffering
                          ? "bg-blue-600 text-white"
                          : "bg-slate-100 text-slate-500"
                      }`}
                    >
                      <BriefcaseBusiness size={16} />
                    </span>

                    <span
                      className={`flex h-3 w-3 items-center justify-center rounded-full border ${
                        isOffering ? "border-blue-600" : "border-slate-300"
                      }`}
                    >
                      {isOffering && (
                        <span className="h-1.5 w-1.5 rounded-full bg-blue-600" />
                      )}
                    </span>
                  </span>

                  <span className="mt-2 text-[9px] font-semibold">
                    Ofereço meus serviços
                  </span>

                  <span className="mt-1 text-[8px] leading-3 text-slate-500">
                    Quero ser contratado por clientes
                  </span>
                </button>
              </div>
            </section>

            <section className="mt-4">
              <div className="mb-1 flex items-center justify-between">
                <label htmlFor="titulo" className="text-[9px] font-medium">
                  {isOffering ? "Nome do prestador" : "Título do anúncio"}
                </label>

                <span className="text-[8px] text-slate-400">
                  {title.length}/{MAX_TITLE}
                </span>
              </div>

              <div className="flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-2.5">
                <Pencil size={12} className="shrink-0 text-slate-400" />

                <input
                  id="titulo"
                  name="titulo"
                  type="text"
                  minLength={3}
                  maxLength={MAX_TITLE}
                  required
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder={
                    isOffering
                      ? "Ex.: Carlos pedreiro"
                      : "Ex.: Eletricista residencial e predial"
                  }
                  className="w-full bg-transparent text-[9px] outline-none placeholder:text-slate-400"
                />
              </div>
            </section>

            <section className="mt-3">
              <label
                htmlFor="categoriaId"
                className="mb-1 block text-[9px] font-medium"
              >
                Tipo de serviço
              </label>

              <div className="relative flex h-9 items-center rounded-lg border border-slate-200 bg-slate-50 px-2.5">
                <LayoutGrid size={12} className="shrink-0 text-slate-400" />

                <select
                  id="categoriaId"
                  name="categoriaId"
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                  required
                  className="h-full w-full appearance-none bg-transparent px-2 text-[9px] text-slate-600 outline-none"
                >
                  <option value="">Selecione a categoria</option>

                  {categories.map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.nome}
                    </option>
                  ))}
                </select>

                <ChevronDown
                  size={13}
                  className="pointer-events-none text-slate-400"
                />
              </div>
            </section>

            <section className="mt-3">
              <div className="mb-1 flex items-center justify-between">
                <label htmlFor="descricao" className="text-[9px] font-medium">
                  Descrição detalhada
                </label>

                <span className="text-[8px] text-slate-400">
                  {description.length}/{MAX_DESCRIPTION}
                </span>
              </div>

              <textarea
                id="descricao"
                name="descricao"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                maxLength={MAX_DESCRIPTION}
                required
                rows={4}
                placeholder={
                  isOffering
                    ? "Descreva os serviços que você faz, sua experiência, o que está incluso, garantia e se fornece os materiais."
                    : "Descreva o que precisa ser feito, o local, o prazo desejado, se há materiais inclusos e outras informações importantes."
                }
                className="min-h-18.5 w-full resize-none rounded-lg border border-slate-200 bg-slate-50 p-2.5 text-[9px] leading-3.5 outline-none placeholder:text-slate-400 focus:border-blue-500"
              />

              <p className="mt-1 text-[8px] text-slate-400">
                {isOffering
                  ? "Anúncios completos passam mais confiança."
                  : "Quanto mais detalhes, melhores as respostas."}
              </p>
            </section>

            <section className="mt-3">
              <label
                htmlFor="preco-visivel"
                className="mb-1 block text-[9px] font-medium"
              >
                {isOffering ? "Quanto você cobra?" : "Quanto pretende pagar?"}
              </label>

              <div
                className={`flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-2.5 ${
                  negotiable ? "opacity-50" : ""
                }`}
              >
                <span className="text-[9px] font-semibold text-slate-700">
                  R$
                </span>

                <input
                  id="preco-visivel"
                  type="number"
                  min="0"
                  step="0.01"
                  disabled={negotiable}
                  value={price}
                  onChange={(e) => setPrice(e.target.value)}
                  placeholder="0,00"
                  className="min-w-0 flex-1 bg-transparent text-[9px] outline-none placeholder:text-slate-400"
                />

                {isOffering && (
                  <select
                    value={paymentType}
                    onChange={(e) => setPaymentType(e.target.value)}
                    aria-label="Como o valor é cobrado"
                    className="rounded-md border border-slate-200 bg-white px-1.5 py-1 text-[8px] outline-none"
                  >
                    <option>por visita</option>
                    <option>por hora</option>
                    <option>por dia</option>
                    <option>por serviço</option>
                  </select>
                )}
              </div>

              <label className="mt-2 flex cursor-pointer items-center gap-2">
                <input
                  type="checkbox"
                  checked={negotiable}
                  onChange={(e) => setNegotiable(e.target.checked)}
                  className="h-3 w-3 accent-blue-600"
                />

                <span className="text-[8px] text-slate-600">
                  {isOffering
                    ? "Prefiro combinar o valor com o cliente"
                    : "Prefiro combinar o valor com o profissional"}
                </span>
              </label>
            </section>

            <section className="mt-4">
              <p className="mb-1.5 text-[9px] font-medium">
                {isOffering
                  ? "Quando você pode atender?"
                  : "Para quando você precisa?"}
              </p>

              <div className="grid grid-cols-3 gap-1.5">
                {availabilityOptions.map(({ label, icon: Icon }) => (
                  <button
                    key={label}
                    type="button"
                    onClick={() => setAvailability(label)}
                    className={`flex h-10.5 items-center justify-center gap-1.5 rounded-lg border text-[8px] transition ${
                      availability === label
                        ? "border-blue-500 bg-blue-50 text-blue-600"
                        : "border-slate-200 bg-white text-slate-600"
                    }`}
                  >
                    <Icon size={12} />
                    {label}
                  </button>
                ))}
              </div>

              <p className="mt-1.5 text-[8px] leading-3 text-slate-400">
                {isOffering
                  ? "Os clientes verão quando você pode começar o serviço."
                  : "Pedidos urgentes aparecem em destaque para os profissionais da sua região."}
              </p>
            </section>

            <section className="mt-4">
              <p className="mb-1.5 text-[9px] font-medium">
                {isOffering ? "Cidade onde você atende" : "Cidade do serviço"}
              </p>

              <div className="grid grid-cols-[1fr_4.5rem] gap-2">
                <div className="flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-2.5">
                  <MapPin size={13} className="shrink-0 text-slate-400" />

                  <input
                    name="cidade"
                    type="text"
                    maxLength={100}
                    required
                    value={city}
                    onChange={(e) => setCity(e.target.value)}
                    placeholder="Ex.: Salvador"
                    aria-label="Cidade"
                    className="w-full min-w-0 bg-transparent text-[9px] outline-none placeholder:text-slate-400"
                  />
                </div>

                <div className="relative flex h-9 items-center rounded-lg border border-slate-200 bg-slate-50 pl-2.5 pr-1.5">
                  <select
                    name="uf"
                    value={uf}
                    onChange={(e) => setUf(e.target.value)}
                    required
                    aria-label="Estado"
                    className="h-full w-full appearance-none bg-transparent text-[9px] text-slate-600 outline-none"
                  >
                    <option value="">UF</option>

                    {ESTADOS.map(({ uf: sigla, nome }) => (
                      <option key={sigla} value={sigla} title={nome}>
                        {sigla}
                      </option>
                    ))}
                  </select>

                  <ChevronDown
                    size={13}
                    className="pointer-events-none shrink-0 text-slate-400"
                  />
                </div>
              </div>
            </section>

            {isOffering && (
              <section className="mt-4">
                <label
                  htmlFor="endereco"
                  className="mb-1.5 block text-[9px] font-medium"
                >
                  Onde você atende?
                </label>

                <div className="flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-2.5">
                  <MapPin size={13} className="shrink-0 text-slate-400" />

                  <input
                    id="endereco"
                    type="text"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    placeholder="Seu bairro ou CEP"
                    className="w-full bg-transparent text-[9px] outline-none placeholder:text-slate-400"
                  />
                </div>

                <div className="mt-2 flex gap-1.5">
                  {["5 km", "10 km", "20 km", "Toda a cidade"].map((item) => (
                    <button
                      key={item}
                      type="button"
                      onClick={() => setRadius(item)}
                      className={`rounded-full border px-2 py-1 text-[8px] ${
                        radius === item
                          ? "border-blue-500 bg-blue-50 text-blue-600"
                          : "border-slate-200 text-slate-600"
                      }`}
                    >
                      {item}
                    </button>
                  ))}
                </div>

                <p className="mt-1.5 text-[8px] text-slate-400">
                  Seu anúncio aparece para clientes dentro dessa área.
                </p>
              </section>
            )}

            <section className="mt-4">
              <div className="mb-1.5 flex items-center justify-between">
                <p className="text-[9px] font-medium">
                  {isOffering ? "Fotos dos seus trabalhos" : "Fotos"}
                </p>

                <span className="text-[8px] text-slate-400">Opcional</span>
              </div>

              <div className="grid grid-cols-3 gap-1.5">
                <label className="relative flex aspect-square cursor-pointer flex-col items-center justify-center gap-1 rounded-lg border border-dashed border-blue-300 bg-blue-50/50 text-blue-600">
                  <ImagePlus size={15} />
                  <span className="text-[8px] font-medium">Adicionar</span>

                  <input
                    type="file"
                    accept="image/*"
                    multiple
                    disabled={photos.length >= MAX_PHOTOS}
                    onChange={(e) => {
                      handlePhotos(e.target.files);
                      e.target.value = "";
                    }}
                    className="hidden"
                  />
                </label>

                {photos.map((photo, index) => (
                  <PhotoPreview
                    key={`${photo.name}-${index}`}
                    file={photo}
                    index={index}
                    onRemove={removePhoto}
                  />
                ))}

                {Array.from({ length: Math.max(0, 2 - photos.length) }).map(
                  (_, index) => (
                    <div
                      key={`empty-${index}`}
                      className="flex aspect-square items-center justify-center rounded-lg border border-dashed border-slate-200 bg-slate-50"
                    >
                      <ImageIcon size={14} className="text-slate-300" />
                    </div>
                  )
                )}
              </div>

              <p className="mt-1.5 text-[8px] text-slate-400">
                Até {MAX_PHOTOS} fotos · JPG ou PNG, até 5 MB cada
              </p>
            </section>
          </fieldset>
        </form>
      </div>

      <footer className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-4 pb-[max(12px,env(safe-area-inset-bottom))] pt-2">
        <div className="mx-auto max-w-md">
          <Button
            text={
              pending
                ? isEditing
                  ? "Salvando..."
                  : "Publicando..."
                : isEditing
                  ? "Salvar alterações"
                  : "Publicar anúncio"
            }
            type="button"
            onClick={() => {
              if (!pending) formRef.current?.requestSubmit();
            }}
          />
        </div>
      </footer>
    </main>
  );
}