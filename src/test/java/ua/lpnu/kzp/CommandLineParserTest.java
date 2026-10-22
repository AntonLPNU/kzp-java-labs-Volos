package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class CommandLineParserTest {
    @Test
    void usesDefaultPathsWhenOptionsAreMissing() {
        CommandLineOptions options = CommandLineParser.parse(new String[0]);

        assertEquals(CommandLineOptions.Action.RUN, options.action());
        assertEquals(Path.of("data", "input.csv"), options.input());
        assertEquals(Path.of("out", "report.txt"), options.output());
    }

    @Test
    void readsInputAndOutputRegardlessOfOrder() {
        CommandLineOptions options = CommandLineParser.parse(new String[] {
                "--output", "out/custom.txt", "--input", "data/custom.csv"
        });

        assertEquals(Path.of("data", "custom.csv"), options.input());
        assertEquals(Path.of("out", "custom.txt"), options.output());
    }

    @Test
    void recognizesHelpAndVersion() {
        assertEquals(CommandLineOptions.Action.HELP,
                CommandLineParser.parse(new String[] {"--help"}).action());
        assertEquals(CommandLineOptions.Action.VERSION,
                CommandLineParser.parse(new String[] {"--version"}).action());
        assertTrue(CommandLineParser.helpText().contains("--input"));
    }

    @Test
    void rejectsMissingValuesAndUnknownOptions() {
        assertTrue(assertThrows(IllegalArgumentException.class,
                () -> CommandLineParser.parse(new String[] {"--input"}))
                .getMessage().contains("Missing value for --input"));
        assertTrue(assertThrows(IllegalArgumentException.class,
                () -> CommandLineParser.parse(new String[] {"--unknown"}))
                .getMessage().contains("Unknown option"));
        assertThrows(IllegalArgumentException.class,
                () -> CommandLineParser.parse(new String[] {"--help", "--version"}));
    }
}
