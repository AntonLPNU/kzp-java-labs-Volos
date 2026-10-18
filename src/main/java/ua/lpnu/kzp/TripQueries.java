package ua.lpnu.kzp;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stream API queries for the fleet trips in variant 7. */
public final class TripQueries {
    private TripQueries() {
    }

    /** Returns trips whose distance is strictly greater than 100 km. */
    public static List<Trip> longTrips(List<Trip> trips) {
        return requireTrips(trips).stream()
                .filter(trip -> trip.getKm() > 100.0)
                .toList();
    }

    /** Returns vehicle names in source order, retaining duplicates. */
    public static List<String> vehicleNames(List<Trip> trips) {
        return requireTrips(trips).stream()
                .map(Trip::getVehicle)
                .toList();
    }

    /** Calculates fuel consumption per 100 km for each vehicle. */
    public static Map<String, Double> fuelByVehicle(List<Trip> trips) {
        Map<String, List<Trip>> groups = requireTrips(trips).stream()
                .collect(Collectors.groupingBy(Trip::getVehicle));

        return groups.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            double km = entry.getValue().stream()
                                    .mapToDouble(Trip::getKm)
                                    .sum();
                            double fuel = entry.getValue().stream()
                                    .mapToDouble(Trip::calculatedFuelLiters)
                                    .sum();
                            return fuel / km * 100.0;
                        }));
    }

    /** Returns count, sum, average, minimum and maximum trip distance. */
    public static DoubleSummaryStatistics distanceStatistics(List<Trip> trips) {
        return requireTrips(trips).stream()
                .collect(Collectors.summarizingDouble(Trip::getKm));
    }

    /** Returns up to N trips ordered by descending distance, then ascending date. */
    public static List<Trip> topTrips(List<Trip> trips, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N must not be negative");
        }

        Comparator<Trip> order = Comparator.comparingDouble(Trip::getKm)
                .reversed()
                .thenComparing(Trip::getDate);

        return requireTrips(trips).stream()
                .sorted(order)
                .limit(n)
                .toList();
    }

    /** Finds the first trip for a vehicle and date, or returns an empty Optional. */
    public static Optional<Trip> findTrip(List<Trip> trips, String vehicle, String date) {
        Objects.requireNonNull(vehicle, "vehicle must not be null");
        Objects.requireNonNull(date, "date must not be null");

        return requireTrips(trips).stream()
                .filter(trip -> trip.getVehicle().equals(vehicle))
                .filter(trip -> trip.getDate().equals(date))
                .findFirst();
    }

    private static List<Trip> requireTrips(List<Trip> trips) {
        return Objects.requireNonNull(trips, "trips must not be null");
    }
}
