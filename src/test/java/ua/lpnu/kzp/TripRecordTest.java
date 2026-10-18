package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TripRecordTest {
    @Test
    void declaresRuntimeCsvColumnsForAllVariantFields() throws ReflectiveOperationException {
        assertEquals("RUNTIME", CsvColumn.class.getAnnotation(java.lang.annotation.Retention.class)
                .value().name());
        assertTrue(CsvColumn.class.isAnnotationPresent(java.lang.annotation.Target.class));

        for (Field field : TripRecord.class.getDeclaredFields()) {
            if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                assertTrue(field.isAnnotationPresent(CsvColumn.class), field.getName());
            }
        }
    }

    @Test
    void convertsPassengerAndCargoTripsWithoutLosingTheirKind() {
        Trip passenger = new PassengerTrip("Route; with delimiter", "Driver", 42, 8, "2026-10-01");
        Trip cargo = new CargoTrip("Truck", "Driver", 130, 20, "2026-10-02", 1000);

        Trip passengerCopy = TripRecord.fromTrip(passenger).toTrip();
        Trip cargoCopy = TripRecord.fromTrip(cargo).toTrip();

        assertEquals(passenger, passengerCopy);
        assertInstanceOf(CargoTrip.class, cargoCopy);
        assertEquals(cargo, cargoCopy);
        assertEquals(22.0, cargoCopy.calculatedFuelLiters(), 0.0001);
    }

    @Test
    void restoresFromSortedCsvFieldsAndPreservesConversionCause() throws DataStorageException {
        TripRecord record = TripRecord.fromCsvFields(
                List.of("2026-10-18", "Driver", "12.5", "105.0", "Bus"));

        assertEquals(new TripRecord("Bus", "Driver", LocalDate.of(2026, 10, 18), 105, 12.5), record);
        DataStorageException error = assertThrows(DataStorageException.class,
                () -> TripRecord.fromCsvFields(List.of("2026-10-18", "Driver", "fuel", "105", "Bus")));
        assertTrue(error.getCause() instanceof NumberFormatException);
    }

    @Test
    void rejectsInvalidFieldCountsAndModelValues() {
        assertThrows(DataStorageException.class, () -> TripRecord.fromCsvFields(List.of("only one")));
        assertThrows(IllegalArgumentException.class,
                () -> new TripRecord("Bus", "Driver", LocalDate.now(), 0, 1));
    }
}
