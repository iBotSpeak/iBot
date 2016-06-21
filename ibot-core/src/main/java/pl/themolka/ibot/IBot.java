package pl.themolka.ibot;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.bson.types.ObjectId;
import org.jdom2.JDOMException;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.bot.BotQueryThread;
import pl.themolka.ibot.command.BotCommandSystem;
import pl.themolka.ibot.log.ConsoleAppender;
import pl.themolka.ibot.log.Logger;
import pl.themolka.ibot.settings.DatabaseSettings;
import pl.themolka.ibot.settings.InstancesSettings;
import pl.themolka.ibot.settings.Settings;
import pl.themolka.ibot.storage.IObjectId;
import pl.themolka.ibot.storage.MongoStorage;
import pl.themolka.ibot.storage.document.MongoServer;
import pl.themolka.ibot.terminal.DevelopmentTerminal;
import pl.themolka.ibot.terminal.Terminal;
import pl.themolka.ibot.terminal.TerminalCommandSystem;
import pl.themolka.ibot.terminal.TerminalThread;
import pl.themolka.ibot.util.PropertiesUtils;
import pl.themolka.ibot.xml.XMLException;
import pl.themolka.iserverquery.event.EventSystem;
import pl.themolka.iserverquery.util.Platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class IBot implements IObjectId {
    public static final String CONSOLE_LOGGER_PATTERN = "%d{HH:mm:ss} %p [%t]: %m%n";
    public static final String FILE_LOGGER_PATTERN = "%d{yyyy-MM-mm HH:mm:ss} %p [%t]: %m%n";

    private static Logger globalLogger = Logger.newBotInstance();

    private final TerminalCommandSystem commands;
    private final boolean demo;
    private final boolean development;
    private final EventSystem events;
    private final List<BotQuery> instances;
    private KeepAliveThread keepAliveThread;
    private final String name;
    private final ObjectId objectId;
    private final Platform platform;
    private final Properties properties;
    private boolean running = false;
    private Settings settings;
    private final ShutdownHook shutdownHook;
    private final File serverDirectory;
    private ServerReaderThread serverReader;
    private final long startTime;
    private final MongoStorage storage;
    private final Terminal terminal;
    private String version = "Unknown";

    public IBot(Properties cli) throws Throwable {
        this.properties = cli;
        this.loadVersion();

        getLogger().info("Initializing...");

        this.name = PropertiesUtils.setOrNotString(cli, "core.name", "Default");
        this.commands = new TerminalCommandSystem(this);
        this.demo = PropertiesUtils.setOrNotBoolean(cli, "core.demo", false);
        this.development = PropertiesUtils.setOrNotBoolean(cli, "core.development", false);
        this.events = new EventSystem(IBot.class.getName());
        this.instances = new CopyOnWriteArrayList<>(); // for the thread-safety reason
        this.objectId = this.loadObjectId();
        this.platform = Platform.system();
        this.shutdownHook = new ShutdownHook(this);
        this.serverDirectory = new File(PropertiesUtils.setOrNotString(cli, "core.servers.directory", "servers"));
        this.startTime = System.currentTimeMillis();
        this.storage = new MongoStorage(getLogger());

        // temp
        this.storage.getConnector().setHost("5.206.225.11");
        this.storage.getConnector().setPort(9000);
        this.storage.getConnector().setDatabase("test");

        if (this.isDevelopment()) {
            this.terminal = new DevelopmentTerminal(this);
        } else {
            this.terminal = new Terminal(this);
        }

        Runtime.getRuntime().addShutdownHook(this.shutdownHook);

        this.commands.registerDefaults();
        this.terminal.setCurrentHandler(null);
        this.terminal.setThread(new TerminalThread(this, System.in));

        new SleepForeverThread().start();
    }

    @Override
    public ObjectId getObjectId() {
        return this.objectId;
    }

    public BotQuery createServer(MongoServer server) {
        try {
            BotQuery botQuery = new BotQuery(
                    IBot.this,
                    server.getFieldEncodingCharset(),
                    server.getFieldId().toString(), // can we change it with the custom name?
                    null,
                    server.getFieldId(),
                    server.getFieldHost(),
                    server.getFieldPort()
            );

            botQuery.setQueryUsername(server.getFieldUsername());
            botQuery.setQueryPassword(server.getFieldPassword());

            botQuery.setThread(new BotQueryThread(botQuery));
            return botQuery;
        } catch (IOException ex) {
            getLogger().error("Could not load " + BotQuery.class.getName() + ": " + ex.getMessage(), ex);
        }

        return null;
    }

    public void copyFiles() {
        File file = new File("settings.xml");
        try {
            if (!file.exists()) {
                getLogger().info("Copying " + file.getName() + " file...");

                URL resource = this.getClass().getClassLoader().getResource(file.getName());
                if (resource != null) {
                    FileUtils.copyURLToFile(resource, file);
                } else {
                    throw new FileNotFoundException(file.getPath() + " does not exists.");
                }
            }
        } catch (IOException | SecurityException ex) {
            getLogger().trace("Could not copy the default settings file.", ex);
        }

        try {
            getLogger().info("Reading the settings file...");
            Settings settings = new Settings(new FileInputStream(file));
            settings.parse();

            settings.read();
            this.settings = settings;
        } catch (IOException | JDOMException | XMLException ex) {
            getLogger().trace("Could not read the default settings file: " + ex.getMessage(), ex);
        }
    }

    public boolean detachInstance(BotQuery query) {
        return this.instances.remove(query);
    }

    public BotCommandSystem getCommands() {
        return this.commands;
    }

    public EventSystem getEvents() {
        return this.events;
    }

    public String getName() {
        return this.name;
    }

    public Platform getPlatform() {
        return this.platform;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public File getServerDirectory() {
        return this.serverDirectory;
    }

    public ShutdownHook getShutdownHook() {
        return this.shutdownHook;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public List<BotQuery> getInstances() {
        return this.getServers();
    }

    public BotQuery getServer(ObjectId objectId) {
        for (BotQuery query : this.getServers()) {
            if (query.getObjectId().equals(objectId)) {
                return query;
            }
        }

        return null;
    }

    public BotQuery getServer(String identifier) {
        for (BotQuery query : this.getServers()) {
            if (query.getIdentifier() != null && query.getIdentifier().equals(identifier)) {
                return query;
            }
        }

        return null;
    }

    public List<BotQuery> getServers() {
        return this.instances;
    }

    public ServerReaderThread getServerReader() {
        return this.serverReader;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public MongoStorage getStorage() {
        return this.storage;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean isDemo() {
        return this.demo;
    }

    public boolean isDevelopment() {
        return this.development;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void readDatabase() throws XMLException {
        DatabaseSettings settings = this.settings.getDatabase();
        for (DatabaseSettings.DatabaseConnection connection : settings.getConnections()) {
            if (this.getStorage() != null) {
                break;
            }

// TODO            this.storage = connection.getDatabase();
        }
    }

    public void readInstances() throws XMLException {
        InstancesSettings settings = this.settings.getInstances();
        for (InstancesSettings.ServerInstance server : settings.getInstances()) {
            try {
                BotQuery query = new BotQuery(
                        this,
                        Charset.forName("UTF-8"),
                        server.getId(),
                        null,
                        new ObjectId(),
                        server.getHost(),
                        server.getPort()
                );

                query.setQueryUsername(server.getQueryUsername());
                query.setQueryPassword(server.getQueryPassword());

                query.setThread(new BotQueryThread(query));
                if (!this.registerInstance(query)) {
                    getLogger().warn("Could not register " + query.getIdentifier() + ".");
                }
            } catch (IOException ex) {
                getLogger().trace(ex);
            }
        }
    }

    public boolean registerInstance(BotQuery query) {
        /*for (BotQuery instance : this.instances) {
            if (this.getServer(instance.getObjectId()) != null || this.getServer(instance.getIdentifier()) != null) {
                return false;
            }
        }*/

        return this.instances.add(query);
    }

    public void start() throws IOException {
        if (this.isRunning()) {
            return;
        }
        this.running = true;

        getLogger().info("Starting iBot version " + this.getVersion() + " by TheMolkaPL as '" +
                this.getName() + "' (ObjectID " + this.getObjectId().toHexString() + ").");

        long took = System.currentTimeMillis();
        this.copyFiles();

        try {
            this.readDatabase();
        } catch (XMLException ex) {
            getLogger().trace("Could not read database: " + ex.getMessage(), ex);
        }

        try {
            this.readInstances();
        } catch (XMLException ex) {
            getLogger().trace("Could not read instance(s): " + ex.getMessage(), ex);
        }

        this.getStorage().connect();

        // load servers
        try {
            this.loadStoredServers();
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException ignored) {
        }

        if (this.instances.isEmpty()) {
            getLogger().warn("No instances were found! You can add them in the settings file.");
        } else {
            getLogger().info("Starting up " + this.instances.size() + " instance(s)...");

            for (BotQuery query : this.instances) {
                getLogger().info("Hooking " + query.getIdentifier() + " to " + query.getHost().getHostName() + ":" + query.getHost().getPort() + "...");

                query.asyncStart();
                query.getThread().start();
            }
        }

        this.keepAliveThread = new KeepAliveThread(this, 300);
        this.keepAliveThread.start();

        this.serverReader = new ServerReaderThread(this);
        this.serverReader.start();

        this.getTerminal().getThread().start();

        getLogger().info("Done - took " + this.toSeconds(took) + " second(s).");
    }

    public void stop() throws InterruptedException {
        if (!this.isRunning()) {
            return;
        }
        this.running = false;

        long took = System.currentTimeMillis();

        this.keepAliveThread.interrupt();
        this.serverReader.interrupt();

        getLogger().info("Shutting down " + this.instances.size() + " instance(s)...");

        for (BotQuery query : this.instances) {
            query.asyncStart();
        }

        getLogger().info("Closing down the MongoDB connection...");
        this.getStorage().disconnect();

        getLogger().info("Done - took " + this.toSeconds(took) + " second(s). Stopping servers...");
        Thread.sleep(3 * 1000L);

        this.getTerminal().getThread().interrupt();
        this.instances.clear();
    }

    public boolean unregisterInstance(BotQuery query) {
        return this.instances.remove(query);
    }

    private ObjectId loadObjectId() {
        File directory = new File("data");
        directory.mkdirs();

        IdDataFile data = new IdDataFile(getLogger(), directory);

        String cli = this.getProperties().getProperty("core.objectId");
        if (cli != null) {
            ObjectId id = new ObjectId(cli);

            data.save(id);
            return id;
        }

        if (data.getFile().exists()) {
            ObjectId id = data.load();

            if (id != null) {
                return id;
            }
        }

        ObjectId id = new ObjectId();
        data.save(id);
        return id;
    }

    private void loadStoredServers() {
        this.getStorage().getServerCollection().findByOperator(new Block<MongoServer>() {
            @Override
            public void apply(MongoServer server) {
                if (IBot.this.getServer(server.getFieldId()) != null) {
                    // we have a copy of it in the database, abort.
                    return;
                }

                getLogger().info("Found new MongoDB query: " + server.getFieldId());

                BotQuery query = IBot.this.createServer(server);
                if (query == null) {
                    return;
                }

                query.setMongo(server);

                if (!IBot.this.registerInstance(query)) {
                    getLogger().error("Could not register query - " + server.getFieldId());
                }
            }
        }, new SingleResultCallback<Void>() {
            @Override
            public void onResult(Void result, Throwable throwable) {
                synchronized (IBot.this) {
                    IBot.this.notify();
                }
            }
        }, this.getObjectId());
    }

    private void loadVersion() throws IOException {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("META-INF/maven/pl.themolka/itsquery/pom.properties");
        if (input != null) {
            Properties properties = new Properties();
            properties.load(input);

            this.version = properties.getProperty("version", this.version);
        }
    }

    private double toSeconds(long took) {
        return (System.currentTimeMillis() - took) / 1000D;
    }

    public static Logger getLogger() {
        return globalLogger;
    }

    public static void setupLogger(Logger logger, String consoleName, String fileName, String filePath) {
        if (consoleName != null) {
            ConsoleAppender console = new ConsoleAppender();
            console.setName(consoleName);
            console.setLayout(new PatternLayout(CONSOLE_LOGGER_PATTERN));
            console.activateOptions();
            logger.addAppender(console);

            logger.setWriter(console.getWriter());
        }

        if (fileName != null) {
            FileAppender file = new FileAppender();
            file.setName(fileName);
            file.setFile(filePath);
            file.setLayout(new PatternLayout(FILE_LOGGER_PATTERN));
            file.setAppend(true);
            file.activateOptions();
            logger.addAppender(file);
        }

        setLogger(logger);
    }

    private static boolean setLogger(Logger logger) {
        if (getLogger() == null) {
            globalLogger = logger;
            return true;
        }

        return false;
    }
}
