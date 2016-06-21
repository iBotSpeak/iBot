package pl.themolka.ibot.response;

import com.google.common.eventbus.Subscribe;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.query.QueryResponseEvent;

import java.util.ArrayList;
import java.util.List;

public class ResponseHandler {
    private final BotQuery botQuery;

    private final List<IResponseQuery> queries = new ArrayList<>();

    public ResponseHandler(BotQuery botQuery) {
        this.botQuery = botQuery;

        this.registerDefaults();
        this.botQuery.getEvents().register(this);
    }

    public List<IResponseQuery> getQueries() {
        return this.queries;
    }

    public boolean registerQuery(IResponseQuery query) {
        return this.queries.add(query);
    }

    public boolean registerQueries(IResponseQuery... queries) {
        boolean result = true;
        for (IResponseQuery query : queries) {
            if (!this.registerQuery(query)) {
                result = false;
            }
        }

        return result;
    }

    @Subscribe
    void onQueryResponse(QueryResponseEvent event) {
        for (CommandContext response : event.getResponse()) {
            for (IResponseQuery query : this.getQueries()) {
                if (query.applies(response)) {
                    query.onResponse(response);
                }
            }
        }
    }

    private void registerDefaults() {
        this.registerQueries(
                new ClientInfoQuery(this.botQuery),
                new ClientListQuery(this.botQuery)
        );
    }
}
