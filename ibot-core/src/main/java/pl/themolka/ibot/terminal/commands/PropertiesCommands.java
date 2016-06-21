package pl.themolka.ibot.terminal.commands;

import pl.themolka.ibot.IBot;
import pl.themolka.ibot.command.BotCommand;
import pl.themolka.ibot.command.CommandContainer;
import pl.themolka.ibot.command.CommandImpl;
import pl.themolka.ibot.util.TerminalUtils;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesCommands extends CommandContainer {
    public PropertiesCommands(IBot iBot) {
        super(iBot);
    }

    @BotCommand(name = "pget", description = "Print property value", usage = "<-all|key>", flags = {"all"})
    public void pget(CommandSender sender, CommandContext context) {
        Properties properties = this.iBot.getProperties();
        if (properties.isEmpty()) {
            sender.sendMessage("The property list is empty.");
            return;
        }

        boolean all = context.hasFlag("all");
        String key = context.getParam(0);

        if (!all && key == null) {
            CommandImpl command = (CommandImpl) context.getCommand();
            sender.sendMessage(command.getDescription());
            sender.sendMessage(command.getUsage());
            return;
        } else if (key != null) {
            key = key.toLowerCase();
        }

        List<String> results = new ArrayList<>();
        if (all) {
            results.addAll(properties.stringPropertyNames());
        } else {
            for (String property : properties.stringPropertyNames()) {
                if (property.toLowerCase().contains(key)) {
                    results.add(property);
                }
            }
        }

        if (results.isEmpty()) {
            sender.sendMessage("No results found!");
            return;
        }

        int resultsSize = results.size();
        if (resultsSize > 15) {
            if (!TerminalUtils.readYesOrNo("The property list contains %d items. Do you want to display them all?", resultsSize)) {
                return;
            }
        }

        sender.sendMessage(this.getTitle("Properties", resultsSize));
        for (int i = 0; i < resultsSize; i++) {
            sender.sendMessage((i + 1) + ". '" + results.get(i) + "' => '" + properties.getProperty(results.get(i)) + "'");
        }
    }

    @BotCommand(name = "pset", description = "Define property value", usage = "<key> <value>")
    public void pset(CommandSender sender, CommandContext context) {
        Properties properties = this.iBot.getProperties();

        String key = context.getParam(0);
        String value = context.getParams(1);

        if (key == null || value == null) {
            CommandImpl command = (CommandImpl) context.getCommand();
            sender.sendMessage(command.getUsage());
            sender.sendMessage(command.getDescription());
            return;
        }

        Object oldValue = properties.setProperty(key, value);

        if (oldValue != null) {
            sender.sendMessage("Done, '" + key + "' has been defined from '" + oldValue + "' to '" + value + "'.");
        } else {
            sender.sendMessage("Done, '" + key + "' has been defined to '" + value + "'.");
        }
    }
}
