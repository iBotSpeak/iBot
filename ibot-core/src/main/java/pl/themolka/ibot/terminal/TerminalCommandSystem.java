package pl.themolka.ibot.terminal;

import pl.themolka.ibot.IBot;
import pl.themolka.ibot.command.BotCommandSystem;
import pl.themolka.ibot.command.CommandContainer;
import pl.themolka.ibot.log.ConsoleWriter;
import pl.themolka.ibot.terminal.commands.GeneralCommands;
import pl.themolka.ibot.terminal.commands.HelpCommand;
import pl.themolka.ibot.terminal.commands.LogCommands;
import pl.themolka.ibot.terminal.commands.PropertiesCommands;
import pl.themolka.ibot.terminal.commands.ServerCommands;
import pl.themolka.ibot.terminal.commands.SettingsCommands;
import pl.themolka.iserverquery.command.Command;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.command.CommandException;
import pl.themolka.iserverquery.command.CommandSender;

public class TerminalCommandSystem extends BotCommandSystem {
    public static final String PREFIX = "";

    private final IBot iBot;

    public TerminalCommandSystem(IBot iBot) {
        super(null);
        this.iBot = iBot;

        this.setPrefix(PREFIX);
    }

    @Override
    public void handleCommand(CommandSender sender, CommandContext context) {
        try {
            context.getCommand().handleCommand(sender, context);
        } catch (CommandException ex) {
            sender.sendMessage("Error: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            sender.sendMessage("Could not convert string to numeric.");
        } catch (Throwable ex) {
            sender.sendMessage(String.format("Internal command error - %s. See the logs.", ex.getMessage()));
        }
    }

    @Override
    public void log(String log) {
        IBot.getLogger().info(log);
    }

    @Override
    public void registerCommand(Command command, boolean override) {
        String name = command.getCommand().toLowerCase();

        if (override) {
            this.commands.put(name, command);
        } else {
            for (int i = 0; i < 10; ++i) {
                if (this.getCommand(name) == null) {
                    this.commands.put(name, command);
                    break;
                }

                name = ":" + name;
            }

        }
    }

    @Override
    public ConsoleWriter getWriter() {
        return IBot.getLogger().getWriter();
    }

    @Override
    public void setWriter(ConsoleWriter writer) {
        throw new UnsupportedOperationException();
    }

    public void registerDefaults() {
        CommandContainer[] containers = new CommandContainer[] {
                new GeneralCommands(this.iBot),
                new HelpCommand(this.iBot, this.iBot.getCommands()),
                new LogCommands(this.iBot),
                new PropertiesCommands(this.iBot),
                new ServerCommands(this.iBot),
                new SettingsCommands(this.iBot)
        };

        for (CommandContainer container : containers) {
            this.registerClass(container);
        }
    }
}
