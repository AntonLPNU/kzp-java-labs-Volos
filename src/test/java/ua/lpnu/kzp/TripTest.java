package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TripTest {
    @Test
    void constructorStoresNormalizedFields() {
        Trip trip = new Trip(" Route ", " Driver ", 29.4, 6.8, " 2026-09-10 ");

        assertEquals("Route", trip.getVehicle());
        assertEquals("Driver", trip.getDriver());
        assertEquals(29.4, trip.getKm());
        assertEquals(6.8, trip.getFuelLiters());
        assertEquals("2026-09-10", trip.getDate());
        assertTrue(trip.toString().contains("29.40 km"));
    }

    @Test
    void constructorRejectsMissingText() {
        assertThrows(IllegalArgumentException.class, () -> new Trip(null, "Driver", 1, 0, "date"));
        assertThrows(IllegalArgumentException.class, () -> new Trip(" ", "Driver", 1, 0, "date"));
        assertThrows(IllegalArgumentException.class, () -> new Trip("Route", null, 1, 0, "date"));
        assertThrows(IllegalArgumentException.class, () -> new Trip("Route", " ", 1, 0, "date"));
        assertThrows(IllegalArgumentException.class, () -> new Trip("Route", "Driver", 1, 0, null));
        assertThrows(IllegalArgumentException.class, () -> new Trip("Route", "Driver", 1, 0, " "));
    }

    @Test
    void constructorRejectsInvalidMeasurements() {
        for (double km : new double[] {0, -1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new Trip("Route", "Driver", km, 0, "date"));
        }
        for (double fuel : new double[] {-1, Double.NaN, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new Trip("Route", "Driver", 1, fuel, "date"));
        }
    }

    @Test
    void factoryParsesValidCsvAndRetainsBlankColumns() {
        Trip trip = Trip.fromCsv(" Route ; Driver ;29.4;6.8;2026-09-10");

        assertEquals("Route", trip.getVehicle());
        assertEquals(29.4, trip.getKm());
        assertTrue(assertThrows(IllegalArgumentException.class,
                () -> Trip.fromCsv(";Driver;29.4;6.8;2026-09-10")).getMessage().contains("vehicle"));
    }

    @Test
    void factoryRejectsMalformedRows() {
        assertTrue(assertThrows(IllegalArgumentException.class,
                () -> Trip.fromCsv("Route;Driver;1;2")).getMessage().contains("expected 5 fields"));
        assertTrue(assertThrows(IllegalArgumentException.class,
                () -> Trip.fromCsv("Route;Driver;bad;2;date")).getMessage().contains("km has invalid number format"));
        assertTrue(assertThrows(IllegalArgumentException.class,
                () -> Trip.fromCsv("Route;Driver;1;-2;date")).getMessage().contains("fuelLiters"));
    }
}
