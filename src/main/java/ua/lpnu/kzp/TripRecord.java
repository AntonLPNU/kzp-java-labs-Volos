package ua.lpnu.kzp;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/** Immutable, flat CSV representation of a fleet trip for variant 7. */
public final class TripRecord {
    @CsvColumn("Авто")
    private final String vehicle;

    @CsvColumn("Водій")
    private final String driver;

    @CsvColumn("Дата")
    private final LocalDate date;

    @CsvColumn("Кілометраж")
    private final double kilometers;

    @CsvColumn("Пальне")
    private final double fuelLiters;

    /** Creates a validated trip record. */
    public TripRecord(String vehicle, String driver, LocalDate date,
                      double kilometers, double fuelLiters) {
        if (vehicle == null || vehicle.isBlank()
                || driver == null || driver.isBlank() || date == null) {
            throw new IllegalArgumentException("Vehicle, driver, and date are required");
        }
        if (!Double.isFinite(kilometers) || kilometers <= 0.0) {
            throw new IllegalArgumentException("Kilometers must be finite and greater than 0");
        }
        if (!Double.isFinite(fuelLiters) || fuelLiters < 0.0) {
            throw new IllegalArgumentException("Fuel must be finite and nonnegative");
        }
        this.vehicle = vehicle.trim();
        this.driver = driver.trim();
        this.date = date;
        this.kilometers = kilometers;
        this.fuelLiters = fuelLiters;
    }

    /** Converts an existing polymorphic trip to its flat export representation. */
    public static TripRecord fromTrip(Trip trip) {
        Objects.requireNonNull(trip, "trip must not be null");
        String vehicleName = trip.getVehicle();
        if (trip instanceof CargoTrip cargo) {
            vehicleName = "CARGO:" + formatWeight(cargo.getLoadKg()) + "kg " + vehicleName;
        }
        return new TripRecord(vehicleName, trip.getDriver(), LocalDate.parse(trip.getDate()),
                trip.getKm(), trip.getFuelLiters());
    }

    /** Recreates a passenger or cargo trip, retaining the cargo marker in the vehicle field. */
    public Trip toTrip() {
        String vehicleName = vehicle;
        double loadKg = 0.0;
        boolean cargoTrip = vehicle.startsWith("CARGO:");
        if (cargoTrip) {
            int markerEnd = vehicle.indexOf("kg ", "CARGO:".length());
            if (markerEnd < 0) {
                throw new IllegalStateException("Invalid cargo marker in stored vehicle");
            }
            String weight = vehicle.substring("CARGO:".length(), markerEnd);
            try {
                loadKg = Double.parseDouble(weight);
            } catch (NumberFormatException exception) {
                throw new IllegalStateException("Invalid cargo weight in stored vehicle", exception);
            }
            vehicleName = vehicle.substring(markerEnd + "kg ".length());
        }
        return cargoTrip
                ? new CargoTrip(vehicleName, driver, kilometers, fuelLiters, date.toString(), loadKg)
                : new PassengerTrip(vehicleName, driver, kilometers, fuelLiters, date.toString());
    }

    /** Restores a record from fields in the exporter's deterministic field order. */
    public static TripRecord fromCsvFields(List<String> fields) throws DataStorageException {
        if (fields.size() != 5) {
            throw new DataStorageException(
                    "Expected 5 CSV fields, received " + fields.size());
        }
        try {
            // Reflection sorts these Java field names: date, driver, fuelLiters, kilometers, vehicle.
            return new TripRecord(fields.get(4), fields.get(1), LocalDate.parse(fields.get(0)),
                    Double.parseDouble(fields.get(3)), Double.parseDouble(fields.get(2)));
        } catch (RuntimeException exception) {
            throw new DataStorageException("Invalid trip record in CSV", exception);
        }
    }

    /** Returns the vehicle name, including the cargo marker when applicable. */
    public String getVehicle() {
        return vehicle;
    }

    /** Returns the driver's name. */
    public String getDriver() {
        return driver;
    }

    /** Returns the trip date. */
    public LocalDate getDate() {
        return date;
    }

    /** Returns the driven distance in kilometers. */
    public double getKilometers() {
        return kilometers;
    }

    /** Returns the recorded fuel amount in liters. */
    public double getFuelLiters() {
        return fuelLiters;
    }

    private static String formatWeight(double value) {
        if (value == Math.rint(value) && value < Long.MAX_VALUE) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TripRecord record)) {
            return false;
        }
        return vehicle.equals(record.vehicle)
                && driver.equals(record.driver)
                && date.equals(record.date)
                && Double.compare(kilometers, record.kilometers) == 0
                && Double.compare(fuelLiters, record.fuelLiters) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicle, driver, date, kilometers, fuelLiters);
    }
}
