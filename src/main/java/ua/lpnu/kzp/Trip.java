package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/** Shared immutable state and validation for passenger and cargo trips. */
public abstract sealed class Trip permits PassengerTrip, CargoTrip {
    private static final int FIELD_COUNT = 5;

    private final String vehicle;
    private final String driver;
    private final double km;
    private final double fuelLiters;
    private final String date;
    private final TripKind kind;

    /** Assigns common fields already validated before construction. */
    protected Trip(ValidatedFields fields, TripKind kind) {
        this.vehicle = fields.vehicle();
        this.driver = fields.driver();
        this.km = fields.km();
        this.fuelLiters = fields.fuelLiters();
        this.date = fields.date();
        this.kind = kind;
    }

    /** Validates the shared state before a subtype constructor starts. */
    protected static ValidatedFields validatedFields(
            String vehicle, String driver, double km, double fuelLiters, String date) {
        String checkedVehicle = requiredText(vehicle, "vehicle");
        String checkedDriver = requiredText(driver, "driver");
        String checkedDate = requiredText(date, "date");
        if (km <= 0.0) {
            throw new IllegalArgumentException("km must be greater than 0");
        }
        if (!Double.isFinite(km)) {
            throw new IllegalArgumentException("km must be finite");
        }
        if (fuelLiters < 0.0) {
            throw new IllegalArgumentException("fuelLiters must not be negative");
        }
        if (!Double.isFinite(fuelLiters)) {
            throw new IllegalArgumentException("fuelLiters must be finite");
        }
        return new ValidatedFields(checkedVehicle, checkedDriver, km, fuelLiters, checkedDate);
    }

    /** An internal carrier for shared fields after validation. */
    protected record ValidatedFields(String vehicle, String driver, double km, double fuelLiters, String date) {
    }

    /** Parses a legacy five-field row or a cargo row marked in its vehicle field. */
    public static Trip fromCsv(String line) {
        if (line == null) {
            throw new IllegalArgumentException("CSV line is required");
        }
        String[] fields = line.split(";", -1);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException(
                    "expected %d fields, got %d".formatted(FIELD_COUNT, fields.length));
        }
        String vehicle = requiredText(fields[0], "vehicle");
        String driver = requiredText(fields[1], "driver");
        String date = requiredText(fields[4], "date");
        double km = parseNumber(fields[2], "km");
        double fuelLiters = parseNumber(fields[3], "fuelLiters");
        if (!vehicle.startsWith("CARGO:")) {
            return new PassengerTrip(vehicle, driver, km, fuelLiters, date);
        }
        int separator = vehicle.indexOf(' ');
        if (separator < 0 || !vehicle.substring(0, separator).endsWith("kg")) {
            throw new IllegalArgumentException("cargo marker must be CARGO:<load>kg <vehicle>");
        }
        String loadText = vehicle.substring("CARGO:".length(), separator - "kg".length());
        double loadKg = parseNumber(loadText, "loadKg");
        String cargoVehicle = requiredText(vehicle.substring(separator + 1), "vehicle");
        return new CargoTrip(cargoVehicle, driver, km, fuelLiters, date, loadKg);
    }

    private static String requiredText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    private static double parseNumber(String value, String field) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(field + " has invalid number format", exception);
        }
    }

    /** Returns the route or vehicle name without the CSV cargo marker. */
    public final String getVehicle() {
        return vehicle;
    }

    /** Returns the driver name. */
    public final String getDriver() {
        return driver;
    }

    /** Returns the trip distance in kilometers. */
    public final double getKm() {
        return km;
    }

    /** Returns the recorded baseline fuel in liters. */
    public final double getFuelLiters() {
        return fuelLiters;
    }

    /** Returns the date field as supplied in the CSV. */
    public final String getDate() {
        return date;
    }

    /** Returns the fixed trip category. */
    public final TripKind getKind() {
        return kind;
    }

    /** Calculates fuel for the trip according to its actual subtype. */
    public abstract double calculatedFuelLiters();

    /** Compares common fields; subclasses can extend equality with their own state. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Trip trip = (Trip) other;
        return Double.compare(km, trip.km) == 0
                && Double.compare(fuelLiters, trip.fuelLiters) == 0
                && vehicle.equals(trip.vehicle)
                && driver.equals(trip.driver)
                && date.equals(trip.date);
    }

    /** Returns a hash consistent with common-field equality. */
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), vehicle, driver, km, fuelLiters, date);
    }

    /** Returns a locale-independent representation of common fields. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s; %s; %.2f km; %.2f l; %s",
                vehicle, driver, km, fuelLiters, date);
    }
}
