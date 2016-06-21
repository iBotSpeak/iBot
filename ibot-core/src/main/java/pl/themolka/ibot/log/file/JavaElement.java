package pl.themolka.ibot.log.file;

import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.log.ErrorHandler;

public class JavaElement extends ErrorFileElement {
    public JavaElement(IBot iBot, ErrorHandler handler) {
        super(iBot, handler);
    }

    @Override
    public Element call() throws Exception {
        return this.nodes("java",
                this.getVendor(),
                this.getVersion(),
                this.getHome()
        );
    }

    public Element getVendor() {
        return this.node("vendor", System.getProperty("java.vendor"));
    }

    public Element getVersion() {
        return this.node("version", System.getProperty("java.version"));
    }

    public Element getHome() {
        return this.node("home", System.getProperty("java.home"));
    }
}
