package ua.lpnu.kzp;

import java.nio.file.Path;

/** Parses command-line options without performing application work. */
final class CommandLineParser {
    private static final Path DEFAULT_INPUT = Path.of("data", "input.csv");
    private static final Path DEFAULT_OUTPUT = Path.of("out", "report.txt");
    private static final String HELP_TEXT = """
            Usage: java -jar target/kzp-java-labs-volos-<version>.jar [--help] [--version] [--input <file>] [--output <file>]

            Options:
              --help           Show this help message.
              --version        Show application version.
              --input <file>   Path to input CSV file. Default: data/input.csv.
              --output <file>  Path to report file. Default: out/report.txt.
            """;

    private CommandLineParser() {
    }

    static CommandLineOptions parse(String[] args) {
        Path input = DEFAULT_INPUT;
        Path output = DEFAULT_OUTPUT;
        boolean help = false;
        boolean version = false;

        for (int index = 0; index < args.length; index++) {
            switch (args[index]) {
                case "--help" -> help = true;
                case "--version" -> version = true;
                case "--input" -> input = Path.of(optionValue(args, index++, "--input"));
                case "--output" -> output = Path.of(optionValue(args, index++, "--output"));
                default -> throw new IllegalArgumentException("Unknown option: " + args[index]);
            }
        }

        if (help && version) {
            throw new IllegalArgumentException("Use either --help or --version");
        }
        CommandLineOptions.Action action = help
                ? CommandLineOptions.Action.HELP
                : version ? CommandLineOptions.Action.VERSION : CommandLineOptions.Action.RUN;
        return new CommandLineOptions(action, input, output);
    }

    static String helpText() {
        return HELP_TEXT;
    }

    private static String optionValue(String[] args, int index, String option) {
        if (index + 1 >= args.length || args[index + 1].startsWith("--")) {
            throw new IllegalArgumentException("Missing value for " + option);
        }
        return args[index + 1];
    }
}
