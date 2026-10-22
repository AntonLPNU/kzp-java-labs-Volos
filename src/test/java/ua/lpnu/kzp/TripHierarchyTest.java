package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TripHierarchyTest {
    @Test
    void factoryBuildsBothSubtypesFromFiveFields() {
        Trip passenger = Trip.fromCsv("Route;Driver;100;20;date");
        Trip cargo = Trip.fromCsv("CARGO:1000kg Route;Driver;100;20;date");

        assertTrue(passenger instanceof PassengerTrip);
        assertTrue(cargo instanceof CargoTrip);
        assertEquals(TripKind.PASSENGER, passenger.getKind());
        assertEquals(TripKind.CARGO, cargo.getKind());
        assertEquals("cargo", cargo.getKind().label());
        assertEquals("Route", cargo.getVehicle());
        assertEquals(1000.0, ((CargoTrip) cargo).getLoadKg());
    }

    @Test
    void polymorphicCollectionUsesSubtypeFuelCalculation() {
        List<Trip> trips = List.of(
                new PassengerTrip("Route", "Driver", 100, 20, "date"),
                new CargoTrip("Route", "Driver", 100, 20, "date", 1000));

        MileageFuel totals = new MileageFuel(0, 0);
        for (Trip trip : trips) {
            totals = totals.plus(trip);
        }

        assertEquals(20.0, trips.get(0).calculatedFuelLiters());
        assertEquals(22.0, trips.get(1).calculatedFuelLiters(), 0.0001);
        assertEquals(21.0, totals.litersPer100Km(), 0.0001);
    }

    @Test
    void cargoRequiresValidLoadAndMarker() {
        for (double load : new double[] {0, -1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new CargoTrip("Route", "Driver", 1, 1, "date", load));
        }
        assertThrows(IllegalArgumentException.class,
                () -> Trip.fromCsv("CARGO:badkg Route;Driver;1;1;date"));
        assertThrows(IllegalArgumentException.class,
                () -> Trip.fromCsv("CARGO:1000kg;Driver;1;1;date"));
        assertThrows(IllegalArgumentException.class,
                () -> Trip.fromCsv("CARGO:0kg Route;Driver;1;1;date"));
        assertThrows(IllegalArgumentException.class,
                () -> new CargoTrip("Route", "Driver", 1, Double.MAX_VALUE, "date", 1000));
    }

    @Test
    void equalityAndHashSetIncludeSubtypeAndCargoLoad() {
        Trip passenger = new PassengerTrip("Route", "Driver", 100, 20, "date");
        Trip samePassenger = new PassengerTrip("Route", "Driver", 100, 20, "date");
        Trip cargo = new CargoTrip("Route", "Driver", 100, 20, "date", 1000);
        Trip sameCargo = new CargoTrip("Route", "Driver", 100, 20, "date", 1000);
        Trip differentLoad = new CargoTrip("Route", "Driver", 100, 20, "date", 500);

        assertEquals(passenger, samePassenger);
        assertEquals(passenger.hashCode(), samePassenger.hashCode());
        assertEquals(cargo, sameCargo);
        assertEquals(cargo.hashCode(), sameCargo.hashCode());
        assertNotEquals(passenger, cargo);
        assertNotEquals(cargo, differentLoad);
        Set<Trip> unique = new HashSet<>(List.of(passenger, samePassenger, cargo, sameCargo, differentLoad));
        assertEquals(3, unique.size());
    }

    @Test
    void mixedInputReportsValidAndInvalidRows() throws IOException {
        String report = new TripReportService().buildReport(Path.of("data", "lab03-mixed.csv"));

        assertTrue(report.contains("Rows: 6"));
        assertTrue(report.contains("Valid rows: 4"));
        assertTrue(report.contains("Invalid rows: 2"));
        assertTrue(report.contains("Average fuel consumption: 23.45 l/100 km"));
        assertTrue(report.contains("Longest trip: Львів 2 - Сокаль 16:20, 78.00 km"));
    }
}
