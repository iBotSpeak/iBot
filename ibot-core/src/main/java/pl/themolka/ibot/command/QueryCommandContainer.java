package pl.themolka.ibot.command;

import pl.themolka.ibot.bot.BotQuery;

public class QueryCommandContainer extends CommandContainer {
    protected final BotQuery botQuery;

    public QueryCommandContainer(BotQuery botQuery) {
        super(botQuery.getBot());
        this.botQuery = botQuery;
    }
}
