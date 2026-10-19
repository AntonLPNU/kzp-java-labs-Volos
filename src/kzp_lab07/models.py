"""Класи рейсів та незмінний результат."""

import math
from dataclasses import dataclass
from datetime import date
from typing import Protocol


class FuelTrip(Protocol):
    """Об'єкт із розрахунком потреби у пальному."""

    def fuel_need(self) -> float:
        """Повертає розрахункові літри пального."""
        ...


class Trip:
    """Спільні поля для пасажирського і вантажного рейсу."""

    def __init__(
        self,
        vehicle: str,
        driver: str,
        trip_date: date,
        kilometers: float,
        fuel_liters: float,
    ) -> None:
        """Створює рейс із перевіркою даних."""
        if not isinstance(vehicle, str) or not vehicle.strip():
            raise ValueError("vehicle не може бути порожнім")
        if not isinstance(driver, str) or not driver.strip():
            raise ValueError("driver не може бути порожнім")
        if not isinstance(trip_date, date):
            raise TypeError("date має бути датою")
        self.vehicle = vehicle.strip()
        self.driver = driver.strip()
        self.date = trip_date
        self.kilometers = kilometers
        self.fuel_liters = fuel_liters

    @property
    def kilometers(self) -> float:
        """Відстань рейсу."""
        return self._kilometers

    @kilometers.setter
    def kilometers(self, value: float) -> None:
        """Дозволяє лише додатну відстань."""
        if not isinstance(value, (int, float)) or not math.isfinite(value) or value <= 0:
            raise ValueError("kilometers має бути додатним скінченним числом")
        self._kilometers = float(value)

    @property
    def fuel_liters(self) -> float:
        """Записана витрата пального."""
        return self._fuel_liters

    @fuel_liters.setter
    def fuel_liters(self, value: float) -> None:
        """Дозволяє нульову або додатну витрату."""
        if not isinstance(value, (int, float)) or not math.isfinite(value) or value < 0:
            raise ValueError("fuel_liters має бути невід'ємним скінченним числом")
        self._fuel_liters = float(value)

    def fuel_need(self) -> float:
        """Спільна операція; формулу задають підтипи."""
        raise NotImplementedError

    def __str__(self) -> str:
        """Короткий опис рейсу."""
        return f"{self.vehicle}: {self.kilometers:.2f} км"

    def __repr__(self) -> str:
        """Зрозуміле подання для перевірки об'єкта."""
        return (
            f"{type(self).__name__}(vehicle={self.vehicle!r}, driver={self.driver!r}, "
            f"date={self.date!r}, kilometers={self.kilometers!r}, "
            f"fuel_liters={self.fuel_liters!r})"
        )

    def __eq__(self, other: object) -> bool:
        """Порівнює два рейси того самого підтипу."""
        if not isinstance(other, Trip):
            return NotImplemented
        return type(self) is type(other) and self.__dict__ == other.__dict__


class PassengerTrip(Trip):
    """Пасажирський рейс без додаткової надбавки."""

    def fuel_need(self) -> float:
        """Повертає фактичні літри пального."""
        return self.fuel_liters


class CargoTrip(Trip):
    """Вантажний рейс із навчальною надбавкою за масу вантажу."""

    def __init__(
        self,
        vehicle: str,
        driver: str,
        trip_date: date,
        kilometers: float,
        fuel_liters: float,
        load_kg: float,
    ) -> None:
        """Створює вантажний рейс і перевіряє масу."""
        super().__init__(vehicle, driver, trip_date, kilometers, fuel_liters)
        if not isinstance(load_kg, (int, float)) or not math.isfinite(load_kg) or load_kg <= 0:
            raise ValueError("load_kg має бути додатним скінченним числом")
        self.load_kg = float(load_kg)
        if not math.isfinite(self.fuel_need()):
            raise ValueError("Розрахована витрата пального завелика")

    def fuel_need(self) -> float:
        """Навчальна формула: +10% витрати на кожну тонну вантажу."""
        return self.fuel_liters * (1 + self.load_kg / 10000)

    def __repr__(self) -> str:
        """Відображає додаткове поле вантажу."""
        return super().__repr__()[:-1] + f", load_kg={self.load_kg!r})"


@dataclass(frozen=True)
class ReportSummary:
    """Незмінні підсумкові значення звіту."""

    count: int
    kilometers: float
    fuel_liters: float
