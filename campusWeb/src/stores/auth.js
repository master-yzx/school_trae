import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: localStorage.getItem('accessToken') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null'),
  }),
  actions: {
    setAuth(payload) {
      this.accessToken = payload.accessToken || '';
      this.user = payload.user || null;
      if (this.accessToken) {
        localStorage.setItem('accessToken', this.accessToken);
      } else {
        localStorage.removeItem('accessToken');
      }
      if (this.user) {
        localStorage.setItem('user', JSON.stringify(this.user));
      } else {
        localStorage.removeItem('user');
      }
    },
    clear() {
      this.setAuth({ accessToken: '', user: null });
    },
  },
});

