package ua.lpnu.kzp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Reads the version injected into the application resources by Maven. */
final class ApplicationVersion {
    private static final String RESOURCE = "/application.properties";

    private ApplicationVersion() {
    }

    static String current() {
        Properties properties = new Properties();
        try (InputStream input = ApplicationVersion.class.getResourceAsStream(RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Application version resource is missing");
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read application version", exception);
        }

        String version = properties.getProperty("application.version");
        if (version == null || version.isBlank() || version.contains("${")) {
            throw new IllegalStateException("Application version was not injected by Maven");
        }
        return version;
    }
}
