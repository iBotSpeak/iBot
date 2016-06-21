package pl.themolka.ibot.terminal;

import pl.themolka.ibot.IBot;
import pl.themolka.ibot.command.BotCommandSystem;
import pl.themolka.iserverquery.command.Console;
import pl.themolka.iserverquery.util.Platform;

public class Terminal extends Console {
    public static final String DISPLAY_NAME = "Terminal";

    private final IBot iBot;

    private BotCommandSystem currentHandler;
    private TerminalThread thread;

    public Terminal(IBot iBot) {
        super(null);
        this.iBot = iBot;
    }

    @Override
    public String getName() {
        return DISPLAY_NAME;
    }

    @Override
    public Platform getPlatform() {
        return this.iBot.getPlatform();
    }

    @Override
    public void sendMessage(String message) {
        this.getCurrentHandler().log(message);
    }

    public BotCommandSystem getCurrentHandler() {
        if (this.currentHandler != null) {
            return this.currentHandler;
        } else {
            return this.iBot.getCommands();
        }
    }

    public TerminalThread getThread() {
        return this.thread;
    }

    public void setCurrentHandler(BotCommandSystem currentHandler) {
        this.getCurrentHandler().getWriter().setWritable(false);
        this.currentHandler = currentHandler;
        this.getCurrentHandler().getWriter().setWritable(true);
    }

    public void setThread(TerminalThread thread) {
        this.thread = thread;
    }
}
