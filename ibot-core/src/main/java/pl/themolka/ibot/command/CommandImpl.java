package pl.themolka.ibot.command;

import pl.themolka.iserverquery.command.Command;

public class CommandImpl extends Command {
    private final BotCommandSystem commands;
    private final String description;
    private final String usage;

    public CommandImpl(String command, BotCommandSystem commands, String description, String[] flags, String usage) {
        super(command, false, flags);

        this.commands = commands;
        this.description = description;
        this.usage = usage;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUsage() {
        String usage = "";
        String prefix = this.commands.getPrefix();

        if (prefix != null && !prefix.isEmpty()) {
            usage = prefix;
        }

        if (this.usage != null) {
            usage += this.getCommand() + " " + this.usage;
        }
        return usage.trim();
    }
}
