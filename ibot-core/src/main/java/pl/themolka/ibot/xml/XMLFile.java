package pl.themolka.ibot.xml;

import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import pl.themolka.ibot.IBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class XMLFile {
    protected Document document;

    public XMLFile() {
    }

    public XMLFile(InputStream input) throws IOException, JDOMException {
        this.load(input);
    }

    public Document getDocument() {
        return this.document;
    }

    public void load(File file) throws IOException, JDOMException {
        this.load(new FileInputStream(file));
    }

    public void load(InputStream input) throws IOException, JDOMException {
        this.document = new SAXBuilder().build(input);
    }

    public void load(Reader reader) throws IOException, JDOMException {
        this.load(reader);
    }

    public void load(URL url) throws IOException, JDOMException {
        this.load(url.openStream());
    }

    public void parse() {
        Iterator<Element> iterator = new ArrayList<>(this.getDocument().getRootElement().getChildren()).iterator();
        while (iterator.hasNext()) {
            this.parseElement(iterator.next());
        }
    }

    private void includeFile(Element element) throws IOException {
        String file = element.getAttributeValue("file");
        String url = element.getAttributeValue("url");

        InputStream input = null;
        if (file != null) {
            File fileObj = new File(file);
            if (!fileObj.exists()) {
                IBot.getLogger().info("Copying " + file + " file...");

                URL resource = this.getClass().getClassLoader().getResource(file);
                if (resource != null) {
                    FileUtils.copyURLToFile(resource, fileObj);
                }
            }

            input = new FileInputStream(fileObj);
        } else if (url != null) {
            input = new URL(url).openStream();
        }

        if (input != null) {
            try {
                XMLFile xml = new XMLFile(input);
                xml.parse();

                Element head = element.getParentElement().getParentElement();
                Element root = xml.getDocument().getRootElement().clone().detach();
                Element parent = element.getParentElement().detach();
                if (head == null) {
                    throw new JDOMException("Could not include the XML here.");
                }

                parent.removeContent();
                head.addContent(root);
            } catch (Throwable ex) {
                IBot.getLogger().trace("Could not parse the XML file.", ex);
            } finally {
                input.close();
            }
        }
    }

    private void parseElement(Element element) {
        try {
            switch (element.getName().toLowerCase()) {
                case "include":
                    this.includeFile(element);
                    break;
            }
        } catch (IOException ex) {
            IBot.getLogger().trace("Could not parse the XML file.", ex);
        }

        Iterator<Element> iterator = new ArrayList<>(element.getChildren()).iterator();
        while (iterator.hasNext()) {
            this.parseElement(iterator.next());
        }
    }
}
