package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TripQueriesTest {
    private List<Trip> trips() {
        return new ArrayList<>(List.of(
                new PassengerTrip("Bus A", "Іван", 100, 20, "2026-09-03"),
                new PassengerTrip("Bus B", "Марія", 120, 24, "2026-09-02"),
                new CargoTrip("Truck", "Олег", 150, 20, "2026-09-04", 1000),
                new PassengerTrip("Bus A", "Анна", 50, 5, "2026-09-05"),
                new PassengerTrip("Bus B", "Петро", 120, 12, "2026-09-01"),
                new PassengerTrip("Bus A", "Ірина", 80, 8, "2026-09-03")));
    }

    @Test
    void filterUsesStrictBoundary() {
        List<Trip> result = TripQueries.longTrips(trips());

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(trip -> trip.getKm() > 100));
    }

    @Test
    void mapReturnsNamesIncludingDuplicatesInSourceOrder() {
        assertEquals(List.of("Bus A", "Bus B", "Truck", "Bus A", "Bus B", "Bus A"),
                TripQueries.vehicleNames(trips()));
    }

    @Test
    void groupingUsesTotalFuelAndTotalDistance() {
        Map<String, Double> result = TripQueries.fuelByVehicle(trips());

        assertEquals(3, result.size());
        assertEquals(3300.0 / 230.0, result.get("Bus A"), 0.0001);
        assertEquals(15.0, result.get("Bus B"), 0.0001);
        assertEquals(22.0 / 150.0 * 100.0, result.get("Truck"), 0.0001);
    }

    @Test
    void statisticsHaveCorrectValues() {
        DoubleSummaryStatistics stats = TripQueries.distanceStatistics(trips());

        assertEquals(6, stats.getCount());
        assertEquals(620.0, stats.getSum(), 0.0001);
        assertEquals(620.0 / 6.0, stats.getAverage(), 0.0001);
        assertEquals(50.0, stats.getMin(), 0.0001);
        assertEquals(150.0, stats.getMax(), 0.0001);
    }

    @Test
    void topFiveUsesDateToBreakDistanceTie() {
        List<Trip> result = TripQueries.topTrips(trips(), 5);

        assertEquals(5, result.size());
        assertEquals("Truck", result.get(0).getVehicle());
        assertEquals("2026-09-01", result.get(1).getDate());
        assertEquals("2026-09-02", result.get(2).getDate());
        assertTrue(TripQueries.topTrips(trips(), 0).isEmpty());
        assertEquals(6, TripQueries.topTrips(trips(), 100).size());
        assertThrows(IllegalArgumentException.class, () -> TripQueries.topTrips(trips(), -1));
    }

    @Test
    void optionalFindsFirstDuplicateOrEmpty() {
        List<Trip> data = trips();

        assertSame(data.get(0), TripQueries.findTrip(data, "Bus A", "2026-09-03").orElseThrow());
        assertTrue(TripQueries.findTrip(data, "Absent", "2026-09-03").isEmpty());
    }

    @Test
    void emptyInputAndUnchangedSource() {
        List<Trip> empty = List.of();

        assertTrue(TripQueries.longTrips(empty).isEmpty());
        assertTrue(TripQueries.vehicleNames(empty).isEmpty());
        assertTrue(TripQueries.fuelByVehicle(empty).isEmpty());
        assertEquals(0, TripQueries.distanceStatistics(empty).getCount());
        assertTrue(TripQueries.topTrips(empty, 5).isEmpty());
        assertTrue(TripQueries.findTrip(empty, "X", "date").isEmpty());

        List<Trip> data = trips();
        List<Trip> before = new ArrayList<>(data);
        TripQueries.topTrips(data, 5);
        TripQueries.fuelByVehicle(data);
        assertEquals(before, data);
    }
}
