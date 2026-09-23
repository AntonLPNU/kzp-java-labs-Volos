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

        System.out.printf("Input file: %s%n", input);
        System.out.printf("Rows: %d%n", lines.size());
        for (int index = 0; index < lines.size(); index++) {
            System.out.printf("%2d | %s%n", index + 1, lines.get(index));
        }
    }
}
