"""Формування та запис текстового звіту."""

from pathlib import Path

from kzp_lab06.analysis import (
    drivers,
    fuel_per_100_km,
    kilometers_by_vehicle,
    long_trips,
    summarize,
)


def format_report(records: list[dict[str, object]]) -> str:
    """Формує звіт у стабільному порядку."""
    totals = summarize(records, "kilometers", "fuel_liters")
    lines = [
        "Автопарк — варіант 7",
        f"Кількість рейсів: {totals['count']}",
        f"Рейсів понад 100 км: {len(long_trips(records))}",
        f"Загальна дистанція: {totals['kilometers']:.2f} км",
        f"Витрачено пального: {totals['fuel_liters']:.2f} л",
        f"Середня витрата: {fuel_per_100_km(records):.2f} л/100 км",
        "Водії: " + (", ".join(sorted(drivers(records))) or "немає"),
        "Кілометраж за автомобілями:",
    ]
    for vehicle, km in kilometers_by_vehicle(records).items():
        lines.append(f"  {vehicle}: {km:.2f} км")
    return "\n".join(lines) + "\n"


def write_report(path: Path, text: str) -> None:
    """Записує звіт з явним кодуванням UTF-8."""
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="\n") as target:
        target.write(text)
