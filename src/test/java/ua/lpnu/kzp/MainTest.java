package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
