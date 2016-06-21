package ibot;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class IBotPluginIncluder {
    private static final List<PluginItem> plugins = new ArrayList<>();

    public static void include(String file) {
        Document xml = buildXML(file);
        if (xml == null) {
            return;
        }

        for (Element item : xml.getRootElement().getChildren()) {
            String className = item.getAttributeValue("class");
            String name = item.getAttributeValue("name");
            String version = item.getAttributeValue("version");

            if (className == null || name == null || version == null) {
                continue;
            }

            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException ex) {
                System.err.println("[Bootstrap] Could not load " + name + ": " + ex.getMessage());
            }

            if (clazz == null) {
                continue;
            }

            plugins.add(new PluginItem(className, name, version));
        }

        if (getPlugins().isEmpty()) {
            System.out.println("[Bootstrap] No plugins were found.");
        } else {
            System.out.println("[Bootstrap] Loaded " + getPlugins().size() + " plugin(s): " + StringUtils.join(getPlugins(), ", "));
        }
    }

    public static Document buildXML(String file) {
        try (InputStream input = IBotPluginIncluder.class.getClassLoader().getResourceAsStream(file)) {
            return new SAXBuilder().build(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static List<PluginItem> getPlugins() {
        return plugins;
    }

    public static class PluginItem {
        private final String main;
        private final String name;
        private final String version;

        public PluginItem(String main, String name, String version) {
            this.main = main;
            this.name = name;
            this.version = version;
        }

        public String getMain() {
            return main;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return this.getName() + " v" + this.getVersion();
        }
    }
}
