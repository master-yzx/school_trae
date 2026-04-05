import axios from 'axios';
import { useAuthStore } from '../stores/auth';

const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

let refreshingPromise = null;

service.interceptors.request.use(
  (config) => {
    const auth = useAuthStore();
    if (auth.accessToken) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${auth.accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

service.interceptors.response.use(
  (response) => response.data,
  async (error) => {
    const auth = useAuthStore();
    const status = error?.response?.status;
    const original = error?.config;

    if (status === 401 && original && !original.__isRetryRequest) {
      try {
        if (!refreshingPromise) {
          refreshingPromise = service.post('/auth/refresh');
        }
        const resp = await refreshingPromise;
        refreshingPromise = null;

        const data = resp?.data || {};
        if (!data.accessToken) {
          auth.clear();
          return Promise.reject(error);
        }

        auth.setAuth({
          accessToken: data.accessToken,
          user: auth.user,
        });

        original.__isRetryRequest = true;
        original.headers = original.headers || {};
        original.headers.Authorization = `Bearer ${data.accessToken}`;
        return service.request(original);
      } catch (e) {
        refreshingPromise = null;
        auth.clear();
        return Promise.reject(error);
      }
    }

    return Promise.reject(error);
  },
);

export default service;


