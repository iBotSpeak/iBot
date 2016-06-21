package pl.themolka.ibot.log.file;

import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.log.ErrorHandler;

public class IBotElement extends ErrorFileElement {
    public IBotElement(IBot iBot, ErrorHandler handler) {
        super(iBot, handler);
    }

    @Override
    public Element call() throws Exception {
        return this.nodes("ibot",
                this.getName(),
                this.getVersion(),
                this.getVersionCommit(),
                this.getStartTime()
        );
    }

    public Element getName() {
        return this.node("name", this.iBot.getName());
    }

    public Element getVersion() {
        return this.node("version", this.iBot.getVersion());
    }

    public Element getVersionCommit() {
        return this.node("commit", null);
    }

    public Element getStartTime() {
        return this.node("start-time", this.iBot.getStartTime())
                .setAttribute("iso", this.datetime(this.iBot.getStartTime()));
    }
}
