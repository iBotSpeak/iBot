package pl.themolka.ibot.log;

import org.jdom2.Element;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.log.file.ErrorFileElement;
import pl.themolka.ibot.log.file.ErrorFileWriter;
import pl.themolka.ibot.plugin.Plugin;

import java.io.File;

public class ServerErrorHandler extends ErrorHandler {
    private final BotQuery botQuery;

    private final File directory;

    public ServerErrorHandler(BotQuery botQuery, String message, Throwable throwable) {
        super(message, throwable);
        this.botQuery = botQuery;

        this.directory = new File(botQuery.getDirectory(), "errors");
        this.directory.mkdirs();
    }

    @Override
    public File getDirectory() {
        return this.directory;
    }

    @Override
    public boolean publish() {
        try {
            ErrorFileWriter writer = new ErrorFileWriter(this.botQuery.getBot(), this);
            writer.writeXML();

            ServerElement serverElement = new ServerElement(this.botQuery);
            writer.getDocument().getRootElement().addContent(serverElement.call().setAttribute("id", this.botQuery.getIdentifier()));

            return writer.writeFile() == null;
        } catch (Throwable ex) {
            return false;
        }
    }

    private class ServerElement extends AbstractServerElement {
        public ServerElement(BotQuery botQuery) {
            super(botQuery, null);
        }

        @Override
        public Element call() throws Exception {
            return this.nodes("server",
                    this.node("state", this.botQuery.getState().getName()),
                    this.node("encoding", this.botQuery.getEncoding().displayName()),
                    this.node("host", this.botQuery.getHost().toString()),
                    this.node("version", this.botQuery.getVersion()),
                    this.node("version-build", this.botQuery.getBuild()),
                    this.node("path", this.botQuery.getDirectory().getPath()),
                    this.getPlugins(),
                    this.getTickHandlers()
            );
        }

        private Element getPlugins() {
            Element parent = this.node("plugins");
            parent.setAttribute("path", this.botQuery.getPlugins().getDirectory().getPath());

            for (Plugin plugin : this.botQuery.getPlugins().getPlugins()) {
                parent.addContent(this.nodes("plugin",
                        this.node("name", plugin.getName()),
                        this.node("version", plugin.getVersion()),
                        this.node("path", plugin.getDataDirectory().getPath())
                ).setAttribute("main", plugin.getClass().getName()));
            }

            return parent;
        }

        private Element getTickHandlers() {
            Element parent = this.node("tick-handlers");
            parent.setAttribute("tick", String.valueOf(this.botQuery.getThread().getTick()));
            parent.setAttribute("last-tick", String.valueOf(this.botQuery.getThread().getLastTick()));

            for (Runnable handler : this.botQuery.getThread().getHandlers()) {
                parent.addContent(this.nodes("handler",
                        this.node("class", handler.getClass().getName()),
                        this.node("string", handler.toString())
                ));
            }

            return parent;
        }
    }

    public static abstract class AbstractServerElement extends ErrorFileElement {
        protected final BotQuery botQuery;

        public AbstractServerElement(BotQuery botQuery, ErrorHandler handler) {
            super(botQuery.getBot(), handler);
            this.botQuery = botQuery;
        }
    }
}
