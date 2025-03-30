# PAP2024Z-Z22

#### Członkowie Zespołu
- Jakub Antas 
- Jerzy Muszyński 
- Mateusz Lewko 
- Albert Skutnik 

### E-commerce Application :shopping_cart:

Głównym celem tego projektu jest stworzenie aplikacji w stylu sklepu-online, która pozwalałaby użytkownikom na przeglądanie, wybór oraz zakup produktów. System będzie posiadał różne role dla użytkownika (klient, admin), jak i również funkcje wspierające cały proces zakupowy, takie jak koszyk, system płatności, katalog dostępnych produktów, możliwość składania zamówień oraz ich śledzenie.

### Technologie wybrane do pracy nad projektem :tools:

---

1. Graficzny interfejs użytkownika =\> **Java FX**
2. Główna logika działania sklepu +Api =\> **Java Spring Boot**
3. Baza danych =\> **PostgreSQL**

### Instrukcja instalacji

---

__Pierwszym__ krokiem który należy wykonać jest sklonowanie repozytorium oraz stworzenie pliku o nazwie `.env` w głównym katalogu projektu. Plik ten odpowiedzialny jest za konfigurację klucza szyfrującego tokeny JWT oraz innych koniecznych zależności jak login i hasło do aplikacji mailowej wysyłającej powiadomienia

```bash
git clone https://gitlab-stud.elka.pw.edu.pl/pap2024z-z22/pap2024z-z22.git
cd pap2024z-z22
```

```bash
#.env
SPRING_DATASOURCE_USERNAME=<DATABASE-USERNAME>
SPRING_DATASOURCE_PASSWORD=<DATABASE-PASSWORD>

JWT_SECRET_KEY=<SECRET-KEY>
JWT_EXPIRATION_TIME=<JWT-EXPIRATION-TIME>

SUPPORT_EMAIL=<MAIL-ADDRESS>
APP_PASSWORD=<MAIL-APP-PASSWORD>
MAIL_HOST=<MAIL-HOST>
MAIL_PORT=<MAIL-PORT>
```

Instalacje oraz uruchomienie programu obsługuje skrypt `run-ecommerce.sh`, który jest odpowiedzialny za pobranie oraz skonfigurowanie wszystkich koniecznych zależności do uruchomienia aplikacji na kontenerze dockerowym (część backendowa) oraz maszynie lokalnej (frontend). Poszczególne etapy są krótko opisane w samym skrypcie. Aplikacje należy uruchomić w następujący sposób

```bash
sh run-ecommerce.sh
```

W przypadku błędów uzyskanych po uruchomieniu skryptu należy przede wszystkim upewnić się że: 
1. nasz system nie konwertuje automatycznie LF -> CRLF przy każdorazowym pobraniu kodu ze zdalnego repozytorium, potencjalnym rozwiązaniem może być zastosowanie komendy  `git config core.autocrlf false`
2. doszło do poprawnej instalacji dockera oraz docker-compose: `docker --version` i `docker-compose --version`

Alternatywą do uruchomienia aplikacji w kontenerze jest posłużenie się którymś z IDE obsługujących projekty mavenowe, np. Intelij IDEA. Z poziomu takiego środowiska należy oddzielnie uruchomić projekt backendowy znajdujący się w `backend/` oraz frontendowy `frontend/`

### Główne funkcjonalności: :rocket:

---

 1. **Podział na użytkowników i administratorów**
    * System posiadałby dwie klasy użytkowników z różnym zakresem dostępu do jego usług
 2. **Rejestracja użytkowników i autoryzacja** =\>
    * Rejestracja oraz logowanie przy użyciu hasła/email oraz ewentualnie dodatkowo implementacja mechanizmu SSO
 3. **Katalog produktów** =\>
    * Główny widok z listą produktów z podstawowymi informacjami =\> zdjęcie, nazwa i cena
 4. **Wyszukiwarka produktów** =\>
    * Pasek wyszukiwania, ułatwiający dostęp do towarów na podstawie ich nazw
 5. **Filtrowanie/sortowanie produktów** =\>
    * Proste filtry katalogu produktów według cen, kategorii, średniej ocen, itd.
 6. **Szczegóły produktu** =\>
    * Oddzielny widok/strona w obrębie aplikacji na szczegółowe dane dotyczące produktu (zdjęcie, opis, recenzje, itd.)
 7. **Koszyk zakupowy** =\>
    * Użytkownik (klient) może dodawać/usuwać/edytować produkty w koszyku
 8. **Proces składania zamówień** =\>
    * Jednoetapowy proces składania zamówień, polegający na podsumowaniu towarów z koszyka i ewentualnym wybraniem adresu dostawy
 9. **Proces zakupu** =\>
    * Potwierdzenie wybranych produktów z koszyka, przekierowanie do transakcji
    * Ewentualna integracja z zewnętrznym narzędziem do bezpiecznych płatności online, typu PayPal
10. **Podsumowanie zamówienia** =\>
    * Po finalizacji procesu zakupowego, klient zostanie przekierowany do widoku z podsumowaniem produktów i adresu dostawy
11. **Zarządzanie zamówieniami** =\>
    * Każdy klient otrzymałby informacje o statusie swoich zamówień/zakupów, wraz z podsumowaniem i możliwością śledzenia statusu realizacji na bieżąco
12. **Panel Administratora** =\>
    * Oddzielne widoki dla użytkowników-administratorów, dzięki któremu mieliby specjalny dostęp do aplikacji
13. **Proste zarządzanie stanem magazynowym** =\>
    * Administratorzy będą mieli możliwość nadzoru nad produktami/zamówieniami/użytkownikami =\> zarządzanie systemu na poziomie zapewniania usług klientowi
14. **Sekcja oceny produktu** =\>
    * Użytkownicy będą mieli możliwość oceny danego produktu w skali 1-5
15. **Sekcja komentarzy pod produktami** =\>
    * Każdy klient miałby możliwość dodawania opinii/komentarzy/recenzji do zakupionych przez siebie produktów, co również pozwoliłoby na wyświetlenie średniej oraz łącznej liczby opinii danego przedmiotu
16. **Edycja profilu użytkownika** =\>
    * Każdy klient miałby możliwość uzupełnienia swojego profilu dodatkowymi informacjami poza loginem i hasłem. Dodatkowo mógłby podać swoje zainteresowania/hobby, kategorie produktów, które szczególnie by go interesowały,
17. **Personalizacje/rekomendacje** =\>
    * Na podstawie profilu klienta otrzymywałby on rekomendacje nowych/ciekawych produktów w postaci wiadomości email
18. **Powiadomienia email** =\> Powiadomienia email po rejestracji oraz zakupie z potwierdzeniem zamówienia
19. **Integracja z kalendarzem** =\>
    * Możliwość dodania wydarzeń do osobistego kalendarza (google calendar) w postaci przypomnień o przewidywanej dacie dostawy bądź dostępności obecnie brakującego towaru
20. **Widok "O nas" i "Kontakt"** =\> Informacyjne widoki wraz z formularzami kontaktowymi