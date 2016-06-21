package pl.themolka.ibot.util;

import pl.themolka.ibot.IBot;
import pl.themolka.iserverquery.event.CancelableEvent;

import java.io.Console;

public class TerminalUtils {
    private static Console console;

    public static void clearOutput(IBot iBot) {
        OutputClearEvent event = new OutputClearEvent();
        iBot.getEvents().post(event);

        if (!event.isCanceled()) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public static String readString() {
        return readString(null);
    }

    public static String readString(String message, Object... args) {
        if (message != null) {
            message += ": ";
        } else {
            message = "Enter the value: ";
        }

        return getConsole().readLine(message, args);
    }

    public static char[] readPassword() {
        return readPassword(null);
    }

    public static char[] readPassword(String message, Object... args) {
        if (message != null) {
            message += ": ";
        } else {
            message = "Enter the password: ";
        }

        return getConsole().readPassword(message, args);
    }

    public static boolean readYesOrNo() {
        return readYesOrNo(null);
    }

    public static boolean readYesOrNo(String message, Object... args) {
        if (message != null) {
            message += " (Y/n)";
        } else {
            message = "Are you sure? (Y/n)";
        }

        String input = readString(message, args);
        if (input == null || input.isEmpty() || input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")) {
            return true;
        }

        System.out.println("Aborted.");
        return false;
    }

    private static Console getConsole() {
        if (console == null) {
            Console system = System.console();

            if (system != null) {
                console = system;
            } else {
                System.out.println("~~~ CONSOLE OBJECT NOT IS SUPPORTED ~~~");
            }
        }

        return console;
    }

    public static class OutputClearEvent extends CancelableEvent {
    }
}
