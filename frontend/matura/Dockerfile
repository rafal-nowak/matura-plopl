FROM node:19-alpine
ARG api_base_url
WORKDIR /app
COPY package*.json .
RUN npm i --silent
COPY .. .
RUN echo "VITE_API_URL=${vite_api_url}" > .env
RUN echo "VITE_PYLSP_URL=${vite_pylsp_url}" > .env
EXPOSE 5173
CMD ["npm", "run", "dev"]