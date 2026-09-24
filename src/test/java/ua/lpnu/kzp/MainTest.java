package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void helpTextMentionsInputAndOutputOptions() {
        String help = Main.helpText();

        assertTrue(help.contains("--input"));
        assertTrue(help.contains("--output"));
    }

    @Test
    void inputPathUsesDefaultWhenOptionIsMissing() {
        assertTrue(Main.inputPath(new String[0]).endsWith(Path.of("data", "input.csv")));
    }

    @Test
    void inputPathUsesCommandLineValue() {
        Path input = Main.inputPath(new String[] {"--input", "data/custom.csv"});

        assertTrue(input.endsWith(Path.of("data", "custom.csv")));
    }

    @Test
    void splitKeepsExpectedTripFields() {
        String line = "Львів 2 - Жовква 06:15;Петро Коваль;29.4;6.8;2026-09-10";

        String[] fields = line.split(";", -1);

        assertTrue(fields.length == 5);
    }

    @Test
    void splitKeepsBlankFieldsForValidation() {
        String line = ";Петро Коваль;29.4;6.8;2026-09-11";

        String[] fields = line.split(";", -1);

        assertTrue(fields.length == 5);
        assertTrue(fields[0].isBlank());
    }

    @Test
    void validTripFieldsHaveNoValidationError() {
        String[] fields = "Львів 2 - Жовква 06:15;Петро Коваль;29.4;6.8;2026-09-10".split(";", -1);

        assertNull(Main.validateTripFields(fields, 1));
    }

    @Test
    void validationRejectsBlankVehicle() {
        String[] fields = ";Петро Коваль;29.4;6.8;2026-09-11".split(";", -1);

        assertTrue(Main.validateTripFields(fields, 9).contains("vehicle is required"));
    }

    @Test
    void validationRejectsInvalidKmNumber() {
        String[] fields = "Львів 2 - Цетуля 16:10;Олена Кравець;помилка;8.2;2026-09-11".split(";", -1);

        assertTrue(Main.validateTripFields(fields, 10).contains("km has invalid number format"));
    }

    @Test
    void validationRejectsNegativeKm() {
        String[] fields = "Львів 2 - Карів 08:50;Степан Мороз;-10.0;2.5;2026-09-11".split(";", -1);

        assertTrue(Main.validateTripFields(fields, 11).contains("km must be greater than 0"));
    }

    @Test
    void validationRejectsBlankDriver() {
        String[] fields = "Львів 2 - Дев'ятир 10:20; ;45.0;10.1;2026-09-11".split(";", -1);

        assertTrue(Main.validateTripFields(fields, 12).contains("driver is required"));
    }

    @Test
    void fuelPer100KmUsesTotalFuelAndTotalDistance() {
        double result = Main.fuelPer100Km(85.2, 375.1);

        assertEquals(22.71, result, 0.01);
    }
}
