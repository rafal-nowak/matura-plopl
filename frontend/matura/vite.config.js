import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',           // Nasłuchuj na wszystkich interfejsach
    port: 5173,                // Domyślny port Vite
    strictPort: true,          // Jeśli port jest zajęty, nie zmienia automatycznie
    allowedHosts: ['test.matura-frontend.it4u.app'],  // Lista dozwolonych hostów
  },
})
