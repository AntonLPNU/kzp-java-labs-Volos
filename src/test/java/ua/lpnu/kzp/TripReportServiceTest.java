package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TripReportServiceTest {
    private final TripReportService service = new TripReportService();

    @Test
    void buildReportContainsCalculatedIndicators() throws IOException {
        String report = service.buildReport(Path.of("data", "input.csv"));

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

        String report = service.buildReport(input);

        assertTrue(report.contains("Valid rows: 0"));
        assertTrue(report.contains("Invalid rows: 2"));
        assertTrue(report.contains("line 1: km must be finite"));
        assertTrue(report.contains("line 2: km must be greater than 0"));
        assertTrue(!report.contains("Total distance:"));
    }

    @Test
    void writeReportCreatesOutputFile(@TempDir Path tempDir) throws IOException {
        Path output = tempDir.resolve("report/report.txt");

        service.writeReport(output, "test report");

        assertEquals("test report", Files.readString(output));
    }

    @Test
    void exportKeepsValidRowsAndReportsSkippedRows(@TempDir Path tempDir) throws Exception {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("output/trips.csv");
        Files.writeString(input, "Route;Driver;120;15;2026-10-01\n"
                + "CARGO:1000kg Truck;Driver;130;20;2026-10-02\n"
                + "bad;line\n");

        assertEquals(1, service.exportCsv(input, output));
        assertEquals(2, TripCsvStorage.load(output).size());
    }
}
