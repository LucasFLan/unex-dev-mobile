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
    <section className="mt-4" aria-labelledby="titulo-categorias">
      <div className="mb-3 flex h-5 items-center justify-between px-5">
        <h2 id="titulo-categorias" className="text-xs font-bold">
          Categorias
        </h2>

        {active && (
          <button
            type="button"
            onClick={() => onSelect(null)}
            className="text-[11px] font-medium text-blue-600"
          >
            Limpar
          </button>
        )}
      </div>

      <ul className="flex gap-3 overflow-x-auto px-5 pb-1.5 pt-1.5 scrollbar-none [&::-webkit-scrollbar]:hidden">
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
                className="flex w-14 flex-col items-center gap-1.5"
              >
                <span
                  className={`flex h-10 w-10 items-center justify-center rounded-xl ${bg} ${text} ${
                    selected ? "ring-2 ring-blue-600 ring-offset-2" : ""
                  }`}
                >
                  <Icon size={19} strokeWidth={1.8} aria-hidden="true" />
                </span>

                <span className="w-full truncate text-center text-[10px] text-slate-700">
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
