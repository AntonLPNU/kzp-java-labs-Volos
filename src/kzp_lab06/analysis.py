"""Розрахунки за записами автопарку."""


def long_trips(records: list[dict[str, object]]) -> list[dict[str, object]]:
    """Відбирає поїздки строго понад 100 км."""
    return [row for row in records if float(row["kilometers"]) > 100]


def drivers(records: list[dict[str, object]]) -> set[str]:
    """Множина унікальних водіїв."""
    return {str(row["driver"]) for row in records}


def kilometers_by_vehicle(records: list[dict[str, object]]) -> dict[str, float]:
    """Повертає сумарну відстань кожного авто."""
    vehicles = {str(row["vehicle"]) for row in records}
    return {
        vehicle: sum(float(row["kilometers"]) for row in records if row["vehicle"] == vehicle)
        for vehicle in sorted(vehicles)
    }


def summarize(
    records: list[dict[str, object]],
    *metrics: str,
    minimum_km: float = 0.0,
    **labels: str,
) -> dict[str, float | int]:
    """Підраховує кількість і вибрані суми з налаштовуваними назвами."""
    allowed = {"kilometers", "fuel_liters"}
    for metric in metrics:
        if metric not in allowed:
            raise ValueError(f"Невідома метрика: {metric}")
    if minimum_km < 0:
        raise ValueError("minimum_km не може бути від'ємним")
    selected = [row for row in records if float(row["kilometers"]) >= minimum_km]
    result: dict[str, float | int] = {"count": len(selected)}
    for metric in metrics:
        result[labels.get(metric, metric)] = sum(float(row[metric]) for row in selected)
    return result


def fuel_per_100_km(records: list[dict[str, object]]) -> float:
    """Загальна витрата літрів на 100 км; для порожнього списку 0."""
    distance = sum(float(row["kilometers"]) for row in records)
    fuel = sum(float(row["fuel_liters"]) for row in records)
    if distance == 0:
        return 0.0
    return fuel / distance * 100
