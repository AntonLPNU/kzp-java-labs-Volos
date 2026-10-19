"""Обробка об'єктів автопарку."""

from kzp_lab07.models import FuelTrip, ReportSummary, Trip


def long_trips(records: list[Trip]) -> list[Trip]:
    """Відбирає рейси строго понад 100 км."""
    return [trip for trip in records if trip.kilometers > 100]


def drivers(records: list[Trip]) -> set[str]:
    """Повертає множину унікальних водіїв."""
    return {trip.driver for trip in records}


def kilometers_by_vehicle(records: list[Trip]) -> dict[str, float]:
    """Підраховує кілометри окремо для кожного автомобіля."""
    vehicles = {trip.vehicle for trip in records}
    return {
        vehicle: sum(trip.kilometers for trip in records if trip.vehicle == vehicle)
        for vehicle in sorted(vehicles)
    }


def fuel_needed(records: list[FuelTrip]) -> float:
    """Викликає спільну операцію підтипів без перевірки їх класів."""
    return sum(trip.fuel_need() for trip in records)


def fuel_need_by_vehicle(records: list[Trip]) -> dict[str, float]:
    """Обчислює розрахункову витрату за кожним автомобілем."""
    vehicles = {trip.vehicle for trip in records}
    return {
        vehicle: sum(trip.fuel_need() for trip in records if trip.vehicle == vehicle)
        for vehicle in sorted(vehicles)
    }


def summary(records: list[Trip]) -> ReportSummary:
    """Повертає незмінний підсумок записаних значень."""
    return ReportSummary(
        len(records),
        sum(trip.kilometers for trip in records),
        sum(trip.fuel_liters for trip in records),
    )


def summarize(
    records: list[Trip],
    *metrics: str,
    minimum_km: float = 0.0,
    **labels: str,
) -> dict[str, float | int]:
    """Сумісний з ЛР6 підрахунок обраних метрик."""
    for metric in metrics:
        if metric not in ("kilometers", "fuel_liters"):
            raise ValueError(f"Невідома метрика: {metric}")
    if minimum_km < 0:
        raise ValueError("minimum_km не може бути від'ємним")
    selected = [trip for trip in records if trip.kilometers >= minimum_km]
    result: dict[str, float | int] = {"count": len(selected)}
    for metric in metrics:
        result[labels.get(metric, metric)] = sum(getattr(trip, metric) for trip in selected)
    return result


def fuel_per_100_km(records: list[Trip]) -> float:
    """Збережена формула звіту ЛР6 для записаних літрів."""
    totals = summary(records)
    return totals.fuel_liters / totals.kilometers * 100 if totals.kilometers else 0.0
