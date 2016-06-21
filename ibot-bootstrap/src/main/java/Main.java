import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Main {
    public static void main(String[] args) {
        new Main().start(args);
    }

    public static final Option[] CLI_OPTIONS = {
            new Option("?", "help", false, "print the help page"),
            new Option("class", true, "target the bootstrap class"),
            new Option("file", true, "target the properties file")
    };
    public static final Class<?> BOOTSTRAP_CLASS = FixedBootstrap.class;

    public void start(String[] args) {
        long took = System.currentTimeMillis();

        CommandLine commandLine = this.parseCLI(args);
        if (commandLine == null) {
            return;
        }

        Class<?> bootstrapClass = this.parseBootstrapClass(commandLine);
        if (bootstrapClass == null) {
            return;
        }

        String log = String.format("Bootstrap Class Loader: '%s'", bootstrapClass.getName());
        if (bootstrapClass.equals(BOOTSTRAP_CLASS)) {
            log += " (default)";
        }
        System.out.println("[Bootstrap] " + log);

        try {
            Constructor constructor = bootstrapClass.getConstructor();
            constructor.setAccessible(true);
            Object loader = constructor.newInstance();

            Method init = loader.getClass().getDeclaredMethod("init", CommandLine.class);
            init.setAccessible(true);
            init.invoke(loader, commandLine);
        } catch (InstantiationException ex) {
            System.err.println("[Bootstrap] Could not create: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            System.err.println("[Bootstrap] Could not access: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            System.err.println("[Bootstrap] Could not find method: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            System.err.println("[Bootstrap] Could not invoke: " + ex.getMessage());
            ex.printStackTrace();
        }

        System.out.println("[Bootstrap] Started in " + (double) (System.currentTimeMillis() - took) / 1000L + " second(s).");
    }

    private Class<?> parseBootstrapClass(CommandLine commandLine) {
        if (!commandLine.hasOption("class")) {
            return BOOTSTRAP_CLASS;
        }

        try {
            return Class.forName(commandLine.getOptionValue("class"));
        } catch (ClassNotFoundException ex) {
            System.err.println("[Bootstrap] Could not find bootstrap class: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    private CommandLine parseCLI(String[] args) {
        Options options = new Options();
        for (Option option : CLI_OPTIONS) {
            options.addOption(option);
        }

        CommandLine commandLine = null;
        try {
            commandLine = new BasicParser().parse(options, args);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        if (commandLine == null || commandLine.hasOption("help")) {
            new HelpFormatter().printHelp(
                    "iBot",
                    null,
                    options,
                    "All Rights Reserved.",
                    false
            );
            return null;
        }

        return commandLine;
    }
}
