import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    // repassa /api para o Spring: para o navegador tudo vem da mesma origem, então não há CORS
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
