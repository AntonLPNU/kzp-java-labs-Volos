package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ApplicationControllerTest {
    @Test
    void runWritesReportAndReturnsSuccess(@TempDir Path tempDir) throws Exception {
        Path report = tempDir.resolve("report.txt");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream errors = new ByteArrayOutputStream();

        int exitCode = new ApplicationController().run(
                new String[] {"--input", "data/input.csv", "--output", report.toString()},
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new PrintStream(errors, true, StandardCharsets.UTF_8));

        assertEquals(0, exitCode);
        assertEquals(Files.readString(report), output.toString(StandardCharsets.UTF_8));
        assertTrue(errors.toString(StandardCharsets.UTF_8).isEmpty());
    }

    @Test
    void invalidArgumentsReturnFailureWithoutRunningService() {
        ByteArrayOutputStream errors = new ByteArrayOutputStream();

        int exitCode = new ApplicationController().run(new String[] {"--input"},
                new PrintStream(new ByteArrayOutputStream()),
                new PrintStream(errors, true, StandardCharsets.UTF_8));

        assertEquals(1, exitCode);
        assertTrue(errors.toString(StandardCharsets.UTF_8).contains("Missing value for --input"));
    }

    @Test
    void versionComesFromMavenFilteredResource() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        int exitCode = new ApplicationController().run(new String[] {"--version"},
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new PrintStream(new ByteArrayOutputStream()));

        assertEquals(0, exitCode);
        assertEquals("kzp-java-labs-volos " + ApplicationVersion.current()
                + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }
}
