import { InputProps } from "./types"

const Input = ({placeholder, type}: InputProps) => {
  return (
    <input 
        placeholder={placeholder} 
        type={type} 
        className="w-full rounded-lg border border-gray-300 px-4 py-3 outline-none transition focus:border-principal focus:ring-2 focus:ring-blue-100 bg-fundo" 
        required
    />
  )
}

export default Input