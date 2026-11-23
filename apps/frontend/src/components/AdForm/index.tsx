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
import Link from "next/link";
import { BackButton } from "@/components/BackButton";
import Button from "@/components/Button";
import type { AnuncioItem } from "@/lib/ads";
import type { Category } from "@/lib/categories-api";
import { ESTADOS } from "@/lib/estados";
import { resizeImageForUpload } from "@/lib/image-resize";
import { MAX_AD_PHOTOS, type AdPhoto } from "@/lib/photos";

type AdType = "procurar" | "oferecer";

const MAX_TITLE = 120;
const MAX_DESCRIPTION = 5000;

/*
 * Mobile: classes do layout original (texto pequeno, coluna única, botão fixo no rodapé).
 * md+: texto e campos em tamanho de leitura. lg+: card largo em duas colunas, com as
 * informações do anúncio à esquerda e quando/onde/fotos à direita.
 */
const LABEL = "text-[9px] font-medium md:text-sm";
const HINT = "text-[8px] text-slate-400 md:text-xs";
const FIELD =
  "flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-2.5 md:h-11 md:px-3 md:focus-within:border-blue-500 md:focus-within:bg-white";
const ICON = "shrink-0 text-slate-400 md:size-4";
const CONTROL = "text-[9px] md:text-sm";

function PhotoThumb({
  src,
  label,
  onRemove,
}: {
  src: string;
  label: string;
  onRemove: () => void;
}) {
  return (
    <div className="relative aspect-square overflow-hidden rounded-lg border border-slate-200">
      {/* eslint-disable-next-line @next/next/no-img-element -- prévia local (blob:) ou foto pelo proxy da API. */}
      <img src={src} alt={label} className="h-full w-full object-cover" />

      <button
        type="button"
        onClick={onRemove}
        aria-label={`Remover ${label.toLowerCase()}`}
        className="absolute right-1 top-1 flex h-5 w-5 items-center justify-center rounded-full bg-white text-slate-700 shadow md:h-6 md:w-6"
      >
        <X size={12} className="md:size-3.5" />
      </button>
    </div>
  );
}

/** Foto nova escolhida no formulário: já reduzida e com a prévia local. */
type NewPhoto = { file: File; preview: string };

type AdFormProps = {
  categories: Category[];
  /** Quando informado, o formulário edita este anúncio (PUT /anuncios/{id}). */
  ad?: AnuncioItem;
  /**
   * Anúncio usado como modelo ao duplicar: preenche o formulário, mas o envio cria um anúncio novo
   * (POST /anuncios). Ignorado quando `ad` é informado.
   */
  template?: AnuncioItem;
  /** Tela-pai: Início ao criar; de onde veio a edição (Histórico ou detalhe). */
  backHref: string;
  /** Aviso no topo do formulário (ex.: o anúncio foi criado, mas as fotos não subiram). */
  notice?: string;
};

export function AdForm({ categories, ad, template, backHref, notice }: AdFormProps) {
  const isEditing = ad !== undefined;
  const isCopy = !isEditing && template !== undefined;
  const initial = ad ?? template;
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
    initial?.tipo === "OFERTA" ? "oferecer" : "procurar"
  );
  const [category, setCategory] = useState(initial?.categoria.id ?? "");
  const [title, setTitle] = useState(initial?.titulo ?? "");
  const [description, setDescription] = useState(initial?.descricao ?? "");
  const [price, setPrice] = useState(
    initial?.preco != null ? String(initial.preco) : ""
  );
  const [negotiable, setNegotiable] = useState(
    initial !== undefined && initial.preco === null
  );
  const [paymentType, setPaymentType] = useState("por visita");
  const [availability, setAvailability] = useState("Esta semana");
  const [radius, setRadius] = useState("10 km");
  const [address, setAddress] = useState("");
  const [city, setCity] = useState(initial?.cidade ?? "");
  const [uf, setUf] = useState(initial?.uf ?? "");
  /** Fotos que o anúncio já tem (só na edição); as removidas são apagadas ao salvar. */
  const [removedPhotos, setRemovedPhotos] = useState<string[]>([]);
  const savedPhotos: AdPhoto[] = (ad?.fotos ?? []).filter(
    (photo) => !removedPhotos.includes(photo.id)
  );
  const [photos, setPhotos] = useState<NewPhoto[]>([]);
  const [photoError, setPhotoError] = useState<string | null>(null);
  const [preparingPhotos, setPreparingPhotos] = useState(false);
  const totalPhotos = savedPhotos.length + photos.length;

  const isOffering = adType === "oferecer";

  /** Reduz as fotos no aparelho (JPEG de até 1600 px): o envio fica leve até pelo celular. */
  async function handlePhotos(files: FileList | null) {
    if (!files || files.length === 0) return;

    const room = Math.max(0, MAX_AD_PHOTOS - totalPhotos);
    const selected = Array.from(files).slice(0, room);
    setPhotoError(
      files.length > room
        ? `O anúncio pode ter até ${MAX_AD_PHOTOS} fotos: ${files.length - room} ficaram de fora.`
        : null
    );

    setPreparingPhotos(true);
    const ready: NewPhoto[] = [];
    for (const [index, file] of selected.entries()) {
      try {
        const resized = await resizeImageForUpload(file, `foto-${Date.now()}-${index}`);
        ready.push({ file: resized, preview: URL.createObjectURL(resized) });
      } catch {
        setPhotoError(`Não foi possível abrir "${file.name}". Escolha fotos em JPG ou PNG.`);
      }
    }
    setPhotos((current) => [...current, ...ready].slice(0, MAX_AD_PHOTOS));
    setPreparingPhotos(false);
  }

  function removePhoto(index: number) {
    setPhotos((current) => {
      URL.revokeObjectURL(current[index].preview);
      return current.filter((_, i) => i !== index);
    });
  }

  /** As fotos não fazem parte dos campos do formulário: entram no envio aqui. */
  function submitWithPhotos(formData: FormData) {
    for (const photo of photos) formData.append("fotos", photo.file);
    for (const id of removedPhotos) formData.append("removerFoto", id);
    formAction(formData);
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

  const typeOptions = [
    {
      value: "procurar" as const,
      icon: Search,
      title: "Procuro um profissional",
      text: "Preciso contratar alguém para um serviço",
      availability: "Esta semana",
    },
    {
      value: "oferecer" as const,
      icon: BriefcaseBusiness,
      title: "Ofereço meus serviços",
      text: "Quero ser contratado por clientes",
      availability: "Hoje",
    },
  ];

  const submitLabel = pending
    ? isEditing
      ? "Salvando..."
      : "Publicando..."
    : isEditing
      ? "Salvar alterações"
      : "Publicar anúncio";

  const heading = isEditing ? "Editar anúncio" : isCopy ? "Duplicar anúncio" : "Criar anúncio";

  return (
    <main className="min-h-screen bg-white text-slate-900 md:bg-fundo md:px-6 md:py-10 lg:px-10">
      <div className="mx-auto min-h-screen w-full max-w-md bg-white pb-24 md:min-h-0 md:overflow-hidden md:rounded-t-2xl md:border md:border-b-0 md:border-slate-200 md:pb-6 lg:max-w-5xl lg:pb-8">
        <header className="flex h-14.5 items-center justify-between px-4 md:h-16 lg:h-auto lg:justify-start lg:gap-3 lg:border-b lg:border-slate-100 lg:px-8 lg:py-6">
          <BackButton href={backHref} />

          <div className="lg:flex lg:flex-col">
            <h1 className="text-[11px] font-semibold md:text-base lg:text-2xl lg:font-bold lg:tracking-tight">
              {heading}
            </h1>
            <p className="hidden text-sm text-slate-500 lg:block">
              {isEditing
                ? "Atualize as informações do seu anúncio."
                : "Preencha as informações e publique para clientes e profissionais da sua cidade."}
            </p>
          </div>

          <div className="w-8 lg:hidden" />
        </header>

        <form ref={formRef} action={submitWithPhotos} className="px-4 md:px-6 lg:px-8 lg:pt-6">
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

          <fieldset
            disabled={pending}
            className="min-w-0 lg:grid lg:grid-cols-[minmax(0,1fr)_minmax(0,21rem)] lg:gap-x-10"
          >
            {isCopy && (
              <p className="mb-3 rounded-lg bg-blue-50 px-3 py-2 text-[11px] leading-4 text-blue-700 md:text-sm md:leading-5 lg:col-span-2 lg:mb-5">
                Copiamos os dados de <strong>“{template?.titulo}”</strong>. Revise o que
                quiser mudar e publique: o anúncio original continua igual. As fotos não são
                copiadas.
              </p>
            )}

            {notice && (
              <p
                role="status"
                className="mb-3 rounded-lg bg-amber-50 px-3 py-2 text-[11px] leading-4 text-amber-800 md:text-sm md:leading-5 lg:col-span-2 lg:mb-5"
              >
                {notice}
              </p>
            )}

            {state?.error && (
              <p
                role="alert"
                className="mb-3 rounded-lg bg-red-50 px-3 py-2 text-[11px] text-red-600 md:text-sm lg:col-span-2 lg:mb-5"
              >
                {state.error}
              </p>
            )}

            {/* Coluna principal (lg+): o que é o anúncio. */}
            <div className="min-w-0">
              <section>
                <h2 className="mb-2 text-[9px] font-medium md:text-sm">
                  O que você deseja fazer?
                </h2>

                <div className="grid grid-cols-2 gap-2 md:gap-3">
                  {typeOptions.map(({ value, icon: Icon, title: optionTitle, text, availability: firstAvailability }) => {
                    const selected = adType === value;

                    return (
                      <button
                        key={value}
                        type="button"
                        aria-pressed={selected}
                        onClick={() => {
                          setAdType(value);
                          setAvailability(firstAvailability);
                        }}
                        className={`relative flex min-h-22.75 flex-col rounded-xl border p-2 text-left transition md:p-4 ${
                          selected
                            ? "border-blue-500 bg-blue-50/50 ring-1 ring-blue-500"
                            : "border-slate-200 bg-white md:hover:border-slate-300 md:hover:bg-slate-50"
                        }`}
                      >
                        <span className="flex items-center justify-between">
                          <span
                            className={`flex h-7 w-7 items-center justify-center rounded-lg md:h-10 md:w-10 ${
                              selected
                                ? "bg-blue-600 text-white"
                                : "bg-slate-100 text-slate-500"
                            }`}
                          >
                            <Icon size={16} className="md:size-5" />
                          </span>

                          <span
                            className={`flex h-3 w-3 items-center justify-center rounded-full border md:h-4 md:w-4 ${
                              selected ? "border-blue-600" : "border-slate-300"
                            }`}
                          >
                            {selected && (
                              <span className="h-1.5 w-1.5 rounded-full bg-blue-600 md:h-2 md:w-2" />
                            )}
                          </span>
                        </span>

                        <span className="mt-2 text-[9px] font-semibold md:mt-3 md:text-sm">
                          {optionTitle}
                        </span>

                        <span className="mt-1 text-[8px] leading-3 text-slate-500 md:text-xs md:leading-4">
                          {text}
                        </span>
                      </button>
                    );
                  })}
                </div>
              </section>

              <section className="mt-4 md:mt-6">
                <div className="mb-1 flex items-center justify-between md:mb-1.5">
                  <label htmlFor="titulo" className={LABEL}>
                    {isOffering ? "Nome do prestador" : "Título do anúncio"}
                  </label>

                  <span className={HINT}>
                    {title.length}/{MAX_TITLE}
                  </span>
                </div>

                <div className={FIELD}>
                  <Pencil size={12} className={ICON} />

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
                    className={`w-full bg-transparent outline-none placeholder:text-slate-400 ${CONTROL}`}
                  />
                </div>
              </section>

              <section className="mt-3 md:mt-5">
                <label
                  htmlFor="categoriaId"
                  className={`mb-1 block md:mb-1.5 ${LABEL}`}
                >
                  Tipo de serviço
                </label>

                <div className={`relative ${FIELD}`}>
                  <LayoutGrid size={12} className={ICON} />

                  <select
                    id="categoriaId"
                    name="categoriaId"
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                    required
                    className={`h-full w-full appearance-none bg-transparent px-2 text-slate-600 outline-none md:px-0 ${CONTROL}`}
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
                    className="pointer-events-none text-slate-400 md:size-4"
                  />
                </div>
              </section>

              <section className="mt-3 md:mt-5">
                <div className="mb-1 flex items-center justify-between md:mb-1.5">
                  <label htmlFor="descricao" className={LABEL}>
                    Descrição detalhada
                  </label>

                  <span className={HINT}>
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
                  className="min-h-18.5 w-full resize-none rounded-lg border border-slate-200 bg-slate-50 p-2.5 text-[9px] leading-3.5 outline-none placeholder:text-slate-400 focus:border-blue-500 md:min-h-36 md:p-3 md:text-sm md:leading-6 md:focus:bg-white lg:min-h-44 lg:resize-y"
                />

                <p className={`mt-1 ${HINT}`}>
                  {isOffering
                    ? "Anúncios completos passam mais confiança."
                    : "Quanto mais detalhes, melhores as respostas."}
                </p>
              </section>

              <section className="mt-3 md:mt-5">
                <label
                  htmlFor="preco-visivel"
                  className={`mb-1 block md:mb-1.5 ${LABEL}`}
                >
                  {isOffering ? "Quanto você cobra?" : "Quanto pretende pagar?"}
                </label>

                <div
                  className={`${FIELD} lg:max-w-sm ${negotiable ? "opacity-50" : ""}`}
                >
                  <span className="text-[9px] font-semibold text-slate-700 md:text-sm">
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
                    className={`min-w-0 flex-1 bg-transparent outline-none placeholder:text-slate-400 ${CONTROL}`}
                  />

                  {isOffering && (
                    <select
                      value={paymentType}
                      onChange={(e) => setPaymentType(e.target.value)}
                      aria-label="Como o valor é cobrado"
                      className="rounded-md border border-slate-200 bg-white px-1.5 py-1 text-[8px] outline-none md:px-2 md:text-xs"
                    >
                      <option>por visita</option>
                      <option>por hora</option>
                      <option>por dia</option>
                      <option>por serviço</option>
                    </select>
                  )}
                </div>

                <label className="mt-2 flex cursor-pointer items-center gap-2 md:mt-3">
                  <input
                    type="checkbox"
                    checked={negotiable}
                    onChange={(e) => setNegotiable(e.target.checked)}
                    className="h-3 w-3 accent-blue-600 md:h-4 md:w-4"
                  />

                  <span className="text-[8px] text-slate-600 md:text-sm">
                    {isOffering
                      ? "Prefiro combinar o valor com o cliente"
                      : "Prefiro combinar o valor com o profissional"}
                  </span>
                </label>
              </section>
            </div>

            {/* Coluna lateral (lg+): quando, onde e fotos. */}
            <div className="min-w-0 lg:border-l lg:border-slate-100 lg:pl-10">
              <section className="mt-4 md:mt-6 lg:mt-0">
                <p className={`mb-1.5 md:mb-2 ${LABEL}`}>
                  {isOffering
                    ? "Quando você pode atender?"
                    : "Para quando você precisa?"}
                </p>

                <div className="grid grid-cols-3 gap-1.5 md:gap-2">
                  {availabilityOptions.map(({ label, icon: Icon }) => (
                    <button
                      key={label}
                      type="button"
                      aria-pressed={availability === label}
                      onClick={() => setAvailability(label)}
                      className={`flex h-10.5 items-center justify-center gap-1.5 rounded-lg border text-[8px] transition md:h-11 md:text-xs md:font-medium ${
                        availability === label
                          ? "border-blue-500 bg-blue-50 text-blue-600"
                          : "border-slate-200 bg-white text-slate-600 md:hover:bg-slate-50"
                      }`}
                    >
                      <Icon size={12} className="md:size-3.5" />
                      {label}
                    </button>
                  ))}
                </div>

                <p className={`mt-1.5 leading-3 md:mt-2 md:leading-4 ${HINT}`}>
                  {isOffering
                    ? "Os clientes verão quando você pode começar o serviço."
                    : "Pedidos urgentes aparecem em destaque para os profissionais da sua região."}
                </p>
              </section>

              <section className="mt-4 md:mt-6">
                <p className={`mb-1.5 md:mb-2 ${LABEL}`}>
                  {isOffering ? "Cidade onde você atende" : "Cidade do serviço"}
                </p>

                <div className="grid grid-cols-[1fr_4.5rem] gap-2 md:grid-cols-[1fr_5.5rem]">
                  <div className={FIELD}>
                    <MapPin size={13} className={ICON} />

                    <input
                      name="cidade"
                      type="text"
                      maxLength={100}
                      required
                      value={city}
                      onChange={(e) => setCity(e.target.value)}
                      placeholder="Ex.: Salvador"
                      aria-label="Cidade"
                      className={`w-full min-w-0 bg-transparent outline-none placeholder:text-slate-400 ${CONTROL}`}
                    />
                  </div>

                  <div className="relative flex h-9 items-center rounded-lg border border-slate-200 bg-slate-50 pl-2.5 pr-1.5 md:h-11 md:pl-3 md:pr-2 md:focus-within:border-blue-500">
                    <select
                      name="uf"
                      value={uf}
                      onChange={(e) => setUf(e.target.value)}
                      required
                      aria-label="Estado"
                      className={`h-full w-full appearance-none bg-transparent text-slate-600 outline-none ${CONTROL}`}
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
                      className="pointer-events-none shrink-0 text-slate-400 md:size-4"
                    />
                  </div>
                </div>
              </section>

              {isOffering && (
                <section className="mt-4 md:mt-6">
                  <label
                    htmlFor="endereco"
                    className={`mb-1.5 block md:mb-2 ${LABEL}`}
                  >
                    Onde você atende?
                  </label>

                  <div className={FIELD}>
                    <MapPin size={13} className={ICON} />

                    <input
                      id="endereco"
                      type="text"
                      value={address}
                      onChange={(e) => setAddress(e.target.value)}
                      placeholder="Seu bairro ou CEP"
                      className={`w-full bg-transparent outline-none placeholder:text-slate-400 ${CONTROL}`}
                    />
                  </div>

                  <div className="mt-2 flex flex-wrap gap-1.5 md:mt-3 md:gap-2">
                    {["5 km", "10 km", "20 km", "Toda a cidade"].map((item) => (
                      <button
                        key={item}
                        type="button"
                        aria-pressed={radius === item}
                        onClick={() => setRadius(item)}
                        className={`rounded-full border px-2 py-1 text-[8px] md:px-3 md:py-1.5 md:text-xs md:font-medium ${
                          radius === item
                            ? "border-blue-500 bg-blue-50 text-blue-600"
                            : "border-slate-200 text-slate-600 md:hover:bg-slate-50"
                        }`}
                      >
                        {item}
                      </button>
                    ))}
                  </div>

                  <p className={`mt-1.5 md:mt-2 ${HINT}`}>
                    Seu anúncio aparece para clientes dentro dessa área.
                  </p>
                </section>
              )}

              <section className="mt-4 md:mt-6">
                <div className="mb-1.5 flex items-center justify-between md:mb-2">
                  <p className={LABEL}>
                    {isOffering ? "Fotos dos seus trabalhos" : "Fotos"}
                  </p>

                    <span className={HINT}>
                    {totalPhotos > 0 ? `${totalPhotos}/${MAX_AD_PHOTOS}` : "Opcional"}
                  </span>
                </div>

                <div className="grid grid-cols-3 gap-1.5 md:gap-2">
                  {totalPhotos < MAX_AD_PHOTOS && (
                    <label
                      className={`relative flex aspect-square cursor-pointer flex-col items-center justify-center gap-1 rounded-lg border border-dashed border-blue-300 bg-blue-50/50 text-blue-600 transition md:hover:bg-blue-50 ${
                        preparingPhotos ? "pointer-events-none opacity-60" : ""
                      }`}
                    >
                      <ImagePlus size={15} className="md:size-5" />
                      <span className="text-[8px] font-medium md:text-xs">
                        {preparingPhotos ? "Preparando..." : "Adicionar"}
                      </span>

                      <input
                        type="file"
                        accept="image/*"
                        multiple
                        disabled={preparingPhotos}
                        onChange={(e) => {
                          void handlePhotos(e.target.files);
                          e.target.value = "";
                        }}
                        className="hidden"
                      />
                    </label>
                  )}

                  {savedPhotos.map((photo, index) => (
                    <PhotoThumb
                      key={photo.id}
                      src={photo.src}
                      label={`Foto ${index + 1}`}
                      onRemove={() => setRemovedPhotos((current) => [...current, photo.id])}
                    />
                  ))}

                  {photos.map((photo, index) => (
                    <PhotoThumb
                      key={photo.preview}
                      src={photo.preview}
                      label={`Foto ${savedPhotos.length + index + 1}`}
                      onRemove={() => removePhoto(index)}
                    />
                  ))}

                  {Array.from({ length: Math.max(0, 2 - totalPhotos) }).map(
                    (_, index) => (
                      <div
                        key={`empty-${index}`}
                        className="flex aspect-square items-center justify-center rounded-lg border border-dashed border-slate-200 bg-slate-50"
                      >
                        <ImageIcon size={14} className="text-slate-300 md:size-5" />
                      </div>
                    )
                  )}
                </div>

                {photoError && (
                  <p role="alert" className="mt-1.5 text-[8px] text-red-600 md:mt-2 md:text-xs">
                    {photoError}
                  </p>
                )}

                <p className={`mt-1.5 md:mt-2 ${HINT}`}>
                  Até {MAX_AD_PHOTOS} fotos · JPG, PNG ou WEBP
                </p>
              </section>
            </div>
          </fieldset>
        </form>
      </div>

      <footer className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-4 pb-[max(12px,env(safe-area-inset-bottom))] pt-2 md:sticky md:mx-auto md:max-w-md md:rounded-b-2xl md:border md:border-slate-200 md:px-6 md:pb-4 md:pt-4 md:shadow-sm lg:max-w-5xl lg:px-8">
        <div className="mx-auto max-w-md lg:mx-0 lg:flex lg:max-w-none lg:items-center lg:justify-end lg:gap-3">
          <Link
            href={backHref}
            className="hidden rounded-lg px-5 py-3 text-sm font-semibold text-slate-600 transition hover:bg-slate-100 lg:block"
          >
            Cancelar
          </Link>

          <div className="lg:w-56">
            <Button
              text={submitLabel}
              type="button"
              onClick={() => {
                if (!pending && !preparingPhotos) formRef.current?.requestSubmit();
              }}
            />
          </div>
        </div>
      </footer>
    </main>
  );
}
