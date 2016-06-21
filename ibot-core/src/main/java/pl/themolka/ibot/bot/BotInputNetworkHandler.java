package pl.themolka.ibot.bot;

import pl.themolka.itsquery.net.DataContainer;
import pl.themolka.itsquery.net.input.InputNetworkHandler;
import pl.themolka.itsquery.query.TSQuery;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BotInputNetworkHandler extends InputNetworkHandler implements Runnable {
    private final List<DataContainer> queueList = new CopyOnWriteArrayList<>();

    public BotInputNetworkHandler(TSQuery tsQuery) {
        super(tsQuery);
    }

    @Override
    public void execute(String command, DataContainer container) {
        // we have to add this query to the queue, when it have to be handled
        if (this.filterQueryCommand(command)) {
            this.addQueueItem(container);
        }
    }

    @Override
    public void run() {
        for (DataContainer container : this.getQueue()) {
            this.handleInputQuery(container);
            this.queueList.remove(container);
        }
    }

    public void addQueueItem(DataContainer container) {
        if (!container.isEmpty()) {
            this.queueList.add(container);
        }
    }

    public boolean filterQueryCommand(String command) {
        return !command.startsWith("notifycliententerview");
    }

    public void handleInputQuery(DataContainer container) {
        // let handle it in the original input handler code
        super.execute(container.get(0).getCommand().getCommand(), container);
    }

    public List<DataContainer> getQueue() {
        return this.queueList;
    }
}
