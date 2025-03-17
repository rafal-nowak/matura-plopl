package pl.lodz.p.liceum.matura;

import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.lodz.p.liceum.matura.domain.template.TaskLanguage;
import pl.lodz.p.liceum.matura.domain.template.Template;
import pl.lodz.p.liceum.matura.domain.template.TemplateService;

import java.time.ZonedDateTime;

@Component
@Log
public class DefaultTemplates implements CommandLineRunner {

    private final TemplateService templateService;

    public DefaultTemplates(TemplateService templateService) {
        this.templateService = templateService;
    }

    private final Template matura05_2023Python = new Template(
            null,
            "https://github.com/HubertM6/MaturaMay2015Task4",
            TaskLanguage.PYTHON,
            "Matura 05.2023 Zadanie 4. Liczby binarne",
            """
                    Zadanie 4. Liczby binarne
                    =================
                    
                    W pliku `liczby.txt` znajduje się n (1 <= n <= 400000) liczb naturalnych zapisanych binarnie. Każda
                    liczba zapisana jest w osobnym wierszu. Pierwsze pięć wierszy zawiera następujące liczby:
                    - 11010100111
                    - 11110111111011101
                    - 1010100111010100
                    - 1101111111111111111111010100101010101001
                    - 1010110011001101010011110101010101010111
                    
                    Każda liczba binarna zawiera co najwyżej **250 cyfr binarnych**, co oznacza, że w wielu
                    językach programowania wartości niektórych z tych liczb nie da się zapamiętać
                    w pojedynczej zmiennej typu całkowitoliczbowego, np. w języku C++ w zmiennej typu
                    `int`.\\
                    Napisz **program**, który da odpowiedzi do poniższych zadań. Odpowiedzi zapisz w pliku
                    `wynik4.txt`, a każdą odpowiedź poprzedź numerem oznaczającym odpowiednie zadanie.\s
                    
                    ## Zadanie 1. (0-3)
                    
                    Podaj, ile liczb z pliku `liczby.txt` ma w swoim zapisie binarnym więcej zer niż jedynek.
                    
                    *Przykład*: Dla zestawu liczb:
                    - 101011010011001100111
                    - <u>10001001</u>
                    - <u>1000000</u>
                    - 101010011100
                    - <u>100010</u>
                    
                    wynikiem jest liczba 3 (3 podkreślone liczby mają w swoim zapisie więcej zer niż jedynek).
                    
                    ## Zadanie 2. (0-3)
                    
                    Podaj, ile liczb w pliku `liczby.txt` jest podzielnych przez 2 oraz ile liczb jest podzielnych
                    przez 8.
                    
                    *Przykład*: Dla zestawu liczb:
                    - 101011010011001100000 (*), (**)
                    - 10001001
                    - 100100 (*)
                    - 101010010101011011000 (*), (**)
                    - 100011
                    
                    trzy liczby są podzielne przez 2 (*) i dwie liczby są podzielne przez 8 (**).
                    
                    ## Zadanie 3 (0-6)
                    
                    Znajdź najmniejszą i największą liczbę w pliku `liczby.txt`. Jako odpowiedź podaj
                    numery wierszy, w których się one znajdują.
                    
                    *Przykład*: Dla zestawu liczb:
                    - 101011010011001100111
                    - 10001001011101010
                    - 1001000
                    - 101010011100
                    - 1000110
                    
                    najmniejsza liczba to: `1000110`\\
                    największa liczba to: `101011010011001100111`\\
                    Prawidłowa odpowiedź dla powyższego przykładu to: `5`, `1`.\s
                    """,
            3,
            0,
            ZonedDateTime.now()
    );
    private final Template matura06_2024Python = new Template(
            null,
            "https://github.com/HubertM6/MaturaJune2024Task3",
            TaskLanguage.PYTHON,
            "Matura 06.2024 Zadanie 3. Słowa",
            """
                    Zadanie 3. Słowa
                    =================
                    
                    W pliku `slowa.txt` podane jest n (1 <= n <= 500000) słów złożonych z małych liter alfabetu angielskiego. Suma długości słów nie przekracza 10000000. Napisz program, dający odpowiedzi do poniższych zadań. Uzyskane odpowiedzi wypisz w konsoli lub zapisz w pliku `wyniki.txt`.
                    
                    ### Zadanie 1
                    Podaj, w ilu spośród podanych słów znajduje się trójliterowy fragment "k?t", gdzie ? oznacza dowolną pojedynczą literę (taki fragment występuje na przykład w słowach "alamakota", albo "brokat", ale nie w słowa "krata").
                    
                    ### Zadanie 2
                    Alfabet angielski zawiera 26 liter. Kodowanie ROT13 zamienia każdą literę na literę, która jest na pozycji o 13 miejsc dalej w alfabecie (a &rarr; n, b &rarr; o itd.), przy czym po przekroczeniu "z" liczymy z powrotem od "a" (czyli m &rarr; z, ale n &rarr; a, o &rarr; b i tak dalej).
                    
                    Słowo **aren** ma ciekawą własność &mdash; po zakodowaniu za pomocą ROT13 staje się słowem **nera**, czyli tym samym słowem czytanym od tyłu. Podaj, ile w pliku `slowa.txt` jest słów, które mają tę własność.\s
                    
                    Jako odpowiedź podaj:
                    W pierwszym wierszu &mdash; liczbę słów w pliku `slowa.txt`, które mają taką własność.
                    W drugim wierszu &mdash; najdłuższe, spośród tych słów.\s
                    
                    ### Zadanie 3
                    Znajdź i wypisz z pliku `slowa.txt` wszystkie takie słowa, w których ta sama litera występuje na co najmniej połowie pozycji.
                    
                    Przykładowo:\s
                    * w słowie "owocowo" litera "o" ma 4 wystąpienia na ogólną liczbę 7 liter w słowie, zatem spełnia podany warunek
                    * w słowie "ambaras" litera "a" ma tylko 3 wystąpienia na 7 liter, więc nie spełnia podanego warunku
                    
                    Słowa te wypisz, w kolejności występowania i każde w osobnej linijce.
                    """,
            3,
            0,
            ZonedDateTime.now()
    );
    private final Template matura05_2023CSharp = new Template(
            null,
            "https://github.com/HubertM6/MaturaBinaryNumbersCSharp",
            TaskLanguage.C_SHARP,
            "Matura 05.2023",
            "Matura 05.2023 C#",
            3,
            0,
            ZonedDateTime.now()
    );
    private final Template matura05_2023Java = new Template(
            null,
            "https://github.com/HubertM6/MaturaBinaryNumbersJava",
            TaskLanguage.JAVA,
            "Matura 05.2023",
            "Matura 05.2023 Java",
            3,
            0,
            ZonedDateTime.now()
    );
    private final Template matura05_2022Python = new Template(
            null,
            "https://github.com",
            TaskLanguage.PYTHON,
            "Matura 05.2022",
            "Matura 05.2022 Python",
            1,
            0,
            ZonedDateTime.now()
    );
    private final Template OI31Etap1BudowaLotniskaPython = new Template(
            null,
            "https://github.com",
            TaskLanguage.PYTHON,
            "31 OI Etap 1 Budowa lotniska",
            "Etap 1, 31 OI, Budowa lotniska",
            0,
            5,
            ZonedDateTime.now()
    );

    // TEMP
    private final Template PAAkwarium = new Template(
            null,
            "https://github.com/HubertM6/Akwarium",
            TaskLanguage.PYTHON,
            "PA 2025",
            """
                    # Zadanie Akwarium
                    
                    Postanowiłeś/aś kupić nowe akwarium dla swojej złotej rybki. W sklepie z akwariami masz bardzo duży wybór:
                    możesz kupić prostopadłościenne akwarium o podstawie a na b oraz wysokości h dla dowolnych
                    całkowitych dodatnich wymiarów a, b i h.
                    Twoja rybka lubi robić poranne ćwiczenia i na rozgrzewkę pływa w tę i z powrotem po jednej z przekątnych
                    akwarium. Długość przekątnej akwarium wyraża się wzorem √(a^2 + b^2 + h^2).
                    Aby ułatwić rybce obliczenia, ile przepłynęła danego dnia, chcesz, aby długość przekątnej również wyrażała
                    się liczbą całkowitą. Zbyt duże akwarium też nie wchodzi w rachubę, więc długość jego przekątnej musi wynosić
                    co najwyżej n.
                    Ile różnych akwariów spełnia wszystkie wymagania? Dwa akwaria uznajemy za różne, jeśli mają inną
                    wysokość lub inną nieuporządkowaną parę {a, b} (akwarium o podstawie a na b i wysokości h oraz akwarium
                    o podstawie b na a i wysokości h są takie same).
                    
                    W pliku "dane.txt" znajduje się jedna liczba całkowita n (1 <= n <= 5000).
                    
                    Odpowiedź zapisz w pliku "wynik.txt"
                    
                    Przykład:
                    Dla danych wejściowych:
                    7
                    poprawnym wynikiem jest:
                    7
                    
                    Wyjaśnienie przykładu:
                    Możliwe są następujące akwaria:
                    - Podstawa 1 na 2, wysokość 2, przekątna 3.
                    - Podstawa 2 na 2, wysokość 1, przekątna 3.
                    - Podstawa 2 na 4, wysokość 4, przekątna 6.
                    - Podstawa 4 na 4, wysokość 2, przekątna 6.
                    - Podstawa 2 na 3, wysokość 6, przekątna 7.
                    - Podstawa 2 na 6, wysokość 3, przekątna 7.
                    - Podstawa 3 na 6, wysokość 2, przekątna 7.
                    
                    Zadanie pochodzi z Potyczek Algorytmicznych 2025 i zostało dodane tylko w calach testowych, nie będzie dostępne w publicznej wersji systemu.
                    """,
            1,
            0,
            ZonedDateTime.now()
    );

    @Override
    public void run(String... args) {
        try {
            addTemplate(matura05_2023Python);
            addTemplate(matura06_2024Python);
            addTemplate(PAAkwarium);
//            addTemplate(matura05_2023CSharp);
//            addTemplate(matura05_2023Java);
//            addTemplate(matura05_2022Python);
//            addTemplate(OI31Etap1BudowaLotniskaPython);
        } catch (Exception ex) {
            log.warning(ex.getMessage());
        }
    }

    private void addTemplate(Template template) {
        templateService.save(template);
    }
}
