package pl.themolka.ibot.terminal.commands;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.command.BotCommand;
import pl.themolka.ibot.command.CommandContainer;
import pl.themolka.ibot.util.TerminalUtils;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.iserverquery.command.CommandSender;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SettingsCommands extends CommandContainer {
    public SettingsCommands(IBot iBot) {
        super(iBot);
    }

    @BotCommand(name = "xml", description = "Preview the XML source", usage = "[-c] [path]", flags = {"c", "r"})
    public void xml(CommandSender sender, CommandContext context) {
        String path = context.getParam(0);

        String[] pathArray = null;
        if (path != null) {
            pathArray = path.split("\\:");
        }

        Element element = this.iBot.getSettings().getDocument().getRootElement();
        if (pathArray != null) {
            for (int i = 0; i < pathArray.length; i++) {
                String[] elementArray = pathArray[i].split("\\?", 2);
                String name = elementArray[0].toLowerCase();

                try {
                    if (name.isEmpty()) {
                        throw new Throwable("name was not specified.");
                    }

                    List<Element> children = element.getChildren(name);
                    if (children.isEmpty()) {
                        throw new Throwable("specified path was not found.");
                    } else if (elementArray.length > 1) {
                        String[] keyValue = elementArray[1].split("=", 2);
                        if (keyValue.length != 2) {
                            throw new Throwable("attribute pattern is not valid.");
                        }

                        for (Element child : children) {
                            Attribute attribute = child.getAttribute(keyValue[0]);
                            if (attribute == null) {
                                throw new Throwable("specified attribute was not found.");
                            } else if (attribute.getValue() != null && attribute.getValue().equals(keyValue[1])) {
                                element = child;
                                break;
                            } else {
                                throw new Throwable("specified attribute value was not found.");
                            }
                        }
                    } else {
                        element = children.get(0);
                    }
                } catch (Throwable ex) {
                    sender.sendMessage("Could not find settings: " + ex.getMessage());
                    return;
                }
            }
        }

        if (element.equals(this.iBot)) {
            if (!TerminalUtils.readYesOrNo("You will print the full XML file. Do you want to continue?")) {
                return;
            }
        }

        Format format = Format.getPrettyFormat();
        if (context.hasFlag("c")) {
            format = Format.getCompactFormat();
        }

        try {
            TerminalOutputStream output = new TerminalOutputStream(System.lineSeparator());
            XMLOutputter xmlOutput = new XMLOutputter(format);
            xmlOutput.output(element, output);

            String breakLine = System.lineSeparator();
            String separator = "+++++ -----";

            sender.sendMessage(breakLine + separator + output.getText() + breakLine + separator);
        } catch (IOException ex) {
            sender.sendMessage("Could not print the XML: " + ex.getMessage());
        }
    }

    private class TerminalOutputStream extends OutputStream {
        private final StringBuilder builder;

        public TerminalOutputStream(String string) {
            this.builder = new StringBuilder(string);
        }

        @Override
        public void write(int write) throws IOException {
            int[] bytes = {write};
            this.builder.append(new String(bytes, 0, bytes.length));
        }

        public String getText() {
            return this.builder.toString();
        }
    }
}
