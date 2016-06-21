package pl.themolka.ibot.settings;

import org.jdom2.Element;

public class SettingsElement {
    private final Element element;

    public SettingsElement(Element element) {
        this.element = element;
    }

    public Element getXML() {
        return this.element;
    }
}
