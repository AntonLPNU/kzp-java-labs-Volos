package ua.lpnu.kzp;

/** Entry point for the lab Java console application. */
public final class Main {
    private Main() {
    }

    /** Starts the application controller. */
    public static void main(String[] args) {
        int exitCode = new ApplicationController().run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
