# BEFORE docker compose up -d --build
#RUN "chmod +x gradlew"
#RUN "./gradlew clean build -x test"

# Użycie ARG do ustawienia obrazu bazowego
ARG IMAGE_NAME
FROM ${IMAGE_NAME}

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj pliki JAR do obrazu
COPY build/libs/*.jar app.jar

# Ustaw port
EXPOSE ${SERVER_PORT}

# Uruchom aplikację
CMD ["java", "-jar", "app.jar"]
