# Użycie obrazu bazowego Docker-in-Docker
FROM docker:24.0.5-dind

# Instalacja Javy 17, Maven, curl i dodatkowych narzędzi
RUN apk update && \
    apk add openjdk17 openjdk17-jdk maven curl git

# Ustawienie zmiennej JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV PATH="$JAVA_HOME/bin:${PATH}"

# Ustawienie katalogu roboczego
WORKDIR /app

# Skopiowanie pliku pom.xml oraz zależności, aby zbudować warstwę cache Maven
COPY pom.xml .

# Pre-kompilacja zależności (umożliwia szybsze budowanie warstw w Dockerze)
RUN mvn dependency:resolve

# Skopiowanie reszty źródeł do obrazu
COPY src ./src

# Budowanie projektu Javy z użyciem Mavena
RUN mvn clean package -DskipTests

# Ustawienie katalogu roboczego na /app/target, gdzie Maven umieszcza plik JAR
WORKDIR /app/target

# Domyślna komenda uruchamiająca aplikację Spring Boot z bezpośrednim odniesieniem do pliku JAR
CMD ["java", "-jar", "worker-0.0.1-SNAPSHOT.jar"]
