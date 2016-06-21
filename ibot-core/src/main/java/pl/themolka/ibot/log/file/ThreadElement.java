package pl.themolka.ibot.log.file;

import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.log.ErrorHandler;
import pl.themolka.ibot.util.ThreadComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ThreadElement extends ErrorFileElement {
    public ThreadElement(IBot iBot, ErrorHandler handler) {
        super(iBot, handler);
    }

    @Override
    public Element call() throws Exception {
        return this.nodes("active-threads",
                this.getActiveThreads()
        );
    }

    public Element[] getActiveThreads() {
        SortedMap<Thread, java.lang.StackTraceElement[]> threadMap = new TreeMap<>(new ThreadComparator());
        threadMap.putAll(Thread.getAllStackTraces());

        List<Element> elements = new ArrayList<>();

        for (Thread thread : threadMap.keySet()) {
            StringBuilder stackTrace = new StringBuilder();
            for (java.lang.StackTraceElement element : threadMap.get(thread)) {
                stackTrace.append(element.toString()).append("\n");
            }

            Element node = this.node("thread")
                    .setAttribute("id", String.valueOf(thread.getId()));
            node.addContent(this.node("name", thread.getName()));
            node.addContent(this.node("class", thread.getClass().getName()));
            node.addContent(this.node("state", thread.getState().toString()));
            node.addContent(this.node("priority", thread.getPriority()));

            if (stackTrace.length() > 0) {
                node.addContent(this.node("stack-trace", stackTrace.toString()));
            }

            elements.add(node);
        }

        return elements.toArray(new Element[elements.size()]);
    }
}
