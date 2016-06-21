import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import pl.themolka.ibot.IBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Bootstrap implements IBootstrap {
    public static final String COPYRIGHT = "Copyright (c) 2016 TheMolkaPL";
    public static final Properties PROPERTIES = new Properties();
    public static final File PROPERTIES_FILE = new File("cli.properties");

    @Override
    public void init(CommandLine commandLine) {
        System.out.println("[Bootstrap] Starting iBot - " + COPYRIGHT + "...");

        File propertiesFile = this.parsePropertiesFile(commandLine);
        Properties properties = this.loadPropertiesFile(propertiesFile);
        this.loadProperties(commandLine, properties);
        this.setupLogger();

        if (properties.getProperty("development", "false").equals("true")) {
            System.err.println("[Bootstrap] Enabling in the development mode - some of the features may not work correctly!");
        } else if (System.console() == null) {
            System.err.println("[Bootstrap] Your JVM does not support the console object.");
            return;
        }

        this.bootstrapIBot(properties);
    }

    public void bootstrapIBot(Properties properties) {
        try {
            new IBot(properties).start();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private void loadProperties(CommandLine commandLine, Properties properties) {
        for (Option option : commandLine.getOptions()) {
            String value = option.getValue();
            if (value == null) {
                value = Boolean.TRUE.toString();
            }

            properties.setProperty("cli." + value.replace("_", "."), value);
        }
    }

    private Properties loadPropertiesFile(File file) {
        Properties properties = PROPERTIES;
        if (file.exists()) {
            try (FileInputStream input = new FileInputStream(file)) {
                if (FilenameUtils.isExtension(file.getName(), "xml")) {
                    properties.loadFromXML(input);
                } else {
                    properties.load(input);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return properties;
    }

    private File parsePropertiesFile(CommandLine commandLine) {
        if (!commandLine.hasOption("file")) {
            return PROPERTIES_FILE;
        }

        return new File(commandLine.getOptionValue("file"));
    }

    private void setupLogger() {
        String date = DateFormatUtils.ISO_DATE_FORMAT.format(System.currentTimeMillis());
        IBot.setupLogger(IBot.getLogger(), "ConsoleLogger", "FileLogger", "logs" + File.separator + date + ".log");
    }
}
