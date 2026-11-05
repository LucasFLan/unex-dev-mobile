import { ReactNode } from "react";

export interface InputProps {
    placeholder: string;
    type: string;
    labelText: string;
    icon?: ReactNode;
}