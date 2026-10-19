# Підсумковий звіт за лабораторними роботами № 1-7

## 1. Загальні відомості

- Курс: «Кросплатформні засоби програмування».
- Студент: Антон Волос; варіант 7; основне середовище розробки: Windows.
- Наскрізна предметна область: навчальний журнал пасажирських і вантажних рейсів автопарку Львова.
- Репозиторій: [kzp-java-labs-Volos](https://github.com/AntonLPNU/kzp-java-labs-Volos).
- Основна гілка: `main-java`.

Робота виконувалась послідовно: ЛР №1-5 розвивали Java-застосунок, а ЛР №6-7 додали окремий Python-пакет. Python не запускає Java, Java не запускає Python; реалізації мають власні точки входу, CSV-файли, тести та засоби збирання. Обидві частини використовують одну предметну область і зберігаються в одному репозиторії.

Цей документ підсумовує всі сім лабораторних. Детальний звіт і контрольні відповіді кожної роботи залишаються в окремих файлах: [ЛР №1](reports/lab01.md), [ЛР №2](reports/lab02.md), [ЛР №3](reports/lab03.md), [ЛР №4](reports/lab04.md), [ЛР №5](reports/lab05.md), [ЛР №6](reports/lab06.md), [ЛР №7](REPORT.md).

## 2. Розвиток продукту

| ЛР | Тема та основний результат | Версія / перевірка | GitHub |
| --- | --- | --- | --- |
| [1](reports/lab01.md) | Створено Java CLI для читання й валідації CSV, обчислення показників рейсів та запису звіту. Налаштовано Maven Wrapper, JUnit, SpotBugs, виконуваний JAR і CI. | Java `1.0.0`; 14 JUnit-тестів; CI для Windows, macOS та Ubuntu. | Issues [#1](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/1), [#2](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/2), [#3](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/3), [#4](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/4); [PR #5](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/5), документаційний [PR #6](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/6). |
| [2](reports/lab02.md) | Рядкові дані замінено незмінним класом `Trip`; додано фабрику CSV та `MileageFuel` як `record`. Зовнішній звіт збережено. | Java `1.1.0`; 23 тести; Maven і SpotBugs успішні; CI на трьох ОС. | Issue [#8](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/8); [PR #9](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/9); тег `v1.1.0`. |
| [3](reports/lab03.md) | Додано поліморфну ієрархію `Trip`, `PassengerTrip`, `CargoTrip`, категорії `TripKind`, рівність і хешування. | Java `1.2.0`; 28 тестів; SpotBugs без зауважень; CI на трьох ОС. | Issues [#11](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/11), [#12](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/12), [#13](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/13), [#14](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/14), [#15](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/15), [#16](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/16); [PR #17](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/17) та уточнення звіту в [PR #19](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/19). |
| [4](reports/lab04.md) | Реалізовано запити Stream API: фільтрацію, проєкцію, групування, статистику, сортування Top-N і пошук через `Optional`; результати звірено зі звичайним циклом. | Java `1.2.0`; 36 тестів; SpotBugs без помилок і попереджень; CI на трьох ОС. | Issues [#23](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/23), [#24](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/24), [#25](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/25), [#26](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/26), [#27](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/27); [PR #28](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/28). |
| [5](reports/lab05.md) | Додано `Repository<T>`, `TripRecord`, анотації CSV-полів, рефлексивний експортер, парсер quoted/multiline CSV і round-trip відновлення об'єктів. | Java `1.2.0`; 54 тести; SpotBugs без зауважень; CI на трьох ОС. | Issues [#29](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/29), [#30](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/30), [#31](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/31), [#32](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/32), [#33](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/33), [#34](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/34), [#35](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/35); [PR #36](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/36). |
| [6](reports/lab06.md) | Створено окремий Python CLI для CSV: типізоване читання, валідація, колекції, підсумки, текстовий звіт і пакування в wheel. | Python package `kzp-lab06-volos` `1.0.0`; 12 Python-тестів; Java-регресія: 54 тести; CI на трьох ОС. | Issues [#37](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/37), [#38](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/38), [#39](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/39), [#40](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/40), [#41](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/41), [#42](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/42); [PR #43](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/43). |
| [7](REPORT.md) | Словники ЛР6 перетворено на об'єкти `Trip`, `PassengerTrip`, `CargoTrip`; додано properties-валидацію, `Protocol`, поліморфний розрахунок пального та незмінний `ReportSummary`. | Спільний Python package `kzp-python-labs-volos` `1.1.0`; 25 Python-тестів разом із ЛР6; Java-регресія: 54 тести; Python і Java CI на трьох ОС. | Issues [#44](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/44), [#45](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/45), [#46](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/46), [#47](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/47), [#48](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/48), [#49](https://github.com/AntonLPNU/kzp-java-labs-Volos/issues/49); [PR #50](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/50), злитий у `main-java`. |

Зазначені кількості тестів є результатами відповідних етапів, а не числами, які треба складати: пізніші Java-набори включають попередні тести. На актуальному стані Java-набір має 54 тести, Python-набір ЛР6-7 — 25.

## 3. Наскрізна архітектура

### Java, лабораторні №1-5

Java 21-застосунок запускається через Maven Wrapper. Початковий п'ятипольний CSV у `data/input.csv` використовує `;` як роздільник. `Main` читає та перевіряє записи, формує звіт і може експортувати типізовані CSV-записи. У ЛР2 поля рейсу були зібрані в незмінну сутність; у ЛР3 додано пасажирський і вантажний підтипи та спільну поведінку; ЛР4 винесла запити колекцій у Stream API; ЛР5 додала типобезпечне сховище й CSV round-trip.

Java-модель відхиляє порожній транспорт і водія, нескінченні та некоректні числові значення, кілометраж `<= 0` і пальне `< 0`. Для вантажного рейсу навчальна формула додає 10% базового пального на кожну тонну. Це демонстраційне припущення, не норматив витрати.

### Python, лабораторні №6-7

Python 3.11+ пакується як wheel `kzp-python-labs-volos` версії `1.1.0`; у ньому залишились команди `kzp-lab06` і `kzp-lab07`. Пакети розташовані в `src/kzp_lab06/` і `src/kzp_lab07/`. Python CSV-файли мають коми як роздільники та окремі назви `data/lab06-input.csv`, `data/lab07-input.csv`, `data/lab07-mixed.csv`; Java-вхід `data/input.csv` не змінюється.

ЛР6 зчитує рядки через `csv.DictReader` та формує типізовані значення. ЛР7 переносить спільні правила в модель, а створення підтипу виконує loader. `PassengerTrip.fuel_need()` повертає записані літри; `CargoTrip` застосовує демонстраційну надбавку за вантаж. Спільний `FuelTrip(Protocol)` дає аналізу викликати метод без умов за конкретним класом. Формат контрольного звіту збережено: старий і новий варіанти дали точний текстовий збіг на тому самому CSV.

## 4. Підсумкова перевірка

На опублікованому стані PR #50 завершилися успішно Python CI і Java CI на Ubuntu, Windows та macOS. Python CI виконує pytest, Ruff lint, перевірку форматування, збірку wheel і завантаження артефакту; Java CI виконує Maven `verify`, пакує та запускає JAR.

- [Python CI, усі три ОС](https://github.com/AntonLPNU/kzp-java-labs-Volos/actions/runs/39252906831).
- [Java CI, усі три ОС](https://github.com/AntonLPNU/kzp-java-labs-Volos/actions/runs/39252906861).
- Локально на Windows: 25 Python-тестів; Ruff lint і format; збірка sdist/wheel; встановлення wheel у чисте середовище та smoke test обох Python CLI; `mvnw.cmd -B verify` із 54 Java-тестами та SpotBugs без помилок.
- Контрольні показники Java на демонстраційному `data/input.csv`: 8 коректних і 4 некоректні записи, 375.10 км, середня витрата 22.71 л/100 км, найдовший рейс 78.00 км.
- Контрольний Python-звіт ЛР7 на `data/lab07-input.csv` побайтно збігається з форматом попередньої словникової реалізації. Змішаний файл додатково перевіряє обидва підтипи.

Усі робочі Pull Request зведені в основну гілку `main-java`: [PR #5](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/5), [#9](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/9), [#17](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/17), [#28](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/28), [#36](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/36), [#43](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/43), [#50](https://github.com/AntonLPNU/kzp-java-labs-Volos/pull/50). Документаційні PR #6 і #19 також злиті. Реалізація кожної лабораторної виконувалась у власній гілці.

## 5. Запуск

Java-збірка й перевірки з кореня репозиторію у Windows:

```powershell
.\mvnw.cmd -B verify
.\mvnw.cmd -B package
java -jar target\kzp-java-labs-volos-1.2.0.jar
```

Python-залежності та команди:

```powershell
py -3 -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -e ".[dev]"
python -m pytest
python -m kzp_lab06.cli data\lab06-input.csv out\lab06-report.txt
python -m kzp_lab07.cli data\lab07-input.csv out\lab07-report.txt
```

## 6. Використання ШІ та внесок

Студент обрав варіант і предметну область, визначав послідовність розвитку одного проєкту, працював із Git і GitHub, перевіряв збірки та CI, надавав власний код для інтеграції й переглядав звіти. У Python-роботах студент передав власні модулі та тести; для Java-робіт також надавав власні фрагменти коду й пробував самостійно реалізовувати рішення, а потім просив перевірити та покращити їх.

ChatGPT/Codex допомагав розбирати методички, планувати зміни, реалізовувати або вдосконалювати частини Java/Python-коду, створювати й розширювати тести, налаштовувати Maven, пакування, CI та документацію. Розподіл конкретних змін наведено в окремих звітах лабораторних; цей підсумок не приписує студентові код, фактично створений асистентом, і не приховує допомогу ШІ. Студент відповідає за фінальний стан репозиторію, достовірність результатів і пояснення рішень під час захисту.

## 7. Висновок

За сім лабораторних робіт навчальний Java-проєкт виріс від консольного CSV-обробника до типізованої ієрархії рейсів зі Stream API та анотованим CSV-сховищем. Останні дві роботи додали поруч окрему Python-реалізацію: спершу процедурний аналіз CSV, далі — об'єктну модель із підтипами та поліморфною поведінкою. Попередні формати й окремі звіти збережені, а основні гілки лабораторних об'єднані в `main-java` після перевірок CI.
