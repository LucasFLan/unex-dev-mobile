import { getCategoryMeta } from "@/lib/categories";
import type { Category } from "@/lib/categories-api";

type CategoryListProps = {
  /** Categorias vindas de GET /categorias. */
  categories: Category[];
  /** Slug da categoria selecionada. */
  active: string | null;
  onSelect: (slug: string | null) => void;
};

export function CategoryList({ categories, active, onSelect }: CategoryListProps) {
  return (
    <section className="mt-4 lg:mt-8" aria-labelledby="titulo-categorias">
      <div className="mb-3 flex h-5 items-center justify-between px-5">
        <h2 id="titulo-categorias" className="text-xs font-bold lg:text-lg">
          Categorias
        </h2>

        {active && (
          <button
            type="button"
            onClick={() => onSelect(null)}
            className="text-[11px] font-medium text-blue-600 lg:text-sm"
          >
            Limpar
          </button>
        )}
      </div>

      <ul className="flex gap-3 overflow-x-auto px-5 pb-1.5 pt-1.5 scrollbar-none [&::-webkit-scrollbar]:hidden lg:mt-4 lg:grid lg:grid-cols-[repeat(auto-fill,minmax(7.5rem,1fr))] lg:gap-3 lg:overflow-visible">
        {categories.map(({ id, slug, nome }) => {
          const { icon: Icon, bg, text } = getCategoryMeta(slug);
          const selected = active === slug;

          return (
            <li key={id} className="shrink-0">
              <button
                type="button"
                onClick={() => onSelect(selected ? null : slug)}
                aria-pressed={selected}
                title={nome}
                className="flex w-14 flex-col items-center gap-1.5 lg:w-full lg:gap-2 lg:rounded-2xl lg:border lg:border-slate-100 lg:py-4 lg:transition lg:hover:border-blue-200 lg:hover:shadow-md"
              >
                <span
                  className={`flex h-10 w-10 items-center justify-center rounded-xl lg:h-12 lg:w-12 ${bg} ${text} ${
                    selected ? "ring-2 ring-blue-600 ring-offset-2" : ""
                  }`}
                >
                  <Icon size={19} strokeWidth={1.8} aria-hidden="true" />
                </span>

                <span className="w-full truncate text-center text-[10px] text-slate-700 lg:px-2 lg:text-xs lg:font-medium">
                  {nome}
                </span>
              </button>
            </li>
          );
        })}
      </ul>
    </section>
  );
}
