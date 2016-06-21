package pl.themolka.ibot.plugin;

import pl.themolka.iserverquery.event.Event;

public class PluginEvent extends Event {
    private final Plugin plugin;

    public PluginEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }
}
