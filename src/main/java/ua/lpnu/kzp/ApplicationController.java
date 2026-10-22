package ua.lpnu.kzp;

import java.io.IOException;
import java.io.PrintStream;

/** Coordinates command-line parsing, report generation, and console output. */
final class ApplicationController {
    private final TripReportService reportService;

    ApplicationController() {
        this(new TripReportService());
    }

    ApplicationController(TripReportService reportService) {
        this.reportService = reportService;
    }

    int run(String[] args, PrintStream output, PrintStream errorOutput) {
        CommandLineOptions options;
        try {
            options = CommandLineParser.parse(args);
        } catch (IllegalArgumentException exception) {
            errorOutput.printf("Error: %s%n", exception.getMessage());
            return 1;
        }

        if (options.action() == CommandLineOptions.Action.HELP) {
            output.print(CommandLineParser.helpText());
            return 0;
        }
        if (options.action() == CommandLineOptions.Action.VERSION) {
            output.printf("kzp-java-labs-volos %s%n", ApplicationVersion.current());
            return 0;
        }

        try {
            String report = reportService.buildReport(options.input());
            reportService.writeReport(options.output(), report);
            output.print(report);
            if (options.csvOutput() != null) {
                int skippedRows = reportService.exportCsv(options.input(), options.csvOutput());
                output.printf("CSV export: %s; skipped invalid rows: %d%n", options.csvOutput(), skippedRows);
            }
            return 0;
        } catch (DataStorageException | IllegalArgumentException | IOException exception) {
            errorOutput.printf("Error: %s%n", exception.getMessage());
            return 1;
        }
    }
}
