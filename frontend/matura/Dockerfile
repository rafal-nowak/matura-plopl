# Stage 1: Build
FROM node:19-alpine AS builder
ARG api_base_url
ARG pylsp_url

WORKDIR /app

# Instalacja zależności
COPY package*.json ./
RUN npm ci --silent

# Kopiowanie reszty plików i budowanie aplikacji
COPY .. .
RUN echo "VITE_API_BASE_URL=${api_base_url}" > .env
RUN echo "VITE_PYLSP_URL=${pylsp_url}" >> .env
RUN npm run build

# Tworzenie dynamicznego pliku konfiguracyjnego
RUN echo "window._env_ = { VITE_API_URL: '${api_base_url}', VITE_PYLSP_URL: '${pylsp_url}' };" > /app/dist/config.js

# Stage 2: Serve with NGINX
FROM nginx:1.25-alpine

# Kopiowanie wygenerowanej aplikacji
COPY --from=builder /app/dist /usr/share/nginx/html

# Dynamiczne zmienne środowiskowe przy starcie kontenera
CMD ["sh", "-c", "envsubst < /usr/share/nginx/html/config.js > /usr/share/nginx/html/config.js && exec nginx -g 'daemon off;'"]

EXPOSE 80
