package pl.themolka.ibot.terminal.commands;

import org.bson.types.ObjectId;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.bot.BotQueryThread;
import pl.themolka.ibot.bot.QueryState;
import pl.themolka.ibot.command.BotCommand;
import pl.themolka.ibot.command.CommandContainer;
import pl.themolka.ibot.command.CommandImpl;
import pl.themolka.ibot.util.TerminalUtils;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.command.CommandSender;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ServerCommands extends CommandContainer {
    public ServerCommands(IBot iBot) {
        super(iBot);
    }

    @BotCommand(name = "cs", description = "Create new temporary server")
    public void cs(CommandSender sender, CommandContext context) {
        String id = TerminalUtils.readString("Enter unique ID");
        if (id == null || id.isEmpty()) {
            sender.sendMessage("ID cannot be empty!");
            return;
        }

        for (BotQuery onlineQuery : this.iBot.getInstances()) {
            if (onlineQuery.getIdentifier().equalsIgnoreCase(id)) {
                sender.sendMessage("Server with the given ID already exists.");
                return;
            }
        }

        String host = TerminalUtils.readString("Enter host (leave empty for your local host)");
        if (host == null || host.isEmpty()) {
            host = "127.0.0.1";
        }

        String port = TerminalUtils.readString("Enter port (leave empty for the default)");
        int numericPort = BotQuery.DEFAULT_PORT;
        if (port == null || !port.isEmpty()) {
            try {
                numericPort = Integer.parseInt(port);
            } catch (NumberFormatException ex) {
                IBot.getLogger().info("Could not parse the given port: " + port);
                return;
            }
        }

        String username = TerminalUtils.readString("Enter Server Query username");
        if (username == null || username.isEmpty()) {
            sender.sendMessage("Server Query username cannot be empty!");
            return;
        }

        char[] password = TerminalUtils.readPassword("Enter Server Query password");
        if (password == null || password.length == 0) {
            sender.sendMessage("Server Query password cannot be empty!");
            return;
        }

        BotQuery query = null;
        try {
            query = new BotQuery(this.iBot, Charset.forName("UTF-8"), id, null, new ObjectId(), host, numericPort);

            query.setQueryUsername(username);
            query.setQueryPassword(String.valueOf(password));

            query.setThread(new BotQueryThread(query));
            if (!this.iBot.registerInstance(query)) {
                throw new Throwable("could not add to the list.");
            }
        } catch (Throwable ex) {
            sender.sendMessage("Could not create a new server: " + ex.getMessage());
        }

        if (query == null) {
            return;
        }

        try {
            IBot.getLogger().info("Hooking " + query.getIdentifier() + " to " + query.getHost().getHostName() + ":" + query.getHost().getPort() + "...");

            query.asyncStart();
            query.getThread().start();
        } catch (Throwable ex) {
            sender.sendMessage("Could not start a new server: " + ex.getMessage());
        }

        sender.sendMessage(id + " has been created. Type 'ss " + id + "' to use its local terminal.");
    }

    @BotCommand(name = "detach", description = "Detach a server", usage = "<-all|-list|server>", flags = {"all", "list"})
    public void detach(CommandSender sender, CommandContext context) {
        if (context.hasFlag("list")) {
            List<BotQuery> ghosts = new ArrayList<>();
            for (BotQuery query : this.iBot.getInstances()) {
                if (query.getState().equals(QueryState.GHOST)) {
                    ghosts.add(query);
                }
            }

            if (ghosts.isEmpty()) {
                sender.sendMessage("No results found!");
                return;
            }

            int ghostsSize = ghosts.size();
            if (ghostsSize > 15) {
                if (!TerminalUtils.readYesOrNo("The server list contains %d items. Do you want to display them all?", ghostsSize)) {
                    return;
                }
            }

            sender.sendMessage(this.getTitle("Ghosts", ghostsSize));
            for (int i = 0; i < ghostsSize; i++) {
                sender.sendMessage((i + 1) + ". " + ghosts.get(i).getIdentifier());
            }
            return;
        }

        boolean all = context.hasFlag("all");
        String serverName = context.getParam(0);

        if (!all && serverName == null) {
            CommandImpl command = (CommandImpl) context.getCommand();
            sender.sendMessage(command.getDescription());
            sender.sendMessage(command.getUsage());
            return;
        } else if (serverName != null) {
            serverName = serverName.toLowerCase();
        }

        List<BotQuery> results = new ArrayList<>();
        if (all) {
            for (BotQuery query : this.iBot.getInstances()) {
                if (query.getState().equals(QueryState.GHOST)) {
                    results.add(query);
                }
            }
        } else {
            for (BotQuery query : this.iBot.getInstances()) {
                if (query.getState().equals(QueryState.GHOST) && query.getIdentifier().toLowerCase().contains(serverName)) {
                    results.add(query);
                }
            }
        }

        if (results.isEmpty()) {
            sender.sendMessage("No results found!");
            return;
        }

        int resultsSize = results.size();
        if (!TerminalUtils.readYesOrNo("You will detach %d server instance(s). Are you sure?", resultsSize)) {
            return;
        }

        int detached = 0;
        Iterator<BotQuery> iterator = results.iterator();
        while (iterator.hasNext()) {
            BotQuery query = iterator.next();

            try {
                if (!this.iBot.detachInstance(query)) {
                    throw new Throwable("could not remove from the list.");
                }

                detached++;
            } catch (Throwable ex) {
                sender.sendMessage("Could not detach " + query.getIdentifier() + ": " + ex.getMessage());
            }
        }

        sender.sendMessage("Detached " + detached + " of " + resultsSize + " server instance(s).");
    }

    @BotCommand(name = "exit", description = "Exit this server session")
    public void exit(CommandSender sender, CommandContext context) {
        sender.sendMessage("You are already in '" + this.iBot.getTerminal().getName() + "'!");
    }

    @BotCommand(name = "kill", description = "Kill the server session", usage = "[-all|server]", flags = {"all"})
    public void kill(CommandSender sender, CommandContext context) {
        boolean all = context.hasFlag("all");
        String serverName = context.getParam(0);

        if (!all && serverName == null) {
            CommandImpl command = (CommandImpl) context.getCommand();
            sender.sendMessage(command.getDescription());
            sender.sendMessage(command.getUsage());
            return;
        } else if (serverName != null) {
            serverName = serverName.toLowerCase();
        }

        List<BotQuery> results = new ArrayList<>();
        if (all) {
            results.addAll(this.iBot.getInstances());
        } else {
            for (BotQuery query : this.iBot.getInstances()) {
                if (!query.getState().equals(QueryState.GHOST) && query.getIdentifier().toLowerCase().contains(serverName)) {
                    results.add(query);
                }
            }
        }

        if (results.isEmpty()) {
            sender.sendMessage("No results found!");
            return;
        }

        int resultsSize = results.size();
        if (!TerminalUtils.readYesOrNo("You will kill %d user instance(s). Are you sure?", resultsSize)) {
            return;
        }

        for (int i = 0; i < resultsSize; i++) {
            BotQuery query = results.get(i);
            sender.sendMessage("Killing " + (i + 1) + " of " + resultsSize + " - '" + query.getIdentifier() + "'...");

            try {
                query.asyncStop();
            } catch (Throwable ex) {
                sender.sendMessage("Could not kill " + query.getIdentifier() + ": " + ex.getMessage());
            }
        }

        try {
            Thread.sleep(3 * 1000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        int killed = 0;
        for (int i = 0; i < resultsSize; i++) {
            BotQuery query = results.get(i);

            try {
                query.stop();
                killed++;
            } catch (Throwable ex) {
                sender.sendMessage("Could not kill " + query.getIdentifier() + ": " + ex.getMessage());
            }
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        sender.sendMessage("Killed " + killed + " of " + resultsSize + " server instance(s).");
        sender.sendMessage("You can now detach them by typing the 'detach' command.");
    }

    @BotCommand(name = "ss", description = "Switch server", usage = "<server>")
    public void ss(CommandSender sender, CommandContext context) {
        String serverName = context.getParam(0);
        if (serverName == null) {
            CommandImpl command = (CommandImpl) context.getCommand();
            sender.sendMessage(command.getDescription());
            sender.sendMessage(command.getUsage());
            return;
        }
        serverName = serverName.toLowerCase();

        List<BotQuery> results = new ArrayList<>();
        for (BotQuery query : this.iBot.getInstances()) {
            if (query.getIdentifier().toLowerCase().contains(serverName)) {
                results.add(query);
            }
        }

        if (results.isEmpty()) {
            sender.sendMessage("No results found!");
            return;
        }

        int resultsSize = results.size();
        if (resultsSize == 1) {
            BotQuery query = results.get(0);
            sender.sendMessage("Switching to '" + query.getIdentifier() + "'...");

            TerminalUtils.clearOutput(this.iBot);
            this.iBot.getTerminal().setCurrentHandler(query.getTerminalCommands());
            sender.sendMessage("Welcome to '" + query.getIdentifier() + "'!");
            return;
        }

        sender.sendMessage("More than 1 result found. Be more specific!");
        sender.sendMessage(this.getTitle("Users", resultsSize));
        Collections.sort(results, new QueryComparator());

        for (int i = 0; i < resultsSize; i++) {
            sender.sendMessage((i + 1) + ". " + this.printQuery(results.get(i)));
        }
    }

    @BotCommand(name = "sl", description = "Print available servers", usage = "[server]")
    public void sl(CommandSender sender, CommandContext context) {
        List<BotQuery> results = new ArrayList<>();

        String username = context.getParam(0);
        if (username != null) {
            for (BotQuery query : this.iBot.getInstances()) {
                if (query.getIdentifier().toLowerCase().contains(username)) {
                    results.add(query);
                }
            }
        } else {
            results.addAll(this.iBot.getInstances());
        }

        if (results.isEmpty()) {
            sender.sendMessage("No results found!");
            return;
        }

        int resultsSize = results.size();
        if (resultsSize > 50) {
            if (!TerminalUtils.readYesOrNo("The user list contains %d items. Do you want to display them all?", resultsSize)) {
                return;
            }
        }

        sender.sendMessage(this.getTitle("Users", resultsSize));
        Collections.sort(results, new QueryComparator());

        for (int i = 0; i < resultsSize; i++) {
            sender.sendMessage((i + 1) + ". " + this.printQuery(results.get(i)));
        }
    }

    private String printQuery(BotQuery query) {
        StringBuilder builder = new StringBuilder();
        builder.append(query.getIdentifier());

        if (query.getState().equals(QueryState.RUNNING)) {
            builder.append(" - ").append(query.getServer().getConnectedClients().size()).append(" clients online");
        } else {
            builder.append(" - ").append(query.getState().getName());
        }

        return builder.toString();
    }

    private class QueryComparator implements Comparator<BotQuery> {
        @Override
        public int compare(BotQuery o1, BotQuery o2) {
            return o1.getIdentifier().compareTo(o2.getIdentifier());
        }
    }
}
