package pl.themolka.ibot.plugin;

import org.jdom2.JDOMException;
import pl.themolka.ibot.xml.XMLFile;

import java.io.File;
import java.io.IOException;

public interface Plugin {
    void copyDefaultResource(File resource) throws IOException;

    void copyDefaultResource(String resource) throws IOException;

    void copyDefaultSettings() throws IOException;

    void copyResource(File resource) throws IOException;

    void copyResource(String resource) throws IOException;

    File getDataDirectory();

    String getName();

    PluginDataFile getPluginData();

    XMLFile getSettings();

    File getSettingsFile();

    String getVersion();

    boolean isEnabled();

    boolean isInitialized();

    void reloadSettings() throws IOException, JDOMException;

    void setEnabled(boolean enabled);
}
