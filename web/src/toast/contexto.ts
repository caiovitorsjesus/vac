import { createContext, useContext } from 'react'

/** Função que exibe uma mensagem passageira (toast). Fornecida pelo ToastProvider. */
export const ToastContexto = createContext<(mensagem: string) => void>(() => {})

export const useToast = () => useContext(ToastContexto)
