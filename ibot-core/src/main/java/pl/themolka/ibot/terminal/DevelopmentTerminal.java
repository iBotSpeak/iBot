package pl.themolka.ibot.terminal;

import com.google.common.eventbus.Subscribe;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.util.TerminalUtils;

public class DevelopmentTerminal extends Terminal {
    public DevelopmentTerminal(IBot iBot) {
        super(iBot);
        iBot.getEvents().register(this);
    }

    @Subscribe
    public void onOutputClear(TerminalUtils.OutputClearEvent event) {
        event.setCanceled(true);
        System.out.println("~~~ OUTPUT TERMINAL HANDLER HAS BEEN CLEARED ~~~");
    }
}
