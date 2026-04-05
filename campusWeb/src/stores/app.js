import { defineStore } from 'pinia';
import request from '../utils/request';

let baseLoadPromise = null;

export const useAppStore = defineStore('app', {
  state: () => ({
    loaded: false,
    categories: [],
    hotSearches: [],
    campuses: [],
    footerConfig: null,
  }),
  actions: {
    async loadBaseData() {
      if (this.loaded) return;
      if (baseLoadPromise) return baseLoadPromise;

      baseLoadPromise = (async () => {
        const safeGet = async (url) => {
          try {
            const res = await request.get(url);
            return res?.data;
          } catch (e) {
            return null;
          }
        };

        const [category, hot, campuses, config] = await Promise.all([
          safeGet('/categories/tree'),
          safeGet('/search/hot'),
          safeGet('/campuses'),
          safeGet('/system/config/footer'),
        ]);

        this.categories = category || [];
        this.hotSearches = hot || [];
        this.campuses = campuses || [];
        this.footerConfig = config || null;
        this.loaded = true;
      })();

      try {
        await baseLoadPromise;
      } finally {
        baseLoadPromise = null;
      }
    },
  },
});

