package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;

/** Builds and persists the fleet report. */
final class TripReportService {
    String buildReport(Path input) throws IOException {
        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        List<Trip> trips = new ArrayList<>();
        int invalidRows = 0;
        StringBuilder report = new StringBuilder();

        report.append("Input file: %s%n".formatted(input));
        report.append("Rows: %d%n".formatted(lines.size()));
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            try {
                trips.add(Trip.fromCsv(line));
                report.append("%2d | valid   | %s%n".formatted(index + 1, line));
            } catch (IllegalArgumentException exception) {
                invalidRows++;
                report.append("%2d | invalid | line %d: %s | %s%n"
                        .formatted(index + 1, index + 1, exception.getMessage(), line));
            }
        }

        report.append("Valid rows: %d%n".formatted(trips.size()));
        report.append("Invalid rows: %d%n".formatted(invalidRows));
        if (!trips.isEmpty()) {
            appendStatistics(report, trips);
        }
        return report.toString();
    }

    void writeReport(Path output, String report) throws IOException {
        Path parent = output.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(output, report, StandardCharsets.UTF_8);
    }

    static String validateTripFields(String[] fields, int lineNumber) {
        try {
            Trip.fromCsv(String.join(";", fields));
            return null;
        } catch (IllegalArgumentException exception) {
            return "line %d: %s".formatted(lineNumber, exception.getMessage());
        }
    }

    static double fuelPer100Km(double fuelLiters, double km) {
        return new MileageFuel(km, fuelLiters).litersPer100Km();
    }

    private static void appendStatistics(StringBuilder report, List<Trip> trips) {
        DoubleSummaryStatistics distance = TripQueries.distanceStatistics(trips);
        double totalFuel = trips.stream()
                .mapToDouble(Trip::calculatedFuelLiters)
                .sum();
        Trip longestTrip = trips.stream()
                .max(Comparator.comparingDouble(Trip::getKm))
                .orElseThrow();

        report.append(String.format(Locale.ROOT, "Total distance: %.2f km%n", distance.getSum()));
        report.append(String.format(Locale.ROOT, "Average fuel consumption: %.2f l/100 km%n",
                totalFuel / distance.getSum() * 100.0));
        report.append(String.format(Locale.ROOT, "Longest trip: %s, %.2f km%n",
                longestTrip.getVehicle(), longestTrip.getKm()));
    }
}
