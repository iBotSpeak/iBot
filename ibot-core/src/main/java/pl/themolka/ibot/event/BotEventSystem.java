package pl.themolka.ibot.event;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.iserverquery.event.EventSystem;

public class BotEventSystem extends EventSystem {
    public BotEventSystem(BotQuery botQuery) {
        super(new BotEventExceptionHandler(botQuery));
    }

    private static class BotEventExceptionHandler implements SubscriberExceptionHandler {
        private final BotQuery botQuery;

        public BotEventExceptionHandler(BotQuery botQuery) {
            this.botQuery = botQuery;
        }

        @Override
        public void handleException(Throwable exception, SubscriberExceptionContext context) {
            this.botQuery.getLogger().trace(String.format(
                    "Could not pass event %s to '%s': %s",
                    context.getEvent().getClass().getName(),
                    context.getSubscriberMethod().getName(),
                    exception.getMessage()), exception
            );
        }
    }
}
