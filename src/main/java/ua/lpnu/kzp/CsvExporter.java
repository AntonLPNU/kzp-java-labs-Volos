package ua.lpnu.kzp;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Writes objects with {@link CsvColumn} fields to UTF-8 CSV files. */
public final class CsvExporter {
    private CsvExporter() {
    }

    /** Writes an annotated object collection with columns ordered by Java field name. */
    public static <T> void write(Path path, Class<T> type, List<? extends T> records)
            throws DataStorageException {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(records, "records must not be null");

        List<Field> fields = csvFields(type);
        if (fields.isEmpty()) {
            throw new DataStorageException("Class has no fields annotated with @CsvColumn: " + type.getName());
        }
        for (Field field : fields) {
            try {
                if (!field.trySetAccessible()) {
                    throw new DataStorageException("Cannot access CSV field: " + field.getName());
                }
            } catch (SecurityException exception) {
                throw new DataStorageException("Cannot access CSV field: " + field.getName(), exception);
            }
        }

        StringBuilder csv = new StringBuilder();
        csv.append(fields.stream()
                .map(field -> escape(field.getAnnotation(CsvColumn.class).value()))
                .collect(Collectors.joining(",")));
        csv.append('\n');
        for (T record : records) {
            if (record == null || !type.isInstance(record)) {
                throw new DataStorageException("CSV record is null or has an incompatible type");
            }
            List<String> values = new ArrayList<>(fields.size());
            for (Field field : fields) {
                try {
                    values.add(escape(Objects.toString(field.get(record), "")));
                } catch (IllegalAccessException exception) {
                    throw new DataStorageException("Cannot read CSV field: " + field.getName(), exception);
                }
            }
            csv.append(String.join(",", values)).append('\n');
        }

        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, csv, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DataStorageException("Cannot write CSV file: " + path, exception);
        }
    }

    /** Escapes one CSV value according to RFC-style quote doubling rules. */
    public static String escape(String value) {
        Objects.requireNonNull(value, "value must not be null");
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0
                || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }

    private static List<Field> csvFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null && current != Object.class;
             current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(CsvColumn.class)) {
                    fields.add(field);
                }
            }
        }
        fields.sort(Comparator.comparing(Field::getName)
                .thenComparing(field -> field.getDeclaringClass().getName()));
        return List.copyOf(fields);
    }
}
