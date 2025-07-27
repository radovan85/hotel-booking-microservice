FROM tomcat:10.1-jdk17

WORKDIR /app

COPY target/auth-service.war /app/app.war

RUN mv /app/app.war /usr/local/tomcat/webapps/ROOT.war

# Usklađene ENV varijable
ENV DB_URL=jdbc:mariadb://mariadb:3306/hotel-db
ENV DB_USERNAME=admin
ENV DB_PASSWORD=admin

CMD ["catalina.sh", "run"]