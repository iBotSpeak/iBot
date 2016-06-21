package pl.themolka.ibot.log.file;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.log.ErrorHandler;

import java.util.concurrent.Callable;

public abstract class ErrorFileElement implements Callable<Element> {
    public static final String UNDEFINED_VALUE = "undefined";

    protected final IBot iBot;
    protected final ErrorHandler handler;

    public ErrorFileElement(IBot iBot, ErrorHandler handler) {
        this.iBot = iBot;
        this.handler = handler;
    }

    public String datetime(long millis) {
        return DateFormatUtils.ISO_DATETIME_FORMAT.format(millis).replace("T", " ");
    }

    public Element node(String name) {
        return new Element(name);
    }

    public Element node(String name, Object value) {
        if (value == null) {
            value = UNDEFINED_VALUE;
        }
        return new Element(name).setText(value.toString());
    }

    public Element nodes(String name, Element... children) {
        Element node = new Element(name);
        for (Element child : children) {
            node.addContent(child);
        }
        return node;
    }

    public static Element buildXMLMap(IBot iBot, ErrorHandler handler, Element root) {
        for (ErrorFileElement element : getElements(iBot, handler)) {
            try {
                Element xml = element.call();
                if (xml != null) {
                    root.addContent(xml);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return root;
    }

    public static ErrorFileElement[] getElements(IBot iBot, ErrorHandler handler) {
        return new ErrorFileElement[] {
                new ErrorElement(iBot, handler),
                new IBotElement(iBot, handler),
                new JavaElement(iBot, handler),
                new JVMElement(iBot, handler),
                new PlatformElement(iBot, handler),
                new StackTraceElement(iBot, handler),
                new ThreadElement(iBot, handler)
        };
    }
}
