package pl.themolka.ibot.client;

import org.bson.types.ObjectId;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.storage.IObjectId;
import pl.themolka.ibot.storage.MongoSerializable;
import pl.themolka.ibot.storage.document.MongoClient;
import pl.themolka.iserverquery.client.UniqueIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TSConnectedClient extends pl.themolka.itsquery.client.TSConnectedClient implements IObjectId, MongoSerializable {
    private final BotQuery botQuery;

    private ObjectId objectId;
    private int id;
    private String ip;
    private long lastUpdateMillis;
    private final Session session;
    private final List<String> usernameHistory = new ArrayList<>();

    public TSConnectedClient(BotQuery botQuery, int databaseId, UniqueIdentifier identifier, ObjectId objectId) {
        super(botQuery, databaseId, 0, identifier);
        this.botQuery = botQuery;

        this.objectId = objectId;
        this.updateLastMillis();

        this.session = new Session(this.botQuery, this);
        this.session.create();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ObjectId getObjectId() {
        return this.objectId;
    }

    @Override
    public Map<String, Object> serialize(Map<String, Object> data) {
        data.put(MongoClient.FIELD_UID, this.getIdentifier().getIdentifier());
        return data;
    }

    @Override
    public void setUsername(String username) {
        this.setUsername(username, true);
    }

    public String getIp() {
        return this.ip;
    }

    public long getLastUpdateMillis() {
        return this.lastUpdateMillis;
    }

    public Session getSession() {
        return this.session;
    }

    public List<String> getUsernameHistory() {
        return this.usernameHistory;
    }

    public boolean timeout() {
        return System.currentTimeMillis() - this.getLastUpdateMillis() > 1000L;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setLastUpdateMillis(long lastUpdateMillis) {
        this.lastUpdateMillis = lastUpdateMillis;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public void setUsername(String username, boolean history) {
        super.setUsername(username);

        if (history && username != null) {
            this.getUsernameHistory().add(username);
        }
    }

    public void updateLastMillis() {
        this.setLastUpdateMillis(System.currentTimeMillis());
    }
}
