package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RepositoryTest {
    @Test
    void returnsImmutableSnapshotAndFindsBySupertypePredicate() {
        Repository<String> repository = new Repository<>();
        repository.add("Lviv");
        repository.add("Sokal");

        List<String> snapshot = repository.all();
        assertNotSame(snapshot, repository.all());
        assertEquals(List.of("Sokal"), repository.find((CharSequence value) -> value.length() > 4));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add("Zhovkva"));
    }

    @Test
    void rejectsNullRecordsAndPredicates() {
        Repository<String> repository = new Repository<>();

        assertThrows(NullPointerException.class, () -> repository.add(null));
        assertThrows(NullPointerException.class, () -> repository.find(null));
    }

    @Test
    void storageExceptionRetainsItsCause() {
        IOException cause = new IOException("disk unavailable");
        DataStorageException exception = new DataStorageException("write failed", cause);

        assertSame(cause, exception.getCause());
        assertEquals("write failed", exception.getMessage());
    }
}
