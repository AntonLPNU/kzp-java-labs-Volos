package ua.lpnu.kzp;

import java.nio.file.Path;

record CommandLineOptions(Action action, Path input, Path output) {
    enum Action {
        RUN,
        HELP,
        VERSION
    }
}
