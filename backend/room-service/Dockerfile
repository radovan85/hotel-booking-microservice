# Koristimo JDK 17 baziran na Alpine
FROM openjdk:17-alpine

# Instaliramo potrebne pakete (bash, unzip)
RUN apk add --no-cache bash unzip

# Postavljamo radni direktorijum
WORKDIR /app

# Kopiramo ZIP paket
COPY target/universal/room-service-1.0-SNAPSHOT.zip /app/room-service.zip

# Raspačavamo Play aplikaciju
RUN unzip /app/room-service.zip && rm /app/room-service.zip

# Postavljamo ENV varijable za bazu
ENV DB_URL=jdbc:mariadb://mariadb:3306/hotel-db
ENV DB_USERNAME=admin
ENV DB_PASSWORD=admin

# Premeštamo folder sa tačnim imenom
RUN mv /app/room-service-1.0-SNAPSHOT /app/room-service

# Dajemo dozvolu za pokretanje aplikacije
RUN chmod +x /app/room-service/bin/room-service

# Postavljamo Bash kao podrazumevani shell i pokrećemo Play aplikaciju
CMD ["/bin/bash", "-c", "/app/room-service/bin/room-service"]