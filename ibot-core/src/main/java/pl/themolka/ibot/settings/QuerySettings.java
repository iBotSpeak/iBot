package pl.themolka.ibot.settings;

import org.jdom2.Element;
import pl.themolka.ibot.xml.XMLException;
import pl.themolka.ibot.xml.XMLReadable;

public class QuerySettings extends SettingsElement implements XMLReadable {
    public QuerySettings(Element element) {
        super(element);
    }

    @Override
    public void read() throws XMLException {

    }
}
