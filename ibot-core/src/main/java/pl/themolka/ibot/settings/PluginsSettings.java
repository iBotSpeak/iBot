package pl.themolka.ibot.settings;

import org.apache.commons.io.FilenameUtils;
import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.plugin.PluginDataFile;
import pl.themolka.ibot.xml.XMLException;
import pl.themolka.ibot.xml.XMLReadable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PluginsSettings extends SettingsElement implements XMLReadable {
    private final List<PluginsDirectory> directories = new ArrayList<>();
    private final List<PluginDataFile> plugins = new ArrayList<>();

    public PluginsSettings(Element element) {
        super(element);
    }

    @Override
    public void read() throws XMLException {
        for (Element plugin : this.getXML().getChildren("plugin")) {
            PluginDataFile data = new PluginDataFile(plugin);
            try {
                data.read();
                this.plugins.add(data);
            } catch (XMLException ex) {
                IBot.getLogger().trace("Could not read plugin " + data.getName() + " v" + data.getVersion() + ": " + ex.getMessage(), ex);
            }
        }

        for (Element directory : this.getXML().getChildren("directory")) {
            PluginsDirectory directoryObj = new PluginsDirectory(directory);
            try {
                directoryObj.read();
                this.directories.add(directoryObj);
            } catch (XMLException ex) {
                IBot.getLogger().trace("Could not read directories.", ex);
            }
        }
    }

    public List<PluginsDirectory> getDirectories() {
        return this.directories;
    }

    public List<PluginDataFile> getPlugins() {
        return this.plugins;
    }

    public static class PluginsDirectory extends SettingsElement implements XMLReadable {
        private final List<PluginsDirectoryFile> files = new ArrayList<>();
        private String path;

        public PluginsDirectory(Element element) {
            super(element);
        }

        @Override
        public void read() throws XMLException {
            this.path = this.getXML().getAttributeValue("path");

            for (Element file : this.getXML().getChildren("file")) {
                PluginsDirectoryFile directoryFile = new PluginsDirectoryFile(file);
                try {
                    directoryFile.read();
                    this.files.add(directoryFile);
                } catch (XMLException ex) {
                    IBot.getLogger().trace("Could not read file settings.", ex);
                }
            }
        }

        public boolean apply(String filename) {
            if (this.files.isEmpty()) {
                return true;
            }

            for (PluginsDirectoryFile file : this.files) {
                if (file.apply(filename)) {
                    return true;
                }
            }

            return false;
        }

        public List<PluginsDirectoryFile> getFiles() {
            return this.files;
        }

        public String getPath() {
            return this.path;
        }
    }

    public static class PluginsDirectoryFile extends SettingsElement implements XMLReadable {
        private final List<String> data = new ArrayList<>();
        private String[] extensions;
        private Pattern pattern;

        public PluginsDirectoryFile(Element element) {
            super(element);
        }

        @Override
        public void read() throws XMLException {
            String extensions = this.getXML().getAttributeValue("extension");
            String pattern = this.getXML().getAttributeValue("pattern");

            if (extensions != null) {
                this.extensions = extensions.split("\\|");
            } else if (pattern != null) {
                try {
                    this.pattern = Pattern.compile(pattern);
                } catch (PatternSyntaxException ex) {
                    IBot.getLogger().trace("Invalid pattern for regular expression: " + pattern, ex);
                }
            }

            for (Element data : this.getXML().getChildren("data")) {
                if (data.getText() != null) {
                    this.data.add(data.getTextTrim());
                }
            }
        }

        public boolean apply(String filename) {
            if (this.extensions != null) {
                return FilenameUtils.isExtension(filename, this.extensions);
            } else if (this.pattern != null) {
                return this.pattern.matcher(filename).matches();
            } else {
                return false;
            }
        }

        public List<String> getDataFiles() {
            return this.data;
        }
    }
}
