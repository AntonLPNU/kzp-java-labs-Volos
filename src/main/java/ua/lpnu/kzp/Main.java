package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Entry point for the lab Java console application. */
public final class Main {
    private static final String VERSION = "1.0.0";
    private static final Path DEFAULT_INPUT = Path.of("data", "input.csv");
    private static final int EXPECTED_FIELD_COUNT = 5;

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
            printInputLines(input);
        } catch (IllegalArgumentException | IOException exception) {
            System.err.printf("Error: %s%n", exception.getMessage());
            System.exit(1);
        }
    }

    static String helpText() {
        return """
                Usage: java -jar target/kzp-java-labs-volos-1.0.0.jar [--help] [--version] [--input <file>] [--output <file>]

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

    static void printInputLines(Path input) throws IOException {
        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        int validRows = 0;
        int invalidRows = 0;

        System.out.printf("Input file: %s%n", input);
        System.out.printf("Rows: %d%n", lines.size());
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            String[] fields = line.split(";", -1);

            String error = validateTripFields(fields, index + 1);
            if (error == null) {
                validRows++;
                System.out.printf("%2d | valid   | %s%n", index + 1, line);
            } else {
                invalidRows++;
                System.out.printf("%2d | invalid | %s | %s%n", index + 1, error, line);
            }
        }
        System.out.printf("Valid rows: %d%n", validRows);
        System.out.printf("Invalid rows: %d%n", invalidRows);
    }

    static String validateTripFields(String[] fields, int lineNumber) {
        if (fields.length != EXPECTED_FIELD_COUNT) {
            return "line %d: expected %d fields, got %d".formatted(lineNumber, EXPECTED_FIELD_COUNT, fields.length);
        }

        String vehicle = fields[0].trim();
        String driver = fields[1].trim();
        String date = fields[4].trim();
        if (vehicle.isBlank()) {
            return "line %d: vehicle is required".formatted(lineNumber);
        }
        if (driver.isBlank()) {
            return "line %d: driver is required".formatted(lineNumber);
        }
        if (date.isBlank()) {
            return "line %d: date is required".formatted(lineNumber);
        }

        Double km = parseDouble(fields[2]);
        if (km == null) {
            return "line %d: km has invalid number format".formatted(lineNumber);
        }
        Double fuelLiters = parseDouble(fields[3]);
        if (fuelLiters == null) {
            return "line %d: fuelLiters has invalid number format".formatted(lineNumber);
        }
        if (km <= 0.0) {
            return "line %d: km must be greater than 0".formatted(lineNumber);
        }
        if (fuelLiters < 0.0) {
            return "line %d: fuelLiters must not be negative".formatted(lineNumber);
        }

        return null;
    }

    private static Double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
