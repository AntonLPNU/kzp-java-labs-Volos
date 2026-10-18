package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Entry point for the lab Java console application. */
public final class Main {
    private static final String VERSION = "1.2.0";
    private static final Path DEFAULT_INPUT = Path.of("data", "input.csv");
    private static final Path DEFAULT_OUTPUT = Path.of("out", "report.txt");

    private Main() {
    }

    /**
     * Starts the console application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0 && "--help".equals(args[0])) {
            System.out.print(helpText());
            return;
        }

        if (args.length > 0 && "--version".equals(args[0])) {
            System.out.printf("kzp-java-labs-volos %s%n", VERSION);
            return;
        }

        try {
            Path input = inputPath(args);
            Path output = outputPath(args);
            String report = buildReport(input);
            System.out.print(report);
            writeReport(output, report);
        } catch (IllegalArgumentException | IOException exception) {
            System.err.printf("Error: %s%n", exception.getMessage());
            System.exit(1);
        }
    }

    static String helpText() {
        return """
                Usage: java -jar target/kzp-java-labs-volos-1.2.0.jar [--help] [--version] [--input <file>] [--output <file>]

                Options:
                  --help           Show this help message.
                  --version        Show application version.
                  --input <file>   Path to input CSV file. Default: data/input.csv.
                  --output <file>  Path to report file. Default: out/report.txt.
                """;
    }

    static Path inputPath(String[] args) {
        for (int index = 0; index < args.length; index++) {
            if ("--input".equals(args[index])) {
                if (index + 1 >= args.length) {
                    throw new IllegalArgumentException("Missing value for --input");
                }
                return Path.of(args[index + 1]);
            }
        }
        return DEFAULT_INPUT;
    }

    static Path outputPath(String[] args) {
        for (int index = 0; index < args.length; index++) {
            if ("--output".equals(args[index])) {
                if (index + 1 >= args.length) {
                    throw new IllegalArgumentException("Missing value for --output");
                }
                return Path.of(args[index + 1]);
            }
        }
        return DEFAULT_OUTPUT;
    }

    static String buildReport(Path input) throws IOException {
        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        List<Trip> validTrips = new ArrayList<>();
        int invalidRows = 0;
        StringBuilder report = new StringBuilder();

        report.append("Input file: %s%n".formatted(input));
        report.append("Rows: %d%n".formatted(lines.size()));
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            try {
                validTrips.add(Trip.fromCsv(line));
                report.append("%2d | valid   | %s%n".formatted(index + 1, line));
            } catch (IllegalArgumentException exception) {
                invalidRows++;
                report.append("%2d | invalid | line %d: %s | %s%n"
                        .formatted(index + 1, index + 1, exception.getMessage(), line));
            }
        }
        report.append("Valid rows: %d%n".formatted(validTrips.size()));
        report.append("Invalid rows: %d%n".formatted(invalidRows));
        if (!validTrips.isEmpty()) {
            MileageFuel totals = new MileageFuel(0.0, 0.0);
            Trip longestTrip = validTrips.get(0);
            for (Trip trip : validTrips) {
                totals = totals.plus(trip);
                if (trip.getKm() > longestTrip.getKm()) {
                    longestTrip = trip;
                }
            }
            report.append(String.format(Locale.ROOT, "Total distance: %.2f km%n", totals.kilometers()));
            report.append(String.format(Locale.ROOT, "Average fuel consumption: %.2f l/100 km%n", totals.litersPer100Km()));
            report.append(String.format(Locale.ROOT, "Longest trip: %s, %.2f km%n", longestTrip.getVehicle(), longestTrip.getKm()));
        }

        return report.toString();
    }

    static void writeReport(Path output, String report) throws IOException {
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
}
