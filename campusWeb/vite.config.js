import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:6666',
        changeOrigin: true,
      },
      '/upload': {
        target: 'http://localhost:6666',
        changeOrigin: true,
      },
      '/ws': {
        target: 'http://localhost:6666',
        changeOrigin: true,
        ws: true,
      },
    },
  },
});

