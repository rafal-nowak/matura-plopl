Aby wyczyścić lokalnie obraz Dockera rafalnowak444/matura-react z tagiem latest i wymusić pobranie najnowszej wersji, wykonaj poniższe kroki:

1. Usuń lokalnie zapisany obraz Dockera:
   Możesz usunąć obraz Dockera za pomocą następującego polecenia:

bash
Skopiuj kod
docker rmi rafalnowak444/matura-react:latest
2. Sprawdź, czy obraz został usunięty:
   Aby upewnić się, że obraz został usunięty, wykonaj poniższe polecenie, które wyświetli wszystkie dostępne lokalne obrazy:

bash
Skopiuj kod
docker images
Sprawdź, czy rafalnowak444/matura-react:latest nie jest już na liście. Jeśli nadal jest, spróbuj usunąć obraz z użyciem parametru --force:

bash
Skopiuj kod
docker rmi -f rafalnowak444/matura-react:latest
3. Uruchom ponownie docker-compose z pobraniem aktualnych obrazów:
   Po usunięciu obrazu uruchom ponownie docker-compose z opcją --pull, aby pobrać najnowszą wersję obrazu z repozytorium:

bash
Skopiuj kod
docker-compose up -d --pull always matura-react
Flaga --pull always wymusza pobranie nowego obrazu, nawet jeśli już istnieje lokalnie (po usunięciu, opcja ta pobierze obraz z repozytorium).

4. Sprawdź, czy pobrany został najnowszy obraz:
   Aby upewnić się, że pobrany został najnowszy obraz, możesz użyć:

bash
Skopiuj kod
docker images | grep matura-react
Sprawdź datę stworzenia (CREATED) lub pobrania obrazu (UPDATED) oraz tag (latest), aby upewnić się, że to aktualna wersja.

5. Opcjonalne czyszczenie nieużywanych obrazów:
   Jeśli nadal występują problemy, możesz usunąć wszystkie nieużywane obrazy, kontenery i dane, aby upewnić się, że Docker jest "czysty":

bash
Skopiuj kod
docker system prune -a
To polecenie usunie wszystkie nieużywane zasoby Dockera (obrazy, kontenery, wolumeny). Uwaga: Spowoduje to usunięcie wszystkich nieużywanych zasobów, więc używaj go ostrożnie.

Po tych krokach uruchom docker-compose ponownie, a kontener matura-react powinien pobrać najnowszy obraz z repozytorium i działać poprawnie.