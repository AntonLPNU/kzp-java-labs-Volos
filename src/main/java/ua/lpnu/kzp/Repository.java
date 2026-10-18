package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/** Type-safe in-memory storage for records of one declared type. */
public final class Repository<T> {
    private final List<T> items = new ArrayList<>();

    /** Adds a non-null record to this repository. */
    public void add(T item) {
        items.add(Objects.requireNonNull(item, "item must not be null"));
    }

    /** Returns an immutable snapshot of the records currently stored. */
    public List<T> all() {
        return List.copyOf(items);
    }

    /** Returns the records accepted by {@code condition}, in insertion order. */
    public List<T> find(Predicate<? super T> condition) {
        Objects.requireNonNull(condition, "condition must not be null");
        return items.stream().filter(condition).toList();
    }
}
