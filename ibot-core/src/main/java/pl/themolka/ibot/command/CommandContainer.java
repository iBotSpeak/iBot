package pl.themolka.ibot.command;

import pl.themolka.ibot.IBot;

public class CommandContainer {
    protected final IBot iBot;

    public CommandContainer(IBot iBot) {
        this.iBot = iBot;
    }

    public String getTitle(String title, int size) {
        return "--------------- " + title.toUpperCase() + " (" + size + ") ---------------";
    }
}
