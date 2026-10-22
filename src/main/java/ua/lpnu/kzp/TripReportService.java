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

/** Builds and persists the fleet report and its optional CSV export. */
final class TripReportService {
    String buildReport(Path input) throws IOException {
        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        Repository<Trip> tripRepository = new Repository<>();
        int invalidRows = 0;
        StringBuilder report = new StringBuilder();

        report.append("Input file: %s%n".formatted(input));
        report.append("Rows: %d%n".formatted(lines.size()));
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            try {
                tripRepository.add(Trip.fromCsv(line));
                report.append("%2d | valid   | %s%n".formatted(index + 1, line));
            } catch (IllegalArgumentException exception) {
                invalidRows++;
                report.append("%2d | invalid | line %d: %s | %s%n"
                        .formatted(index + 1, index + 1, exception.getMessage(), line));
            }
        }

        List<Trip> validTrips = tripRepository.all();
        report.append("Valid rows: %d%n".formatted(validTrips.size()));
        report.append("Invalid rows: %d%n".formatted(invalidRows));
        if (!validTrips.isEmpty()) {
            appendStatistics(report, validTrips);
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

    int exportCsv(Path input, Path output) throws IOException, DataStorageException {
        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        List<TripRecord> records = new ArrayList<>();
        int skippedRows = 0;
        for (String line : lines) {
            try {
                records.add(TripRecord.fromTrip(Trip.fromCsv(line)));
            } catch (IllegalArgumentException exception) {
                skippedRows++;
            }
        }
        TripCsvStorage.save(output, records);
        return skippedRows;
    }

    private static void appendStatistics(StringBuilder report, List<Trip> trips) {
        DoubleSummaryStatistics distance = TripQueries.distanceStatistics(trips);
        double totalFuel = trips.stream().mapToDouble(Trip::calculatedFuelLiters).sum();
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
