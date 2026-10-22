package ua.lpnu.kzp;

import java.nio.file.Path;

record CommandLineOptions(Action action, Path input, Path output, Path csvOutput) {
    enum Action {
        RUN,
        HELP,
        VERSION
    }
}
