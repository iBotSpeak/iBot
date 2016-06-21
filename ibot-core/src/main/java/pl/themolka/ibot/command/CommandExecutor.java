package pl.themolka.ibot.command;

import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.command.CommandException;
import pl.themolka.iserverquery.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandExecutor {
    private final BotCommandSystem commands;

    public CommandExecutor(BotCommandSystem commands) {
        this.commands = commands;
    }

    public void registerCommandClass(final CommandContainer clazz, final Method method, BotCommand command) {
        CommandImpl commandObj = new CommandImpl(command.name(), this.commands, command.description(), command.flags(), command.usage()) {
            @Override
            public void handleCommand(CommandSender sender, CommandContext context) throws CommandException {
                try {
                    method.invoke(clazz, sender, context);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        };

        this.commands.registerCommand(commandObj, false);
    }
}
