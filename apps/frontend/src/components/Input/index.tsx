import { InputProps } from "./types"

const Input = ({placeholder, type, labelText, icon}: InputProps) => {
  return (
    <>
      <label
        className="text-sm font-medium text-gray-700"
      >
        {labelText}
      </label>
      <div className="flex h-9.75 items-center gap-2.5 rounded-[10px] border border-slate-200 bg-slate-50 px-3.5 transition focus-within:border-blue-500 focus-within:ring-2 focus-within:ring-blue-100">
        {icon && (
          <span className="shrink-0 text-slate-400">
            {icon}
          </span>
        )}
      <input 
          className="h-full min-w-0 flex-1 bg-transparent text-xs text-slate-800 outline-none placeholder:text-slate-400"
          placeholder={placeholder} 
          type={type} 
          required
      />
      </div>
    </>
  )
}

export default Input