package pl.themolka.ibot.terminal.commands;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import org.bson.Document;
import org.bson.conversions.Bson;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.command.BotCommand;
import pl.themolka.ibot.command.CommandImpl;
import pl.themolka.ibot.command.QueryCommandContainer;
import pl.themolka.ibot.storage.collection.ServerCollection;
import pl.themolka.ibot.storage.document.MongoServer;
import pl.themolka.ibot.terminal.Terminal;
import pl.themolka.ibot.util.TerminalUtils;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.command.CommandSender;

public class ServerTerminalCommands extends QueryCommandContainer {
    public ServerTerminalCommands(BotQuery botQuery) {
        super(botQuery);
    }

    @BotCommand(name = "db", description = "Manage the database", usage = "<drop|pull|push>")
    public void db(final CommandSender sender, CommandContext context) {
        String arg = context.getParam(0);
        if (arg == null) {
            CommandImpl command = (CommandImpl) context.getCommand();
            sender.sendMessage(command.getDescription());
            sender.sendMessage(command.getUsage());
            return;
        }

        MongoServer server = this.botQuery.getMongo();
        ServerCollection collection = this.botQuery.getStorage().getServerCollection();
        Bson findClause = MongoServer.idFilter(this.botQuery.getObjectId());

        switch (arg.toLowerCase()) {
            case "drop":
                if (!TerminalUtils.readYesOrNo("You want to PERMANENTLY delete this server from the database. Are you sure?")) {
                    return;
                }

                sender.sendMessage("Dropping this server from the database...");
                collection.getMongo().findOneAndDelete(findClause, new SingleResultCallback<Document>() {
                    @Override
                    public void onResult(Document result, Throwable throwable) {
                        if (throwable != null) {
                            sender.sendMessage("Could not drop from the database: " + throwable.getMessage());
                        } else {
                            ServerTerminalCommands.this.botQuery.setMongo(null);
                            sender.sendMessage("Successfully dropped from the database!");
                        }
                    }
                });
                break;

            // the user should restart the server...
            case "pull":
                sender.sendMessage("Pulling this server from the database...");
                collection.findById(new Block<MongoServer>() {
                    @Override
                    public void apply(MongoServer server) {
                        BotQuery botQuery = ServerTerminalCommands.this.botQuery;
                        botQuery.setMongo(server);
                        botQuery.setEncoding(server.getFieldEncodingCharset());

                        botQuery.setQueryUsername(server.getFieldUsername());
                        botQuery.setQueryPassword(server.getFieldPassword());
                    }
                }, new SingleResultCallback<Void>() {
                    @Override
                    public void onResult(Void result, Throwable throwable) {
                        if (throwable != null) {
                            sender.sendMessage("Could not pull from the database: " + throwable.getMessage());
                        } else {
                            sender.sendMessage("Successfully pulled from the database!");
                        }
                    }
                }, this.botQuery.getObjectId());
                break;

            case "push":
                boolean insert = false;

                if (this.botQuery.getMongo() != null) {
                    sender.sendMessage("Pushing (updating) this server to the database...");
                } else {
                    sender.sendMessage("Pushing (inserting) this server to the database...");
                    insert = true;

                    server = new MongoServer(this.botQuery.getStorage());
                    this.botQuery.setMongo(server);
                }

                if (insert) {
                    /*collection.getMongo().insertOne(new Document(server), new SingleResultCallback<Void>() {
                        @Override
                        public void onResult(Void result, Throwable throwable) {
                            if (throwable != null) {
                                sender.sendMessage("Could not insert to the database: " + throwable.getMessage());
                            } else {
                                sender.sendMessage("Successfully inserted to the database!");
                            }
                        }
                    });*/

                    sender.sendMessage("Not supported yet!");
                } else {
                    collection.getMongo().findOneAndUpdate(findClause, new Document(server), new SingleResultCallback<Document>() {
                        @Override
                        public void onResult(Document result, Throwable throwable) {
                            if (throwable != null) {
                                sender.sendMessage("Could not update the database: " + throwable.getMessage());
                            } else {
                                sender.sendMessage("Successfully updated the database!");
                            }
                        }
                    });
                }
                break;

            default:
                CommandImpl command = (CommandImpl) context.getCommand();
                sender.sendMessage(command.getDescription());
                sender.sendMessage(command.getUsage());
                break;
        }
    }

    @BotCommand(name = "exit", description = "Exit this server session")
    public void exit(CommandSender sender, CommandContext context) {
        sender.sendMessage("Closing '" + this.botQuery.getIdentifier() + "' session...");

        Terminal terminal = this.iBot.getTerminal();
        TerminalUtils.clearOutput(this.iBot);
        terminal.setCurrentHandler(this.iBot.getCommands());

        sender.sendMessage("Welcome back to '" + terminal.getName() + "'!");
    }

    @BotCommand(name = "sudo", description = "Execute a Query command", usage = "<command>")
    public void sudo(CommandSender sender, CommandContext context) {
        String query = context.getParams(0);
        if (query == null) {
            CommandImpl commandImpl = (CommandImpl) context.getCommand();
            sender.sendMessage(commandImpl.getDescription());
            sender.sendMessage(commandImpl.getUsage());
            return;
        }

        if (query.isEmpty()) {
            sender.sendMessage("Query command was empty.");
            return;
        }

        if (this.botQuery.getOutputHandler().executeRaw(query)) {
            sender.sendMessage("Query command sent.");
        }
    }
}
