export interface ErrorResponse { timestamp: string; status: number; error: string; mensaje: string; path: string; errores: Record<string, string> | null }
