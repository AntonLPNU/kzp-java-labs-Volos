"""Запуск програми через командний рядок."""

import argparse
import sys
from pathlib import Path

from kzp_lab07.errors import DataError
from kzp_lab07.loader import load_records
from kzp_lab07.reporting import format_report, write_report


def main(argv: list[str] | None = None) -> int:
    """Повертає 0 для успіху та 1 для очікуваної помилки."""
    parser = argparse.ArgumentParser(description="ЛР №7: автопарк, варіант 7")
    parser.add_argument("input", type=Path, help="вхідний CSV")
    parser.add_argument("output", type=Path, help="вихідний текстовий звіт")
    args = parser.parse_args(argv)
    try:
        records = load_records(args.input)
        write_report(args.output, format_report(records))
    except (DataError, OSError) as error:
        try:
            sys.stderr.reconfigure(encoding="utf-8")
        except (AttributeError, ValueError):
            pass
        print(f"Помилка: {error}", file=sys.stderr)
        return 1
    print(f"Звіт збережено: {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
