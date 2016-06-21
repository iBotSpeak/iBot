package pl.themolka.ibot.plugins.welcomemessage;

import com.google.common.eventbus.Subscribe;
import pl.themolka.ibot.plugin.IBotPlugin;
import pl.themolka.iserverquery.client.ClientConnectEvent;

public class WelcomeMessagePlugin extends IBotPlugin {
    private String messageLayout = null;

    @Override
    public void onEnable() {
        this.messageLayout = "Witaj %[client.name] na cyka-blyat.pl! Twoje UID to %[client.uid]; jesteś z kraju %[client.country].";
    }

    @Subscribe
    public void onClientConnect(ClientConnectEvent event) {
        WelcomeMessage message = new WelcomeMessage(this);
        message.prepareClient(event.getClient());

        event.getClient().sendMessage(message.getMessage());
    }

    public String getMessageLayout() {
        return this.messageLayout;
    }
}
