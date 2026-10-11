package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MileageFuelTest {
    @Test
    void recordStoresValuesAndHasValueEquality() {
        MileageFuel first = new MileageFuel(12.5, 2.5);

        assertEquals(12.5, first.kilometers());
        assertEquals(2.5, first.fuelLiters());
        assertEquals(new MileageFuel(12.5, 2.5), first);
    }

    @Test
    void plusCreatesNewTotal() {
        MileageFuel initial = new MileageFuel(0, 0);
        MileageFuel total = initial.plus(new Trip("Route", "Driver", 25, 5, "date"));

        assertNotSame(initial, total);
        assertEquals(new MileageFuel(25, 5), total);
        assertEquals(20, total.litersPer100Km());
    }

    @Test
    void rejectsInvalidTotalsAndZeroDistanceRatio() {
        assertThrows(IllegalArgumentException.class, () -> new MileageFuel(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new MileageFuel(Double.NaN, 0));
        assertThrows(IllegalArgumentException.class, () -> new MileageFuel(1, -1));
        assertThrows(IllegalArgumentException.class, () -> new MileageFuel(1, Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> new MileageFuel(0, 0).litersPer100Km());
    }
}
