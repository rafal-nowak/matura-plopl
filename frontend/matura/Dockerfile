# Stage 1: Build
FROM node:19-alpine AS builder
ARG api_base_url
ARG pylsp_url

WORKDIR /app

# Kopiowanie zależności i instalacja
COPY package*.json ./
RUN npm ci --silent

# Kopiowanie reszty plików i budowanie aplikacji
COPY .. .
RUN echo "VITE_API_BASE_URL=${api_base_url}" > .env
RUN echo "VITE_PYLSP_URL=${pylsp_url}" >> .env
RUN npm run build

# Stage 2: Serve
FROM nginx:1.25-alpine
COPY --from=builder /app/dist /usr/share/nginx/html

# Konfiguracja NGINX
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
