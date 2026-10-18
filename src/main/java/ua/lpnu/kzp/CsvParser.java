package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Parses a complete CSV document, including quoted values spanning lines. */
public final class CsvParser {
    private CsvParser() {
    }

    /** Parses a CSV document into immutable rows and fields. */
    public static List<List<String>> parseDocument(String text) throws DataStorageException {
        if (text == null) {
            throw new DataStorageException("CSV document must not be null");
        }
        if (text.isEmpty()) {
            return List.of();
        }

        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        boolean quoteClosed = false;
        boolean fieldStarted = false;

        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (quoted) {
                if (character == '"') {
                    if (index + 1 < text.length() && text.charAt(index + 1) == '"') {
                        field.append('"');
                        index++;
                    } else {
                        quoted = false;
                        quoteClosed = true;
                    }
                } else {
                    field.append(character);
                }
                continue;
            }

            if (character == ',') {
                row.add(field.toString());
                field.setLength(0);
                quoteClosed = false;
                fieldStarted = false;
            } else if (character == '\n' || character == '\r') {
                row.add(field.toString());
                rows.add(List.copyOf(row));
                row = new ArrayList<>();
                field.setLength(0);
                quoteClosed = false;
                fieldStarted = false;
                if (character == '\r' && index + 1 < text.length() && text.charAt(index + 1) == '\n') {
                    index++;
                }
            } else if (character == '"' && !fieldStarted && !quoteClosed) {
                quoted = true;
                fieldStarted = true;
            } else {
                if (character == '"' || quoteClosed) {
                    throw new DataStorageException("Unexpected character after quoted CSV field");
                }
                field.append(character);
                fieldStarted = true;
            }
        }

        if (quoted) {
            throw new DataStorageException("Unclosed quoted CSV field");
        }
        if (!row.isEmpty() || fieldStarted || quoteClosed || field.length() > 0) {
            row.add(field.toString());
            rows.add(List.copyOf(row));
        }
        return List.copyOf(rows);
    }
}
