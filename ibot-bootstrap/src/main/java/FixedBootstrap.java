import org.apache.commons.cli.CommandLine;

import java.util.Properties;

public class FixedBootstrap extends Bootstrap {
    @Override
    public void init(CommandLine commandLine) {
        super.init(commandLine);
    }

    @Override
    public void bootstrapIBot(Properties properties) {
        try {
            new ibot.FixedIBot(properties).start();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
