package pl.themolka.ibot.response;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import org.bson.Document;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.client.TSConnectedClient;
import pl.themolka.ibot.storage.StorageDocument;
import pl.themolka.ibot.storage.document.MongoClient;
import pl.themolka.ibot.storage.document.MongoSession;
import pl.themolka.iserverquery.client.ClientConnectEvent;
import pl.themolka.iserverquery.client.UniqueIdentifier;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.util.Platform;
import pl.themolka.itsquery.server.TSServer;

public class ClientInfoQuery extends ResponseQuery {
    public ClientInfoQuery(BotQuery botQuery) {
        super(botQuery);
    }

    @Override
    public boolean applies(CommandContext context) {
        return this.hasParameters(context,
//                "cid",
                "client_unique_identifier",
                "client_nickname",
                "client_platform",
                "client_country"
        );
    }

    @Override
    public void onResponse(CommandContext context) {
        final int channelId = context.getFlagInt("cid");
        // client_idle_time
        final String uniqueId = context.getFlag("client_unique_identifier");
        final String username = context.getFlag("client_nickname");
        final String version = context.getFlag("client_version");
        final String platform = context.getFlag("client_platform");
        final boolean inputMuted = context.getFlagBoolean("client_input_muted");
        final boolean outputMuted = context.getFlagBoolean("client_output_muted");
        final boolean outputOnlyMuted = context.getFlagBoolean("client_outputonly_muted");
        final boolean inputHardware = context.getFlagBoolean("client_input_hardware");
        final boolean outputHardware = context.getFlagBoolean("client_input_hardware");
//        final int defaultChannel = context.getFlagInt("client_default_channel");
        final String metaData = context.getFlag("client_meta_data");
        final boolean recording = context.getFlagBoolean("client_is_recording");
        final String versionSign = context.getFlag("client_version_sign");
        final String securityHash = context.getFlag("client_security_hash");
        final String loginName = context.getFlag("client_login_name");
        final int databaseId = context.getFlagInt("client_database_id");
        final int channelGroupId = context.getFlagInt("client_channel_group_id");
        final String serverGroups = context.getFlag("client_servergroups");
        final long created = context.getFlagInt("client_created");
        final long lastConnected = context.getFlagInt("client_lastconnected");
        final int totalConnections = context.getFlagInt("client_totalconnections");
        final boolean away = context.getFlagBoolean("client_away");
        final String awayMessage = context.getFlag("client_away_message");
        final int clientType = context.getFlagInt("client_type");
        final String avatar = context.getFlag("client_flag_avatar");
        final int talkPower = context.getFlagInt("client_talk_power");
        final int talkRequest = context.getFlagInt("client_talk_request");
        final String talkRequestMessage = context.getFlag("client_talk_request_msg");
        final String description = context.getFlag("client_description");
        final boolean talker = context.getFlagBoolean("client_is_talker");
        final long monthUploaded = context.getFlagInt("client_month_bytes_uploaded");
        final long monthDownloaded = context.getFlagInt("client_month_bytes_downloaded");
        final long totalUploaded = context.getFlagInt("client_total_bytes_uploaded");
        final long totalDownloaded = context.getFlagInt("client_total_bytes_downloaded");
        final boolean prioritySpeaker = context.getFlagBoolean("client_is_priority_speaker");
        final String phoneticUsername = context.getFlag("client_nickname_phonetic");
        final int queryViewPower = context.getFlagInt("client_needed_serverquery_view_power");
        final String defaultToken = context.getFlag("client_default_token");
        final int iconId = context.getFlagInt("client_icon_id");
        final boolean channelCommander = context.getFlagBoolean("client_is_channel_commander");
        final String country = context.getFlag("client_country");
        // client_channel_group_inherited_channel_id
        final String badges = context.getFlag("client_badges");
        final long connectedTime = context.getFlagInt("connection_connected_time");
        final String clientIp = context.getFlag("connection_client_ip");

        UniqueIdentifier uniqueIdObj = UniqueIdentifier.valueOf(uniqueId);

        // client
        TSConnectedClient client = (TSConnectedClient) botQuery.getServer().getConnectedClient(uniqueIdObj);
        if (client == null) {
            client = new TSConnectedClient(
                    botQuery,
                    databaseId,
                    uniqueIdObj,
                    null
            );
        }

        // setup
        client.setAway(away);
        client.setAwayMessage(awayMessage);
        client.setChannel(this.botQuery.getServer().getChannel(channelId));
        client.setChannelCommander(channelCommander);
        client.setCountry(country);
        client.setDescription(description);
        client.setIp(clientIp);
        client.setMuted(inputMuted);
        client.setPlatform(Platform.valueOf(platform));
        client.setPrioritySpeaker(prioritySpeaker);
        client.setRecording(recording);
        client.setTalkPower(talkPower);
        client.setTalkRequest(talkRequest);
        client.setTalkRequestMessage(talkRequestMessage);
        client.setUsername(username);
        client.setVersion(version);

        ((TSServer) this.botQuery.getServer()).registerClient(client);
        this.botQuery.getEvents().post(new ClientConnectEvent(client));

        if (client.getObjectId() == null) {
            findClientDocument(this.botQuery, client);
        }
    }

    public static void findClientDocument(final BotQuery botQuery, final TSConnectedClient client) {
        botQuery.getStorage().getClientCollection().findByUid(new Block<MongoClient>() {
            @Override
            public void apply(MongoClient mongoClient) {
                client.setObjectId(mongoClient.getFieldId());
            }
        }, new SingleResultCallback<Void>() {
            @Override
            public void onResult(Void result, Throwable throwable) {
                if (client.getObjectId() != null) {
                    createSessionDocument(botQuery, client);
                } else {
                    createClientDocument(botQuery, client);
                }
            }
        }, client.getIdentifier().getIdentifier());
    }

    public static void createClientDocument(final BotQuery botQuery, final TSConnectedClient client) {
        Document document = StorageDocument.createServer(botQuery, client);
        botQuery.getStorage().getClientCollection().getMongo().insertOne(document, new SingleResultCallback<Void>() {
            @Override
            public void onResult(Void result, Throwable throwable) {
                if (throwable != null) {
                    findClientDocument(botQuery, client);
                }
            }
        });
    }

    public static void findSessionDocument(BotQuery botQuery, final TSConnectedClient client) {
        botQuery.getStorage().getSessionCollection().findById(new Block<MongoSession>() {
            @Override
            public void apply(MongoSession mongoSession) {
                client.getSession().setObjectId(mongoSession.getFieldId());
            }
        }, new SingleResultCallback<Void>() {
            @Override
            public void onResult(Void result, Throwable throwable) {

            }
        }, client.getSession().getObjectId());
    }

    public static void createSessionDocument(final BotQuery botQuery, final TSConnectedClient client) {
        Document document = StorageDocument.createServer(botQuery, client.getSession());
        botQuery.getStorage().getSessionCollection().getMongo().insertOne(document, new SingleResultCallback<Void>() {
            @Override
            public void onResult(Void result, Throwable throwable) {
                if (throwable != null) {
                    findSessionDocument(botQuery, client);
                }
            }
        });
    }
}
