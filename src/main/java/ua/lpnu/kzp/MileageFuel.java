package ua.lpnu.kzp;

/** Immutable accumulated distance and fuel consumption. */
public record MileageFuel(double kilometers, double fuelLiters) {
    public MileageFuel {
        if (!Double.isFinite(kilometers) || kilometers < 0.0) {
            throw new IllegalArgumentException("kilometers must be finite and nonnegative");
        }
        if (!Double.isFinite(fuelLiters) || fuelLiters < 0.0) {
            throw new IllegalArgumentException("fuelLiters must be finite and nonnegative");
        }
    }

    /** Returns a new total that includes one trip. */
    public MileageFuel plus(Trip trip) {
        return new MileageFuel(kilometers + trip.getKm(), fuelLiters + trip.getFuelLiters());
    }

    /** Computes liters per 100 km, requiring a positive distance. */
    public double litersPer100Km() {
        if (kilometers <= 0.0) {
            throw new IllegalArgumentException("km must be greater than 0");
        }
        return fuelLiters / kilometers * 100.0;
    }
}
