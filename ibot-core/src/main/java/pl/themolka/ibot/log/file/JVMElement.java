package pl.themolka.ibot.log.file;

import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.log.ErrorHandler;

public class JVMElement extends ErrorFileElement {
    public JVMElement(IBot iBot, ErrorHandler handler) {
        super(iBot, handler);
    }

    @Override
    public Element call() throws Exception {
        return this.nodes("jvm",
                this.getVendor(),
                this.getName(),
                this.getVersion(),
                this.getInfo()
        );
    }

    public Element getVendor() {
        return this.node("vendor", System.getProperty("java.vm.vendor"));
    }

    public Element getName() {
        return this.node("name", System.getProperty("java.vm.name"));
    }

    public Element getVersion() {
        return this.node("version", System.getProperty("java.vm.version"));
    }

    public Element getInfo() {
        return this.node("info", System.getProperty("java.vm.info"));
    }
}
