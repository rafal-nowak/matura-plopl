FROM node:19-alpine
ARG api_base_url
ARG pylsp_url

WORKDIR /app
COPY package*.json .
RUN npm i --silent
COPY .. .

# Tworzymy plik .env z przekazanymi zmiennymi środowiskowymi
RUN echo "VITE_API_BASE_URL=${api_base_url}" > .env
RUN echo "VITE_PYLSP_URL=${pylsp_url}" >> .env

# Budowanie aplikacji
RUN npm run build

# Użycie serwera statycznego
EXPOSE 5173
CMD ["npm", "run", "dev"]
