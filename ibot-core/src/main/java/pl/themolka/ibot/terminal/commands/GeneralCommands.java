package pl.themolka.ibot.terminal.commands;

import pl.themolka.ibot.IBot;
import pl.themolka.ibot.command.BotCommand;
import pl.themolka.ibot.command.CommandContainer;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GeneralCommands extends CommandContainer {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Server Threads:
     * - Query data synchronizer thread         (synchronizing data between "TS server -> bot")
     * - Query read thread                      (reading queries from the TS server)
     * - Query thread                           (main query thread to provide the bot logic)
     * - Query write thread                     (writing queries to the TS server)
     *
     * Global Threads:
     * - Keep alive thread                      (keeping the bots alive by sending fake queries to the write threads)
     * - Main thread (provided by the JVM)      (main thread provided by the JVM - used only on startup)
     * - Server reader thread                   (synchronizing data between "MongoDB -> process")
     * - Shutdown hook thread                   (thread provided to safe shutdown by the JVM)
     * - Sleep forever thread                   (blocking JVM to detach this process)
     * - Terminal thread                        (reading console inputs)
     *
     * + MongoDB Java Driver threads
     */
    public static final int GLOBAL_THREADS = 6;
    public static final int INSTANCE_THREADS = 4;

    public GeneralCommands(IBot iBot) {
        super(iBot);
    }

    @BotCommand(name = "gc", description = "Print Java Garbage Collector statistics")
    public void gc(CommandSender sender, CommandContext context) {
        Runtime runtime = Runtime.getRuntime();
        String unknown = "Unknown";

        sender.sendMessage(String.format("System: %s version %s (%s)",
                System.getProperty("os.name", unknown),
                System.getProperty("os.version", unknown),
                System.getProperty("os.arch", unknown)
        ));

        sender.sendMessage(String.format("Java: %s version %s (installed in %s)",
                System.getProperty("java.vendor", unknown),
                System.getProperty("java.version", unknown),
                System.getProperty("java.home", unknown)
        ));

        sender.sendMessage(String.format("JVM: %s %s version %s",
                System.getProperty("java.vm.vendor", unknown),
                System.getProperty("java.vm.name", unknown),
                System.getProperty("java.vm.version", unknown)
        ));

        sender.sendMessage(String.format("Processors: %s",
                runtime.availableProcessors()
        ));

        sender.sendMessage(String.format("Active servers: %s",
                this.iBot.getInstances().size()
        ));

        sender.sendMessage(String.format("Active threads: %s: %s per server + %s global + %s storage",
                Thread.activeCount(),
                INSTANCE_THREADS,
                GLOBAL_THREADS,
                Thread.activeCount() - GLOBAL_THREADS - (INSTANCE_THREADS * this.iBot.getInstances().size())
        ));

        sender.sendMessage(String.format("Uptime: %s (started %s)",
                this.parseUptime(System.currentTimeMillis() - this.iBot.getStartTime()),
                DATE_FORMAT.format(new Date(this.iBot.getStartTime()))
        ));

        sender.sendMessage(String.format("Total memory: %s MB",
                runtime.maxMemory() / 1024.0 / 1024.0
        ));

        sender.sendMessage(String.format("JVM allocated memory: %s MB",
                runtime.totalMemory() / 1024.0 / 1024.0
        ));

        sender.sendMessage(String.format("Free allocated memory: %s MB",
                runtime.freeMemory() / 1024.0 / 1024.0
        ));
    }

    @BotCommand(name = "restart", description = "Restart all services")
    public void restart(CommandSender sender, CommandContext context) {
        sender.sendMessage("Restarting enabled services...");

        try {
            this.iBot.stop();
            IBot.getLogger().info(System.lineSeparator() + System.lineSeparator() + "Restarting...");
            this.iBot.start();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        sender.sendMessage("Done!");
    }

    @BotCommand(name = "stop", description = "Stop all enabled services and close")
    public void stop(CommandSender sender, CommandContext context) {
        sender.sendMessage("Stopping enabled services...");
        Runtime.getRuntime().removeShutdownHook(this.iBot.getShutdownHook());

        try {
            this.iBot.stop();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        sender.sendMessage("Bye!");
        System.exit(0);
    }

    private String parseUptime(long uptime) {
        StringBuilder builder = new StringBuilder();

        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        if (days > 0) {
            builder.append(days).append(" day(s) ");
        }

        long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        if (days > 0 || hours > 0) {
            builder.append(hours).append(" hour(s) ");
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);
        if (days > 0 || hours > 0 || minutes > 0) {
            builder.append(minutes).append(" minute(s) ");
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
        builder.append(seconds).append(" second(s)");

        return builder.toString();
    }
}
