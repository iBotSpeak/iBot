package ibot;

import pl.themolka.ibot.IBot;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.plugin.PluginDataFile;
import pl.themolka.ibot.plugin.PluginManager;
import pl.themolka.ibot.plugin.PluginReader;

import java.lang.reflect.Field;
import java.util.Properties;

public class FixedIBot extends IBot {
    public FixedIBot(Properties cli) throws Throwable {
        super(cli);
        IBotPluginIncluder.include("plugin-list.xml");
    }

    @Override
    public boolean registerInstance(BotQuery query) {
        if (super.registerInstance(query)) {
            this.registerPlugins(query);
            return true;
        }
        return false;
    }

    private void registerPlugins(BotQuery query) {
        PluginManager plugins = query.getPlugins();

        int amount = 0;
        for (IBotPluginIncluder.PluginItem item : IBotPluginIncluder.getPlugins()) {
            try {
                PluginDataFile dataFile = new PluginDataFile(null);

                Field main = dataFile.getClass().getDeclaredField("main");
                main.setAccessible(true);
                main.set(dataFile, item.getMain());

                Field name = dataFile.getClass().getDeclaredField("name");
                name.setAccessible(true);
                name.set(dataFile, item.getName());

                Field version = dataFile.getClass().getDeclaredField("version");
                version.setAccessible(true);
                version.set(dataFile, item.getVersion());

                PluginReader.PluginClass pluginClass = plugins.getPluginReader().readInternalPlugin(dataFile);
                plugins.registerPlugin(plugins.loadPlugin(pluginClass));
                amount++;
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        if (amount == 0) {
            query.getLogger().info("[Bootstrap] No plugins were found to include.");
        } else {
            query.getLogger().info("[Bootstrap] Included " + amount + " plugin(s) to this query.");
        }
    }
}
