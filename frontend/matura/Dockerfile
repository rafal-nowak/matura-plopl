# Stage 1: Build
FROM node:19-alpine AS builder
ARG api_base_url
ARG pylsp_url

WORKDIR /app

# Kopiowanie plików konfiguracyjnych i instalacja zależności
COPY package*.json ./
RUN npm ci --silent

# Kopiowanie całego projektu i budowanie aplikacji
COPY .. .
RUN npm run build

# Tworzenie pliku konfiguracyjnego z dynamicznymi zmiennymi środowiskowymi
RUN mkdir -p /app/dist/public
RUN echo "window._env_ = { VITE_API_URL: '${api_base_url}', VITE_PYLSP_URL: '${pylsp_url}' };" > /app/dist/public/config.js

# Stage 2: Serve
FROM nginx:1.25-alpine
COPY --from=builder /app/dist /usr/share/nginx/html

# Dynamiczne zmienne środowiskowe przy starcie kontenera
COPY ./public/config.js /usr/share/nginx/html/public/config.js
CMD ["sh", "-c", "envsubst < /usr/share/nginx/html/public/config.js > /usr/share/nginx/html/public/config.js && exec nginx -g 'daemon off;'"]

EXPOSE 80
