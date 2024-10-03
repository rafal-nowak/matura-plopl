Utworzenie klastra Kubernetesowego na platformie Digial Ocean
($48/month per node - 8GB total RAM / 4 vCPUs // 160 GB storage)
(Nodes - 1)


Dodanie nowego zaufanego adresu IP do instancji bazy danych na DigitalOcean.
Ponieważ korzystasz z instancji zarządzanej bazy danych na DigitalOcean, dostęp do bazy danych może być ograniczony tylko do wybranych adresów IP lub sieci (ang. trusted sources).
Ponieważ nowy klaster Kubernetes ma inny adres IP, trzeba dodać go jako zaufany adres w konfiguracji instancji bazy danych.


Dodanie klastra do kubectl
doctl kubernetes cluster kubeconfig save 89b6e4a8-9eab-4c43-9db5-620bb43b0eec

=====
You can switch from local (minikube) to gcloud and back with:
kubectl config use-context CONTEXT_NAME
to list all contexts:
kubectl config get-contexts

doctl kubernetes cluster kubeconfig save 89b6e4a8-9eab-4c43-9db5-620bb43b0eec
kubectl config get-contexts
kubectl config delete-cluster do-ams3-k8s-1-31-1-do-0-ams3-1726992184554
kubectl config delete-context do-ams3-k8s-1-31-1-do-0-ams3-1726992184554
=====

Deploy Apache Kafka on Kubernetes
Wydajemy komendy:
kubectl create namespace kafka
kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
kubectl get pod -n kafka
kubectl apply -f https://strimzi.io/examples/latest/kafka/kraft/kafka-single-node.yaml -n kafka
a następnie w dwóch oddzielnych terminalach:
kubectl -n kafka run kafka-producer -ti --image=quay.io/strimzi/kafka:0.43.0-kafka-3.8.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic Test_Topic_json
kubectl -n kafka run kafka-consumer -ti --image=quay.io/strimzi/kafka:0.43.0-kafka-3.8.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic Test_Topic_json --from-beginning




Krok 1: Konfiguracja Klastra Kubernetesa
Zainstaluj Ingress Controller Najpierw zainstaluj Ingress Controller (np. NGINX Ingress Controller), który będzie zarządzał ruchem HTTP/S w klastrze i przekierowywał go do odpowiednich aplikacji:
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
Sprawdź status kontrolera Ingress:
kubectl get pods -n ingress-nginx

Zainstaluj cert-manager Cert-manager zarządza certyfikatami SSL. Dodaj jego repozytorium i zainstaluj w klastrze:
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.12.0/cert-manager.yaml
Po zainstalowaniu sprawdź, czy cert-manager jest aktywny:
kubectl get pods -n cert-manager

Krok 2: Konfiguracja Let's Encrypt
Skonfiguruj ClusterIssuer dla Let's Encrypt
kubectl apply -f letsencrypt-prod.yaml

Krok 3: Utworzenie Secretów dla Bazy Danych
kubectl apply -f secret.yaml
kubectl apply -f ssl-secret.yaml

Krok 4: Utworzenie Deploymentów dla Aplikacji
kubectl apply -f deployment-counter.yaml
kubectl apply -f deployment-receiver.yaml

Krok 5: Konfiguracja Ingress
kubectl apply -f ingress.yaml
kubectl get ingress

Zweryfikuj dostępność aplikacji: Przejdź pod adres https://demo.it4u.app/api/v1/counter lub https://demo.it4u.app/api/v1/box.


Po wydaniu polecenia:
kubectl get ingress
otrzymujemy:

NAME                        CLASS    HOSTS           ADDRESS           PORTS     AGE
cm-acme-http-solver-xfmrc   <none>   demo.it4u.app   159.223.240.193   80        15s
counter-ingress             nginx    demo.it4u.app   159.223.240.193   80, 443   18s

Adres IP 159.223.240.193 to publiczny adres IP przypisany do Ingress Controllera. Aby aplikacje były dostępne przez domenę demo.it4u.app, należy przekierować tę domenę na ten adres IP.




ERR_CERT_AUTHORITY_INVALID

kubectl get certificates

NAME                READY   SECRET              AGE
demo-it4u-app-tls   False   demo-it4u-app-tls   35m

Z certyfikatem problem sam się rozwiązał, wystarczyło poczekać.


Vercel
=====
Aby aplikacja React dostępna pod adresem https://kubernetes-demo.vercel.app/ mogła być dostępna również pod nową domeną https://kubernetes-demo.it4u.app, musisz wykonać kilka kroków związanych z:

Skonfigurowaniem rekordu DNS dla domeny kubernetes-demo.it4u.app.
Skonfigurowaniem domeny niestandardowej w Vercel.
Zapewnieniem obsługi certyfikatu SSL dla nowej domeny (Vercel automatycznie obsłuży ten proces).
Kroki:
Krok 1: Skonfiguruj rekord DNS dla kubernetes-demo.it4u.app
Musisz dodać rekord DNS dla swojej subdomeny kubernetes-demo.it4u.app, który będzie wskazywał na serwery Vercel. Można to zrobić w panelu administracyjnym DNS u dostawcy domeny (np. nazwa.pl).

W Vercel zazwyczaj używane są rekordy CNAME, które przekierowują domenę do serwerów Vercel.
Zaloguj się do panelu DNS na nazwa.pl.

Dodaj nowy rekord CNAME dla subdomeny kubernetes-demo.it4u.app:

Nazwa: kubernetes-demo.it4u.app
Typ rekordu: CNAME
Wartość: cname.vercel-dns.com (domyślny adres serwerów Vercel dla domen niestandardowych)
Przykład dla panelu DNS:

Typ	Nazwa	Wartość	TTL
CNAME	kubernetes-demo.it4u.app	cname.vercel-dns.com	3600
Krok 2: Dodaj domenę niestandardową w Vercel
Po skonfigurowaniu DNS, musisz poinformować Vercel, że aplikacja powinna być dostępna pod nową domeną kubernetes-demo.it4u.app.

Zaloguj się do swojego konta w Vercel.

Przejdź do projektu kubernetes-demo.

Wejdź w zakładkę "Settings" (Ustawienia).

Znajdź sekcję "Domains" (Domeny).

Kliknij "Add" lub "Add Domain" i dodaj swoją nową domenę kubernetes-demo.it4u.app.

Vercel sprawdzi teraz konfigurację DNS, aby upewnić się, że rekord CNAME został poprawnie ustawiony.

Krok 3: Zapewnienie obsługi certyfikatu SSL
Vercel automatycznie generuje i obsługuje certyfikat SSL dla każdej dodanej domeny. Jeśli rekord CNAME zostanie poprawnie skonfigurowany, certyfikat SSL powinien być automatycznie aktywowany dla kubernetes-demo.it4u.app. Nie musisz robić nic więcej w kwestii certyfikatu.

Krok 4: Testowanie
Po propagacji DNS (zwykle zajmuje od kilku minut do kilku godzin), Twoja aplikacja powinna być dostępna pod nowym adresem https://kubernetes-demo.it4u.app.

Możesz to sprawdzić za pomocą przeglądarki lub narzędzi takich jak curl:

bash
Skopiuj kod
curl -I https://kubernetes-demo.it4u.app
Powinno to zwrócić odpowiedź z serwera Vercel.

Podsumowanie:
Skonfiguruj rekord DNS CNAME dla kubernetes-demo.it4u.app, wskazując na cname.vercel-dns.com.
Dodaj domenę niestandardową w Vercel w ustawieniach projektu.
Vercel automatycznie obsłuży certyfikat SSL, a aplikacja będzie dostępna pod nową domeną po zakończeniu propagacji DNS.
Po tych krokach Twoja aplikacja React będzie dostępna pod https://kubernetes-demo.it4u.app.
=====


CA certificate
=====
A co z CA certificate? Przecież sslmode = require obowiązuje zarówno dla połączenia zewnętrznego jak i z klastra Kubernetesowego.
ChatGPT powiedział:
ChatGPT
Masz rację – skoro połączenie do bazy danych PostgreSQL hostowanej na DigitalOcean wymaga użycia SSL, musisz uwzględnić konfigurację dla certyfikatu SSL (CA certificate) również w klastrze Kubernetes. Oto jak możesz to zrobić, aby zapewnić poprawne połączenie z bazą danych z poziomu aplikacji w Kubernetes, uwzględniając sslmode=require i certyfikat CA.

1. Dodaj certyfikat CA jako Secret w Kubernetes
   Najpierw musisz przekazać pobrany certyfikat CA do klastra Kubernetes jako Secret. Zakoduj certyfikat w formacie base64, a następnie stwórz plik ssl-secret.yaml, który będzie zawierał ten certyfikat.

Zakoduj plik certyfikatu w formacie base64:

Jeśli masz plik certyfikatu o nazwie np. ca-certificate.crt, możesz zakodować go w terminalu:

bash
Skopiuj kod
cat ca-certificate.crt | base64
Skopiuj wynik base64.
=====


Dodanie plików
=====
Aby umożliwić aplikacji counter zapisywanie plików na dysku w klastrze Kubernetes na platformie DigitalOcean, należy dodać wsparcie dla trwałych woluminów (PersistentVolume i PersistentVolumeClaim). Trwałe woluminy umożliwiają aplikacji dostęp do miejsca na dysku, które jest przechowywane niezależnie od cyklu życia podów, co oznacza, że dane nie zostaną utracone po restarcie kontenera.

Kroki do wdrożenia:
Stworzenie PersistentVolumeClaim (PVC).
Zaktualizowanie pliku deployment-counter.yaml, aby używał PVC.
Zaktualizowanie pliku application-prod.yml, aby aplikacja wskazywała na odpowiednią ścieżkę.
Dodanie nowej ścieżki do pliku ingress.yaml, która będzie przekierowywać żądania zaczynające się od /api/v1/files do counter-service.
=====


Jak połączyć się z bazą danych PostgreSQL na platformie DigitalOcean za pomocą IntelliJ IDEA Ultimate
=====
Krok 1: Otwórz IntelliJ IDEA Ultimate
Otwórz IntelliJ IDEA Ultimate.
W prawym dolnym rogu znajdź zakładkę Database lub otwórz ją z menu górnego: View > Tool Windows > Database.
Krok 2: Dodaj nową konfigurację połączenia
W zakładce Database, kliknij ikonę + (lub kliknij prawym przyciskiem na panelu i wybierz Add New Data Source).
Z listy dostępnych baz danych wybierz PostgreSQL.
Krok 3: Skonfiguruj połączenie
Po wyborze PostgreSQL, otworzy się okno konfiguracyjne. Teraz wpisz parametry połączenia, które otrzymałeś od DigitalOcean:

Host: Wpisz host, który otrzymałeś, czyli:
db-postgresql-ams3-17133-do-user-17810680-0.g.db.ondigitalocean.com

Port: Wpisz numer portu:
25060

User: Wpisz nazwę użytkownika:
doadmin

Password: Wprowadź hasło, które zostało wygenerowane podczas tworzenia bazy danych (kliknij ikonkę klucza, aby wpisać hasło).

Database: Ustaw nazwę bazy danych na:
defaultdb

SSL:
Kliknij na zakładkę SSL (znajduje się w sekcji opcji połączenia).

SSL Mode: Ustaw na require.
Use SSL: Zaznacz tę opcję.
CA Certificate: Załaduj pobrany plik CA certificate (powinieneś mieć możliwość dodania go z lokalnego dysku).
Krok 4: Testuj połączenie
Po uzupełnieniu wszystkich pól, kliknij przycisk Test Connection w dolnej części okna. IntelliJ spróbuje połączyć się z bazą danych.
Jeśli test się powiedzie, zobaczysz komunikat o pomyślnym połączeniu.
Krok 5: Zapisz i połącz
Po pomyślnym teście połączenia kliknij OK, aby zapisać konfigurację.
Baza danych powinna pojawić się w panelu Database. Możesz teraz przeglądać tabele, wykonywać zapytania SQL i zarządzać bazą danych bezpośrednio z IntelliJ.
Dodatkowe Uwagi:
Jeśli nie masz jeszcze zainstalowanych sterowników PostgreSQL w IntelliJ, IntelliJ powinno automatycznie pobrać je podczas tworzenia połączenia. Jeśli nie, pobierz je ręcznie, klikając opcję Download missing driver files.
Gotowe! Możesz teraz zarządzać swoją bazą danych PostgreSQL z poziomu IntelliJ.
=====
