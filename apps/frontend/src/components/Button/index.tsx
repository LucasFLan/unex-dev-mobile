import { ButtonProps } from "./types"

const Button = ({text, type}: ButtonProps) => {
  return (
    <button
        type={type}
        className="w-full rounded-lg bg-principal px-4 py-3 font-semibold text-white transition hover:opacity-90"
        >
        {text}
    </button>
  )
}

export default Button