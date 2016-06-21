package pl.themolka.ibot.plugin;

import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.settings.PluginsSettings;
import pl.themolka.ibot.xml.XMLException;
import pl.themolka.ibot.xml.XMLFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginReader {
    private final BotQuery botQuery;

    public PluginReader(BotQuery botQuery) {
        this.botQuery = botQuery;
    }

    public PluginClass readExternalPlugin(File file, PluginsSettings.PluginsDirectoryFile filter) throws Exception {
        JarFile jar = new JarFile(file);
        JarEntry entry = null;

        Iterator<String> dataFiles = filter.getDataFiles().iterator();
        while (dataFiles.hasNext()) {
            entry = jar.getJarEntry(dataFiles.next());
        }

        if (entry == null) {
            throw new XMLException("JAR plugin file does not contain any data files.");
        }

        InputStream input = jar.getInputStream(entry);
        XMLFile xml = new XMLFile(input);
        xml.parse();

        PluginDataFile data = new PluginDataFile(xml.getDocument().getRootElement());
        data.read();

        jar.close();
        input.close();

        return this.readPlugin(data, new URLClassLoader(new URL[] {file.toURI().toURL()}, this.getClass().getClassLoader()));
    }

    public List<PluginClass> readExternalPlugins(PluginsSettings settings) {
        List<PluginClass> plugins = new ArrayList<>();
        for (PluginsSettings.PluginsDirectory directory : settings.getDirectories()) {
            for (PluginClass plugin : this.readExternalPlugins(directory)) {
                if (!plugins.contains(plugin)) {
                    plugins.add(plugin);
                }
            }
        }
        return plugins;
    }

    public List<PluginClass> readExternalPlugins(PluginsSettings.PluginsDirectory directory) {
        List<PluginClass> plugins = new ArrayList<>();
        for (PluginsSettings.PluginsDirectoryFile filter : directory.getFiles()) {
            for (PluginClass plugin : this.readExternalPluginsByFilter(directory, filter)) {
                if (!plugins.contains(plugin)) {
                    plugins.add(plugin);
                }
            }
        }
        return plugins;
    }

    public List<PluginClass> readExternalPluginsByFilter(PluginsSettings.PluginsDirectory directory, PluginsSettings.PluginsDirectoryFile filter) {
        List<PluginClass> plugins = new ArrayList<>();
        if (directory.getPath() == null) {
            throw new IllegalArgumentException("Path directory cannot be null.");
        }

        File path = new File(this.botQuery.getDirectory(), directory.getPath());
        File[] fileList = path.listFiles();
        if (fileList == null) {
            return new ArrayList<>();
        }

        for (File file : fileList) {
            if (!filter.apply(file.getName())) {
                continue;
            }

            try {
                PluginClass clazz = this.readExternalPlugin(file, filter);
                if (clazz != null) {
                    plugins.add(clazz);
                }
            } catch (Exception ex) {
                this.botQuery.getLogger().trace("Could not load plugin from file " + file.getAbsolutePath() + ": " + ex.getMessage(), ex);
            }
        }
        return plugins;
    }

    public PluginClass readInternalPlugin(PluginDataFile data) {
        return this.readPlugin(data, this.getClass().getClassLoader());
    }

    public List<PluginClass> readInternalPlugins(PluginsSettings settings) {
        List<PluginClass> plugins = new ArrayList<>();
        for (PluginDataFile data : settings.getPlugins()) {
            PluginClass clazz = this.readInternalPlugin(data);

            if (clazz != null) {
                plugins.add(clazz);
            }
        }
        return plugins;
    }

    public PluginClass readPlugin(PluginDataFile data, ClassLoader loader) {
        data.setDirectory(new File(this.botQuery.getPlugins().getDirectory(), data.getName()));

        try {
            return new PluginClass(data, Class.forName(data.getMain(), true, loader).asSubclass(IBotPlugin.class));
        } catch (ClassCastException ex) {
            this.botQuery.getLogger().trace("Main class for " + data.getName() + " does not inherit the " + IBotPlugin.class, ex);
        } catch (ClassNotFoundException ex) {
            this.botQuery.getLogger().trace("Could not find the main plugin class for " + data.getName(), ex);
        } catch (Throwable ex) {
            this.botQuery.getLogger().trace("Could not read " + data.getName() + ": " + ex.getMessage(), ex);
        }
        return null;
    }

    public List<PluginClass> readPlugins(PluginsSettings settings) {
        List<PluginClass> plugins = new ArrayList<>();
        for (PluginClass internal : this.readInternalPlugins(settings)) {
            if (!plugins.contains(internal)) {
                plugins.add(internal);
            }
        }

        for (PluginClass external : this.readExternalPlugins(settings)) {
            if (!plugins.contains(external)) {
                plugins.add(external);
            }
        }
        return plugins;
    }

    public static class PluginClass {
        private final PluginDataFile data;
        private final Class<? extends IBotPlugin> main;

        public PluginClass(PluginDataFile data, Class<? extends IBotPlugin> main) {
            this.data = data;
            this.main = main;
        }

        public PluginDataFile getData() {
            return this.data;
        }

        public Class<? extends IBotPlugin> getMain() {
            return this.main;
        }
    }
}
