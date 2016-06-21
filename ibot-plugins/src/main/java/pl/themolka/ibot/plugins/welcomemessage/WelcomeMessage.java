package pl.themolka.ibot.plugins.welcomemessage;

import pl.themolka.ibot.util.StringFields;
import pl.themolka.iserverquery.client.ConnectedClient;
import pl.themolka.iserverquery.text.Message;

public class WelcomeMessage implements Message {
    private final WelcomeMessagePlugin plugin;

    private StringFields message = new StringFields();
    private String rawMessage;

    public WelcomeMessage(WelcomeMessagePlugin plugin) {
        this.plugin = plugin;

        this.rawMessage = plugin.getMessageLayout();
    }

    @Override
    public String getMessage() {
        return this.message.format(this.rawMessage);
    }

    public void prepareClient(ConnectedClient client) {
        this.message
                .append("client.away.message", client.getAwayMessage())
                .append("client.country", client.getCountry())
                .append("client.db.id", client.getDatabaseId())
                .append("client.description", client.getDescription())
                .append("client.id", client.getId())
                .append("client.name", client.getUsername())
                .append("client.uid", client.getIdentifier().getIdentifier());
    }
}
