package pl.themolka.ibot.log.file;

import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.log.ErrorHandler;

public class StackTraceElement extends ErrorFileElement {
    public StackTraceElement(IBot iBot, ErrorHandler handler) {
        super(iBot, handler);
    }

    @Override
    public Element call() throws Exception {
        return this.nodes("stack-trace",
                this.getMessage(),
                this.getTraceMessage(),
                this.getTrace()
        );
    }

    public Element getMessage() {
        return this.node("message", this.handler.getMessage());
    }

    public Element getTraceMessage() {
        return this.node("trace-message", this.handler.getThrowable().getMessage());
    }

    public Element getTrace() {
        StringBuilder builder = new StringBuilder();
        for (java.lang.StackTraceElement element : this.handler.getThrowable().getStackTrace()) {
            builder.append(element.toString()).append("\n");
        }

        return this.node("trace", builder.toString())
                .setAttribute("class", this.handler.getThrowable().getClass().getName());
    }
}
