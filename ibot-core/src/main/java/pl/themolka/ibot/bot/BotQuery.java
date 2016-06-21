package pl.themolka.ibot.bot;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.bson.types.ObjectId;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.IdDataFile;
import pl.themolka.ibot.command.BotCommandSystem;
import pl.themolka.ibot.command.CommandContainer;
import pl.themolka.ibot.command.def.PluginCommands;
import pl.themolka.ibot.event.BotEventSystem;
import pl.themolka.ibot.log.ServerLogger;
import pl.themolka.ibot.plugin.Plugin;
import pl.themolka.ibot.plugin.PluginManager;
import pl.themolka.ibot.plugin.PluginReader;
import pl.themolka.ibot.response.ResponseHandler;
import pl.themolka.ibot.scheduler.SchedulerExecutor;
import pl.themolka.ibot.scheduler.SchedulerManager;
import pl.themolka.ibot.settings.PluginsSettings;
import pl.themolka.ibot.stats.BotStatistics;
import pl.themolka.ibot.storage.MongoSerializable;
import pl.themolka.ibot.storage.MongoStorage;
import pl.themolka.ibot.storage.document.MongoServer;
import pl.themolka.ibot.terminal.Terminal;
import pl.themolka.ibot.terminal.TerminalCommandSystem;
import pl.themolka.ibot.terminal.commands.HelpCommand;
import pl.themolka.ibot.terminal.commands.ServerTerminalCommands;
import pl.themolka.ibot.util.Copyable;
import pl.themolka.ibot.util.TerminalUtils;
import pl.themolka.itsquery.query.TSQuery;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BotQuery extends TSQuery implements Copyable<BotQuery>, MongoSerializable {
    public static final int DEFAULT_PORT = 10011;

    private final IBot iBot;

    private final File directory;
    private final String identifier;
    private final ServerLogger logger;
    private ObjectId objectId;
    private MongoServer mongo;
    private PluginManager plugins;
    private String queryUsername;
    private String queryPassword;
    private final ResponseHandler responses;
    private final SchedulerManager scheduler;
    private final BotStatistics statistics;
    private final MongoStorage storage;
    private BotCommandSystem terminalCommands;
    private BotQueryThread thread;
    private final IdDataFile uidFile;

    public BotQuery(
            IBot iBot,
            Charset encoding,
            String identifier,
            ServerLogger logger,
            ObjectId objectId,
            String host,
            int port
    ) throws IOException {
        super(encoding, host, port);
        this.iBot = iBot;

        // injection
        this.inputHandler = new BotInputNetworkHandler(this);

        this.directory = new File(this.iBot.getServerDirectory(), identifier);
        this.directory.mkdirs();

        this.events = new BotEventSystem(this);
        this.identifier = identifier;
        if (logger == null) {
            this.logger = ServerLogger.newInstance(this);
        } else {
            this.logger = logger;
        }
        this.objectId = objectId;
        this.plugins = new PluginManager(this);
        this.responses = new ResponseHandler(this);
        this.scheduler = new SchedulerManager(this);
        this.statistics = new BotStatistics(this);
        this.storage = iBot.getStorage();
        this.terminalCommands = new BotCommandSystem(this);
        this.uidFile = new IdDataFile(this.getLogger(), this.getDirectory());

        Runtime.getRuntime().removeShutdownHook(this.getShutdownHook());

        String date = DateFormatUtils.ISO_DATE_FORMAT.format(System.currentTimeMillis());
        String log = this.getDirectory().getPath() + File.separator + "logs" + File.separator + date + ".log";
        IBot.setupLogger(this.logger, "ConsoleLogger-" + this.identifier, "FileLogger-" + this.identifier, log);
        this.terminalCommands.setWriter(this.logger.getWriter());

        this.getStorage().setConnector(this.getBot().getStorage().getConnector().copy());

        this.terminalCommands.setPrefix(TerminalCommandSystem.PREFIX);
        this.registerDefaultTerminalCommands();
    }

    @Override
    public BotQuery copy() throws Throwable {
        return new BotQuery(
                this.getBot(),
                this.getEncoding(),
                this.getIdentifier(),
                this.getLogger(),
                this.getObjectId(),
                this.getHost().getHostName(),
                this.getHost().getPort()
        );
    }

    @Override
    public ObjectId getObjectId() {
        return this.objectId;
    }

    @Override
    public Map<String, Object> serialize(Map<String, Object> data) {
        data.put(MongoServer.FIELD_ENCODING, this.getEncoding().name());
        data.put(MongoServer.FIELD_OPERATOR, this.getBot().getObjectId());

        data.put(MongoServer.FIELD_HOST, this.getHost().getHostName());
        data.put(MongoServer.FIELD_PORT, this.getHost().getPort());
        data.put(MongoServer.FIELD_USERNAME, this.getQueryUsername());
        data.put(MongoServer.FIELD_PASSWORD, this.getQueryPassword());
        return data;
    }

    @Override
    public void start() {
        if (this.isRunning()) {
            return;
        }

        // read the local UID data file
        this.loadUidFile();

        try {
            QueryServerIdentifier serverIdentifier = new QueryServerIdentifier(this);
            serverIdentifier.run();

            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException ignored) {
        }

        for (Plugin plugin : this.getPlugins().getPlugins()) {
            this.getPlugins().enablePlugin(plugin);
        }

        // load plugins
        PluginsSettings settings = this.getBot().getSettings().getPlugins();
        List<PluginReader.PluginClass> plugins = this.getPlugins().getReader().readPlugins(settings);

        for (PluginReader.PluginClass clazz : plugins) {
            this.getPlugins().registerPlugin(this.getPlugins().loadPlugin(clazz));
        }

        // start the query itself
        super.start();

        this.getReader().setName(this.getIdentifier() + " reader");
        this.getWriter().setName(this.getIdentifier() + " writer");

        // register input queries in the server tick loop
        this.getThread().registerHandler((Runnable) this.getInputHandler());

        // register the synchronize handler
        this.getThread().registerHandler(new Synchronizer(this));
    }
    
    @Override
    public void stop() {
        if (!this.isRunning()) {
            return;
        }

        for (SchedulerExecutor executor : new ArrayList<>(this.getScheduler().getExecutors())) {
            this.getScheduler().cancel(executor.getId());
        }

        // stop the query itself
        super.stop();

        for (Plugin plugin : this.getPlugins().getPlugins()) {
            this.getPlugins().disablePlugin(plugin);
        }
        this.getScheduler().cancelAll();

        this.getStorage().disconnect();

        Terminal terminal = this.iBot.getTerminal();
        if (terminal.getCurrentHandler().equals(this.getTerminalCommands())) {
            TerminalUtils.clearOutput(this.iBot);
            terminal.setCurrentHandler(this.iBot.getCommands());

            IBot.getLogger().info("Your session were closed and you were automatically moved to the " + terminal.getName() + ".");
        }
    }

    public void asyncStart() {
        this.getThread().enable(true);
    }

    public void asyncStop() {
        this.getThread().disable(true);
    }

    public IBot getBot() {
        return this.iBot;
    }

    public File getDirectory() {
        return this.directory;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public ServerLogger getLogger() {
        return this.logger;
    }

    public MongoServer getMongo() {
        return this.mongo;
    }

    public PluginManager getPlugins() {
        return this.plugins;
    }

    public String getQueryUsername() {
        return this.queryUsername;
    }

    public String getQueryPassword() {
        return this.queryPassword;
    }

    public ResponseHandler getResponses() {
        return this.responses;
    }

    public SchedulerManager getScheduler() {
        return this.scheduler;
    }

    public QueryState getState() {
        if (!this.isRunning() || this.getSocket().isClosed()) {
            return QueryState.GHOST;
        } else if (!this.getThread().isInterrupted() && !this.getReader().isInterrupted() && !this.getWriter().isInterrupted()) {
            return QueryState.RUNNING;
        }

        return QueryState.LOADING;
    }

    public BotStatistics getStatistics() {
        return this.statistics;
    }

    public MongoStorage getStorage() {
        return this.storage;
    }

    public BotCommandSystem getTerminalCommands() {
        return this.terminalCommands;
    }

    public BotQueryThread getThread() {
        return this.thread;
    }

    public IdDataFile getUidFile() {
        return this.uidFile;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public void setHost(InetSocketAddress host) {
        this.host = host;
    }

    public void setHost(String host, int port) {
        this.setHost(new InetSocketAddress(host, port));
    }

    public void setMongo(MongoServer mongo) {
        this.mongo = mongo;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public void setQueryUsername(String username) {
        this.queryUsername = username;
    }

    public void setQueryPassword(String password) {
        this.queryPassword = password;
    }

    public void setThread(BotQueryThread thread) {
        this.thread = thread;
    }

    private boolean loadUidFile() {
        if (this.getUidFile().getFile().exists()) {
            ObjectId id = this.getUidFile().load();

            if (id != null) {
                this.setObjectId(id);
                return true;
            }
        }

        return this.getUidFile().save(this.getObjectId());
    }

    /*private void registerDefaultQueryCommands() {
        CommandContainer[] containers = new CommandContainer[]{
                new HelpCommand(this.getBot(), this.getCommands())
        };

        for (CommandContainer container : containers) {
            this.getCommands().registerClass(container);
        }
    }*/

    private void registerDefaultTerminalCommands() {
        CommandContainer[] containers = new CommandContainer[] {
                new HelpCommand(this.iBot, this.getTerminalCommands()),
                new PluginCommands(this),
                new ServerTerminalCommands(this)
        };

        for (CommandContainer container : containers) {
            this.getTerminalCommands().registerClass(container);
        }
    }
}
