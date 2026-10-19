"""Тести об'єктної моделі ЛР №7."""

from dataclasses import FrozenInstanceError
from datetime import date
from pathlib import Path

import pytest

from kzp_lab07.analysis import (
    drivers,
    fuel_need_by_vehicle,
    fuel_needed,
    kilometers_by_vehicle,
    long_trips,
    summarize,
    summary,
)
from kzp_lab07.cli import main
from kzp_lab07.errors import DataError
from kzp_lab07.loader import load_records
from kzp_lab07.models import CargoTrip, PassengerTrip, ReportSummary
from kzp_lab07.reporting import format_report


@pytest.fixture
def records():
    return load_records(Path(__file__).resolve().parents[1] / "data/lab07-input.csv")


def test_load_and_comma(records):
    assert len(records) == 7
    assert isinstance(records[0], PassengerTrip)
    assert records[4].vehicle == '"Доставка, північ"'


def test_property_setter():
    trip = PassengerTrip("A", "B", date(2026, 9, 1), 100, 0)
    trip.kilometers = 120
    trip.fuel_liters = 0
    assert trip.kilometers == 120
    with pytest.raises(ValueError):
        trip.kilometers = 0
    with pytest.raises(ValueError):
        trip.fuel_liters = -1


def test_subtypes_and_protocol():
    passenger = PassengerTrip("A", "B", date(2026, 9, 1), 100, 20)
    cargo = CargoTrip("A", "B", date(2026, 9, 1), 100, 20, 1000)
    assert passenger.fuel_need() == 20
    assert cargo.fuel_need() == pytest.approx(22)
    assert fuel_needed([passenger, cargo]) == pytest.approx(42)
    assert fuel_need_by_vehicle([passenger, cargo]) == {"A": pytest.approx(42)}


def test_special_methods():
    first = PassengerTrip("A", "B", date(2026, 9, 1), 100, 20)
    same = PassengerTrip("A", "B", date(2026, 9, 1), 100, 20)
    cargo = CargoTrip("A", "B", date(2026, 9, 1), 100, 20, 1000)
    assert first == same
    assert first != cargo
    assert first != "text"
    assert "100.00 км" in str(first)
    assert "PassengerTrip" in repr(first)
    assert "load_kg=1000.0" in repr(cargo)


def test_frozen_summary(records):
    result = summary(records)
    assert isinstance(result, ReportSummary)
    assert result.count == 7
    with pytest.raises(FrozenInstanceError):
        result.count = 5


def test_analysis_and_boundary(records):
    assert len(long_trips(records)) == 4
    assert len(drivers(records)) == 4
    assert kilometers_by_vehicle(records)["Львів-Сокаль"] == 240
    result = summarize(records, "kilometers", minimum_km=110, kilometers="Відстань")
    assert result["Відстань"] == pytest.approx(510.2)
    with pytest.raises(ValueError):
        summarize(records, "unknown")


def test_empty_and_report(records):
    assert summary([]) == ReportSummary(0, 0, 0)
    assert long_trips([]) == []
    assert fuel_needed([]) == 0
    assert "Кількість рейсів: 0" in format_report([])
    assert "Загальна дистанція: 694.60 км" in format_report(records)


def test_cargo_csv(tmp_path):
    path = tmp_path / "cargo.csv"
    path.write_text(
        "vehicle,driver,date,kilometers,fuel_liters\n"
        "CARGO:1000kg Вантажівка,Іван,2026-09-10,100,20\n",
        encoding="utf-8",
    )
    result = load_records(path)
    assert isinstance(result[0], CargoTrip)
    assert result[0].fuel_need() == pytest.approx(22)


def test_mixed_sample_uses_both_subtypes():
    path = Path(__file__).resolve().parents[1] / "data/lab07-mixed.csv"
    records = load_records(path)
    assert len(records) == 8
    assert any(isinstance(trip, PassengerTrip) for trip in records)
    assert any(isinstance(trip, CargoTrip) for trip in records)
    assert fuel_needed(records) == pytest.approx(164.3)


def test_bad_input_and_cause(tmp_path):
    path = tmp_path / "bad.csv"
    path.write_text(
        "vehicle,driver,date,kilometers,fuel_liters\nA,B,2026-09-10,wrong,5\n",
        encoding="utf-8",
    )
    with pytest.raises(DataError, match="Рядок 2:.*kilometers") as info:
        load_records(path)
    assert isinstance(info.value.__cause__, ValueError)


def test_header_only_csv_is_empty_input(tmp_path):
    path = tmp_path / "empty.csv"
    path.write_text("vehicle,driver,date,kilometers,fuel_liters\n", encoding="utf-8")
    assert load_records(path) == []


def test_missing_and_extra_fields(tmp_path):
    path = tmp_path / "bad.csv"
    path.write_text(
        "vehicle,driver,date,kilometers,fuel_liters\nA,B,2026-09-10,1\n", encoding="utf-8"
    )
    with pytest.raises(DataError, match="fuel_liters"):
        load_records(path)
    path.write_text(
        "vehicle,driver,date,kilometers,fuel_liters\nA,B,2026-09-10,1,2,3\n", encoding="utf-8"
    )
    with pytest.raises(DataError, match="зайві"):
        load_records(path)


def test_cli(tmp_path):
    input_path = Path(__file__).resolve().parents[1] / "data/lab07-input.csv"
    output = tmp_path / "report.txt"
    assert main([str(input_path), str(output)]) == 0
    assert output.exists()
    assert main([str(tmp_path / "missing.csv"), str(output)]) == 1
