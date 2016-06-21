package pl.themolka.ibot.response;

import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.iserverquery.command.CommandContext;

public class ClientListQuery extends ResponseQuery {
    public ClientListQuery(BotQuery botQuery) {
        super(botQuery);
    }

    @Override
    public boolean applies(CommandContext context) {
        return this.hasParameters(context,
                "clid",
                "cid",
                "client_database_id"
        );
    }

    @Override
    public void onResponse(CommandContext context) {
    }
}
