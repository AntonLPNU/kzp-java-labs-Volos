package ua.lpnu.kzp;

/** Fixed categories of trips in the fleet. */
public enum TripKind {
    PASSENGER("passenger"),
    CARGO("cargo");

    private final String label;

    TripKind(String label) {
        this.label = label;
    }

    /** Returns the stable label used in documentation and diagnostics. */
    public String label() {
        return label;
    }
}
