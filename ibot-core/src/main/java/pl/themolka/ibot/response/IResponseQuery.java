package pl.themolka.ibot.response;

import pl.themolka.iserverquery.command.CommandContext;

public interface IResponseQuery {
    boolean applies(CommandContext context);

    void onResponse(CommandContext context);
}
