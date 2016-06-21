package pl.themolka.ibot.log.file;

import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.log.ErrorHandler;

public class ErrorElement extends ErrorFileElement {
    public ErrorElement(IBot iBot, ErrorHandler handler) {
        super(iBot, handler);
    }

    @Override
    public Element call() throws Exception {
        return this.nodes("error",
                this.getDatetime()
        );
    }

    public Element getDatetime() {
        long now = System.currentTimeMillis();
        return this.node("datetime", now)
                .setAttribute("iso", this.datetime(now));
    }
}
