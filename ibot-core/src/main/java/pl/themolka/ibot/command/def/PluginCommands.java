package pl.themolka.ibot.command.def;

import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.command.BotCommand;
import pl.themolka.ibot.command.CommandImpl;
import pl.themolka.ibot.command.QueryCommandContainer;
import pl.themolka.ibot.plugin.Plugin;
import pl.themolka.ibot.util.TerminalUtils;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PluginCommands extends QueryCommandContainer {
    public PluginCommands(BotQuery botQuery) {
        super(botQuery);
    }

    @BotCommand(name = "plugin", description = "Describe a plugin", usage = "<-all|plugin>", flags = "all")
    public void plugin(CommandSender sender, CommandContext context) {
        boolean all = context.hasFlag("all");

        String pluginName = context.getParam(0);
        if (!all && pluginName == null) {
            CommandImpl command = (CommandImpl) context.getCommand();
            sender.sendMessage(command.getDescription());
            sender.sendMessage(command.getUsage());
            return;
        }

        List<Plugin> results = new ArrayList<>();
        if (all) {
            results.addAll(this.botQuery.getPlugins().getPlugins());
        } else {
            pluginName = pluginName.toLowerCase();
            for (Plugin plugin : this.botQuery.getPlugins().getPlugins()) {
                if (plugin.getName().toLowerCase().contains(pluginName)) {
                    results.add(plugin);
                }
            }
        }

        if (results.isEmpty()) {
            sender.sendMessage("No results found!");
            return;
        }

        if (results.size() > 50) {
            if (!TerminalUtils.readYesOrNo("Plugins list contains %d items. Do you want to display them all?", results.size())) {
                return;
            }
        }

        if (results.size() > 1) {
            sender.sendMessage(this.getTitle("Plugins", results.size()));
        }

        for (Plugin result : results) {
            String enabled = "disabled";
            if (result.isEnabled()) {
                enabled = "enabled";
            }

            sender.sendMessage(String.format("Plugin '%s' version %s is %s and localized in '%s'.",
                    result.getName(),
                    result.getVersion(),
                    enabled,
                    result.getDataDirectory().getPath()
            ));
        }
    }

    @BotCommand(name = "plugins", description = "Print a list of plugins")
    public void plugins(CommandSender sender, CommandContext context) {
        List<Plugin> pluginList = this.botQuery.getPlugins().getPlugins();
        StringBuilder builder = new StringBuilder();

        if (pluginList.isEmpty()) {
            builder.append("No available plugins!");
        } else {
            builder.append("Plugins (").append(pluginList.size()).append("): ");

            for (int i = 0; i < pluginList.size(); i++) {
                builder.append(pluginList.get(i).getName());

                if (pluginList.size() - 1 != i) {
                    builder.append(", ");
                }
            }
        }

        sender.sendMessage(builder.toString());
    }
}
