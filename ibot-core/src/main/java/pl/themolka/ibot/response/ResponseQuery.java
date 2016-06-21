package pl.themolka.ibot.response;

import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.iserverquery.command.CommandContext;

public abstract class ResponseQuery implements IResponseQuery {
    protected final BotQuery botQuery;

    public ResponseQuery(BotQuery botQuery) {
        this.botQuery = botQuery;
    }

    public boolean hasParameters(CommandContext context, String... parameters) {
        for (String parameter : parameters) {
            if (!context.hasFlag(parameter)) {
                return false;
            }
        }

        return true;
    }
}
