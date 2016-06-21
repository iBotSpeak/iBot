package pl.themolka.ibot.log.file;

import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.log.ErrorHandler;

public class PlatformElement extends ErrorFileElement {
    public PlatformElement(IBot iBot, ErrorHandler handler) {
        super(iBot, handler);
    }

    @Override
    public Element call() throws Exception {
        return this.nodes("platform",
                this.getSystemName(),
                this.getSystemArch(),
                this.getSystemVersion(),
                this.getData()
        );
    }

    public Element getSystemName() {
        return this.node("os-name", System.getProperty("os.name"));
    }

    public Element getSystemArch() {
        return this.node("os-arch", System.getProperty("os.arch"));
    }

    public Element getSystemVersion() {
        return this.node("os-version", System.getProperty("os.version"));
    }

    public Element getData() {
        return this.node("data", this.iBot.getPlatform().getId())
                .setAttribute("name", this.iBot.getPlatform().getName());
    }
}
