package pl.themolka.ibot.terminal.commands;

import pl.themolka.ibot.IBot;
import pl.themolka.ibot.command.BotCommand;
import pl.themolka.ibot.command.BotCommandSystem;
import pl.themolka.ibot.command.CommandContainer;
import pl.themolka.ibot.command.CommandImpl;
import pl.themolka.ibot.util.TerminalUtils;
import pl.themolka.iserverquery.command.Command;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HelpCommand extends CommandContainer {
    private final BotCommandSystem commands;

    public HelpCommand(IBot iBot, BotCommandSystem commands) {
        super(iBot);

        this.commands = commands;
    }

    @BotCommand(name = "help", description = "Print help page", usage = "[[-c|-d|-u] <query>]", flags = {"c", "d", "u"})
    public void help(CommandSender sender, CommandContext context) {
        boolean all = false;

        String query = context.getParam(0);
        if (query != null) {
            query = query.toLowerCase();
        } else {
            all = true;
        }

        List<Command> results = new ArrayList<>();
        if (all) {
            results.addAll(this.commands.getCommands().values());
        }

        if (query != null) {
            boolean name = context.hasFlag("c");
            boolean description = context.hasFlag("d");
            boolean usage = context.hasFlag("u");

            boolean global = !name && !usage && !description;

            for (String command : this.commands.getCommands().keySet()) {
                Command commandObj = this.commands.getCommand(command);

                if ((global || name) && !results.contains(commandObj)) {
                    if (commandObj.getCommand().contains(query)) {
                        results.add(commandObj);
                    }
                }

                if ((global || description) && !results.contains(commandObj)) {
                    if (commandObj instanceof CommandImpl && ((CommandImpl) commandObj).getDescription().toLowerCase().contains(query)) {
                        results.add(commandObj);
                    }
                }

                if ((global || usage) && !results.contains(commandObj)) {
                    if (commandObj instanceof CommandImpl && ((CommandImpl) commandObj).getUsage().toLowerCase().contains(query)) {
                        results.add(commandObj);
                    }
                }
            }
        }

        if (results.isEmpty()) {
            sender.sendMessage("No results found.");
            return;
        }

        int resultsSize = results.size();
        if (resultsSize > 50) {
            if (!TerminalUtils.readYesOrNo("The help list contains %d items. Do you want to display them all?", resultsSize)) {
                return;
            }
        }

        sender.sendMessage(this.getTitle("Help", resultsSize));
        Collections.sort(results, new Comparator<Command>() {
            @Override
            public int compare(Command o1, Command o2) {
                return o1.getCommand().compareTo(o2.getCommand());
            }
        });

        for (int i = 0; i < resultsSize; i++) {
            Command command = results.get(i);

            if (command instanceof CommandImpl) {
                CommandImpl commandImpl = (CommandImpl) command;
                sender.sendMessage((i + 1) + ". " + commandImpl.getUsage() + " - " + commandImpl.getDescription());
            } else {
                sender.sendMessage((i + 1) + ". " + this.commands.getPrefix() + command.getCommand());
            }
        }
    }
}
