"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import {
  ArrowLeft,
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
  X
} from "lucide-react";
import Button from "@/components/Button";

type AdType = "procurar" | "oferecer";

export default function CreateAd() {
  const router = useRouter();

  const [adType, setAdType] = useState<AdType>("procurar");
  const [category, setCategory] = useState("");
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [negotiable, setNegotiable] = useState(false);
  const [paymentType, setPaymentType] = useState("por visita");
  const [availability, setAvailability] = useState("Esta semana");
  const [radius, setRadius] = useState("10 km");
  const [address, setAddress] = useState("");
  const [photos, setPhotos] = useState<File[]>([]);

  const categories = [
    "Eletricista",
    "Encanador",
    "Diarista",
    "Pedreiro",
    "Pintor",
    "Montador de móveis",
    "Jardineiro",
    "Técnico de informática",
    "Outro",
  ];

  const isOffering = adType === "oferecer";

  function handlePhotos(files: FileList | null) {
    if (!files) return;

    const selected = Array.from(files).slice(
      0,
      Math.max(0, 6 - photos.length)
    );

    setPhotos((current) => [...current, ...selected]);
  }

  function removePhoto(index: number) {
    setPhotos((current) =>
      current.filter((_, i) => i !== index)
    );
  }

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!category || !title.trim() || !description.trim()) {
      alert("Preencha o título, a categoria e a descrição.");
      return;
    }

    alert("Anúncio preenchido!");
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
          <button
            type="button"
            onClick={() => router.back()}
            aria-label="Voltar"
            className="flex h-8 w-8 items-center justify-center rounded-lg border border-slate-200"
          >
            <ArrowLeft size={17} />
          </button>

          <h1 className="text-[11px] font-semibold">
            Criar anúncio
          </h1>

          <div className="w-8" />
        </header>

        <form onSubmit={handleSubmit} className="px-4">

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
                  <span className={`flex h-7 w-7 items-center justify-center rounded-lg ${
                    !isOffering
                      ? "bg-blue-600 text-white"
                      : "bg-slate-100 text-slate-500"
                  }`}>
                    <Search size={16} />
                  </span>

                  <span className={`flex h-3 w-3 items-center justify-center rounded-full border ${
                    !isOffering
                      ? "border-blue-600"
                      : "border-slate-300"
                  }`}>
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
                  <span className={`flex h-7 w-7 items-center justify-center rounded-lg ${
                    isOffering
                      ? "bg-blue-600 text-white"
                      : "bg-slate-100 text-slate-500"
                  }`}>
                    <BriefcaseBusiness size={16} />
                  </span>

                  <span className={`flex h-3 w-3 items-center justify-center rounded-full border ${
                    isOffering
                      ? "border-blue-600"
                      : "border-slate-300"
                  }`}>
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
              <label className="text-[9px] font-medium">
                Título do anúncio
              </label>

              <span className="text-[8px] text-slate-400">
                {title.length}/60
              </span>
            </div>

            <div className="flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-2.5">
              <Pencil size={12} className="shrink-0 text-slate-400" />

              <input
                type="text"
                maxLength={60}
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder={
                  isOffering
                    ? "Ex.: Eletricista residencial e predial"
                    : "Ex.: Eletricista para instalar chuveiro"
                }
                className="w-full bg-transparent text-[9px] outline-none placeholder:text-slate-400"
              />
            </div>
          </section>

          <section className="mt-3">
            <label className="mb-1 block text-[9px] font-medium">
              Tipo de serviço
            </label>

            <div className="relative flex h-9 items-center rounded-lg border border-slate-200 bg-slate-50 px-2.5">
              <LayoutGrid size={12} className="shrink-0 text-slate-400" />

              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                required
                className="h-full w-full appearance-none bg-transparent px-2 text-[9px] text-slate-600 outline-none"
              >
                <option value="">Selecione a categoria</option>

                {categories.map((item) => (
                  <option key={item} value={item}>
                    {item}
                  </option>
                ))}
              </select>

              <ChevronDown size={13} className="pointer-events-none text-slate-400" />
            </div>
          </section>

          <section className="mt-3">
            <div className="mb-1 flex items-center justify-between">
              <label className="text-[9px] font-medium">
                Descrição detalhada
              </label>

              <span className="text-[8px] text-slate-400">
                {description.length}/1000
              </span>
            </div>

            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              maxLength={1000}
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
            <label className="mb-1 block text-[9px] font-medium">
              {isOffering
                ? "Quanto você cobra?"
                : "Quanto pretende pagar?"}
            </label>

            <div className="flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-2.5">

              <span className="text-[9px] font-semibold text-slate-700">
                R$
              </span>

              <input
                type="number"
                min="0"
                step="0.01"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                placeholder="0,00"
                className="min-w-0 flex-1 bg-transparent text-[9px] outline-none placeholder:text-slate-400"
              />

              {isOffering && (
                <select
                  value={paymentType}
                  onChange={(e) => setPaymentType(e.target.value)}
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
            <label className="mb-1.5 block text-[9px] font-medium">
              {isOffering
                ? "Quando você pode atender?"
                : "Para quando você precisa?"}
            </label>

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

          {isOffering && (
            <section className="mt-4">
              <label className="mb-1.5 block text-[9px] font-medium">
                Onde você atende?
              </label>

              <div className="flex h-9 items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-2.5">
                <MapPin size={13} className="shrink-0 text-slate-400" />

                <input
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
              <label className="text-[9px] font-medium">
                {isOffering
                  ? "Fotos dos seus trabalhos"
                  : "Fotos"}
              </label>

              <span className="text-[8px] text-slate-400">
                Opcional
              </span>
            </div>

            <div className="grid grid-cols-3 gap-1.5">

              <label className="relative flex aspect-square cursor-pointer flex-col items-center justify-center gap-1 rounded-lg border border-dashed border-blue-300 bg-blue-50/50 text-blue-600">
                <ImagePlus size={15} />
                <span className="text-[8px] font-medium">
                  Adicionar
                </span>

                <input
                  type="file"
                  accept="image/*"
                  multiple
                  disabled={photos.length >= 6}
                  onChange={(e) => {
                    handlePhotos(e.target.files);
                    e.target.value = "";
                  }}
                  className="hidden"
                />
              </label>

              {photos.slice(0, 2).map((photo, index) => (
                <div
                  key={`${photo.name}-${index}`}
                  className="relative aspect-square overflow-hidden rounded-lg border border-slate-200"
                >
                  <img
                    src={URL.createObjectURL(photo)}
                    alt={`Foto ${index + 1}`}
                    className="h-full w-full object-cover"
                  />

                  <button
                    type="button"
                    onClick={() => removePhoto(index)}
                    aria-label="Remover foto"
                    className="absolute right-1 top-1 flex h-5 w-5 items-center justify-center rounded-full bg-white text-slate-700 shadow"
                  >
                    <X size={12} />
                  </button>
                </div>
              ))}

              {Array.from({
                length: Math.max(0, 2 - Math.min(photos.length, 2)),
              }).map((_, index) => (
                <div
                  key={`empty-${index}`}
                  className="flex aspect-square items-center justify-center rounded-lg border border-dashed border-slate-200 bg-slate-50"
                >
                  <ImageIcon size={14} className="text-slate-300" />
                </div>
              ))}
            </div>

            <p className="mt-1.5 text-[8px] text-slate-400">
              Até 6 fotos · JPG ou PNG, até 5 MB cada
            </p>
          </section>

        </form>
      </div>

      <footer className="fixed inset-x-0 bottom-0 z-20 border-t border-slate-100 bg-white px-4 pb-[max(12px,env(safe-area-inset-bottom))] pt-2">
        <div className="mx-auto max-w-md">
          <Button text="Publicar anúncio" type="button" onClick={() => {
              const form = document.querySelector("form");
              form?.requestSubmit();
            }}/>
        </div>
      </footer>
    </main>
  );
}
