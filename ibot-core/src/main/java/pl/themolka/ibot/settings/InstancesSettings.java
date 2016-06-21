package pl.themolka.ibot.settings;

import org.apache.commons.lang3.RandomStringUtils;
import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.xml.XMLException;
import pl.themolka.ibot.xml.XMLReadable;

import java.util.ArrayList;
import java.util.List;

public class InstancesSettings extends SettingsElement implements XMLReadable {
    public static final int DEFAULT_PORT = 10011;

    private final List<ServerInstance> instances = new ArrayList<>();

    public InstancesSettings(Element element) {
        super(element);
    }

    @Override
    public void read() throws XMLException {
        for (Element element : this.getXML().getChildren("server")) {
            ServerInstance instance = new ServerInstance(element);
            try {
                instance.read();
                this.instances.add(instance);
            } catch (XMLException ex) {
                IBot.getLogger().trace("Could not read server " + instance.getId(), ex);
            }
        }
    }

    public List<ServerInstance> getInstances() {
        return this.instances;
    }

    public static class ServerInstance extends SettingsElement implements XMLReadable {
        private String id;
        private String host;
        private int port;

        private String queryUsername;
        private String queryPassword;

        private int selectId;
        private int selectPort;
        private boolean selectVirtual;

        private QuerySettings settings;

        public ServerInstance(Element element) {
            super(element);
        }

        @Override
        public void read() throws XMLException {
            String id = this.getXML().getAttributeValue("id");
            String host = this.getXML().getAttributeValue("host");
            String port = this.getXML().getAttributeValue("port");

            Element query = this.getXML().getChild("login");
            String queryUsername = query.getAttributeValue("username");
            String queryPassword = query.getAttributeValue("password");

            Element select = this.getXML().getChild("select");
            String selectId = select.getAttributeValue("id");
            String selectPort = select.getAttributeValue("port");
            String selectVirtual = select.getAttributeValue("virtual");

            Element settings = this.getXML().getChild("setttings");

            if (id == null) {
                id = RandomStringUtils.randomAlphanumeric(10);
            } else if (host == null) {
                throw new XMLException("Host is not set.");
            } else if (port == null) {
                port = String.valueOf(DEFAULT_PORT);
            } else if (queryUsername == null) {
                throw new XMLException("Query username is not set.");
            } else if (queryPassword == null) {
                throw new XMLException("Query password is not set.");
            } else if (selectVirtual == null) {
                selectVirtual = Boolean.FALSE.toString();
            }

            try {
                this.id = id;
                this.host = host;
                this.port = Integer.parseInt(port);

                this.queryUsername = queryUsername;
                this.queryPassword = queryPassword;

                if (selectId != null) {
                    this.selectId = Integer.parseInt(selectId);
                } else if (selectPort != null) {
                    this.selectPort = Integer.parseInt(selectPort);
                }
                this.selectVirtual = Boolean.parseBoolean(selectVirtual);

                QuerySettings settingsObj = new QuerySettings(settings);
                settingsObj.read();
                this.settings = settingsObj;
            } catch (Throwable ex) {
                IBot.getLogger().trace(ex);
            }
        }

        public String getId() {
            return this.id;
        }

        public String getHost() {
            return this.host;
        }

        public int getPort() {
            return this.port;
        }

        public String getQueryUsername() {
            return this.queryUsername;
        }

        public String getQueryPassword() {
            return this.queryPassword;
        }

        public int getSelectId() {
            return this.selectId;
        }

        public int getSelectPort() {
            return this.selectPort;
        }

        public boolean isSelectVirtual() {
            return this.selectVirtual;
        }

        public QuerySettings getSettings() {
            return this.settings;
        }
    }
}
