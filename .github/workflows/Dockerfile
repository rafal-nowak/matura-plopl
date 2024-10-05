# Rozpoczynamy od obrazu bazowego eclipse-temurin:17
FROM eclipse-temurin:17

# Instalacja narzędzi: docker, docker-compose i ich zależności
USER root
RUN apt-get update && \
    apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
    echo "deb [arch=$(dpkg --print-architecture)] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list && \
    apt-get update && \
    apt-get install -y docker-ce docker-ce-cli containerd.io && \
    curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose && \
    ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Ustaw użytkownika na non-root (opcjonalne, jeśli chcesz uniknąć uruchamiania kontenera jako root)
USER eclipse

# Domyślna komenda uruchamiająca aplikację Javy (opcjonalne)
CMD ["java", "-version"]
