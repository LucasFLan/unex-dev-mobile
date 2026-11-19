"use client";

import { useState } from "react";
import { Eye, EyeOff, LockKeyhole } from "lucide-react";
import Input from "@/components/Input";

type PasswordFieldProps = {
  labelText: string;
  name: string;
  placeholder?: string;
  autoComplete?: string;
};

export function PasswordField({
  labelText,
  name,
  placeholder,
  autoComplete,
}: PasswordFieldProps) {
  const [visible, setVisible] = useState(false);

  return (
    // flex-col mantém o rótulo (20px) acima do campo, para o olho alinhar com ele
    <div className="relative flex flex-col">
      <Input
        labelText={labelText}
        name={name}
        type={visible ? "text" : "password"}
        placeholder={placeholder}
        autoComplete={autoComplete}
        icon={<LockKeyhole size={17} />}
      />

      <button
        type="button"
        onClick={() => setVisible((current) => !current)}
        aria-label={visible ? "Ocultar senha" : "Mostrar senha"}
        className="absolute right-3 top-7.5 text-slate-400 hover:text-slate-600"
      >
        {visible ? <EyeOff size={18} /> : <Eye size={18} />}
      </button>
    </div>
  );
}