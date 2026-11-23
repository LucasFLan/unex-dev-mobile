import { useId, type InputHTMLAttributes, type ReactNode } from "react";

export type InputProps = {
  labelText: string;
  icon?: ReactNode;
  /** Conteúdo à direita, dentro do campo (ex.: botão de mostrar a senha). */
  trailing?: ReactNode;
} & InputHTMLAttributes<HTMLInputElement>;

const Input = ({ labelText, icon, trailing, id, ...props }: InputProps) => {
  const generatedId = useId();
  const inputId = id ?? generatedId;

  return (
    <>
      <label htmlFor={inputId} className="text-sm font-medium text-gray-700">
        {labelText}
      </label>

      <div className="flex h-9.75 items-center gap-2.5 rounded-[10px] border border-slate-200 bg-slate-50 px-3.5 transition focus-within:border-blue-500 focus-within:ring-2 focus-within:ring-blue-100">
        {icon && (
          <span className="shrink-0 text-slate-400" aria-hidden="true">
            {icon}
          </span>
        )}

        <input
          required
          {...props}
          id={inputId}
          className="h-full min-w-0 flex-1 bg-transparent text-xs text-slate-800 outline-none placeholder:text-slate-400"
        />

        {trailing}
      </div>
    </>
  );
};

export default Input;