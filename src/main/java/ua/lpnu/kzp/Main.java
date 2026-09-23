package ua.lpnu.kzp;

/** Entry point for the lab Java console application. */
public final class Main {
    private static final String VERSION = "1.0.0";

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

        System.out.println("Lab 01: bus trips report for Lviv-Zhovkva route");
        System.out.println("CSV processing will be implemented in the next step.");
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
}
