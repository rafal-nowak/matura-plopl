FROM node:19-alpine
ARG vite_api_url
ARG vite_pylsp_url
WORKDIR /app
COPY package*.json .
RUN npm i --silent
COPY .. .
RUN echo "VITE_API_URL=${vite_api_url}" > .env
RUN echo "VITE_PYLSP_URL=${vite_pylsp_url}" > .env
RUN npm run build
EXPOSE 8080
CMD [ "npm", "run", "preview" ]