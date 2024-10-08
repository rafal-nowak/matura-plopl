#!/bin/sh

# Uruchomienie dockerd w tle z odpowiednimi opcjami
dockerd -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock &

# Sprawdzenie stanu dockerd, odczekanie na jego pełne uruchomienie
echo "Waiting for dockerd to start..."
while ! docker info >/dev/null 2>&1; do
  echo -n "."
  sleep 1
done

echo "Docker daemon is up and running."

# Uruchomienie głównej aplikacji Java
echo "Starting the main application..."
exec "$@"
