package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvStorageTest {
    private static final String HEADER = "Дата,Водій,Пальне,Кілометраж,Авто";

    @Test
    void csvRoundTripPreservesQuotedUnicodeAndMultilineValues(@TempDir Path directory)
            throws Exception {
        Path path = directory.resolve("nested/trips.csv");
        List<TripRecord> expected = List.of(
                record("Маршрут, \"нічний\"\nрейс"), record("Звичайне авто"),
                record("Маршрут із комою, другий"), record("Авто 4"), record("Авто 5"));

        TripCsvStorage.save(path, expected);

        String contents = Files.readString(path);
        assertTrue(contents.startsWith(HEADER + "\n"));
        assertTrue(contents.contains("\"Маршрут, \"\"нічний\"\"\nрейс\""));
        assertEquals(expected, TripCsvStorage.load(path));
    }

    @Test
    void parserHandlesCrLfTrailingEmptyFieldsAndEmptyDocument() throws Exception {
        assertEquals(List.of(), CsvParser.parseDocument(""));
        assertEquals(List.of(List.of("a", "b", ""), List.of("x", "y", "z")),
                CsvParser.parseDocument("a,b,\r\nx,y,z\r\n"));
        assertEquals(List.of(List.of("one\r\ntwo", "value")),
                CsvParser.parseDocument("\"one\r\ntwo\",value"));
    }

    @Test
    void emptyCollectionAndEmptyFileLoadAsEmpty(@TempDir Path directory) throws Exception {
        Path path = directory.resolve("trips.csv");
        TripCsvStorage.save(path, List.of());
        assertEquals(List.of(), TripCsvStorage.load(path));
        Files.writeString(path, "");
        assertEquals(List.of(), TripCsvStorage.load(path));
    }

    @Test
    void malformedCsvAndRecordsRetainUsefulCauses(@TempDir Path directory) throws Exception {
        Path path = directory.resolve("broken.csv");
        Files.writeString(path, HEADER + "\n2026-10-01,Driver,1,2\n");
        assertThrows(DataStorageException.class, () -> TripCsvStorage.load(path));

        Files.writeString(path, HEADER + "\n2026-10-01,Driver,not-a-number,2,Car\n");
        DataStorageException invalidRecord = assertThrows(DataStorageException.class,
                () -> TripCsvStorage.load(path));
        assertNotNull(invalidRecord.getCause());
        assertNotNull(invalidRecord.getCause().getCause());

        Files.writeString(path, HEADER + "\n2026-10-01,Driver,1,2,\"unfinished\n");
        assertThrows(DataStorageException.class, () -> TripCsvStorage.load(path));
        assertThrows(DataStorageException.class, () -> CsvParser.parseDocument("a\"b,c"));
    }

    @Test
    void emptyHeaderOnlyFileAndWrongHeaderAreHandled(@TempDir Path directory) throws Exception {
        Path path = directory.resolve("trips.csv");
        Files.writeString(path, HEADER + "\n");
        assertEquals(List.of(), TripCsvStorage.load(path));
        Files.writeString(path, "wrong,header\n");
        assertThrows(DataStorageException.class, () -> TripCsvStorage.load(path));
    }

    @Test
    void fileReadAndExporterValidationPreserveFailures(@TempDir Path directory) throws Exception {
        DataStorageException missing = assertThrows(DataStorageException.class,
                () -> TripCsvStorage.load(directory.resolve("missing.csv")));
        assertNotNull(missing.getCause());

        Path path = directory.resolve("record.csv");
        assertThrows(DataStorageException.class,
                () -> CsvExporter.write(path, TripRecord.class, java.util.Arrays.asList(record("x"), null)));
        assertThrows(DataStorageException.class,
                () -> CsvExporter.write(path, Unannotated.class, List.of(new Unannotated())));
        assertFalse(Files.exists(directory.resolve("not-created.csv")));
    }

    @Test
    void universalExporterIncludesAnnotatedSuperclassFields(@TempDir Path directory) throws Exception {
        Path path = directory.resolve("child.csv");
        CsvExporter.write(path, Child.class, List.of(new Child()));
        assertEquals("База,Дочірнє\nbase,child\n", Files.readString(path));
    }

    @Test
    void exportKeepsValidRowsAndReportsSkippedRows(@TempDir Path directory) throws Exception {
        Path input = directory.resolve("input.csv");
        Path output = directory.resolve("output/trips.csv");
        Files.writeString(input, "Route;Driver;120;15;2026-10-01\n"
                + "CARGO:1000kg Truck;Driver;130;20;2026-10-02\n"
                + "bad;line\n");

        assertEquals(1, Main.exportCsv(input, output));
        List<TripRecord> restored = TripCsvStorage.load(output);
        assertEquals(2, restored.size());
        assertTrue(restored.get(0).toTrip() instanceof PassengerTrip);
        Trip cargo = restored.get(1).toTrip();
        assertTrue(cargo instanceof CargoTrip);
        assertEquals(22.0, cargo.calculatedFuelLiters(), 0.001);
    }

    @Test
    void roundTripCollectionWithMalformedSourceRow(@TempDir Path directory) throws Exception {
        Path input = directory.resolve("input.csv");
        Path output = directory.resolve("round-trip.csv");
        Files.writeString(input, "Route, one;Driver;120;15;2026-10-01\n"
                + "Route two;Driver;90;10;2026-10-02\n"
                + "Route three;Driver;70;8;2026-10-03\n"
                + "Route four;Driver;60;7;2026-10-04\n"
                + "Route five;Driver;50;6;2026-10-05\n"
                + "Route broken;Driver;not-a-number;1;2026-10-06\n");

        assertEquals(1, Main.exportCsv(input, output));
        assertEquals(5, TripCsvStorage.load(output).size());
        assertTrue(Files.readString(output).contains("\"Route, one\""));
    }

    private static TripRecord record(String vehicle) {
        return new TripRecord(vehicle, "Водій", LocalDate.of(2026, 10, 1), 105.0, 12.5);
    }

    private static final class Unannotated {
    }

    private static class Base {
        @CsvColumn("База")
        private final String base = "base";
    }

    private static final class Child extends Base {
        @CsvColumn("Дочірнє")
        private final String child = "child";
    }
}
