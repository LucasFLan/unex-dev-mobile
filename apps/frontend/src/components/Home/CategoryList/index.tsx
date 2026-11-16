import {
  CATEGORY_META,
  CATEGORY_NAMES,
  type CategoryName,
} from "@/lib/categories";

type CategoryListProps = {
  active: CategoryName | null;
  onSelect: (category: CategoryName | null) => void;
};

export function CategoryList({ active, onSelect }: CategoryListProps) {
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
        {CATEGORY_NAMES.map((name) => {
          const { icon: Icon, bg, text } = CATEGORY_META[name];
          const selected = active === name;

          return (
            <li key={name} className="shrink-0">
              <button
                type="button"
                onClick={() => onSelect(selected ? null : name)}
                aria-pressed={selected}
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
                  {name}
                </span>
              </button>
            </li>
          );
        })}
      </ul>
    </section>
  );
}