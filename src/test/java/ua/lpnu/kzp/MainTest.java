package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {
    @Test
    void helpTextMentionsInputAndOutputOptions() {
        String help = CommandLineParser.helpText();

        assertTrue(help.contains("--input"));
        assertTrue(help.contains("--output"));
    }

    @Test
    void inputPathUsesDefaultWhenOptionIsMissing() {
        assertTrue(CommandLineParser.parse(new String[0]).input().endsWith(Path.of("data", "input.csv")));
    }

    @Test
    void inputPathUsesCommandLineValue() {
        Path input = CommandLineParser.parse(new String[] {"--input", "data/custom.csv"}).input();

        assertTrue(input.endsWith(Path.of("data", "custom.csv")));
    }

    @Test
    void outputPathUsesCommandLineValue() {
        Path output = CommandLineParser.parse(new String[] {"--output", "out/custom-report.txt"}).output();

        assertTrue(output.endsWith(Path.of("out", "custom-report.txt")));
    }

    @Test
    void splitKeepsExpectedTripFields() {
        String line = "Львів 2 - Жовква 06:15;Петро Коваль;29.4;6.8;2026-09-10";

        String[] fields = line.split(";", -1);

        assertTrue(fields.length == 5);
    }

    @Test
    void splitKeepsBlankFieldsForValidation() {
        String line = ";Петро Коваль;29.4;6.8;2026-09-11";

        String[] fields = line.split(";", -1);

        assertTrue(fields.length == 5);
        assertTrue(fields[0].isBlank());
    }

    @Test
    void validTripFieldsHaveNoValidationError() {
        String[] fields = "Львів 2 - Жовква 06:15;Петро Коваль;29.4;6.8;2026-09-10".split(";", -1);

        assertNull(TripReportService.validateTripFields(fields, 1));
    }

    @Test
    void validationRejectsBlankVehicle() {
        String[] fields = ";Петро Коваль;29.4;6.8;2026-09-11".split(";", -1);

        assertTrue(TripReportService.validateTripFields(fields, 9).contains("vehicle is required"));
    }

    @Test
    void validationRejectsInvalidKmNumber() {
        String[] fields = "Львів 2 - Цетуля 16:10;Олена Кравець;помилка;8.2;2026-09-11".split(";", -1);

        assertTrue(TripReportService.validateTripFields(fields, 10).contains("km has invalid number format"));
    }

    @Test
    void validationRejectsNegativeKm() {
        String[] fields = "Львів 2 - Карів 08:50;Степан Мороз;-10.0;2.5;2026-09-11".split(";", -1);

        assertTrue(TripReportService.validateTripFields(fields, 11).contains("km must be greater than 0"));
    }

    @Test
    void validationRejectsBlankDriver() {
        String[] fields = "Львів 2 - Дев'ятир 10:20; ;45.0;10.1;2026-09-11".split(";", -1);

        assertTrue(TripReportService.validateTripFields(fields, 12).contains("driver is required"));
    }

    @Test
    void fuelPer100KmUsesTotalFuelAndTotalDistance() {
        double result = TripReportService.fuelPer100Km(85.2, 375.1);

        assertEquals(22.71, result, 0.01);
    }

    @Test
    void buildReportContainsCalculatedIndicators() throws IOException {
        String report = new TripReportService().buildReport(Path.of("data", "input.csv"));

        assertTrue(report.contains("Valid rows: 8"));
        assertTrue(report.contains("Invalid rows: 4"));
        assertTrue(report.contains("Total distance: 375.10 km"));
        assertTrue(report.contains("Average fuel consumption: 22.71 l/100 km"));
        assertTrue(report.contains("Longest trip: Львів 2 - Сокаль 16:20, 78.00 km"));
    }

    @Test
    void invalidTripsNeverEnterStatistics(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("invalid.csv");
        Files.writeString(input, "Route;Driver;NaN;1;date\nRoute;Driver;0;1;date\n", StandardCharsets.UTF_8);

        String report = new TripReportService().buildReport(input);

        assertTrue(report.contains("Valid rows: 0"));
        assertTrue(report.contains("Invalid rows: 2"));
        assertTrue(report.contains("line 1: km must be finite"));
        assertTrue(report.contains("line 2: km must be greater than 0"));
        assertTrue(!report.contains("Total distance:"));
    }

    @Test
    void writeReportCreatesOutputFile(@TempDir Path tempDir) throws IOException {
        Path output = tempDir.resolve("report.txt");

        new TripReportService().writeReport(output, "test report");

        assertEquals("test report", Files.readString(output));
    }
}
