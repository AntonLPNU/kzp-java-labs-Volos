"""Тести ЛР 6."""

import csv
from pathlib import Path

import pytest

from kzp_lab06.analysis import (
    drivers,
    fuel_per_100_km,
    kilometers_by_vehicle,
    long_trips,
    summarize,
)
from kzp_lab06.cli import main
from kzp_lab06.errors import DataError
from kzp_lab06.loader import load_records
from kzp_lab06.reporting import format_report, write_report


@pytest.fixture
def records() -> list[dict[str, object]]:
    return load_records(Path(__file__).resolve().parents[1] / "data/lab06-input.csv")


def test_loading_and_comma(records):
    assert len(records) == 7
    assert records[4]["vehicle"] == "Доставка, північ"
    assert isinstance(records[0]["kilometers"], float)


def test_filter_boundary(records):
    assert len(long_trips(records)) == 4
    assert all(row["kilometers"] > 100 for row in long_trips(records))


def test_group_and_duplicates(records):
    assert kilometers_by_vehicle(records)["Львів-Сокаль"] == 240
    assert len(drivers(records)) == 4


def test_summary_args_kwargs(records):
    assert summarize(records, "kilometers")["count"] == len(records)
    result = summarize(records, "kilometers", minimum_km=110, kilometers="Відстань")
    assert result["count"] == 4
    assert result["Відстань"] == pytest.approx(510.2)
    with pytest.raises(ValueError):
        summarize(records, "unknown")


def test_empty():
    assert kilometers_by_vehicle([]) == {}
    assert drivers([]) == set()
    assert long_trips([]) == []
    assert summarize([], "kilometers")["kilometers"] == 0
    assert fuel_per_100_km([]) == 0
    assert "Кількість рейсів: 0" in format_report([])


def test_missing_header(tmp_path):
    path = tmp_path / "wrong.csv"
    path.write_text("vehicle,driver,date\na,b,c\n", encoding="utf-8")
    with pytest.raises(DataError, match="заголовок"):
        load_records(path)


def test_empty_file_requires_header_but_header_only_is_valid(tmp_path):
    path = tmp_path / "empty.csv"
    path.write_text("", encoding="utf-8")
    with pytest.raises(DataError, match="заголовок"):
        load_records(path)

    path.write_text("vehicle,driver,date,kilometers,fuel_liters\n", encoding="utf-8")
    assert load_records(path) == []


def test_bad_number_cause(tmp_path):
    path = tmp_path / "bad.csv"
    path.write_text(
        "vehicle,driver,date,kilometers,fuel_liters\na,b,2026-09-10,wrong,2\n",
        encoding="utf-8",
    )
    with pytest.raises(DataError, match="Рядок 2:.*kilometers") as result:
        load_records(path)
    assert isinstance(result.value.__cause__, ValueError)


def test_missing_value(tmp_path):
    path = tmp_path / "missing.csv"
    path.write_text(
        "vehicle,driver,date,kilometers,fuel_liters\na,b,2026-09-10,10\n",
        encoding="utf-8",
    )
    with pytest.raises(DataError, match="fuel_liters"):
        load_records(path)


def test_extra_column(tmp_path):
    path = tmp_path / "extra.csv"
    path.write_text(
        "vehicle,driver,date,kilometers,fuel_liters\na,b,2026-09-10,10,2,extra\n",
        encoding="utf-8",
    )
    with pytest.raises(DataError, match="зайві"):
        load_records(path)


def test_negative_and_nan(tmp_path):
    for value in ["-2", "NaN"]:
        path = tmp_path / "value.csv"
        with path.open("w", encoding="utf-8", newline="") as file:
            writer = csv.writer(file)
            writer.writerow(["vehicle", "driver", "date", "kilometers", "fuel_liters"])
            writer.writerow(["a", "b", "2026-09-10", value, "2"])
        with pytest.raises(DataError, match="kilometers"):
            load_records(path)


def test_report_and_cli(tmp_path, records):
    output = tmp_path / "report.txt"
    write_report(output, format_report(records))
    text = output.read_text(encoding="utf-8")
    assert "Загальна дистанція: 694.60 км" in text
    assert main([str(tmp_path / "nonexistent.csv"), str(output)]) == 1
    assert (
        main([str(Path(__file__).resolve().parents[1] / "data/lab06-input.csv"), str(output)]) == 0
    )
