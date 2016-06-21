package pl.themolka.ibot.terminal;

import pl.themolka.ibot.IBot;
import pl.themolka.iserverquery.command.DefaultContextParser;

import java.io.InputStream;
import java.util.Scanner;

public class TerminalThread extends Thread {
    public static final String THREAD_NAME = "terminal";
    private final IBot iBot;

    private final InputStream input;

    public TerminalThread(IBot iBot, InputStream input) {
        super(THREAD_NAME);
        this.iBot = iBot;

        this.input = input;
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(this.getInputStream())) {
            while (!this.isInterrupted() && scanner.hasNextLine()) {
                this.readLine(scanner.nextLine());
            }
        }
    }

    public InputStream getInputStream() {
        return input;
    }

    public void readLine(String line) {
        Terminal terminal = this.iBot.getTerminal();

        String log = "%s issued command \"%s\".";
        IBot.getLogger().info(String.format(log, terminal.getName(), line));

        DefaultContextParser parser = new DefaultContextParser();
        terminal.getCurrentHandler().handleCommand(terminal, line, parser);
    }
}
