"""Перетворення CSV-рядків на об'єкти рейсів."""

import csv
from datetime import date
from pathlib import Path

from kzp_lab07.errors import DataError
from kzp_lab07.models import CargoTrip, PassengerTrip, Trip

COLUMNS = ["vehicle", "driver", "date", "kilometers", "fuel_liters"]


def parse_row(row: dict[str, str | None], line: int) -> Trip:
    """Створює пасажирський або вантажний рейс; додає контекст помилки."""
    for field in COLUMNS:
        value = row.get(field)
        if value is None or not value.strip():
            raise DataError(f"Рядок {line}: відсутнє поле {field}")

    vehicle = str(row["vehicle"]).strip()
    driver = str(row["driver"]).strip()
    try:
        kilometers = float(str(row["kilometers"]))
    except ValueError as error:
        raise DataError(f"Рядок {line}: некоректне число kilometers") from error
    try:
        fuel = float(str(row["fuel_liters"]))
    except ValueError as error:
        raise DataError(f"Рядок {line}: некоректне число fuel_liters") from error
    try:
        trip_date = date.fromisoformat(str(row["date"]).strip())
    except ValueError as error:
        raise DataError(f"Рядок {line}: некоректна дата date") from error

    try:
        # Позначка вантажного рейсу зберігається в vehicle, без нового CSV-стовпця.
        if vehicle.startswith("CARGO:"):
            marker, separator, name = vehicle.partition(" ")
            if not separator or not marker.endswith("kg") or not name.strip():
                raise ValueError("формат CARGO:<маса>kg <назва>")
            try:
                load_kg = float(marker[6:-2])
            except ValueError as error:
                raise ValueError("некоректна маса вантажу") from error
            return CargoTrip(name, driver, trip_date, kilometers, fuel, load_kg)
        return PassengerTrip(vehicle, driver, trip_date, kilometers, fuel)
    except ValueError as error:
        raise DataError(f"Рядок {line}: {error}") from error


def load_records(path: Path) -> list[Trip]:
    """Читає CSV і повертає тільки перевірені об'єкти."""
    try:
        with path.open("r", encoding="utf-8", newline="") as source:
            reader = csv.DictReader(source, strict=True)
            if reader.fieldnames != COLUMNS:
                raise DataError("Неправильний заголовок CSV: очікується " + ",".join(COLUMNS))
            records = []
            for row in reader:
                if None in row:
                    raise DataError(f"Рядок {reader.line_num}: зайві CSV-поля")
                records.append(parse_row(row, reader.line_num))
            return records
    except (OSError, UnicodeError, csv.Error) as error:
        raise DataError(f"Не вдалося прочитати CSV: {path}") from error
