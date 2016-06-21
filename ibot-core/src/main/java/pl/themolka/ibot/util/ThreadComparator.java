package pl.themolka.ibot.util;

import java.util.Comparator;

public class ThreadComparator implements Comparator<Thread> {
    @Override
    public int compare(Thread o1, Thread o2) {
        return new Integer((int) o1.getId()).compareTo((int) o2.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ThreadComparator) {
            return true;
        }
        return false;
    }
}
