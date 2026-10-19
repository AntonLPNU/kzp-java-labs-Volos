"""Читання CSV та перевірка записів автопарку."""

import csv
import math
from datetime import date
from pathlib import Path

from kzp_lab06.errors import DataError

COLUMNS = ["vehicle", "driver", "date", "kilometers", "fuel_liters"]


def parse_row(row: dict[str, str | None], line: int) -> dict[str, object]:
    """Перетворює рядкові дані на перевірений запис."""
    for field in COLUMNS:
        value = row.get(field)
        if value is None or not value.strip():
            raise DataError(f"Рядок {line}: відсутнє поле {field}")

    try:
        kilometers = float(row["kilometers"])
    except (TypeError, ValueError) as error:
        raise DataError(f"Рядок {line}: некоректне число kilometers") from error
    try:
        fuel = float(row["fuel_liters"])
    except (TypeError, ValueError) as error:
        raise DataError(f"Рядок {line}: некоректне число fuel_liters") from error
    try:
        trip_date = date.fromisoformat(str(row["date"]).strip())
    except ValueError as error:
        raise DataError(f"Рядок {line}: некоректна дата date") from error
    if not math.isfinite(kilometers) or kilometers <= 0:
        raise DataError(f"Рядок {line}: kilometers має бути додатним скінченним числом")
    if not math.isfinite(fuel) or fuel < 0:
        raise DataError(f"Рядок {line}: fuel_liters має бути невід'ємним скінченним числом")
    return {
        "vehicle": str(row["vehicle"]).strip(),
        "driver": str(row["driver"]).strip(),
        "date": trip_date,
        "kilometers": kilometers,
        "fuel_liters": fuel,
    }


def load_records(path: Path) -> list[dict[str, object]]:
    """Читає CSV у UTF-8; порожній файл без заголовка відхиляє."""
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
