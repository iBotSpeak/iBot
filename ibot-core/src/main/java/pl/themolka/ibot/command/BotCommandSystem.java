package pl.themolka.ibot.command;

import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.log.ConsoleWriter;
import pl.themolka.iserverquery.command.CommandSystem;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class BotCommandSystem extends CommandSystem {
    private final BotQuery botQuery;

    private final CommandExecutor executor;
    private ConsoleWriter writer;

    public BotCommandSystem(BotQuery query) {
        super(query);
        this.botQuery = query;

        this.executor = new CommandExecutor(this);
    }

    public CommandExecutor getExecutor() {
        return this.executor;
    }

    public ConsoleWriter getWriter() {
        return this.writer;
    }

    public void log(String log) {
        this.botQuery.getLogger().info(log);
    }

    public void registerClass(CommandContainer clazz) {
        for (Method method : clazz.getClass().getDeclaredMethods()) {
            method.setAccessible(true);

            Annotation commandAnnotation = method.getDeclaredAnnotation(BotCommand.class);
            if (commandAnnotation == null) {
                continue;
            }

            this.getExecutor().registerCommandClass(clazz, method, (BotCommand) commandAnnotation);
        }
    }

    public void setWriter(ConsoleWriter writer) {
        this.writer = writer;
    }
}
