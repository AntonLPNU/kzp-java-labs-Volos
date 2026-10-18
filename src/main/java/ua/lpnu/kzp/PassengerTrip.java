package ua.lpnu.kzp;

/** A passenger trip whose calculated fuel equals its recorded fuel. */
public final class PassengerTrip extends Trip {
    /** Creates a passenger trip with the shared trip invariants. */
    public PassengerTrip(String vehicle, String driver, double km, double fuelLiters, String date) {
        super(validatedFields(vehicle, driver, km, fuelLiters, date), TripKind.PASSENGER);
    }

    /** Returns the recorded passenger-trip fuel without adjustment. */
    @Override
    public double calculatedFuelLiters() {
        return getFuelLiters();
    }
}
