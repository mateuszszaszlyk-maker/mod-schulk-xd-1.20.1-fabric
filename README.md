# Sculk XP Control (Fabric 1.20.1)

Mały, jednofunkcyjny mod: zmienia **tylko** ilość XP wypadającego po zniszczeniu bloków sculk.
Ustawiasz to komendą w grze, bez restartu.

Obsługiwane bloki: `sculk`, `catalyst`, `sensor`, `calibrated_sensor`, `shrieker`.

## Wymagania
- Minecraft 1.20.1
- Fabric Loader 0.14.21+
- Fabric API (dowolna wersja do 1.20.1)

## Komendy (wymagają uprawnień op / włączonych cheatów)
| Komenda | Opis |
|---|---|
| `/sculkxp` | pokazuje aktualne ustawienia |
| `/sculkxp multiplier <0-1000>` | mnożnik dla wszystkich bloków sculk (np. `3` = 3x więcej XP, `0.5` = połowa) |
| `/sculkxp set <blok> <0-1000>` | bazowa ilość XP dla jednego bloku, np. `/sculkxp set sculk 10` |
| `/sculkxp reset` | przywraca wartości z vanilli |
| `/sculkxp reload` | wczytuje ponownie plik konfiguracyjny |

Wzór: **XP = XP bazowe bloku × mnożnik**. Domyślnie: sculk = 1, pozostałe = 5, mnożnik = 1
(czyli dokładnie jak w vanilli, dopóki sam czegoś nie zmienisz).
Ułamki są losowo zaokrąglane (2.5 XP = 2 lub 3, średnio 2.5).

W świecie jednoosobowym komendy działają, jeśli świat ma włączone cheaty
(przy tworzeniu świata albo "Otwórz dla sieci LAN" → "Zezwalaj na cheaty").

## Konfiguracja
Plik `config/sculk_xp_control.json` w folderze instancji. Można go też edytować ręcznie
(po zmianie użyj `/sculkxp reload` albo zrestartuj grę).

## Kompatybilność
Mod nie dodaje bloków, itemów ani niczego do rejestrów i nie nadpisuje żadnych klas.
Ma jeden mały mixin do `Block#dropExperience`, który zmienia ilość XP wyłącznie dla bloków sculk.
Silk Touch i gamerule `doTileDrops` działają jak w vanilli.
Na serwerze wystarczy mod po stronie serwera, gracze nie muszą go mieć.

## Budowanie
**Sposób 1 (najprościej, bez instalowania czegokolwiek):**
1. Załóż darmowe konto na github.com i utwórz nowe repozytorium.
2. Wgraj do niego całą zawartość tego folderu (razem z ukrytym folderem `.github`).
3. Wejdź w zakładkę **Actions** → uruchomi się build "build" (albo kliknij *Run workflow*).
4. Po ~2-3 minutach otwórz zakończony build i pobierz artefakt `sculk-xp-control`.
   W środku jest plik `sculk-xp-control-1.0.0.jar`.

**Sposób 2 (lokalnie):** zainstaluj JDK 17 oraz Gradle 8.6+, w tym folderze uruchom `gradle build`.
Gotowy plik: `build/libs/sculk-xp-control-1.0.0.jar`.

## Instalacja
1. W launcherze utwórz instancję **Minecraft 1.20.1 z Fabric Loader**.
2. Do folderu `mods` tej instancji wrzuć: `sculk-xp-control-1.0.0.jar` oraz **Fabric API**.
3. Uruchom grę, wejdź do świata z cheatami i wpisz `/sculkxp`.
