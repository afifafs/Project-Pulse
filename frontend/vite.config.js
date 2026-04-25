import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/activities': 'http://localhost:8080',
      '/instructors': 'http://localhost:8080',
      '/peer-evaluations': 'http://localhost:8080',
      '/sections': 'http://localhost:8080',
      '/students': 'http://localhost:8080',
      '/teams': 'http://localhost:8080',
      '/rubrics': 'http://localhost:8080',
    },
  },
})
