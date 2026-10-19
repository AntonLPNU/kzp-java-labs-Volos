package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Saves and restores fleet trip records as annotated CSV. */
public final class TripCsvStorage {
    private static final List<String> HEADER = List.of("Дата", "Водій", "Пальне", "Кілометраж", "Авто");

    private TripCsvStorage() {
    }

    /** Writes records through the generic reflection-based CSV exporter. */
    public static void save(Path path, List<TripRecord> trips) throws DataStorageException {
        CsvExporter.write(path, TripRecord.class, trips);
    }

    /** Reads UTF-8 CSV and fails with the original cause when input is invalid. */
    public static List<TripRecord> load(Path path) throws DataStorageException {
        Objects.requireNonNull(path, "path must not be null");
        String text;
        try {
            text = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DataStorageException("Cannot read CSV file: " + path, exception);
        }

        List<List<String>> rows = CsvParser.parseDocument(text);
        if (rows.isEmpty()) {
            return List.of();
        }
        if (!rows.get(0).equals(HEADER)) {
            throw new DataStorageException("Unexpected CSV header: " + rows.get(0));
        }

        List<TripRecord> records = new ArrayList<>();
        for (int index = 1; index < rows.size(); index++) {
            try {
                records.add(TripRecord.fromCsvFields(rows.get(index)));
            } catch (DataStorageException exception) {
                throw new DataStorageException("Invalid CSV record at row " + (index + 1), exception);
            }
        }
        return List.copyOf(records);
    }
}
