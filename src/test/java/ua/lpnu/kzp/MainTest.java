package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void helpTextMentionsInputAndOutputOptions() {
        String help = Main.helpText();

        assertTrue(help.contains("--input"));
        assertTrue(help.contains("--output"));
    }
}
