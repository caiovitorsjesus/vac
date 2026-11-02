/** "7,5" → 7.5; vazio → null (o backend responde que o campo é obrigatório). */
export function lerNumero(texto: string): number | null {
  const limpo = texto.trim().replace(',', '.')
  return limpo === '' ? null : Number(limpo)
}

/** Número vindo da API para dentro de um campo de texto: 7.5 → "7,5". */
export const paraCampo = (valor: number) => String(valor).replace('.', ',')

export const classeBotaoPrimario =
  'inline-flex items-center justify-center gap-2 rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground hover:bg-primary-hover disabled:opacity-60'

export const classeBotaoSecundario =
  'inline-flex items-center justify-center gap-2 rounded-xl border border-border bg-card px-4 py-2.5 text-sm font-semibold text-primary hover:bg-secondary disabled:opacity-60'
