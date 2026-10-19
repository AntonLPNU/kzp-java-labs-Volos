"""Запуск обробки з командного рядка."""

import argparse
import sys
from pathlib import Path

from kzp_lab06.errors import DataError
from kzp_lab06.loader import load_records
from kzp_lab06.reporting import format_report, write_report


def main(argv: list[str] | None = None) -> int:
    """Повертає 0 при успіху і 1 при очікуваній помилці."""
    parser = argparse.ArgumentParser(description="ЛР №6: автопарк, варіант 7")
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
    sys.exit(main())
