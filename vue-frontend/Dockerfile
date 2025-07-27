# ==== Build stage ====
FROM node:22-alpine as build
WORKDIR /app

# Kopiraj source
COPY . .

# Instaliraj dependency i builduj aplikaciju
RUN npm install && npm run build

# ==== Serve stage ====
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Kopiraj buildovani frontend iz prve faze
COPY --from=build /app/dist /usr/share/nginx/html

# Kopiraj custom Nginx config za SPA rutiranje i sigurnost
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Otvori port
EXPOSE 80

# Startuj Nginx
CMD ["nginx", "-g", "daemon off;"]
