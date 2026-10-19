package ua.lpnu.kzp;

/** Reports an error while validating, parsing, reading, or writing stored data. */
public class DataStorageException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Creates a storage error with a human-readable message. */
    public DataStorageException(String message) {
        super(message);
    }

    /** Creates a storage error and preserves the original cause. */
    public DataStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
