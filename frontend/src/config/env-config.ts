export const getEnvConfig = () => {
  if (import.meta.env.DEV) {
    return {
      BASE_API_URL: import.meta.env.VITE_BASE_API_URL || 'http://localhost:8080',
      API_URL: import.meta.env.VITE_API_URL || 'http://localhost:8080'
    };
  }

  const windowEnv = (window as any)._env_ || {};
  
  return {
    BASE_API_URL: windowEnv.VITE_BASE_API_URL || '/api',
    API_URL: windowEnv.VITE_API_URL || '/api'
  };
};

export const API_CONFIG = getEnvConfig();

export const buildApiUrl = (endpoint: string): string => {
  const baseUrl = API_CONFIG.BASE_API_URL;
  const cleanEndpoint = endpoint.startsWith('/') ? endpoint.slice(1) : endpoint;
  const cleanBaseUrl = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl;
  
  return `${cleanBaseUrl}/${cleanEndpoint}`;
};