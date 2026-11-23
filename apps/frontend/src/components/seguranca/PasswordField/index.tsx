"use client";

import { useState } from "react";
import { Eye, EyeOff, LockKeyhole } from "lucide-react";
import Input from "@/components/Input";

type PasswordFieldProps = {
  labelText: string;
  name: string;
  placeholder?: string;
  autoComplete?: string;
  /** Classes do bloco (rótulo + campo). */
  className?: string;
};

/**
 * Campo de senha com o botão de mostrar/ocultar dentro do campo. O olho nativo do navegador (Edge) fica
 * escondido no globals.css: ele some quando o campo perde o foco.
 */
export function PasswordField({
  labelText,
  name,
  placeholder,
  autoComplete,
  className = "flex flex-col",
}: PasswordFieldProps) {
  const [visible, setVisible] = useState(false);

  return (
    <div className={className}>
      <Input
        labelText={labelText}
        name={name}
        type={visible ? "text" : "password"}
        placeholder={placeholder}
        autoComplete={autoComplete}
        icon={<LockKeyhole size={17} />}
        trailing={
          <button
            type="button"
            onClick={() => setVisible((current) => !current)}
            aria-label={visible ? "Ocultar senha" : "Mostrar senha"}
            aria-pressed={visible}
            className="-mr-1 flex h-8 w-8 shrink-0 items-center justify-center rounded-md text-slate-400 hover:text-slate-600 focus-visible:outline-2 focus-visible:outline-blue-600"
          >
            {visible ? <EyeOff size={18} /> : <Eye size={18} />}
          </button>
        }
      />
    </div>
  );
}
