package pl.themolka.ibot.client;

import org.bson.types.ObjectId;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.storage.IObjectId;
import pl.themolka.ibot.storage.MongoSerializable;
import pl.themolka.ibot.storage.document.MongoSession;

import java.util.Map;

public class Session implements IObjectId, MongoSerializable {
    private final BotQuery botQuery;

    private final TSConnectedClient client;
    private boolean created = false;
    private ObjectId destroyed;
    private ObjectId objectId;

    public Session(BotQuery botQuery, TSConnectedClient client) {
        this(botQuery, client, new ObjectId());
    }

    public Session(BotQuery botQuery, TSConnectedClient client, ObjectId objectId) {
        this.botQuery = botQuery;

        this.client = client;
        this.objectId = objectId;
    }

    @Override
    public ObjectId getObjectId() {
        return this.objectId;
    }

    @Override
    public Map<String, Object> serialize(Map<String, Object> data) {
        TSConnectedClient client = this.getClient();

        data.put(MongoSession.FIELD_CLIENT, client.getObjectId());
        data.put(MongoSession.FIELD_IP, client.getIp());
        data.put(MongoSession.FIELD_COUNTRY, client.getCountry());
        data.put(MongoSession.FIELD_USERNAME, client.getUsernameHistory());

        if (this.isDestroyed()) {
            data.put(MongoSession.FIELD_DESTROY, this.getDestroyId());
        }

        return data;
    }

    public void create() {
        if (!this.created) {
            this.created = true;
            this.botQuery.getEvents().post(new SessionCreateEvent(this));
        }
    }

    public ObjectId destroy() {
        this.setDestroyed(new ObjectId());
        this.botQuery.getEvents().post(new SessionDestroyEvent(this));
        return this.getDestroyId();
    }

    public TSConnectedClient getClient() {
        return this.client;
    }

    public ObjectId getDestroyId() {
        return this.destroyed;
    }

    public boolean isDestroyed() {
        return this.getDestroyId() != null;
    }

    public void setDestroyed(ObjectId objectId) {
        this.destroyed = objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }
}
