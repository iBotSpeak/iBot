package pl.themolka.ibot.plugin;

import org.apache.commons.io.FileUtils;
import org.jdom2.JDOMException;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.log.ServerLogger;
import pl.themolka.ibot.storage.MongoStorage;
import pl.themolka.ibot.xml.XMLFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class IBotPlugin implements Plugin {
    private BotQuery query;

    private boolean enabled;
    private boolean initialized;
    private ServerLogger logger;
    private PluginDataFile pluginData;
    private final XMLFile settings = new XMLFile();
    private File settingsFile;

    public IBotPlugin() {
        this.initialized = false;
    }

    @Override
    public void copyDefaultResource(File resource) throws IOException {
        if (!resource.exists()) {
            this.copyResource(resource);
        }
    }

    @Override
    public void copyDefaultResource(String resource) throws IOException {
        this.copyDefaultResource(new File(resource));
    }

    @Override
    public void copyDefaultSettings() throws IOException {
        this.copyDefaultResource(this.getSettingsFile());
    }

    @Override
    public void copyResource(File resource) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(resource.getPath());
        if (url == null) {
            throw new FileNotFoundException(resource.getPath());
        }

        File directory = this.getDataDirectory();
        directory.mkdirs();

        File target = new File(this.getDataDirectory().getPath() + File.separator + resource.getPath());
        if (!target.exists()) {
            FileUtils.copyURLToFile(url, target);
        }
    }

    @Override
    public void copyResource(String resource) throws IOException {
        this.copyResource(new File(resource));
    }

    @Override
    public File getDataDirectory() {
        return this.getPluginData().getDirectory();
    }

    @Override
    public String getName() {
        return this.getPluginData().getName();
    }

    @Override
    public XMLFile getSettings() {
        return this.settings;
    }

    @Override
    public File getSettingsFile() {
        return this.settingsFile;
    }

    @Override
    public PluginDataFile getPluginData() {
        return this.pluginData;
    }

    @Override
    public String getVersion() {
        return this.getPluginData().getVersion();
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void reloadSettings() throws IOException, JDOMException {
        if (!this.getSettingsFile().exists()) {
            this.copyResource(this.getSettingsFile());
        }

        this.getSettings().load(this.getSettingsFile());
        this.getSettings().parse();
    }

    @Override
    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }

        this.enabled = enabled;
        if (enabled) {
            this.query.getLogger().info("Enabling plugin " + this.getName() + " version " + this.getVersion() + "...");

            try {
                this.getQuery().getEvents().post(new PluginEnableEvent(this));
                this.onEnable();
            } catch (Throwable ex) {
                this.query.getLogger().trace("Could not enable plugin " + this.getName() + ": " + ex.getMessage(), ex);
            }

            this.registerEvents(this);
        } else {
            this.query.getLogger().info("Disabling plugin " + this.getName() + " version " + this.getVersion() + "...");

            try {
                this.getQuery().getEvents().post(new PluginDisableEvent(this));
                this.onDisable();
            } catch (Throwable ex) {
                this.query.getLogger().trace("Could not disable plugin " + this.getName() + ": " + ex.getMessage(), ex);
            }

            this.unregisterEvents(this);
            this.getQuery().getScheduler().cancelAll(this);
        }
    }

    public MongoStorage getStorage() {
        return this.query.getStorage();
    }

    public ServerLogger getLogger() {
        return this.logger;
    }

    public final BotQuery getQuery() {
        return this.query;
    }

    public void onDisable() {
    }

    public void onEnable() {
    }

    public void onLoad() {
    }

    public void registerEvents(Object listener) {
        this.getQuery().getEvents().register(listener);
    }

    public void unregisterEvents(Object listener) {
        this.getQuery().getEvents().unregister(listener);
    }

    protected final void initialize(BotQuery query, PluginDataFile pluginData) {
        if (this.isInitialized()) {
            throw new UnsupportedOperationException("Plugin already initialized!");
        }

        this.query = query;
        this.registerEvents(this);

        this.initialized = true;
        this.pluginData = pluginData;

        this.logger = this.query.getLogger();
        this.settingsFile = new File(this.getDataDirectory(), "settings.xml");

        try {
            this.reloadSettings();
        } catch (FileNotFoundException ex) {
        } catch (Throwable ex) {
            IBot.getLogger().trace("Could not load settings file for " + this.getName() + ": " + ex.getMessage(), ex);
        }

        this.query.getLogger().info("Loading plugin " + this.getName() + " version " + this.getVersion() + "...");
        try {
            query.getEvents().post(new PluginLoadEvent(this));
            this.onLoad();
        } catch (Throwable ex) {
            this.query.getLogger().trace("Could not load plugin " + this.getName() + ": " + ex.getMessage(), ex);
        }
    }
}
