import Link from "next/link";
import {
  ArrowRight,
  CheckCircle2,
  Search,
  ShieldCheck,
  Star,
  Zap,
  PaintRoller,
  Wrench,
  Sparkles,
  Hammer,
  Laptop,
  TreePine,
  Users,
  Clock3,
  MapPin,
  Menu,
} from "lucide-react";
import Image from "next/image";
import Logo from "@/../public/logo.svg"

const categories = [
  { name: "Eletricista", icon: Zap, color: "bg-amber-100 text-amber-600" },
  { name: "Encanador", icon: Wrench, color: "bg-blue-100 text-blue-600" },
  { name: "Diarista", icon: Sparkles, color: "bg-purple-100 text-purple-600" },
  { name: "Pintor", icon: PaintRoller, color: "bg-pink-100 text-pink-600" },
  { name: "Pedreiro", icon: Hammer, color: "bg-orange-100 text-orange-600" },
  { name: "Informática", icon: Laptop, color: "bg-indigo-100 text-indigo-600" },
  { name: "Jardineiro", icon: TreePine, color: "bg-green-100 text-green-600" },
  { name: "Outros serviços", icon: Users, color: "bg-slate-100 text-slate-600" },
];

const steps = [
  {
    number: "01",
    title: "Encontre um profissional",
    description:
      "Escolha a categoria do serviço e encontre profissionais disponíveis na sua região.",
    icon: Search,
  },
  {
    number: "02",
    title: "Compare as opções",
    description:
      "Veja os anúncios, converse com profissionais e compare propostas.",
    icon: Users,
  },
  {
    number: "03",
    title: "Contrate com facilidade",
    description:
      "Escolha quem melhor atende à sua necessidade e combine os detalhes.",
    icon: CheckCircle2,
  },
];

export default function HomePage() {
  return (
    <main className="min-h-screen overflow-hidden bg-white text-slate-800">
      <header className="sticky top-0 z-50 border-b border-slate-100 bg-white/95 backdrop-blur">
        <div className="mx-auto flex h-19 max-w-7xl items-center justify-between px-5 lg:px-8">
          <div className="flex items-center gap-2">
            <Image src={Logo} alt="Logo ChamaAi" width={60} height={60}/>
            <span className="text-2xl font-extrabold tracking-tight text-slate-900">
              Chama<span className="text-blue-600">Aí</span>
            </span>
          </div>

          <nav className="hidden items-center gap-8 md:flex">
            <a href="#como-funciona" className="text-sm font-medium text-slate-600 transition hover:text-blue-600">
              Como funciona
            </a>
            <a href="#categorias" className="text-sm font-medium text-slate-600 transition hover:text-blue-600">
              Categorias
            </a>
            <a href="#vantagens" className="text-sm font-medium text-slate-600 transition hover:text-blue-600">
              Vantagens
            </a>
          </nav>

          <div className="hidden items-center gap-3 sm:flex">
            <Link
              href="/login"
              className="rounded-xl px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
            >
              Entrar
            </Link>
            <Link
              href="/register"
              className="rounded-xl bg-blue-600 px-5 py-3 text-sm font-semibold text-white shadow-lg shadow-blue-200 transition hover:bg-blue-700"
            >
              Criar conta
            </Link>
          </div>

          <button className="rounded-lg p-2 text-slate-700 sm:hidden" aria-label="Menu">
            <Menu size={24} />
          </button>
        </div>
      </header>

      <section className="relative isolate overflow-hidden bg-linear-to-br from-blue-50 via-white to-purple-50">
        <div className="pointer-events-none absolute -right-32 -top-32 h-96 w-96 rounded-full bg-blue-200/30 blur-3xl" />
        <div className="pointer-events-none absolute -bottom-32 left-0 h-80 w-80 rounded-full bg-purple-200/30 blur-3xl" />

        <div className="relative mx-auto grid max-w-7xl items-center gap-14 px-5 py-16 md:grid-cols-2 md:py-24 lg:px-8">
          <div>
            <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-blue-100 bg-white px-4 py-2 shadow-sm">
              <span className="h-2 w-2 rounded-full bg-green-500" />
              <span className="text-xs font-semibold text-blue-700">
                Serviços mais perto de você
              </span>
            </div>

            <h1 className="max-w-xl text-4xl font-extrabold leading-[1.12] tracking-tight text-slate-900 sm:text-5xl lg:text-6xl">
              Precisou de um serviço?
              <span className="mt-2 block text-blue-600">
                ChamaAí!
              </span>
            </h1>

            <p className="mt-6 max-w-lg text-base leading-7 text-slate-600 sm:text-lg">
              Encontre profissionais para resolver o que você precisa ou
              divulgue seus serviços para quem está procurando. Simples,
              prático e perto de você.
            </p>

            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <Link
                href="/register"
                className="inline-flex items-center justify-center gap-2 rounded-xl bg-blue-600 px-7 py-4 text-sm font-bold text-white shadow-xl shadow-blue-200 transition hover:-translate-y-1 hover:bg-blue-700"
              >
                Encontrar profissional
                <ArrowRight size={18} />
              </Link>

              <Link
                href="/register"
                className="inline-flex items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-7 py-4 text-sm font-bold text-slate-700 transition hover:border-blue-300 hover:text-blue-600"
              >
                Quero oferecer serviços
              </Link>
            </div>

            <div className="mt-9 flex flex-wrap items-center gap-x-6 gap-y-3 text-xs font-medium text-slate-500">
              <span className="flex items-center gap-1.5">
                <CheckCircle2 size={15} className="text-green-500" />
                Fácil de usar
              </span>
              <span className="flex items-center gap-1.5">
                <CheckCircle2 size={15} className="text-green-500" />
                Diversas categorias
              </span>
              <span className="flex items-center gap-1.5">
                <CheckCircle2 size={15} className="text-green-500" />
                Conexões locais
              </span>
            </div>
          </div>

          <div className="relative mx-auto w-full max-w-122.5">
            <div className="absolute -right-5 -top-8 h-40 w-40 rounded-full bg-purple-200/50 blur-3xl" />

            <div className="relative rounded-[28px] border border-slate-200 bg-white p-4 shadow-2xl shadow-blue-900/10 sm:p-6">
              <div className="mb-5 flex items-center justify-between">
                <div>
                  <p className="text-xs text-slate-400">Bem-vindo ao</p>
                  <p className="text-xl font-extrabold text-slate-900">
                    Chama<span className="text-blue-600">Aí</span>
                  </p>
                </div>
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-50 text-blue-600">
                  <Search size={20} />
                </div>
              </div>

              <div className="rounded-xl bg-slate-50 p-3">
                <p className="mb-2 text-xs font-semibold text-slate-700">
                  Qual serviço você precisa?
                </p>
                <div className="flex items-center gap-2 rounded-lg border border-slate-200 bg-white px-3 py-3">
                  <Search size={16} className="text-slate-400" />
                  <span className="text-xs text-slate-400">
                    Eletricista, pintor...
                  </span>
                </div>
              </div>

              <div className="mt-5 flex items-center justify-between">
                <h3 className="text-sm font-bold text-slate-800">
                  Categorias populares
                </h3>
                <span className="text-[10px] font-semibold text-blue-600">
                  Ver todas
                </span>
              </div>

              <div className="mt-3 grid grid-cols-4 gap-2">
                {categories.slice(0, 4).map((category) => {
                  const Icon = category.icon;
                  return (
                    <div
                      key={category.name}
                      className="flex flex-col items-center gap-2 rounded-xl border border-slate-100 bg-white p-2"
                    >
                      <div className={`flex h-10 w-10 items-center justify-center rounded-xl ${category.color}`}>
                        <Icon size={19} />
                      </div>
                      <span className="text-center text-[9px] font-medium text-slate-600">
                        {category.name}
                      </span>
                    </div>
                  );
                })}
              </div>

              <div className="mt-5">
                <div className="mb-3 flex items-center justify-between">
                  <h3 className="text-sm font-bold text-slate-800">
                    Profissionais disponíveis
                  </h3>
                  <span className="text-[10px] text-slate-400">
                    Exemplos
                  </span>
                </div>

                {[
                  { name: "Carlos Mendes", job: "Eletricista", initials: "CM", color: "bg-amber-100 text-amber-700" },
                  { name: "Mariana Souza", job: "Diarista", initials: "MS", color: "bg-purple-100 text-purple-700" },
                ].map((person) => (
                  <div
                    key={person.name}
                    className="mb-2 flex items-center gap-3 rounded-xl border border-slate-100 p-3"
                  >
                    <div className={`flex h-10 w-10 items-center justify-center rounded-full text-xs font-bold ${person.color}`}>
                      {person.initials}
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="text-xs font-bold text-slate-800">
                        {person.name}
                      </p>
                      <p className="text-[10px] text-slate-500">
                        {person.job}
                      </p>
                      <div className="mt-1 flex items-center gap-1 text-[9px] text-amber-500">
                        <Star size={10} fill="currentColor" />
                        <Star size={10} fill="currentColor" />
                        <Star size={10} fill="currentColor" />
                        <Star size={10} fill="currentColor" />
                        <Star size={10} fill="currentColor" />
                      </div>
                    </div>
                    <span className="rounded-lg bg-blue-50 px-2 py-1.5 text-[9px] font-semibold text-blue-600">
                      Ver perfil
                    </span>
                  </div>
                ))}
              </div>
            </div>

            <div className="absolute -bottom-5 -left-5 hidden items-center gap-3 rounded-2xl border border-slate-100 bg-white p-4 shadow-xl sm:flex">
              <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-green-100 text-green-600">
                <CheckCircle2 size={22} />
              </div>
              <div>
                <p className="text-xs font-bold text-slate-800">
                  Tudo mais fácil!
                </p>
                <p className="text-[10px] text-slate-500">
                  Encontre o serviço ideal
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section id="categorias" className="mx-auto max-w-7xl px-5 py-20 lg:px-8">
        <div className="mx-auto mb-12 max-w-xl text-center">
          <span className="text-xs font-bold uppercase tracking-widest text-blue-600">
            Encontre o que precisa
          </span>
          <h2 className="mt-3 text-3xl font-extrabold tracking-tight text-slate-900 sm:text-4xl">
            Serviços para facilitar seu dia a dia
          </h2>
          <p className="mt-4 text-sm leading-6 text-slate-500">
            Diversas categorias para você encontrar o profissional certo para
            cada necessidade.
          </p>
        </div>

        <div className="grid grid-cols-2 gap-3 sm:grid-cols-4 lg:gap-5">
          {categories.map((category) => {
            const Icon = category.icon;
            return (
              <Link
                href="/register"
                key={category.name}
                className="group rounded-2xl border border-slate-100 bg-white p-5 transition hover:-translate-y-1 hover:border-blue-200 hover:shadow-xl hover:shadow-blue-900/5 sm:p-7"
              >
                <div className={`mb-5 flex h-12 w-12 items-center justify-center rounded-2xl ${category.color}`}>
                  <Icon size={23} />
                </div>
                <h3 className="text-sm font-bold text-slate-800">
                  {category.name}
                </h3>
                <div className="mt-3 flex items-center gap-1 text-xs font-semibold text-blue-600 opacity-0 transition group-hover:opacity-100">
                  Encontrar <ArrowRight size={13} />
                </div>
              </Link>
            );
          })}
        </div>
      </section>

      <section id="como-funciona" className="bg-slate-50 py-20">
        <div className="mx-auto max-w-7xl px-5 lg:px-8">
          <div className="mx-auto mb-14 max-w-xl text-center">
            <span className="text-xs font-bold uppercase tracking-widest text-blue-600">
              É simples assim
            </span>
            <h2 className="mt-3 text-3xl font-extrabold tracking-tight text-slate-900 sm:text-4xl">
              Como funciona o ChamaAí?
            </h2>
            <p className="mt-4 text-sm leading-6 text-slate-500">
              Em poucos passos você encontra ajuda ou divulga seu trabalho.
            </p>
          </div>

          <div className="grid gap-8 md:grid-cols-3">
            {steps.map((step) => {
              const Icon = step.icon;
              return (
                <div key={step.number} className="relative rounded-2xl border border-slate-100 bg-white p-7 shadow-sm">
                  <div className="mb-6 flex items-center justify-between">
                    <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-blue-50 text-blue-600">
                      <Icon size={26} />
                    </div>
                    <span className="text-4xl font-black text-slate-100">
                      {step.number}
                    </span>
                  </div>
                  <h3 className="text-lg font-bold text-slate-900">
                    {step.title}
                  </h3>
                  <p className="mt-3 text-sm leading-6 text-slate-500">
                    {step.description}
                  </p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      <section id="vantagens" className="mx-auto grid max-w-7xl items-center gap-12 px-5 py-20 md:grid-cols-2 lg:px-8">
        <div>
          <span className="text-xs font-bold uppercase tracking-widest text-blue-600">
            Muito mais praticidade
          </span>
          <h2 className="mt-3 text-3xl font-extrabold leading-tight text-slate-900 sm:text-4xl">
            Uma maneira mais simples de conectar pessoas e serviços.
          </h2>
          <p className="mt-5 text-sm leading-7 text-slate-500">
            O ChamaAí foi pensado para aproximar clientes e profissionais,
            facilitando a divulgação de serviços e a busca por quem pode ajudar.
          </p>

          <div className="mt-8 space-y-5">
            {[
              {
                icon: MapPin,
                title: "Profissionais da sua região",
                text: "Encontre serviços próximos de você.",
              },
              {
                icon: Clock3,
                title: "Mais praticidade",
                text: "Pesquise e compare opções em um só lugar.",
              },
              {
                icon: ShieldCheck,
                title: "Mais informações",
                text: "Consulte os perfis e detalhes dos anúncios.",
              },
            ].map((item) => {
              const Icon = item.icon;
              return (
                <div key={item.title} className="flex gap-4">
                  <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-blue-50 text-blue-600">
                    <Icon size={21} />
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-slate-800">
                      {item.title}
                    </h3>
                    <p className="mt-1 text-xs text-slate-500">
                      {item.text}
                    </p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <div className="relative rounded-[28px] bg-linear-to-br from-blue-600 to-indigo-700 p-8 text-white shadow-2xl shadow-blue-200 sm:p-12">
          <div className="absolute right-0 top-0 h-48 w-48 rounded-full bg-white/10 blur-3xl" />
          <div className="relative">
            <div className="mb-8 flex h-14 w-14 items-center justify-center rounded-2xl bg-white/15">
              <Users size={29} />
            </div>
            <h3 className="max-w-sm text-3xl font-extrabold leading-tight">
              Seu próximo serviço começa com um ChamaAí.
            </h3>
            <p className="mt-5 max-w-sm text-sm leading-7 text-blue-100">
              Seja para encontrar ajuda ou conquistar novos clientes, dê o
              primeiro passo.
            </p>
            <Link
              href="/register"
              className="mt-8 inline-flex items-center gap-2 rounded-xl bg-white px-6 py-3.5 text-sm font-bold text-blue-700 transition hover:bg-blue-50"
            >
              Começar agora
              <ArrowRight size={17} />
            </Link>
          </div>
        </div>
      </section>

      <section className="px-5 pb-20">
        <div className="mx-auto max-w-7xl overflow-hidden rounded-[28px] bg-slate-900 px-6 py-14 text-center sm:px-12">
          <span className="text-xs font-bold uppercase tracking-widest text-blue-300">
            Faça parte
          </span>
          <h2 className="mx-auto mt-4 max-w-2xl text-3xl font-extrabold text-white sm:text-4xl">
            Está procurando um profissional ou novos clientes?
          </h2>
          <p className="mx-auto mt-4 max-w-xl text-sm leading-7 text-slate-300">
            Cadastre-se no ChamaAí e descubra uma forma mais prática de
            conectar serviços e pessoas.
          </p>
          <Link
            href="/register"
            className="mt-8 inline-flex items-center gap-2 rounded-xl bg-blue-600 px-7 py-4 text-sm font-bold text-white transition hover:bg-blue-500"
          >
            Criar minha conta
            <ArrowRight size={18} />
          </Link>
        </div>
      </section>

      <footer className="border-t border-slate-100 bg-white">
        <div className="mx-auto flex max-w-7xl flex-col items-center justify-between gap-5 px-5 py-8 sm:flex-row lg:px-8">
          <Link href="/" className="text-xl font-extrabold text-slate-900">
            Chama<span className="text-blue-600">Aí</span>
          </Link>

          <p className="text-center text-xs text-slate-400">
            © {new Date().getFullYear()} ChamaAí. Todos os direitos reservados.
          </p>

          <div className="flex gap-5 text-xs font-medium text-slate-500">
            <a href="#como-funciona" className="hover:text-blue-600">
              Como funciona
            </a>
            <a href="#categorias" className="hover:text-blue-600">
              Categorias
            </a>
          </div>
        </div>
      </footer>
    </main>
  );
}
