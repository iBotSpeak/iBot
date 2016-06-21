package pl.themolka.ibot.plugin;

import pl.themolka.ibot.bot.BotQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginManager {
    private final BotQuery botQuery;

    private final File directory;
    private final List<Plugin> plugins = new ArrayList<>();
    private final PluginReader reader;

    public PluginManager(BotQuery botQuery) {
        this.botQuery = botQuery;

        this.directory = new File(botQuery.getDirectory(), "plugins");
        this.directory.mkdirs();

        this.reader = new PluginReader(botQuery);
    }

    public Plugin disablePlugin(Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new UnsupportedOperationException("Plugin already disabled!");
        }

        plugin.setEnabled(false);
        return plugin;
    }

    public Plugin enablePlugin(Plugin plugin) {
        if (plugin.isEnabled()) {
            throw new UnsupportedOperationException("Plugin already enabled!");
        }

        plugin.setEnabled(true);
        return plugin;
    }

    public File getDirectory() {
        return this.directory;
    }

    public Plugin getPlugin(String name) {
        for (Plugin plugin : this.getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(name)) {
                return plugin;
            }
        }
        return null;
    }

    public PluginReader getPluginReader() {
        return this.reader;
    }

    public List<Plugin> getPlugins() {
        return this.plugins;
    }

    public PluginReader getReader() {
        return this.reader;
    }

    public IBotPlugin loadPlugin(PluginReader.PluginClass plugin) {
        String name = "Unknown plugin";

        try {
            if (plugin.getMain() == null) {
                throw new IllegalArgumentException("Main class cannot be null.");
            } else if (plugin.getData() == null) {
                throw new IllegalArgumentException("Plugin data cannot be null.");
            }

            name = plugin.getData().getName();

            IBotPlugin pluginObj = plugin.getMain().newInstance();
            pluginObj.initialize(this.botQuery, plugin.getData());
            return pluginObj;
        } catch (IllegalAccessException ex) {
            this.botQuery.getLogger().trace("Could not access constructor of the main class for " + name, ex);
        } catch (InstantiationException ex) {
            this.botQuery.getLogger().trace("Main class for " + name + " needs to be a class type.", ex);
        } catch (Throwable ex) {
            this.botQuery.getLogger().trace("Could not load plugin " + name + ": " + ex.getMessage(), ex);
        }

        return null;
    }

    public void registerPlugin(Plugin plugin) {
        this.plugins.add(plugin);
    }
}
