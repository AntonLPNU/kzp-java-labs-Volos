package ua.lpnu.kzp;

import java.util.Locale;

/** An immutable trip with validated route, driver, distance, fuel, and date. */
public final class Trip {
    private static final int FIELD_COUNT = 5;

    private final String vehicle;
    private final String driver;
    private final double km;
    private final double fuelLiters;
    private final String date;

    /** Creates a trip, rejecting missing text and invalid measurements. */
    public Trip(String vehicle, String driver, double km, double fuelLiters, String date) {
        this.vehicle = requiredText(vehicle, "vehicle");
        this.driver = requiredText(driver, "driver");
        this.date = requiredText(date, "date");
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
        this.km = km;
        this.fuelLiters = fuelLiters;
    }

    /** Parses one semicolon-separated CSV row without a header. */
    public static Trip fromCsv(String line) {
        if (line == null) {
            throw new IllegalArgumentException("CSV line is required");
        }
        String[] fields = line.split(";", -1);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException(
                    "expected %d fields, got %d".formatted(FIELD_COUNT, fields.length));
        }
        // Preserve the existing validation order when several fields are invalid.
        String vehicle = requiredText(fields[0], "vehicle");
        String driver = requiredText(fields[1], "driver");
        String date = requiredText(fields[4], "date");
        double km = parseNumber(fields[2], "km");
        double fuelLiters = parseNumber(fields[3], "fuelLiters");
        return new Trip(vehicle, driver, km, fuelLiters, date);
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

    public String getVehicle() {
        return vehicle;
    }

    public String getDriver() {
        return driver;
    }

    public double getKm() {
        return km;
    }

    public double getFuelLiters() {
        return fuelLiters;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s; %s; %.2f km; %.2f l; %s",
                vehicle, driver, km, fuelLiters, date);
    }
}
