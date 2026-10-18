package ua.lpnu.kzp;

import java.util.Objects;

/** A cargo trip with an illustrative load-dependent fuel estimate. */
public final class CargoTrip extends Trip {
    private final double loadKg;
    private final double calculatedFuelLiters;

    /** Creates a cargo trip with a positive finite load in kilograms. */
    public CargoTrip(String vehicle, String driver, double km, double fuelLiters, String date, double loadKg) {
        super(validatedFields(vehicle, driver, km, fuelLiters, date), TripKind.CARGO);
        if (loadKg <= 0.0 || !Double.isFinite(loadKg)) {
            throw new IllegalArgumentException("loadKg must be finite and greater than 0");
        }
        this.loadKg = loadKg;
        this.calculatedFuelLiters = getFuelLiters() * (1.0 + loadKg / 10_000.0);
        if (!Double.isFinite(calculatedFuelLiters)) {
            throw new IllegalArgumentException("calculated fuel must be finite");
        }
    }

    /** Returns the carried load in kilograms. */
    public double getLoadKg() {
        return loadKg;
    }

    /** Estimates cargo fuel with an illustrative 10% increase per metric ton. */
    @Override
    public double calculatedFuelLiters() {
        return calculatedFuelLiters;
    }

    /** Includes cargo load in logical equality. */
    @Override
    public boolean equals(Object other) {
        return super.equals(other) && Double.compare(loadKg, ((CargoTrip) other).loadKg) == 0;
    }

    /** Includes cargo load in the hash code. */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), loadKg);
    }
}
