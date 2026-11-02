/** "7,5" → 7.5; vazio → null (o backend responde que o campo é obrigatório). */
export function lerNumero(texto: string): number | null {
  const limpo = texto.trim().replace(',', '.')
  return limpo === '' ? null : Number(limpo)
}

/**
 * Data de hoje no fuso do usuário, no formato da API ("2026-11-02").
 * toISOString() usaria UTC: no Brasil, depois das 21h, daria amanhã e a API recusaria como data futura.
 */
export const hoje = () => new Date().toLocaleDateString('sv-SE')

/** Número vindo da API para dentro de um campo de texto: 7.5 → "7,5". */
export const paraCampo = (valor: number) => String(valor).replace('.', ',')

export const classeBotaoPrimario =
  'inline-flex min-h-11 items-center justify-center gap-2 rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground transition-colors hover:bg-primary-hover disabled:opacity-60'

export const classeBotaoSecundario =
  'inline-flex min-h-11 items-center justify-center gap-2 rounded-xl border border-border bg-card px-4 py-2.5 text-sm font-semibold text-primary transition-colors hover:bg-secondary disabled:opacity-60'
